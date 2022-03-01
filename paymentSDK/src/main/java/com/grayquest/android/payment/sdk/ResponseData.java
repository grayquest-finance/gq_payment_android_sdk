package com.grayquest.android.payment.sdk;

import com.google.gson.annotations.SerializedName;

public class ResponseData {
    @SerializedName("customer_id")
    int customer_id;
    @SerializedName("customer_code")
String customer_code;

    public int getCustomer_id() {
        return customer_id;
    }

    public String getCustomer_code() {
        return customer_code;
    }
}
