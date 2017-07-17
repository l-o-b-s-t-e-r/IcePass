package com.yamert.icepass.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Lobster on 22.03.17.
 */

public class VisitorDtoPrivacy {

    private String name;

    @SerializedName("kt_id")
    private String ktId;

    @SerializedName("is_active")
    private int status;

    private String token;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKtId() {
        return ktId;
    }

    public void setKtId(String ktId) {
        this.ktId = ktId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isActive() {
        return (status == 1);
    }
}
