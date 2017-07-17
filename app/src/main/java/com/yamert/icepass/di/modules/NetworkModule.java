package com.yamert.icepass.di.modules;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.yamert.icepass.api.AuthService;
import com.yamert.icepass.api.PrivacyService;
import com.yamert.icepass.api.UsageService;
import com.yamert.icepass.misc.AuthInterceptor;

import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

/**
 * Created by Lobster on 17.02.17.
 */

@Module
public class NetworkModule {

    //public static final String USAGE_ENDPOINT = "";
    private static final String USAGE_ENDPOINT = "";
    private static final String PRIVACY_ENDPOINT = "";

    @Provides
    @Singleton
    public OkHttpClient provideHttpBuilder() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        return new OkHttpClient.Builder()
                .addInterceptor(logging)
                .addNetworkInterceptor(new AuthInterceptor())
                .connectTimeout(180, TimeUnit.SECONDS)
                .writeTimeout(180, TimeUnit.SECONDS)
                .readTimeout(180, TimeUnit.SECONDS)
                .build();
    }

    @Provides
    @Singleton
    @Named("AuthApiRestAdapter")
    public Retrofit provideAuthRetrofit(OkHttpClient client) {
        return new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .baseUrl(USAGE_ENDPOINT)
                .build();
    }

    @Provides
    @Singleton
    @Named("UsageApiRestAdapter")
    public Retrofit provideUsageRetrofit(OkHttpClient client) {
        return new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .baseUrl(USAGE_ENDPOINT)
                .build();
    }


    @Provides
    @Singleton
    @Named("PrivacyApiRestAdapter")
    public Retrofit providePrivacyRetrofit(OkHttpClient client) {
        return new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .baseUrl(PRIVACY_ENDPOINT)
                .build();
    }

    @Provides
    @Singleton
    UsageService provideUsageService(@Named("UsageApiRestAdapter") Retrofit retrofit) {
        return retrofit.create(UsageService.class);
    }

    @Provides
    @Singleton
    PrivacyService providePrivacyService(@Named("PrivacyApiRestAdapter") Retrofit retrofit) {
        return retrofit.create(PrivacyService.class);
    }

    @Provides
    @Singleton
    AuthService provideAuthService(@Named("AuthApiRestAdapter") Retrofit retrofit) {
        return retrofit.create(AuthService.class);
    }

}
