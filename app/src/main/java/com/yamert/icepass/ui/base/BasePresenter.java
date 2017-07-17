package com.yamert.icepass.ui.base;

/**
 * Created by Lobster on 17.02.17.
 */

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.realm.Realm;

public class BasePresenter<V> {

    protected Realm realm;
    protected V view;
    private CompositeDisposable mCompositeDisposable;

    public BasePresenter() {
        mCompositeDisposable = new CompositeDisposable();
    }

    @CallSuper
    public void bindView(@NonNull V view) {
        final V previousView = this.view;
        if (previousView != null) {
            throw new IllegalStateException("Previous view is not unbounded! previousView = " + previousView);
        }
        this.view = view;
    }

    public void addDisposable(Disposable disposable) {
        if (mCompositeDisposable != null) {
            mCompositeDisposable.add(disposable);
        }
    }

    public Realm getRealm() {
        return realm;
    }

    public void setRealm(Realm realm) {
        this.realm = realm;
    }

    public void stop() {
        mCompositeDisposable.clear();
    }

    public interface View {

        void printError(Throwable throwable);

        void showError(String message);

        void showProgress();

        void hideProgress();

    }

}