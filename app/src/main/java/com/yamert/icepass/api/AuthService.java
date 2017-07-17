package com.yamert.icepass.api;

import com.yamert.icepass.models.Auth;

import io.reactivex.Observable;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Lobster on 23.03.17.
 */

public interface AuthService {

    @POST("authenticate")
    Observable<Auth> authenticate(@Query("username") String username, @Query("password") String password, @Query("device_id") String deviceId);

}
