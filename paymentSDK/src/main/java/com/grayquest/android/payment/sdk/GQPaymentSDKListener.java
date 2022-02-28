package com.grayquest.android.payment.sdk;

public interface GQPaymentSDKListener {

    void onSuccess(String message);

    void onFailed(String message);

    void onCancel(String message);
}
