package com.yamert.icepass.ui.main;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.csm.R;
import com.firebase.csm.databinding.FragmentMeetingsBinding;
import com.yamert.icepass.adapters.MeetingAdapter;
import com.yamert.icepass.misc.AndroidUtils;
import com.yamert.icepass.misc.EndlessRecyclerViewScrollListener;
import com.yamert.icepass.misc.EventUpdateMeeting;
import com.yamert.icepass.misc.FragmentCallback;
import com.yamert.icepass.models.Meeting;
import com.yamert.icepass.ui.base.BaseFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import timber.log.Timber;

import static com.yamert.icepass.ui.main.MainActivity.INIT_PAGE;

/**
 * Created by Lobster on 21.02.17.
 */

public class MeetingsFragment extends BaseFragment {

    private static final String FRAGMENT_TITLE = "fragment_title";
    private static final String FRAGMENT_TYPE = "fragment_type";

    private FragmentMeetingsBinding mBinding;
    private MeetingAdapter mMeetingAdapter;
    private EndlessRecyclerViewScrollListener scrollListener;
    private int type;
    private FragmentCallback callback;

    public static MeetingsFragment newInstance(String tabTitle, @MainActivity.MeetingType int type, FragmentCallback callback) {
        Bundle args = new Bundle();
        args.putString(FRAGMENT_TITLE, tabTitle);
        args.putInt(FRAGMENT_TYPE, type);
        MeetingsFragment f = new MeetingsFragment();
        f.setCallback(callback);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_meetings, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBinding = DataBindingUtil.bind(view);

        type = getArguments().getInt(FRAGMENT_TYPE);
        mMeetingAdapter = new MeetingAdapter(type, getActivity());
        mBinding.meetings.setAdapter(mMeetingAdapter);
        mBinding.meetings.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mBinding.meetings.addOnScrollListener(scrollListener = new EndlessRecyclerViewScrollListener(mBinding.meetings.getLayoutManager()) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                callback.loadMoreMeetings(page);
            }
        });

        if (AndroidUtils.isOnline() && callback != null) {
            callback.loadMoreMeetings(INIT_PAGE);
        } else {
            longToast(getString(R.string.error_internet));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventUpdateMeeting event) {
        if (event.getType()== type) {
            mMeetingAdapter.updateMeeting(event.getMeeting());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void clearMeetings() {
        mMeetingAdapter.clearMeetings();
        scrollListener.resetState();
    }

    public void showMeetings(List<Meeting> meetings) {
        mMeetingAdapter.addAllMeetings(meetings);
    }

    public void setCallback(FragmentCallback callback) {
        this.callback = callback;
    }

    public String title() {
        return getArguments().getString(FRAGMENT_TITLE);
    }
}
