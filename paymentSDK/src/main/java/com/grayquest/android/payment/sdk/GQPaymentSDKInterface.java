package com.grayquest.android.payment.sdk;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

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
        Log.e(TAG, "Data: " + data);

        JSONObject testObjet = null;
        try {
            testObjet = new JSONObject(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        Toast.makeText(mContext, data.toString(), Toast.LENGTH_SHORT).show();

        if (testObjet!=null) {
            ((WebActivity) mContext).sdkSuccess(testObjet);
        }
//        GQPaymentSDK.successSDK(data);

//        return testObjet.toString();
    }

    @JavascriptInterface
    public void sdkError(String data) {
        Log.e(TAG, "Data: " + data);

//        Toast.makeText(mContext, data.toString(), Toast.LENGTH_SHORT).show();

        if (data!=null) {
            JSONObject testObjet = null;
            try {
                testObjet = new JSONObject(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
//        return testObjet.toString();
            ((WebActivity) mContext).sdkFailed(testObjet);
        }
//        GQPaymentSDK.failedSDK(data);
    }

    @JavascriptInterface
    public void sdkCancel(String data) {
        Log.e(TAG, "Data: " + data);

//        Toast.makeText(mContext, data.toString(), Toast.LENGTH_SHORT).show();

//        return testObjet.toString();
        if (data!=null) {
            JSONObject testObjet = null;
            try {
                testObjet = new JSONObject(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ((WebActivity) mContext).sdkCancel(testObjet);
        }
//        GQPaymentSDK.cancelSDK(data);
    }

    @JavascriptInterface
    public void sendADOptions(String data){
        Log.e(TAG, "Data: "+data);

        if (data!=null){
            JSONObject dataObject = null;
            try {
                 dataObject = new JSONObject(data);

                ((WebActivity)mContext).ADOptions(data);
            }catch (JSONException e){
                e.printStackTrace();
            }
        }
    }
}
