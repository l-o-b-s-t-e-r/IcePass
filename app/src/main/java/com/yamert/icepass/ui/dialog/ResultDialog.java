package com.yamert.icepass.ui.dialog;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.BounceInterpolator;

import com.firebase.csm.R;
import com.firebase.csm.databinding.DialogResultBinding;
import com.yamert.icepass.misc.DialogCallback;

import timber.log.Timber;

/**
 * Created by Lobster on 04.03.17.
 */

public class ResultDialog extends DialogFragment {

    static public String USER_NAME = "user_name";
    static public String USER_ID = "user_id";
    static public String STATUS_CODE = "status_code";
    static public String IS_ACTIVE = "is_active";

    private DialogResultBinding mBinding;
    private DialogCallback forceRegisterCallback;
    private Handler dismissHandler;
    private Runnable dismissRunnable;

    public void show(FragmentManager fragmentManager, String tag) {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.add(this, tag);
        ft.commitAllowingStateLoss();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_result, container, true);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mBinding.userName.setText(bundle.getString(USER_NAME));
            //mBinding.userId.setText(bundle.getString(USER_ID));

            if (getArguments().getBoolean(IS_ACTIVE)) {
                mBinding.title.setText(
                        getArguments().getInt(STATUS_CODE, 0) == 200 ? getResources().getString(R.string.dialog_already_registered) : getResources().getString(R.string.dialog_success_title)
                );
                mBinding.fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorDialogFabSuccess)));
                mBinding.fab.setImageResource(R.drawable.ic_done_white_24dp);
                dismissHandler = new Handler();
                dismissHandler.postDelayed(dismissRunnable = this::dismiss, 7000);
            } else {
                mBinding.title.setText(getResources().getString(R.string.dialog_fail_title));
                mBinding.fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorDialogFabFail)));
                mBinding.fab.setImageResource(R.drawable.ic_clear_white_24dp);
                mBinding.actionButtons.setVisibility(View.VISIBLE);
                mBinding.forceRegister.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        forceRegisterCallback.call();
                        dismiss();
                    }
                });
                mBinding.notRegister.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                    }
                });
            }
        }

        mBinding.fab.setOnClickListener(v -> dismiss());
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
    }

    @Override
    public void onResume() {
        super.onResume();
        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(
                mBinding.fab,
                PropertyValuesHolder.ofFloat("scaleX", 0.0f, 1.0f),
                PropertyValuesHolder.ofFloat("scaleY", 0.0f, 1.0f)
        );

        animator.setDuration(2000);
        animator.setInterpolator(new BounceInterpolator());
        animator.start();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (dismissHandler != null) {
            dismissHandler.removeCallbacks(dismissRunnable);
        }
    }

    public void setForceRegisterClick(DialogCallback callback) {
        forceRegisterCallback = callback;
    }
}
