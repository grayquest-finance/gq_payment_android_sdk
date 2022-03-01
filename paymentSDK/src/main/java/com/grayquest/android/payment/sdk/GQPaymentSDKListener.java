package com.grayquest.android.payment.sdk;

import org.json.JSONObject;

public interface GQPaymentSDKListener {

    void onSuccess(JSONObject message);

    void onFailed(JSONObject message);

    void onCancel(JSONObject message);
}
