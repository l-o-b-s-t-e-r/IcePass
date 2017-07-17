package com.yamert.icepass;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.firebase.csm.R;
import com.yamert.icepass.di.components.AppComponent;
import com.yamert.icepass.di.components.DaggerAppComponent;
import com.yamert.icepass.di.modules.AppModule;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import timber.log.Timber;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by Lobster on 04.02.17.
 */

public class App extends Application {

    private static final String APP_PREFERENCES_NAME = "icepass_pref";

    private static App instance;

    private static AppComponent appComponent;

    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);
        Realm.setDefaultConfiguration(
                new RealmConfiguration.Builder()
                        .deleteRealmIfMigrationNeeded()
                        .build());

        Timber.plant(new Timber.DebugTree());

        instance = this;

        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setFontAttrId(R.attr.fontPath)
                .build());
    }

    public static AppComponent appComponent() {
        return appComponent;
    }

    public static SharedPreferences sharedPreferences() {
        return getInstance().getSharedPreferences(APP_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }
}
