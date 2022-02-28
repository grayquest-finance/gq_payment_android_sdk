package com.grayquest.android.payment.sdk;

import com.google.gson.annotations.SerializedName;

public class PaymentSDk_Contributor {
    @SerializedName("success")
    boolean success;
    @SerializedName("status_code")
    int status_code;
    @SerializedName("message")
    String message;
    @SerializedName("data")
    ResponseData data;

    public boolean isSuccess() {
        return success;
    }

    public int getStatus_code() {
        return status_code;
    }

    public String getMessage() {
        return message;
    }

    public ResponseData getData() {
        return data;
    }
}
