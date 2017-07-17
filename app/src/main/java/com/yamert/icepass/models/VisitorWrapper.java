package com.yamert.icepass.models;

/**
 * Created by Lobster on 25.03.17.
 */

public class VisitorWrapper {

    private VisitorDtoUsage visitorUsage;

    private VisitorDtoPrivacy visitorPrivacy;

    private String token;

    private String cardNumber;

    public VisitorWrapper() {

    }

    public VisitorWrapper(VisitorDtoUsage dto) {
        visitorUsage = dto;
        token = dto.getToken();
    }

    public VisitorWrapper(VisitorDtoPrivacy dto) {
        visitorPrivacy = dto;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public VisitorDtoUsage getVisitorUsage() {
        return visitorUsage;
    }

    public void setVisitorUsage(VisitorDtoUsage visitorUsage) {
        this.visitorUsage = visitorUsage;
    }

    public VisitorDtoPrivacy getVisitorPrivacy() {
        return visitorPrivacy;
    }

    public void setVisitorPrivacy(VisitorDtoPrivacy visitorPrivacy) {
        this.visitorPrivacy = visitorPrivacy;
    }

    public boolean isActive() {
        if (visitorPrivacy != null) {
            return visitorPrivacy.isActive();
        } else if (visitorUsage != null) {
            return visitorUsage.isActive();
        }

        return false;
    }

    public static VisitorWrapper createFullVisitorWrapper(VisitorDtoUsage dto) {
        VisitorWrapper wrapper = new VisitorWrapper();
        wrapper.setVisitorUsage(dto);
        wrapper.setToken(dto.getToken());

        VisitorDtoPrivacy visitorDtoPrivacy = new VisitorDtoPrivacy();
        visitorDtoPrivacy.setToken(dto.getToken());
        visitorDtoPrivacy.setName(dto.getToken());
        visitorDtoPrivacy.setKtId(dto.getToken());
        visitorDtoPrivacy.setStatus(dto.isActive() ? 1 : 0);

        wrapper.setVisitorPrivacy(visitorDtoPrivacy);

        return wrapper;
    }

    public VisitorWrapper addCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
        return this;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }
}
