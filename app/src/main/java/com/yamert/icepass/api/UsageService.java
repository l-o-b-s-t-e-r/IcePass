package com.yamert.icepass.api;

import com.google.gson.JsonObject;
import com.yamert.icepass.models.Attendance;
import com.yamert.icepass.models.DeviceWrapper;
import com.yamert.icepass.models.Meeting;
import com.yamert.icepass.models.MeetingDto;
import com.yamert.icepass.models.VisitorDtoUsage;
import com.yamert.icepass.models.MeetingWrapper;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Lobster on 17.02.17.
 */

public interface UsageService {

    @GET("events")
    Observable<List<MeetingDto>> getMeetings(@Query("per_page") Integer perPage, @Query("page") Integer page, @Query("available") Integer available);

    @GET("events/{id}")
    Observable<Meeting> getMeeting(@Path("id") Long id);

    @GET("events/{aevent_id}")
    Observable<MeetingWrapper> getVisitors(@Path("aevent_id") Long meetingId, @Query("details") Long id);

    @GET("cardholders/{card_number}")
    Observable<VisitorDtoUsage> getVisitorByUID(@Path("card_number") String id, @Query("event_id") Long meetingId);

    @PUT("devices/{id}")
    Observable<JsonObject> registerDevice(@Path("id") String id, @Body DeviceWrapper device);

    @PUT("devices/{id}/reset")
    Observable<JsonObject> unregisterDevice(@Path("id") String id);

    @POST("events/{aevent_id}/attendances")
    Observable<Response<VisitorDtoUsage>> registerVisitor(@Path("aevent_id") Long meetingId, @Body Attendance attendance);

}