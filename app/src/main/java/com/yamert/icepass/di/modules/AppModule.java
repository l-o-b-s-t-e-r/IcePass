package com.yamert.icepass.di.modules;

import com.yamert.icepass.App;
import com.yamert.icepass.db.DataManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Lobster on 04.02.17.
 */

@Module
public class AppModule {

    private final App mApplication;

    public AppModule(App application) {
        mApplication = application;
    }

    @Provides
    @Singleton
    App provideApplication() {
        return mApplication;
    }

    @Provides
    @Singleton
    DataManager provideDataManager() {
        return new DataManager();
    }
}

