package com.yamert.icepass.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;

import com.firebase.csm.R;
import com.yamert.icepass.App;
import com.yamert.icepass.db.DataManager;
import com.yamert.icepass.ui.LoginActivity;
import com.yamert.icepass.ui.base.BaseActivity;
import com.yamert.icepass.ui.main.MainActivity;

import javax.inject.Inject;

import static com.yamert.icepass.db.DataManager.AUTH_REGISTERED;

public class SplashActivity extends BaseActivity {

    @Inject
    DataManager dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.appComponent().inject(this);

        if (App.sharedPreferences().getBoolean(AUTH_REGISTERED, false)) {
            startActivity(new Intent(this, MainActivity.class));
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }

        finish();
    }
}
