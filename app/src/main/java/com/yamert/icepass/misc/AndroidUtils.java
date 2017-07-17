package com.yamert.icepass.misc;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.SearchView;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.firebase.csm.R;
import com.yamert.icepass.App;
import com.yamert.icepass.models.RegistrationError;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;
import timber.log.Timber;

public class AndroidUtils {

    public static int dpToPx(int dp) {
        return (int) (dp * App.getInstance().getResources().getDisplayMetrics().density);
    }

    public static boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) App.getInstance().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static String byteArrayToHexString(byte[] uid) {
        int i, j, in;
        String[] hex = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};
        String out = "";

        for (j = 0; j < uid.length; ++j) {
            in = (int) uid[j] & 0xff;
            i = (in >> 4) & 0x0f;
            out += hex[i];
            i = in & 0x0f;
            out += hex[i];
        }
        return out;
    }

    public static String reverseCardId(String cardId) {
        if (cardId.length() % 2 != 0) {
            return cardId;
        }

        String reverseCardId = "";
        for (int i = cardId.length(); i > 0; i-=2) {
            reverseCardId+=cardId.substring(i - 2, i);
        }

        return reverseCardId;
    }

    public static String parseQrCode(String url) {
        String parts[] = url.split("/");

        for (int i = parts.length - 1; i >= 0; i--) {
            if (parts[i].length() == 8) {
                return parts[i];
            }
        }

        return "";
    }

    public static String getUniqueDeviceId() {
        String deviceId = Settings.Secure.getString(App.getInstance().getContentResolver(), Settings.Secure.ANDROID_ID);

        if (deviceId == null || deviceId.isEmpty()) {
            deviceId = getShortUniquePseudoID();
        }

        return deviceId;
    }

    public static String getShortUniquePseudoID() {
        return String.valueOf(System.currentTimeMillis()) + "-" + Long.toHexString(Double.doubleToLongBits(Math.random()));
    }

    /*public static String getUniquePseudoID() {
        String m_szDevIDShort = "35" + (Build.BOARD.length() % 10) + (Build.BRAND.length() % 10) + (Build.DEVICE.length() % 10) + (Build.MANUFACTURER.length() % 10) + (Build.MODEL.length() % 10) + (Build.PRODUCT.length() % 10);

        String serial = null;
        try {
            serial = android.os.Build.class.getField("SERIAL").get(null).toString();
            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
        } catch (Exception exception) {
            serial = "l-o-b-s-t-e-r";
        }

        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
    }*/

    public static boolean isMainThread() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }
}
