package com.yamert.icepass.api;

import com.google.gson.JsonObject;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * Created by Lobster on 21.03.17.
 */

public interface PrivacyService {

    @GET(".")
    Observable<JsonObject> getToken(@QueryMap Map<String, String> params);

    @GET(".")
    Observable<JsonObject> getInfo(@QueryMap Map<String, String> params);

}
