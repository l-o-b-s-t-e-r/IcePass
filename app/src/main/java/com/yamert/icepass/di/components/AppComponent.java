package com.yamert.icepass.di.components;

import com.yamert.icepass.db.DataManager;
import com.yamert.icepass.di.modules.AppModule;
import com.yamert.icepass.di.modules.NetworkModule;
import com.yamert.icepass.ui.LoginActivity;
import com.yamert.icepass.ui.detail.one.MeetingDetailOneActivity;
import com.yamert.icepass.ui.detail.zero.MeetingDetailZeroActivity;
import com.yamert.icepass.ui.main.MainActivity;
import com.yamert.icepass.ui.splash.SplashActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Lobster on 04.02.17.
 */

@Singleton
@Component(modules = {AppModule.class, NetworkModule.class})
public interface AppComponent {

    void inject(SplashActivity activity);

    void inject(LoginActivity activity);

    void inject(MainActivity activity);

    void inject(MeetingDetailZeroActivity activity);

    void inject(MeetingDetailOneActivity activity);

    void inject(DataManager manager);
}
