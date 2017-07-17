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
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.BounceInterpolator;

import com.firebase.csm.R;
import com.firebase.csm.databinding.DialogCardsListBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yamert.icepass.adapters.CardsAdapter;
import com.yamert.icepass.misc.AndroidUtils;
import com.yamert.icepass.misc.DialogCallback;
import com.yamert.icepass.models.Card;

import org.antlr.v4.misc.Utils;

import java.util.List;

import timber.log.Timber;

/**
 * Created by Lobster on 18.06.17.
 */

public class CardsListDialog extends DialogFragment {

    static public String CARDS_LIST = "cards_list";

    private DialogCardsListBinding mBinding;
    private CardsAdapter mCardsAdapter = new CardsAdapter();

    public interface CardsListDialogCallback {
        void call(String cardNumber);
    }

    private CardsListDialogCallback callback;

    public void show(FragmentManager fragmentManager, String tag) {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.add(this, tag);
        ft.commitAllowingStateLoss();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_cards_list, container, true);

        mBinding.cardsList.setAdapter(mCardsAdapter = new CardsAdapter());
        mBinding.cardsList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mCardsAdapter.updateVisitors(new Gson().fromJson(bundle.getString(CARDS_LIST), new TypeToken<List<Card>>(){}.getType()));

            mBinding.forceRegister.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.call(mCardsAdapter.getChosenCardNumber().getCardNumber());
                    dismiss();
                }
            });
        }

        mBinding.notRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
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

    public void setCallback(CardsListDialogCallback callback) {
        this.callback = callback;
    }

}
