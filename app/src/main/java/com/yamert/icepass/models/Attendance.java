package com.yamert.icepass.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Lobster on 27.03.17.
 */

public class Attendance {

    private Long id;

    private String token;

    private boolean status;

    @SerializedName("card_number")
    private String cardNumber;

    public Attendance() {
        
    }

    public Attendance(Long id, String token, boolean status) {
        this.id = id;
        this.token = token;
        this.status = status;
    }

    public Attendance(Long id, String token, boolean status, String cardNumber) {
        this.id = id;
        this.token = token;
        this.status = status;
        this.cardNumber = cardNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }
}
