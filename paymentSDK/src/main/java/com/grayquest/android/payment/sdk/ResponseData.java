package com.grayquest.android.payment.sdk;

import com.google.gson.annotations.SerializedName;

public class ResponseData {
    @SerializedName("customer_id")
    int customer_id;

    public int getCustomer_id() {
        return customer_id;
    }
}
