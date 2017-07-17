package com.yamert.icepass.ui.main;

import com.firebase.csm.R;
import com.google.gson.JsonObject;
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;
import com.yamert.icepass.App;
import com.yamert.icepass.db.DataManager;
import com.yamert.icepass.models.Meeting;
import com.yamert.icepass.models.MeetingDto;
import com.yamert.icepass.ui.base.BasePresenter;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

import static com.yamert.icepass.ui.main.MainActivity.ACTUAL;
import static com.yamert.icepass.ui.main.MainActivity.PAST;


/**
 * Created by Lobster on 04.02.17.
 */

public class MainPresenter extends BasePresenter<IMainPresenter.View> implements IMainPresenter.Actions {

    private DataManager mDataManager;

    public MainPresenter(DataManager dataManager, IMainPresenter.View view) {
        mDataManager = dataManager;
        bindView(view);
    }

    @Override
    public void loadMeetings(@MainActivity.MeetingType int meetingType, int page) {
        switch (meetingType) {
            case ACTUAL:
                loadActualMeetings(page);
                break;
            case PAST:
                loadPastMeetings(page);
                break;
        }
        ;
    }

    private void loadActualMeetings(int page) {
        Disposable disposable =
                mDataManager.getActualMeetings(page)
                        .doOnSubscribe(disposable1 -> view.showProgress())
                        .doOnDispose(view::hideProgress)
                        .map(new Function<List<MeetingDto>, List<Meeting>>() {
                            @Override
                            public List<Meeting> apply(List<MeetingDto> meetingDtos) throws Exception {
                                List<Meeting> meetings = new ArrayList<>();
                                for (MeetingDto dto : meetingDtos) {
                                    Meeting meeting = Meeting.getMeetingById(dto.getId(), realm);

                                    if (meeting == null) {
                                        meeting = new Meeting(dto);
                                        Meeting.saveOrUpdate(meeting, realm);
                                    } else {
                                        Meeting.update(meeting, dto, realm);
                                    }

                                    meetings.add(meeting);
                                }
                                return meetings;
                            }
                        })
                        .subscribe(new Consumer<List<Meeting>>() {
                            @Override
                            public void accept(List<Meeting> meetings) throws Exception {
                                view.showMeetings(meetings, MainActivity.ACTUAL);
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                if (throwable instanceof HttpException) {
                                    int code = ((HttpException) throwable).code();
                                    if (code == HttpURLConnection.HTTP_UNAUTHORIZED || code == 422) {
                                        view.showError(App.getInstance().getString(R.string.error_device_not_registered));
                                        view.logout();
                                        return;
                                    }
                                }

                                view.showError(App.getInstance().getString(R.string.error_meetings_loading));
                                List<Meeting> meetings = realm.where(Meeting.class).findAll();
                                if (meetings != null) {
                                    view.showMeetings(meetings, MainActivity.ACTUAL);
                                }
                            }
                        });

        addDisposable(disposable);
    }

    private void loadPastMeetings(int page) {
        Disposable disposable =
                mDataManager.getPastMeetings(page)
                        .doOnSubscribe(disposable1 -> view.showProgress())
                        .doOnDispose(view::hideProgress)
                        .map(new Function<List<MeetingDto>, List<Meeting>>() {
                            @Override
                            public List<Meeting> apply(List<MeetingDto> meetingDtos) throws Exception {
                                List<Meeting> meetings = new ArrayList<>();
                                for (MeetingDto dto : meetingDtos) {
                                    Meeting meeting = Meeting.getMeetingById(dto.getId(), realm);

                                    if (meeting == null) {
                                        meeting = new Meeting(dto);
                                        Meeting.saveOrUpdate(meeting, realm);
                                    } else {
                                        Meeting.update(meeting, dto, realm);
                                    }

                                    meetings.add(meeting);
                                }
                                return meetings;
                            }
                        })
                        .subscribe(new Consumer<List<Meeting>>() {
                            @Override
                            public void accept(List<Meeting> meetings) throws Exception {
                                view.showMeetings(meetings, MainActivity.PAST);
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                if (throwable instanceof HttpException) {
                                    int code = ((HttpException) throwable).code();
                                    if (code == HttpURLConnection.HTTP_UNAUTHORIZED || code == 422) {
                                        view.showError(App.getInstance().getString(R.string.error_device_not_registered));
                                        view.logout();
                                        return;
                                    }
                                }

                                view.showError(App.getInstance().getString(R.string.error_meetings_loading));
                                List<Meeting> meetings = realm.where(Meeting.class).findAll();
                                if (meetings != null) {
                                    view.showMeetings(meetings, MainActivity.PAST);
                                }
                            }
                        });

        addDisposable(disposable);
    }

    @Override
    public void unregisterDevice(String tempId) {
        Disposable disposable =
                mDataManager.unregisterDevice(tempId)
                        .doOnSubscribe(disposable1 -> view.showProgress())
                        .doOnDispose(view::hideProgress)
                        .subscribe(new Consumer<JsonObject>() {
                            @Override
                            public void accept(JsonObject jsonObject) throws Exception {
                                view.logout();
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                if (throwable instanceof HttpException) {
                                    int code = ((HttpException) throwable).code();
                                    if (code == HttpURLConnection.HTTP_UNAUTHORIZED) {
                                        view.showError(App.getInstance().getString(R.string.error_device_expired));
                                    } else if (code == HttpURLConnection.HTTP_NOT_FOUND) {
                                        view.showError(App.getInstance().getString(R.string.error_device_not_found));
                                    }
                                }

                                view.printError(throwable);
                            }
                        });

        addDisposable(disposable);
    }
}
