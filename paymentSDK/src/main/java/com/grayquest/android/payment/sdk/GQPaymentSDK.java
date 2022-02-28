package com.grayquest.android.payment.sdk;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GQPaymentSDK {

    private static String user;
    private static int customer_id;
    private static Context context;
    private static JSONObject jsonObject;

    private static final String TAG = GQPaymentSDK.class.getSimpleName();

    private static GQPaymentSDKListener gqPaymentSDKListener;

    public static void initiate(Context context1, JSONObject options) {

        GQPaymentSDK.context = context1;
        gqPaymentSDKListener = (GQPaymentSDKListener) context1;
        GQPaymentSDK.jsonObject = options;

        Log.e(TAG, "Options: " + options.toString());

        try {
            if (!options.getString("customer_mobile").equals("")) {
                Log.e(TAG, "CustomerMobile: " + options.getString("customer_mobile"));
                if (gqPaymentSDKListener!=null){
                    gqPaymentSDKListener.onCancel("Close SDK");
                }
                showProgress();
                createCustomer(options.getString("customer_mobile"));
            } else {
                setUser("existing");
//                user = "existing";
                if (gqPaymentSDKListener!=null){
                    gqPaymentSDKListener.onCancel("Close SDK");
                }
                Intent intent = new Intent(context, WebActivity.class);
                intent.putExtra("options", String.valueOf(jsonObject));
                context.startActivity(intent);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static ProgressDialog progress = null;

    public static void showProgress() {
        progress = new ProgressDialog(context);
        if (!progress.isShowing()) {
            progress.setCanceledOnTouchOutside(false);
            progress.setCancelable(false);
            progress.setMessage("Please wait...");
            progress.show();
        }

    }

    public static void hideProgress() {
        if (context != null) {
            if (progress.isShowing()) {
                progress.dismiss();
            }
        }
//        else
//            progress.dismiss();
    }

    private static void createCustomer(String customer_mobile) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("customer_mobile", customer_mobile);

        ApiInterface apiInterface = API_Client.getRetrofit().create(ApiInterface.class);
        Call<PaymentSDk_Contributor> create = apiInterface.createCustomer(jsonObject);

        create.enqueue(new Callback<PaymentSDk_Contributor>() {
            @Override
            public void onResponse(@NonNull Call<PaymentSDk_Contributor> call, @NonNull Response<PaymentSDk_Contributor> response) {
                try {
                    Log.e(TAG, "ResponseCode: " + response.code());
                    if (response.isSuccessful() && response.body() != null) {
                        /*Success Response*/
                        hideProgress();
                        boolean success = response.body().success;
                        String message = response.body().getMessage();
                        if (success) {
                            ResponseData responseData = new ResponseData();

                            responseData = response.body().getData();
                            Log.e(TAG, "CustomerId: " + responseData.getCustomer_id());
                            setCustomer_id(responseData.getCustomer_id());
//
//                            Bundle bundle = new Bundle();
//                            bundle.putString("options", jsonObject);

                            Intent intent = new Intent(context, WebActivity.class);
                            intent.putExtra("options", String.valueOf(jsonObject));
                            context.startActivity(intent);
                        }
                    } else if (response.errorBody() != null) {
                        /*Error Response*/
                        Log.e(TAG, "ErrorResponse: " + response.errorBody().string());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    hideProgress();
                    Log.e(TAG, "Error: " + e.getCause());
                    Log.e(TAG, "Error: " + e.getMessage());
                    Log.e(TAG, "Error: " + e.getLocalizedMessage());
                }
            }

            @Override
            public void onFailure(@NonNull Call<PaymentSDk_Contributor> call, @NonNull Throwable t) {
                hideProgress();
                Log.e(TAG, "Error1: " + t.getCause());
                Log.e(TAG, "Error1: " + t.getMessage());
                Log.e(TAG, "Error1: " + t.getLocalizedMessage());
            }
        });
    }

    public static String getUser() {
        return user;
    }

    public static void setUser(String user) {
        GQPaymentSDK.user = user;
    }

    public static int getCustomer_id() {
        return customer_id;
    }

    public static void setCustomer_id(int customer_id) {
        GQPaymentSDK.customer_id = customer_id;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        GQPaymentSDK.context = context;
    }

    public static void closeSDK(String message){
        gqPaymentSDKListener.onCancel(message);
    }
}
