package com.yamert.icepass.ui.detail;

import com.google.gson.JsonObject;
import com.yamert.icepass.db.DataManager;
import com.yamert.icepass.models.Attendance;
import com.yamert.icepass.models.Meeting;
import com.yamert.icepass.models.Visitor;
import com.yamert.icepass.models.VisitorDtoUsage;
import com.yamert.icepass.models.VisitorWrapper;
import com.yamert.icepass.ui.base.BasePresenter;
import com.yamert.icepass.ui.detail.IMeetingDetailPresenter;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import timber.log.Timber;

/**
 * Created by Lobster on 17.02.17.
 */

public abstract class MeetingDetailPresenter extends BasePresenter<IMeetingDetailPresenter.View> implements IMeetingDetailPresenter.Actions {

    protected DataManager mDataManager;
    private Long meetingId;

    public MeetingDetailPresenter(DataManager dataManager, IMeetingDetailPresenter.View view) {
        mDataManager = dataManager;
        bindView(view);
    }

    @Override
    public void getMeeting(Long id) {
        view.showMeetingDetail(Meeting.getMeetingById(id, realm));
    }

    public Long getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(Long meetingId) {
        this.meetingId = meetingId;
    }
}
