package com.yamert.icepass.ui.detail.zero;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.firebase.csm.R;
import com.firebase.csm.databinding.ActivityMeetingDetailZeroBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yamert.icepass.App;
import com.yamert.icepass.adapters.VisitorsAdapter;
import com.yamert.icepass.db.DataManager;
import com.yamert.icepass.misc.AndroidUtils;
import com.yamert.icepass.misc.NfcHelper;
import com.yamert.icepass.misc.TestUtil;
import com.yamert.icepass.models.Card;
import com.yamert.icepass.models.Meeting;
import com.yamert.icepass.models.Visitor;
import com.yamert.icepass.models.VisitorWrapper;
import com.yamert.icepass.ui.barcode.BarCodeScannerActivity;
import com.yamert.icepass.ui.base.BaseMvpActivity;
import com.yamert.icepass.ui.detail.IMeetingDetailPresenter;
import com.yamert.icepass.ui.detail.MeetingDetailPresenter;
import com.yamert.icepass.ui.dialog.CardsListDialog;
import com.yamert.icepass.ui.dialog.ResultDialog;
import com.yamert.icepass.ui.main.MainActivity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

import static com.yamert.icepass.ui.dialog.CardsListDialog.CARDS_LIST;
import static com.yamert.icepass.ui.main.MainActivity.ACTUAL;

public class MeetingDetailZeroActivity extends BaseMvpActivity<MeetingDetailPresenter> implements IMeetingDetailPresenter.View {

    private static final String MEETING_ID = "meeting_id";
    private static final String MEETING_TYPE = "meeting_type";
    private static final int SCAN_BAR_CODE = 283;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 119;

    @Inject
    DataManager dataManager;

    private Meeting mMeeting;
    private VisitorsAdapter mVisitorsAdapter = new VisitorsAdapter();
    private ActivityMeetingDetailZeroBinding mBinding;
    private NfcAdapter mNfcAdapter;
    private NfcHelper mNfcHelper;
    private Long meetingId;
    private boolean isRefreshing;
    private String people;
    private int type;
    private ObjectAnimator cardAnimator;

    public static void startActivity(Long meetingId, @MainActivity.MeetingType int type, Context context) {
        context.startActivity(
                new Intent(context, MeetingDetailZeroActivity.class)
                        .putExtra(MEETING_ID, meetingId)
                        .putExtra(MEETING_TYPE, type),
                ActivityOptionsCompat.makeCustomAnimation(context, R.anim.right_in, R.anim.scale_out).toBundle()
        );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getInstance().appComponent().inject(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_meeting_detail_zero);
        setSupportActionBar(mBinding.toolbar);
        setupBackButton(mBinding.toolbar);
        setDisplayShowTitleEnabled(false);

        initialization(new MeetingDetailZeroPresenter(dataManager, this));
    }

    @Override
    public void initialization(MeetingDetailPresenter presenter) {
        super.initialization(presenter);

        type = getIntent().getIntExtra(MEETING_TYPE, 0);
        people = (type == ACTUAL ? App.getInstance().getString(R.string.people_actual) : App.getInstance().getString(R.string.people_past));

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        mBinding.visitors.setAdapter(mVisitorsAdapter);
        mBinding.visitors.setLayoutManager(layoutManager);

        mBinding.swipeLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        mBinding.swipeLayout.setOnRefreshListener(mRefreshListener);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            mBinding.card.setVisibility(View.GONE);
            mBinding.scanLine.setVisibility(View.GONE);
            mBinding.cardTitle.setVisibility(View.GONE);
        } else if (!mNfcAdapter.isEnabled()) {
            longToast(getString(R.string.error_nfc_not_enabled));
        } else {
            mNfcHelper =
                    new NfcHelper.Builder()
                            .setPendingIntent(PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0))
                            .setIntentFilters(
                                    new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED),
                                    new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED),
                                    new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)
                            )
                            .build();
        }

        if (AndroidUtils.isOnline()) {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                meetingId = bundle.getLong(MEETING_ID);
                presenter.getMeeting(meetingId);
                presenter.loadMeeting(meetingId);
            }
        } else {
            longToast(getString(R.string.error_internet));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mNfcAdapter != null && mNfcAdapter.isEnabled()) {
            mNfcAdapter.enableForegroundDispatch(
                    this,
                    mNfcHelper.getNfcPendingIntent(),
                    mNfcHelper.getIntentFilters(),
                    mNfcHelper.getTechListsArray()
            );
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mNfcAdapter != null && mNfcAdapter.isEnabled()) {
            mNfcAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //EventBus.getDefault().post(new EventUpdateMeeting(mMeeting, type));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE &&
                grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            BarCodeScannerActivity.startActivityForResult(MeetingDetailZeroActivity.this, SCAN_BAR_CODE);
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        byte uidAsBites[] = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);

        if (uidAsBites != null) {
            String uid = AndroidUtils.byteArrayToHexString(uidAsBites);
            String reverseCardId = AndroidUtils.reverseCardId(uid);
            Timber.d("Security ID from card: " + uid);
            Timber.d("Reversed security ID from card: " + reverseCardId);
            //longToast(reverseCardId); //TEMP
            presenter.loadVisitorByUID(reverseCardId, meetingId);
        } else {
            longToast(getString(R.string.error_failed_to_scan_card));
        }
    }

    @Override
    public void showMeetingDetail(Meeting meeting) {
        mMeeting = meeting;
        mVisitorsAdapter.updateVisitors(meeting.getVisitors());
        mBinding.toolbarTitle.setText(meeting.getTitle());
        mBinding.description.setText(meeting.getDescription());
        mBinding.visitorsCount.setText(String.valueOf(meeting.getCount()) + " " + people);
    }

    @Override
    public void showVisitors(List<Visitor> visitors) {
        mBinding.visitorsCount.setText(String.valueOf(visitors.size()) + " " + people);
        mVisitorsAdapter.updateVisitors(visitors);
    }

    @Override
    public void showProgress() {
        mBinding.swipeLayout.setRefreshing(true);
    }

    @Override
    public void hideProgress() {
        isRefreshing = false;
        mBinding.swipeLayout.setRefreshing(false);
    }

    @Override
    public void showResult(VisitorWrapper visitor) {
        ResultDialog resultDialog = new ResultDialog();

        Bundle bundle = new Bundle();
        bundle.putBoolean(ResultDialog.IS_ACTIVE, true);
        bundle.putString(ResultDialog.USER_NAME, visitor.getVisitorPrivacy().getName());

        resultDialog.setArguments(bundle);
        resultDialog.show(getSupportFragmentManager(), "result_dialog");
    }

    @Override
    public void showResult(VisitorWrapper visitor, int code) {
        ResultDialog resultDialog = new ResultDialog();

        Bundle bundle = new Bundle();
        bundle.putBoolean(ResultDialog.IS_ACTIVE, true);
        bundle.putString(ResultDialog.USER_NAME, visitor.getVisitorPrivacy().getName());
        bundle.putInt(ResultDialog.STATUS_CODE, code);

        resultDialog.setArguments(bundle);
        resultDialog.show(getSupportFragmentManager(), "result_dialog");
    }

    @Override
    public void showScanCardAnimation() {
        if (mNfcAdapter == null) {
            return;
        }

        mBinding.scanLine.setVisibility(View.VISIBLE);

        if (cardAnimator == null) {
            float cardBorderWidth = mBinding.card.getWidth() * 0.05f;

            cardAnimator = ObjectAnimator.ofFloat(mBinding.scanLine, "translationX", cardBorderWidth, mBinding.card.getWidth() - mBinding.scanLine.getWidth() - cardBorderWidth);
            cardAnimator.setDuration(1000);
            cardAnimator.setRepeatCount(ObjectAnimator.INFINITE);
            cardAnimator.setRepeatMode(ValueAnimator.REVERSE);
            cardAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        }

        cardAnimator.start();
    }

    @Override
    public void hideScanCardAnimation() {
        if (mNfcAdapter == null) {
            return;
        }

        cardAnimator.end();
        mBinding.scanLine.setVisibility(View.INVISIBLE);
    }

    private SwipeRefreshLayout.OnRefreshListener mRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            if (!AndroidUtils.isOnline()) {
                longToast(getString(R.string.error_internet));
                mBinding.swipeLayout.setRefreshing(false);
                return;
            }

            if (!isRefreshing && mMeeting != null) {
                isRefreshing = true;
                presenter.loadMeeting(mMeeting.getId());
            }
        }
    };

    @Override
    public void showCardsListDialog(VisitorWrapper visitor, List<Card> cards) {
        CardsListDialog cardsListDialog = new CardsListDialog();
        cardsListDialog.setCallback(new CardsListDialog.CardsListDialogCallback() {
            @Override
            public void call(String cardNumber) {
                presenter.registerVisitor(mMeeting.getId(), visitor.addCardNumber(cardNumber));
            }
        });

        Bundle bundle = new Bundle();
        bundle.putString(CARDS_LIST, new Gson().toJson(cards, new TypeToken<List<Card>>(){}.getType()));

        cardsListDialog.setArguments(bundle);
        cardsListDialog.show(getSupportFragmentManager(), "cards_list_dialog");
    }
}
