package com.yamert.icepass.ui.base;

import android.content.Context;
import android.support.annotation.StringRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.csm.R;
import com.yamert.icepass.misc.AndroidUtils;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Lobster on 20.02.17.
 */

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                overridePendingTransition(R.anim.left_in, R.anim.scale_out);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void setupBackButton(Toolbar toolbar) {
        ActionBar ab = getSupportActionBar();
        if (toolbar == null || ab == null) {
            return;
        }
        ab.setDisplayShowHomeEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    protected void setDisplayShowTitleEnabled(boolean titleEnabled) {
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayShowTitleEnabled(titleEnabled);
        }
    }

    private void toast(final String text, final int length) {
        if (!isFinishing()) {
            if (!AndroidUtils.isMainThread()) {
                runOnUiThread(() -> Toast.makeText(BaseActivity.this, text, length).show());
            } else {
                Toast.makeText(this, text, length).show();
            }
        }
    }

    public void shortToast(String text) {
        toast(text, Toast.LENGTH_SHORT);
    }

    public void shortToast(@StringRes int resId) {
        toast(getString(resId), Toast.LENGTH_SHORT);
    }

    public void longToast(String text) {
        toast(text, Toast.LENGTH_LONG);
    }

    public void longToast(@StringRes int resId) {
        toast(getString(resId), Toast.LENGTH_LONG);
    }
}