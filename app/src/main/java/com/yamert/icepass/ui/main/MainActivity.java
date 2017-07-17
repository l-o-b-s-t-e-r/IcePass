package com.yamert.icepass.ui.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.firebase.csm.R;
import com.firebase.csm.databinding.ActivityMainBinding;
import com.yamert.icepass.App;
import com.yamert.icepass.db.DataManager;
import com.yamert.icepass.di.components.AppComponent;
import com.yamert.icepass.misc.AndroidUtils;
import com.yamert.icepass.misc.FragmentCallback;
import com.yamert.icepass.models.Meeting;
import com.yamert.icepass.ui.LoginActivity;
import com.yamert.icepass.ui.base.BaseMvpActivity;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import static com.yamert.icepass.db.DataManager.AUTH_REGISTERED;
import static com.yamert.icepass.db.DataManager.AUTH_TOKEN;
import static com.yamert.icepass.db.DataManager.UNIQUE_ID;

public class MainActivity extends BaseMvpActivity<MainPresenter> implements IMainPresenter.View {

    public static final int ACTUAL = 0;
    public static final int PAST = 1;
    public static final int INIT_PAGE = 1;

    @IntDef({ACTUAL, PAST})
    public @interface MeetingType {

    }

    @Inject
    DataManager dataManager;

    private ActivityMainBinding mBinding;
    private MeetingPagerAdapter mMeetingPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setSupportActionBar(mBinding.toolbar);
        setDisplayShowTitleEnabled(false);

        initialization(new MainPresenter(dataManager, this));
    }

    @Override
    public void initialization(MainPresenter presenter) {
        super.initialization(presenter);

        mMeetingPagerAdapter = new MeetingPagerAdapter(
                getSupportFragmentManager(),
                MeetingsFragment.newInstance(getString(R.string.tab_actual), ACTUAL, new FragmentCallback() {
                    @Override
                    public void loadMoreMeetings(int page) {
                        presenter.loadMeetings(ACTUAL, page);
                    }
                }),
                MeetingsFragment.newInstance(getString(R.string.tab_past), PAST, new FragmentCallback() {
                    @Override
                    public void loadMoreMeetings(int page) {
                        presenter.loadMeetings(PAST, page);
                    }
                })
        );

        mBinding.pager.setAdapter(mMeetingPagerAdapter);
        mBinding.pager.setOffscreenPageLimit(mMeetingPagerAdapter.getCount());
        mBinding.tabs.setupWithViewPager(mBinding.pager);

        mBinding.swipeLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        mBinding.swipeLayout.setOnRefreshListener(mRefreshListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.unregister_device:
                presenter.unregisterDevice(App.sharedPreferences().getString(DataManager.UNIQUE_ID, AndroidUtils.getUniqueDeviceId()));
                return true;
            case R.id.log_out:
                logout();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void inject(AppComponent component) {
        component.inject(this);
    }

    @Override
    public void showProgress() {
        mBinding.swipeLayout.post(() -> mBinding.swipeLayout.setRefreshing(true));
    }

    @Override
    public void hideProgress() {
        mBinding.swipeLayout.setRefreshing(false);
    }

    @Override
    public void showMeetings(List<Meeting> meetings, @MeetingType int meetingType) {
        mMeetingPagerAdapter.getItem(meetingType).showMeetings(meetings);
    }

    @Override
    public void logout() {
        App.sharedPreferences().edit()
                .putBoolean(AUTH_REGISTERED, false)
                .putString(AUTH_TOKEN, "")
                .putString(UNIQUE_ID, "")
                .apply();

        realm.beginTransaction();
        realm.deleteAll();
        realm.commitTransaction();

        Intent startLogIn = new Intent(this, LoginActivity.class);
        startLogIn.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startLogIn, ActivityOptionsCompat.makeCustomAnimation(this, R.anim.left_in, R.anim.scale_out).toBundle());
        finish();
    }

    private SwipeRefreshLayout.OnRefreshListener mRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            if (!AndroidUtils.isOnline()) {
                longToast(getString(R.string.error_internet));
                mBinding.swipeLayout.setRefreshing(false);
                return;
            }

            if (mBinding.swipeLayout.isRefreshing()) {
                switch (mBinding.pager.getCurrentItem()) {
                    case ACTUAL:
                        mMeetingPagerAdapter.getItem(ACTUAL).clearMeetings();
                        presenter.loadMeetings(ACTUAL, INIT_PAGE);
                        break;

                    case PAST:
                        mMeetingPagerAdapter.getItem(PAST).clearMeetings();
                        presenter.loadMeetings(PAST, INIT_PAGE);
                        break;
                }
            }
        }
    };

    @Override
    public void showUnregisterDialog() {
        View alertDialog = getLayoutInflater().inflate(R.layout.dialog_join_by_sid, null);
        EditText textView = (EditText) alertDialog.findViewById(R.id.sid);
        textView.setHint(getString(R.string.hint_device));

        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.enter_temp_id_title))
                .setView(alertDialog)
                .setPositiveButton(getString(R.string.unregister), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        presenter.unregisterDevice(textView.getText().toString());
                    }
                })
                .setNegativeButton(getString(R.string.enter_sid_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    private static class MeetingPagerAdapter extends FragmentPagerAdapter {

        private List<MeetingsFragment> mFragments;

        public MeetingPagerAdapter(FragmentManager fm, MeetingsFragment... fragments) {
            super(fm);
            mFragments = Arrays.asList(fragments);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragments.get(position).title();
        }

        @Override
        public MeetingsFragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }
    }
}
