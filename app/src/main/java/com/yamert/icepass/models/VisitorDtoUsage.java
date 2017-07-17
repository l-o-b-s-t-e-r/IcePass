package com.yamert.icepass.models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by Lobster on 25.03.17.
 */

public class VisitorDtoUsage {

    private String token;

    private boolean status;

    @SerializedName("arrived_at")
    private Date arrivalTime;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Date getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(Date arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public boolean isActive() {
        return status;
    }

    @Override
    public String toString() {
        return "VisitorDtoUsage{" +
                "token='" + token + '\'' +
                ", status=" + status +
                ", arrivalTime=" + arrivalTime +
                '}';
    }
}
