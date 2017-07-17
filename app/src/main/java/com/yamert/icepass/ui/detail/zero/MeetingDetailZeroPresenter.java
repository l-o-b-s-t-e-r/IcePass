package com.yamert.icepass.ui.detail.zero;

import com.firebase.csm.R;
import com.google.gson.Gson;
import com.yamert.icepass.App;
import com.yamert.icepass.db.DataManager;
import com.yamert.icepass.models.Attendance;
import com.yamert.icepass.models.Meeting;
import com.yamert.icepass.models.RegistrationError;
import com.yamert.icepass.models.Visitor;
import com.yamert.icepass.models.VisitorDtoUsage;
import com.yamert.icepass.models.VisitorWrapper;
import com.yamert.icepass.models.MeetingWrapper;
import com.yamert.icepass.ui.detail.IMeetingDetailPresenter;
import com.yamert.icepass.ui.detail.MeetingDetailPresenter;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import retrofit2.Response;

/**
 * Created by Lobster on 11.05.17.
 */

public class MeetingDetailZeroPresenter extends MeetingDetailPresenter {

    public MeetingDetailZeroPresenter(DataManager dataManager, IMeetingDetailPresenter.View view) {
        super(dataManager, view);
    }

    @Override
    public void loadMeeting(Long id) {
        Disposable disposable =
                mDataManager.getTokens(id)
                        .doOnSubscribe(disposable1 -> view.showProgress())
                        .doOnDispose(view::hideProgress)
                        .subscribe(new Consumer<MeetingWrapper>() {
                            @Override
                            public void accept(MeetingWrapper wrapper) throws Exception {
                                List<Visitor> visitors = new ArrayList<>();
                                for (VisitorDtoUsage v: wrapper.getPresent()) {
                                    visitors.add(new Visitor(v));
                                }

                                Meeting meeting = Meeting.getMeetingById(id, realm);
                                Meeting.update(meeting, wrapper, realm);
                                Meeting.updateVisitors(meeting, visitors, realm);

                                view.showMeetingDetail(meeting);
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                view.printError(throwable);
                                view.showError(App.getInstance().getString(R.string.error_meeting_loading));
                            }
                        });

        addDisposable(disposable);
    }

    @Override
    public void loadVisitorByUID(String cardNumber, Long meetingId) {
         Disposable disposable =
                mDataManager.getVisitorTokenByUID(cardNumber, meetingId)
                        .doOnSubscribe(disposable1 -> view.showScanCardAnimation())
                        .doOnDispose(view::hideScanCardAnimation)
                        .subscribe(new Consumer<VisitorDtoUsage>() {
                            @Override
                            public void accept(VisitorDtoUsage visitor) throws Exception {
                                VisitorWrapper visitorWrapper = VisitorWrapper.createFullVisitorWrapper(visitor);
                                registerVisitor(meetingId, visitorWrapper);
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                view.printError(throwable);
                                view.showError(App.getInstance().getString(R.string.error_visitor_not_exist));
                            }
                        });

        addDisposable(disposable);
    }

    @Override
    public void registerVisitor(Long meetingId, VisitorWrapper visitor) {
        Disposable disposable =
                mDataManager.registerVisitor(meetingId, new Attendance(meetingId, visitor.getToken(), visitor.isActive(), visitor.getCardNumber()))
                        .subscribe(new Consumer<Response<VisitorDtoUsage>>() {
                            @Override
                            public void accept(Response<VisitorDtoUsage> response) throws Exception {
                                if (response.isSuccessful()) {
                                    view.showResult(visitor, response.code());

                                    Visitor v = new Visitor(response.body());
                                    Meeting meeting = Meeting.getMeetingById(meetingId, realm);

                                    if (!meeting.getVisitors().contains(v)) {
                                        Meeting.updateCount(meeting, meeting.getCount() + 1, realm);
                                        Meeting.addVisitor(meeting, v, realm);
                                        view.showMeetingDetail(meeting);
                                    }
                                } else {
                                    if (response.code() == HttpURLConnection.HTTP_CONFLICT) {
                                        RegistrationError error = new Gson().fromJson(response.errorBody().string(), RegistrationError.class);
                                        view.showCardsListDialog(visitor, error.getCards());
                                    } else {
                                        view.printError(new Exception(response.errorBody().string()));
                                    }
                                }
                            }
                        });

        addDisposable(disposable);
    }

    @Override
    public void loadVisitorBySSID(String id) {

    }
}
