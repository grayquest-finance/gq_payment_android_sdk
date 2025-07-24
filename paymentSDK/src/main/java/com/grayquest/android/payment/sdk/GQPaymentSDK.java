package com.grayquest.android.payment.sdk;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.StringWriter;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GQPaymentSDK {

    private static final String TAG = GQPaymentSDK.class.getSimpleName();
    private static String user;
    private static int customer_id;
    private static String customer_code;
    private static Context context;
    private static JSONObject optionsJSON;
    private static JSONObject configJSON;
    private static JSONObject authJSON;
    private static JSONObject customizationJSON;

    private static GQPaymentSDKListener gqPaymentSDKListener;

    public static void initiate(Context context1, JSONObject config, JSONObject options) {

        String client_id = null, client_secret_key = null, gq_api_key = null, student_id, env, reference_id,
                customer_number, fee_amount, payable_amount, theme_color;
        boolean fee_editable;
        boolean isInValid = false;
        StringWriter errorMessage = new StringWriter();

        GQPaymentSDK.context = context1;
        gqPaymentSDKListener = (GQPaymentSDKListener) context1;
        GQPaymentSDK.optionsJSON = options;
        GQPaymentSDK.configJSON = config;

        try {
            String jsonAuth = config.getString("auth");
            GQPaymentSDK.authJSON = new JSONObject(jsonAuth);
//            Log.e(TAG, "Auth: " + jsonAuth.toString());

//            Log.e(TAG, "Config: " + config.toString());

            if (GQPaymentSDK.authJSON.has("client_id") && !GQPaymentSDK.authJSON.getString("client_id").isEmpty()) {
                client_id = GQPaymentSDK.authJSON.getString("client_id");
//                Log.e(TAG, "client_id: " + client_id);
            } else {
                errorMessage.append("Client Id required");
                isInValid = true;
            }
            if (GQPaymentSDK.authJSON.has("client_secret_key") && !GQPaymentSDK.authJSON.getString("client_secret_key").isEmpty()) {
                client_secret_key = GQPaymentSDK.authJSON.getString("client_secret_key");
//                Log.e(TAG, "client_secret_key: " + client_secret_key);
            } else {
                errorMessage.append(", Client secret key required");
                isInValid = true;
            }
            if (GQPaymentSDK.authJSON.has("gq_api_key") && !GQPaymentSDK.authJSON.getString("gq_api_key").isEmpty()) {
                gq_api_key = GQPaymentSDK.authJSON.getString("gq_api_key");
//                Log.e(TAG, "gq_api_key: " + gq_api_key);
            } else {
                errorMessage.append(", GQ Api Key required");
                isInValid = true;
            }
            if (config.has("student_id") && !config.getString("student_id").isEmpty()) {
                student_id = config.getString("student_id");
//                Log.e(TAG, "student_id: " + student_id);
            } else {
                errorMessage.append(", Student Id required");
                isInValid = true;
            }

            if (config.has("env") && !config.getString("env").isEmpty()) {
                env = config.getString("env");
                Environment.env(env);
//                Log.e(TAG, "env: " + env);
            } else {
                errorMessage.append(", Environment required");
                isInValid = true;
            }

            if(config.has("reference_id") && !config.getString("reference_id").isEmpty()){
                reference_id = config.getString("reference_id");
            }

            if (config.has("customization")) {
//                Log.e(TAG, "Customization: " + config.getString("customization"));
                String jsonCustomization = config.getString("customization");
                GQPaymentSDK.customizationJSON = new JSONObject(jsonCustomization);

            } else {

//                Log.e(TAG, "No Customization");
            }
            if (config.has("pp_config")) {
                String pp_config = config.getString("pp_config");
                JSONObject ppConfig = new JSONObject(pp_config);

                if (ppConfig.has("card_code") && !ppConfig.has("slug")) {
                    isInValid = true;
                    errorMessage.append(", Payment Configuration Slug required");
                } else if (ppConfig.has("slug") && ppConfig.getString("slug").isEmpty()) {
                    isInValid = true;
                    errorMessage.append(", Payment Configuration Slug required");
                }
            }

            if (isInValid) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("error", errorMessage);
                gqPaymentSDKListener.onFailed(jsonObject);
            } else {

                if (config.has("customer_number") && !config.getString("customer_number").isEmpty()) {
//                    Log.e(TAG, "CustomerNumber: " + config.getString("customer_number"));

                    String base = client_id + ":" + client_secret_key;
                    String abase = "Basic " + Base64.encodeToString(base.getBytes(), Base64.NO_WRAP);

                    showProgress();
                    createCustomer(config.getString("customer_number"), gq_api_key, abase);
                } else {
                    setUser("new");

                    Intent intent = new Intent(context, GQWebActivity.class);
                    if (options != null) {
//                        Log.e(TAG, "Optional: " + options.toString());
                        intent.putExtra("options", options.toString());
                    }
                    intent.putExtra("config", String.valueOf(configJSON));
                    intent.putExtra("customer_id", getCustomer_id());
                    intent.putExtra("customer_code", getCustomer_code());
                    intent.putExtra("user", getUser());
                    context.startActivity(intent);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void tokenCheckout(Context context, String token, String environment){
        GQPaymentSDK.context = context;
        gqPaymentSDKListener = (GQPaymentSDKListener) context;
        boolean isInValid = false;
        StringWriter errorMessage = new StringWriter();

        if (environment!=null && !environment.isEmpty()){
            Environment.env(environment);

        }else {
            errorMessage.append("Environment required");
            isInValid = true;
        }

        if (token!= null && !token.isEmpty()){
//            isInValid = false;
        }else {
            errorMessage.append(", Token required");
            isInValid = true;
        }

        if (isInValid){
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("error", errorMessage);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            gqPaymentSDKListener.onFailed(jsonObject);
        }else {
            showProgress();
            sessionCode(token, environment);
        }
    }

//    private static ProgressDialog progress = null;
    private static AlertDialog progressDialog = null;

    public static void showProgress() {
//        progress = new ProgressDialog(context);
//        if (!progress.isShowing()) {
//            progress.setCanceledOnTouchOutside(false);
//            progress.setCancelable(false);
//            progress.setMessage("Please wait...");
//            progress.show();
//        }
        if (progressDialog == null || !progressDialog.isShowing()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View customView = LayoutInflater.from(context).inflate(R.layout.progress_dialog, null);
            builder.setView(customView);
            builder.setCancelable(false);
            progressDialog = builder.create();
            progressDialog.show();
        }
    }

    public static void hideProgress() {
//        if (context != null) {
//            if (progress.isShowing()) {
//                progress.dismiss();
//            }
//        }

        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private static void sessionCode(String token, String env){
        ApiInterface apiInterface = API_Client.getRetrofit().create(ApiInterface.class);
        Call<PaymentSDk_Contributor> getSessionCode = apiInterface.sessionCode("Bearer "+token);

        getSessionCode.enqueue(new Callback<PaymentSDk_Contributor>() {
            @Override
            public void onResponse(Call<PaymentSDk_Contributor> call, Response<PaymentSDk_Contributor> response) {
                try{

                    if (response.isSuccessful() && response.body() != null) {
                        /*Success Response*/
                        hideProgress();
                        boolean success = response.body().success;
                        int status_code = response.body().getStatus_code();
                        String message = response.body().getMessage();
                        if (success) {
                            ResponseData responseData = new ResponseData();

                            responseData = response.body().getData();

                            String session_code = responseData.getSession_code();

                            Log.e(TAG, "SessionCode: "+session_code);

                            Intent intent = new Intent(context, GQWebActivity.class);
                            intent.putExtra("session_code", session_code);
                            context.startActivity(intent);
                        }
                    } else if (response.errorBody() != null) {
                        /*Error Response*/
                        hideProgress();
                        String result = response.errorBody().string();

//                        Log.e(TAG, "Result: " + result);
                        JSONObject errorResponse = new JSONObject(result);

                        boolean success = errorResponse.getBoolean("success");
                        String message = errorResponse.getString("message");

//                        Log.e(TAG, "Success: " + success);
//                        Log.e(TAG, "Message: " + message);
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("error", message);
                        gqPaymentSDKListener.onFailed(jsonObject);
//                        Log.e(TAG, "ErrorResponse: " + response.errorBody().string());
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    hideProgress();
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("error", "Oop's something went wrong contact support@grayquest.com");
                    } catch (JSONException jsonException) {
                        jsonException.printStackTrace();
                    }
                    gqPaymentSDKListener.onFailed(jsonObject);
                }
            }

            @Override
            public void onFailure(Call<PaymentSDk_Contributor> call, Throwable t) {
                hideProgress();
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("error", "Oop's something went wrong contact support@grayquest.com");
                } catch (JSONException jsonException) {
                    jsonException.printStackTrace();
                }
                gqPaymentSDKListener.onFailed(jsonObject);
//                Log.e(TAG, "Error1: " + t.getCause());
//                Log.e(TAG, "Error1: " + t.getMessage());
//                Log.e(TAG, "Error1: " + t.getLocalizedMessage());
            }
        });
    }

    private static void createCustomer(String customer_mobile, String gq_api_key, String abase) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("customer_mobile", customer_mobile);

//        Log.e(TAG, "ABASE: " + abase.trim());
//        Log.e(TAG, "GQAPIKEY: " + gq_api_key);

        ApiInterface apiInterface = API_Client.getRetrofit().create(ApiInterface.class);
        Call<PaymentSDk_Contributor> create = apiInterface.createCustomer(gq_api_key, abase, jsonObject);

        create.enqueue(new Callback<PaymentSDk_Contributor>() {
            @Override
            public void onResponse(@NonNull Call<PaymentSDk_Contributor> call, @NonNull Response<PaymentSDk_Contributor> response) {
                try {
//                    Log.e(TAG, "ResponseCode: " + response.code());
                    if (response.isSuccessful() && response.body() != null) {
                        /*Success Response*/
                        hideProgress();
                        boolean success = response.body().success;
                        int status_code = response.body().getStatus_code();
                        String message = response.body().getMessage();
                        if (success) {
                            ResponseData responseData = new ResponseData();

                            responseData = response.body().getData();
//                            Log.e(TAG, "CustomerId: " + responseData.getCustomer_id());
                            setCustomer_id(responseData.getCustomer_id());
                            setCustomer_code(responseData.getCustomer_code());
                            if (status_code == 201) {
                                setUser("new");
                            } else {
                                setUser("existing");
                            }
//
//                            Bundle bundle = new Bundle();
//                            bundle.putString("options", jsonObject);

                            Intent intent = new Intent(context, GQWebActivity.class);
                            if (optionsJSON != null) {
                                intent.putExtra("options", optionsJSON.toString());
                            }
                            intent.putExtra("config", String.valueOf(configJSON));
                            intent.putExtra("customer_id", getCustomer_id());
                            intent.putExtra("customer_code", getCustomer_code());
                            intent.putExtra("user", getUser());
                            context.startActivity(intent);
                        }
                    } else if (response.errorBody() != null) {
                        /*Error Response*/
                        hideProgress();
                        String result = response.errorBody().string();

//                        Log.e(TAG, "Result: " + result);
                        JSONObject errorResponse = new JSONObject(result);

                        boolean success = errorResponse.getBoolean("success");
                        String message = errorResponse.getString("message");

//                        Log.e(TAG, "Success: " + success);
//                        Log.e(TAG, "Message: " + message);
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("error", message);
                        gqPaymentSDKListener.onFailed(jsonObject);
//                        Log.e(TAG, "ErrorResponse: " + response.errorBody().string());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    hideProgress();
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("error", "Oop's something went wrong contact support@grayquest.com");
                    } catch (JSONException jsonException) {
                        jsonException.printStackTrace();
                    }
                    gqPaymentSDKListener.onFailed(jsonObject);
//                    Log.e(TAG, "Error: " + e.getCause());
//                    Log.e(TAG, "Error: " + e.getMessage());
//                    Log.e(TAG, "Error: " + e.getLocalizedMessage());
                }
            }

            @Override
            public void onFailure(@NonNull Call<PaymentSDk_Contributor> call, @NonNull Throwable t) {
                hideProgress();
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("error", "Oop's something went wrong contact support@grayquest.com");
                } catch (JSONException jsonException) {
                    jsonException.printStackTrace();
                }
                gqPaymentSDKListener.onFailed(jsonObject);
//                Log.e(TAG, "Error1: " + t.getCause());
//                Log.e(TAG, "Error1: " + t.getMessage());
//                Log.e(TAG, "Error1: " + t.getLocalizedMessage());
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

    public static String getCustomer_code() {
        return customer_code;
    }

    public static void setCustomer_code(String customer_code) {
        GQPaymentSDK.customer_code = customer_code;
    }

    public void setContext(Context context) {
        GQPaymentSDK.context = context;
    }

    public static void cancelSDK(JSONObject message) {
        gqPaymentSDKListener.onCancel(message);
    }

    public static void successSDK(JSONObject jsonObject) {
        gqPaymentSDKListener.onSuccess(jsonObject);
    }

    public static void failedSDK(JSONObject jsonObject) {
        gqPaymentSDKListener.onFailed(jsonObject);
    }
}
