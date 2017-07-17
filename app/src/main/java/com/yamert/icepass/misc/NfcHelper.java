package com.yamert.icepass.misc;

import android.app.PendingIntent;
import android.content.IntentFilter;

/**
 * Created by Lobster on 20.02.17.
 */

public class NfcHelper {

    private PendingIntent mNfcPendingIntent;
    private IntentFilter mIntentFilters[];
    private String mTechListsArray[][];

    private NfcHelper() {

    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public PendingIntent getNfcPendingIntent() {
        return mNfcPendingIntent;
    }

    private void setNfcPendingIntent(PendingIntent nfcPendingIntent) {
        mNfcPendingIntent = nfcPendingIntent;
    }

    public IntentFilter[] getIntentFilters() {
        return mIntentFilters;
    }

    private void setIntentFilters(IntentFilter intentFilters[]) {
        mIntentFilters = intentFilters;
    }

    public String[][] getTechListsArray() {
        return mTechListsArray;
    }

    private void setTechListsArray(String[][] techListsArray) {
        mTechListsArray = techListsArray;
    }

    public static class Builder {

        private NfcHelper mNfc;

        public Builder() {
            mNfc = new NfcHelper();
        }

        public Builder setPendingIntent(PendingIntent pendingIntent) {
            mNfc.setNfcPendingIntent(pendingIntent);
            return this;
        }

        public Builder setIntentFilters(IntentFilter... intentFilters) {
            mNfc.setIntentFilters(intentFilters);
            return this;
        }

        public Builder setTechListsArray(String techListsArray[][]) {
            mNfc.setTechListsArray(techListsArray);
            return this;
        }

        public NfcHelper build() {
            return mNfc;
        }
    }
}
