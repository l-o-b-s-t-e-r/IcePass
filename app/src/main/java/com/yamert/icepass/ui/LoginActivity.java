package com.yamert.icepass.ui;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.firebase.csm.R;
import com.firebase.csm.databinding.ActivityLoginBinding;
import com.google.gson.JsonObject;
import com.yamert.icepass.App;
import com.yamert.icepass.db.DataManager;
import com.yamert.icepass.misc.AndroidUtils;
import com.yamert.icepass.models.Auth;
import com.yamert.icepass.models.Device;
import com.yamert.icepass.ui.base.BaseActivity;
import com.yamert.icepass.ui.main.MainActivity;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

import static com.yamert.icepass.db.DataManager.AUTH_REGISTERED;
import static com.yamert.icepass.db.DataManager.UNIQUE_ID;

public class LoginActivity extends BaseActivity {

    @Inject
    DataManager dataManager;

    private ActivityLoginBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.appComponent().inject(this);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        mBinding.progressBarIndeterminate.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_IN);
        mBinding.logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBinding.progressBarLayoutStatic.setVisibility(View.INVISIBLE);
                mBinding.progressBarLayoutIndeterminate.setVisibility(View.VISIBLE);

                String username = mBinding.username.getText().toString().trim();
                String password = mBinding.password.getText().toString().trim();
                String tempId = mBinding.device.getText().toString().trim();

                Device device = new Device(AndroidUtils.getUniqueDeviceId());

                dataManager.authentication(username, password, tempId)
                        .doOnError(new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                longToast(R.string.error_log_in);
                            }
                        })
                        .flatMap(new Function<Auth, Observable<JsonObject>>() {
                            @Override
                            public Observable<JsonObject> apply(Auth auth) throws Exception {
                                return dataManager.registerDevice(tempId, device)
                                        .doOnError(new Consumer<Throwable>() {
                                            @Override
                                            public void accept(Throwable throwable) throws Exception {
                                                longToast(R.string.error_register);
                                            }
                                        });
                            }
                        })
                        .subscribe(new Consumer<JsonObject>() {
                            @Override
                            public void accept(JsonObject jsonObject) throws Exception {
                                App.sharedPreferences().edit()
                                        .putBoolean(AUTH_REGISTERED, true)
                                        .putString(UNIQUE_ID, device.device_id)
                                        .apply();

                                startActivity(new Intent(LoginActivity.this, MainActivity.class),
                                        ActivityOptionsCompat.makeCustomAnimation(LoginActivity.this, R.anim.right_in, R.anim.scale_out).toBundle());

                                finish();
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                mBinding.progressBarLayoutStatic.setVisibility(View.VISIBLE);
                                mBinding.progressBarLayoutIndeterminate.setVisibility(View.INVISIBLE);
                            }
                        });
            }
        });
    }
}

