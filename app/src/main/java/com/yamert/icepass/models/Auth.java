package com.yamert.icepass.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Lobster on 24.03.17.
 */

public class Auth {

    @SerializedName("auth_token")
    private String token;

    @SerializedName("lookup_type")
    private Integer lookupType;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getLookupType() {
        return lookupType;
    }

    public void setLookupType(Integer lookupType) {
        this.lookupType = lookupType;
    }

    @Override
    public String toString() {
        return "Auth{" +
                "token='" + token + '\'' +
                ", lookupType=" + lookupType +
                '}';
    }
}
