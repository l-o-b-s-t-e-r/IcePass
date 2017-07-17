package com.yamert.icepass.ui.barcode;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.zxing.Result;
import com.yamert.icepass.ui.base.BaseActivity;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by Lobster on 21.02.17.
 */

public class BarCodeScannerActivity extends BaseActivity implements ZXingScannerView.ResultHandler {
    public static final String BAR_CODE_RESULT ="bar_code_result";

    private ZXingScannerView mScannerView;

    public static void startActivityForResult(Activity activity, int requestCode) {
        activity.startActivityForResult(new Intent(activity, BarCodeScannerActivity.class), requestCode);
    }

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setAutoFocus(true);
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        setResult(RESULT_OK, getIntent().putExtra(BAR_CODE_RESULT, rawResult.getText()));
        finish();
    }
}
