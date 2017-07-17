package com.yamert.icepass.ui.base;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;
import com.yamert.icepass.App;
import com.yamert.icepass.di.components.AppComponent;

import io.realm.Realm;

/**
 * Created by Lobster on 17.02.17.
 */

public class BaseMvpActivity<T extends BasePresenter> extends BaseActivity implements BasePresenter.View {

    protected T presenter;
    protected Realm realm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
        inject(App.appComponent());
    }

    @CallSuper
    protected void initialization(T presenter) {
        this.presenter = presenter;
        this.presenter.setRealm(realm);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (presenter != null) {
            presenter.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public void printError(Throwable throwable) {
        FirebaseCrash.report(throwable);
    }

    @Override
    public void showError(String message) {
        if (!message.isEmpty())
            longToast(message);
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    protected void inject(AppComponent component) {

    }
}

