package com.grayquest.android.payment.sdk;

import android.content.Context;
import android.webkit.JavascriptInterface;

import org.json.JSONException;
import org.json.JSONObject;

public class GQPaymentSDKInterface {
    private static final String TAG = GQPaymentSDKInterface.class.getSimpleName();
    Context mContext;

    public GQPaymentSDKInterface(Context ctx) {
        this.mContext = ctx;
    }

    @JavascriptInterface
    public void sdkSuccess(String data) {
//        Log.e(TAG, "Data: " + data);

        JSONObject testObjet = null;
        try {
            testObjet = new JSONObject(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        Toast.makeText(mContext, data.toString(), Toast.LENGTH_SHORT).show();

        if (testObjet!=null) {
            ((GQWebActivity) mContext).sdkSuccess(testObjet);
        }
//        GQPaymentSDK.successSDK(data);

//        return testObjet.toString();
    }

    @JavascriptInterface
    public void sdkError(String data) {
//        Log.e(TAG, "Data: " + data);

//        Toast.makeText(mContext, data.toString(), Toast.LENGTH_SHORT).show();

        if (data!=null) {
            JSONObject testObjet = null;
            try {
                testObjet = new JSONObject(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
//        return testObjet.toString();
            ((GQWebActivity) mContext).sdkFailed(testObjet);
        }
//        GQPaymentSDK.failedSDK(data);
    }

    @JavascriptInterface
    public void sdkCancel(String data) {
//        Log.e(TAG, "Data: " + data);

//        Toast.makeText(mContext, data.toString(), Toast.LENGTH_SHORT).show();

//        return testObjet.toString();
        if (data!=null) {
            JSONObject testObjet = null;
            try {
                testObjet = new JSONObject(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ((GQWebActivity) mContext).sdkCancel(testObjet);
        }
//        GQPaymentSDK.cancelSDK(data);
    }

    @JavascriptInterface
    public void sendADOptions(String data){
//        Log.e(TAG, "Data: "+data);

        if (data!=null){
            JSONObject dataObject = null;
            try {
                 dataObject = new JSONObject(data);

                ((GQWebActivity)mContext).ADOptions(data);
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    }

    @JavascriptInterface
    public void sendPGOptions(String data){
//        Log.e(TAG, "Data: "+data);
        if (data!=null){
            ((GQWebActivity)mContext).PGOptions(data);
        }
    }
}
