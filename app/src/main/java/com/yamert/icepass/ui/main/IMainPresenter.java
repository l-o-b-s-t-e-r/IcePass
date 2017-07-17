package com.yamert.icepass.ui.main;

import com.yamert.icepass.models.Meeting;
import com.yamert.icepass.ui.base.BasePresenter;

import java.util.List;

/**
 * Created by Lobster on 04.02.17.
 */

public interface IMainPresenter {

    interface Actions {

        void loadMeetings(@MainActivity.MeetingType int meetingType, int page);

        void unregisterDevice(String tempId);

    }

    interface View extends BasePresenter.View {

        void showMeetings(List<Meeting> meetings, @MainActivity.MeetingType int meetingType);

        void showUnregisterDialog();

        void logout();

    }

}
