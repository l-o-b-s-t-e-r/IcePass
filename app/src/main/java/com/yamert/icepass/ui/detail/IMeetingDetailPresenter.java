package com.yamert.icepass.ui.detail;

import com.yamert.icepass.models.Card;
import com.yamert.icepass.models.Meeting;
import com.yamert.icepass.models.Visitor;
import com.yamert.icepass.models.VisitorWrapper;
import com.yamert.icepass.ui.base.BasePresenter;

import java.util.List;

/**
 * Created by Lobster on 17.02.17.
 */

public interface IMeetingDetailPresenter {

    interface Actions {

        void getMeeting(Long id);

        void loadMeeting(Long id);

        void loadVisitorBySSID(String id);

        void loadVisitorByUID(String cardNumber, Long meetingId);

        void registerVisitor(Long meetingId, VisitorWrapper visitor);

    }

    interface View extends BasePresenter.View {

        void showMeetingDetail(Meeting meeting);

        void showVisitors(List<Visitor> visitors);

        void showResult(VisitorWrapper visitor);

        void showResult(VisitorWrapper visitor, int code);

        void showScanCardAnimation();

        void hideScanCardAnimation();

        void showCardsListDialog(VisitorWrapper visitor, List<Card> cards);

    }

}
