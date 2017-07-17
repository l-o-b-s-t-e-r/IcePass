package com.yamert.icepass.ui.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.util.List;

import static com.yamert.icepass.misc.AndroidUtils.isMainThread;

/**
 * Created by Lobster on 21.02.17.
 */

public class BaseFragment extends Fragment {

    protected BaseActivity activity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BaseActivity) {
            activity = (BaseActivity) context;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<Fragment> fragments = getChildFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment f : fragments) {
                if (f != null) {
                    f.onActivityResult(requestCode, resultCode, data);
                }
            }
        }
    }

    protected void addFragment(@NonNull Fragment f, int containerId, boolean addToBackStack, String backStackName) {
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.add(containerId, f, backStackName);
        if (addToBackStack) {
            ft.addToBackStack(null);
        }
        if (isAdded()) {
            ft.commit();
        }
    }

    protected void addFragment(@NonNull Fragment f, int containerId, boolean addToBackStack) {
        addFragment(f, containerId, addToBackStack, f.getClass().getName());
    }

    protected void replaceFragment(@NonNull Fragment f, int containerId, boolean addToBackStack, String backStackName) {
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.replace(containerId, f);
        if (addToBackStack) {
            ft.addToBackStack(backStackName);
        }
        if (isAdded()) {
            ft.commit();
        }
    }

    protected void replaceFragment(@NonNull Fragment f, int containerId, boolean addToBackStack) {
        replaceFragment(f, containerId, addToBackStack, f.getClass().getName());
    }

    protected void clearBackStack() {
        FragmentManager fm = getChildFragmentManager();
        if (fm.getBackStackEntryCount() > 0 && isAdded() && isResumed()) {
            fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    public boolean onBackPressed() {
        return false;
    }

    // ------------------------------ TOAST ---------------------------------
    private void toast(final String text, final int length) {
        if (isAdded() && activity != null && !activity.isFinishing()) {
            if (!isMainThread()) {
                activity.runOnUiThread(() -> Toast.makeText(activity, text, length).show());
            } else {
                Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected void shortToast(String text) {
        toast(text, Toast.LENGTH_SHORT);
    }

    protected void shortToast(@StringRes int resId) {
        Activity activity = getActivity();
        if (activity != null && isAdded()) {
            String message = getString(resId);
            toast(message, Toast.LENGTH_SHORT);
        }
    }

    protected void longToast(String text) {
        toast(text, Toast.LENGTH_LONG);
    }

    protected void longToast(int resId) {
        Activity activity = getActivity();
        if (activity != null && isAdded()) {
            String message = getString(resId);
            toast(message, Toast.LENGTH_LONG);
        }
    }

    // ------------------------------ SNACK ---------------------------------
    private void snack(View view, final String text, final int length) {
        if (isAdded() && activity != null && !activity.isFinishing()) {
            if (!isMainThread()) {
                activity.runOnUiThread(() -> Snackbar.make(view, text, length).show());
            } else {
                Snackbar.make(view, text, length).show();
            }
        }
    }

    protected void shortSnack(View view, String text) {
        snack(view, text, Toast.LENGTH_SHORT);
    }

    protected void shortSnack(View view, @StringRes int resId) {
        Activity activity = getActivity();
        if (activity != null && isAdded()) {
            String message = getString(resId);
            shortSnack(view, message);
        }
    }

    protected void longSnack(View view, String text) {
        snack(view, text, Toast.LENGTH_LONG);
    }

    protected void longSnack(View view, @StringRes int resId) {
        Activity activity = getActivity();
        if (activity != null && isAdded()) {
            String message = getString(resId);
            longSnack(view, message);
        }
    }

    // Arbitrary value; set it to some reasonable default
    private static final int DEFAULT_CHILD_ANIMATION_DURATION = 600;

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        final Fragment parent = getParentFragment();
        // Apply the workaround only if this is a child fragment, and the parent
        // is being removed.
        if (!enter && parent != null && parent.isRemoving()) {
            // This is a workaround for the bug where child fragments disappear when
            // the parent is removed (as all children are first removed from the parent)
            // See https://code.google.com/p/android/issues/detail?id=55228
            Animation doNothingAnim = new AlphaAnimation(1, 1);
            doNothingAnim.setDuration(getNextAnimationDuration(parent, DEFAULT_CHILD_ANIMATION_DURATION));
            return doNothingAnim;
        } else {
            return super.onCreateAnimation(transit, enter, nextAnim);
        }
    }

    private static long getNextAnimationDuration(Fragment fragment, long defValue) {
        try {
            // Attempt to get the resource ID of the next animation that
            // will be applied to the given fragment.
            Field nextAnimField = Fragment.class.getDeclaredField("mNextAnim");
            nextAnimField.setAccessible(true);
            int nextAnimResource = nextAnimField.getInt(fragment);
            if (nextAnimResource == 0) return defValue;
            Animation nextAnim = AnimationUtils.loadAnimation(fragment.getActivity(), nextAnimResource);
            // ...and if it can be loaded, return that animation's duration
            return (nextAnim == null) ? defValue : nextAnim.getDuration();
        } catch (NoSuchFieldException | IllegalAccessException | Resources.NotFoundException ex) {
            Log.w("BaseFragment", "Unable to load next animation from parent.", ex);
            return defValue;
        }
    }

}
