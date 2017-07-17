package com.yamert.icepass.db;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.yamert.icepass.App;
import com.yamert.icepass.api.AuthService;
import com.yamert.icepass.api.PrivacyService;
import com.yamert.icepass.api.UsageService;
import com.yamert.icepass.misc.AndroidUtils;
import com.yamert.icepass.models.Attendance;
import com.yamert.icepass.models.Auth;
import com.yamert.icepass.models.Device;
import com.yamert.icepass.models.DeviceWrapper;
import com.yamert.icepass.models.Meeting;
import com.yamert.icepass.models.MeetingDto;
import com.yamert.icepass.models.VisitorDtoPrivacy;
import com.yamert.icepass.models.VisitorDtoUsage;
import com.yamert.icepass.models.VisitorWrapper;
import com.yamert.icepass.models.MeetingWrapper;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

/**
 * Created by Lobster on 21.03.17.
 */

public class DataManager {

    public static final String AUTH_TOKEN = "auth_token";
    public static final String AUTH_REGISTERED = "vr_fundir_registered";
    public static final String LOOKUP_TYPE = "lookup_type";
    public static final String UNIQUE_ID = "unique_id";

    private final int ACTUAL_EVENTS = 1;
    private final int PAST_EVENTS = 0;
    private final int PER_PAGE = 15;

    @Inject
    AuthService authService;

    @Inject
    UsageService usageService;

    @Inject
    PrivacyService privacyService;

    public DataManager() {
        App.appComponent().inject(this);
    }

    public Observable<Auth> authentication(String username, String password, String device) {
        return authService.authenticate(username, password, device)
                .doOnNext(new Consumer<Auth>() {
                    @Override
                    public void accept(Auth auth) throws Exception {
                        App.sharedPreferences().edit()
                                .putString(AUTH_TOKEN, auth.getToken())
                                .putInt(LOOKUP_TYPE, auth.getLookupType())
                                .apply();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<JsonObject> registerDevice(String tempId, Device device) {
        return usageService.registerDevice(tempId, new DeviceWrapper(device))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<JsonObject> unregisterDevice(String tempId) {
        return usageService.unregisterDevice(tempId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<MeetingDto>> getActualMeetings(int page) {
        return usageService.getMeetings(PER_PAGE, page, ACTUAL_EVENTS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<MeetingDto>> getPastMeetings(int page) {
        return usageService.getMeetings(PER_PAGE, page, PAST_EVENTS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Meeting> getMeeting(Long meetingId) {
        return usageService.getMeeting(meetingId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<VisitorWrapper> getVisitorByUID(String cardNumber, Long meetingId) {
        return usageService.getVisitorByUID(cardNumber, meetingId)
                .flatMap(new Function<VisitorDtoUsage, Observable<VisitorWrapper>>() {
                    @Override
                    public Observable<VisitorWrapper> apply(VisitorDtoUsage dto) throws Exception {
                        return getVisitorByToken(new VisitorWrapper(dto));
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    public Observable<VisitorDtoUsage> getVisitorTokenByUID(String id, Long meetingId) {
        return usageService.getVisitorByUID(id, meetingId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    public Observable<MeetingWrapper> getVisitors(Long meetingId) {
        return usageService.getVisitors(meetingId, 1L)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<MeetingWrapper> getTokens(Long meetingId) {
        return usageService.getVisitors(meetingId, 1L)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Response<VisitorDtoUsage>> registerVisitor(Long meetingId, Attendance attendance) {
        return usageService.registerVisitor(meetingId, attendance)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<VisitorWrapper> getVisitorById(String id) {
        Map<String, String> q = new HashMap<>();

        return privacyService.getToken(q)
                .map(new Function<JsonObject, VisitorWrapper>() {
                    @Override
                    public VisitorWrapper apply(JsonObject jsonObject) throws Exception {
                        List<VisitorDtoPrivacy> visitors =
                                new Gson().fromJson(jsonObject.getAsJsonArray("result"), new TypeToken<List<VisitorDtoPrivacy>>() {}.getType());

                        VisitorDtoPrivacy dto = visitors.get(0);
                        VisitorWrapper wrapper = new VisitorWrapper(dto);
                        wrapper.setToken(dto.getToken());

                        return wrapper;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<VisitorWrapper> getVisitorByToken(VisitorWrapper visitorWrapper) {
        Map<String, String> q = new HashMap<>();

        return privacyService.getInfo(q)
                .map(new Function<JsonObject, VisitorWrapper>() {
                    @Override
                    public VisitorWrapper apply(JsonObject jsonObject) throws Exception {
                        List<VisitorDtoPrivacy> visitors =
                                new Gson().fromJson(jsonObject.getAsJsonArray("result"), new TypeToken<List<VisitorDtoPrivacy>>() {}.getType());

                        visitorWrapper.setVisitorPrivacy(visitors.get(0));
                        return visitorWrapper;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    public Observable<List<VisitorDtoPrivacy>> getVisitorsByToken(List<String> tokens) {
        String token = "";
        if (!tokens.isEmpty()) {
            token = tokens.get(0);
            for (int i = 1; i < tokens.size(); i++) {
                token += "," + tokens.get(i);
            }
        }

        Map<String, String> q = new HashMap<>();

        return privacyService.getInfo(q)
                .map(new Function<JsonObject, List<VisitorDtoPrivacy>>() {
                    @Override
                    public List<VisitorDtoPrivacy> apply(JsonObject jsonObject) throws Exception {
                        return new Gson().fromJson(jsonObject.getAsJsonArray("result"), new TypeToken<List<VisitorDtoPrivacy>>() {}.getType());
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    private Map<String, String> getDefaults() {
        Map<String, String> q = new HashMap<>();
        return q;
    }

    private String getSign(Map<String, String> q) {
        return null;
    }

    private String md5(String message) {
        return null;
    }
}
