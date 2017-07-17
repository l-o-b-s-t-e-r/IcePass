package com.yamert.icepass.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Lobster on 18.06.17.
 */

public class Card {

    @SerializedName("card_number")
    private String cardNumber;

    @SerializedName("seller_id")
    private Long sellerId;

    @SerializedName("seller_name")
    private String sellerName;

    @SerializedName("seller_external_id")
    private String sellerExternalId;

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getSellerExternalId() {
        return sellerExternalId;
    }

    public void setSellerExternalId(String sellerExternalId) {
        this.sellerExternalId = sellerExternalId;
    }
}
