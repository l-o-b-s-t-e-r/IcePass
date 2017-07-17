package com.yamert.icepass.misc;

import com.yamert.icepass.App;
import com.yamert.icepass.db.DataManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Lobster on 23.03.17.
 */

public class AuthInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();

        Request request = original.newBuilder()
                .header("Authorization", getAuthToken())
                .header("Accept", "application/json")
                .method(original.method(), original.body())
                .build();

        return chain.proceed(request);
    }

    private String getAuthToken() {
        return App.sharedPreferences().getString(DataManager.AUTH_TOKEN, "");
    }
}