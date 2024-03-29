package com.grayquest.android.payment.sdk;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GQPaymentSDK {

    private static String user;
    private static int customer_id;
    private static String customer_code;
    private static Context context;
    private static JSONObject optionsJSON;
    private static JSONObject configJSON;
    private static JSONObject authJSON;
    private static JSONObject financingMode;
    private static JSONObject customizationJSON;

    private static final String TAG = GQPaymentSDK.class.getSimpleName();

    private static GQPaymentSDKListener gqPaymentSDKListener;

    public static void initiate(Context context1, JSONObject config, JSONObject options) {

        String client_id = null, client_secret_key = null, gq_api_key = null, student_id, env,
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
            if (config.has("fee_editable")) {
                fee_editable = config.getBoolean("fee_editable");
//                Log.e(TAG, "fee_editable: " + fee_editable);
            } else {
                errorMessage.append(", Fee Editable required");
                isInValid = true;
            }
            if (config.has("env") && !config.getString("env").isEmpty()) {
                env = config.getString("env");
//                Log.e(TAG, "env: " + env);
            } else {
                errorMessage.append(", Environment required");
                isInValid = true;
            }

            if (config.has("financing_config")) {
                String financing = null;
                try {
                    financing = config.getString("financing_config");
                    GQPaymentSDK.financingMode = new JSONObject(financing);
                    Log.e(TAG, "FinancingConfig: " + financing.toString());
                    if (financingMode.has("monthly_emi") && financingMode.getJSONObject("monthly_emi").has("amount")) {
                        if (!financingMode.getJSONObject("monthly_emi").getString("amount").isEmpty()) {
                            JSONObject monthly_emi = new JSONObject(String.valueOf(financingMode.get("monthly_emi")));
                            String amount = monthly_emi.getString("amount");
                            Double amountINt = Double.parseDouble(amount);
//                            Log.e(TAG, "MonthlyEmiJsonObject: " + monthly_emi);
//                            Log.e(TAG, "MonthlyEmiAmount: " + amount);
                            if (amountINt > 0.0) {
//                                Log.e(TAG, "MonthlyEmiAmount: " + amountINt);
                            } else {
                                errorMessage.append(", Monthly Emi amount must be greater than 0");
                                isInValid = true;
                            }
                        }else {
                            errorMessage.append(", Please enter Monthly Emi Amount");
                            isInValid = true;
                        }
                    }
                    if (financingMode.has("auto_debit") && financingMode.getJSONObject("auto_debit").has("amount")) {
                        if (!financingMode.getJSONObject("auto_debit").getString("amount").isEmpty()) {
                            JSONObject auto_debit = new JSONObject(String.valueOf(financingMode.get("auto_debit")));
                            String amount = auto_debit.getString("amount");
                            Double amountINt = Double.parseDouble(amount);
//                            Log.e(TAG, "AutoDebitJsonObject: " + auto_debit);
//                            Log.e(TAG, "AutoDebitAmount: " + amount);
                            if (amountINt > 0.0) {
//                                Log.e(TAG, "AutoDebitAmount: " + amountINt);
                            } else {
                                errorMessage.append(", Auto Debit amount must be greater than 0");
                                isInValid = true;
                            }
                        }else {
                            errorMessage.append(", Please enter Auto Debit Amount");
                            isInValid = true;
                        }
                    }
                    if (financingMode.has("auto_debit") && financingMode.getJSONObject("auto_debit").has("schedule")) {
                        JSONObject auto_debit = new JSONObject(String.valueOf(financingMode.get("auto_debit")));
//                        Log.e(TAG, "ScheduleArraySize: " + auto_debit.get("schedule"));
                        JSONArray scheduleArray = auto_debit.getJSONArray("schedule");
//                        Log.e(TAG, "ScheduleArraySize: " + scheduleArray.length());
                        for (int i = 0; i < scheduleArray.length(); i++) {
                            JSONObject schedule = scheduleArray.getJSONObject(i);
                            String date = schedule.getString("date");
//                            Log.e(TAG, "Schedule Date: " + i + " - " + date);
                            if (!schedule.getString("amount").isEmpty()) {
                                String amount = schedule.getString("amount");
//                                Log.e(TAG, "Schedule Amount: " + i + " - " + amount);
                                Double amountINt = Double.parseDouble(amount);
                                if (amountINt > 0.0) {
//                                    Log.e(TAG, "AutoDebitScheduleAmount: " + amountINt);
                                } else {
                                    errorMessage.append(", Auto Debit Schedule date "+date+" amount must be greater than 0");
                                    isInValid = true;
                                }
                            }else {
                                errorMessage.append(", Please enter Auto Debit Schedule date "+date+" Amount");
                                isInValid = true;
                            }
                        }
                    }
                    if (financingMode.has("direct") && financingMode.getJSONObject("direct").has("amount")) {
                        if (!financingMode.getJSONObject("direct").getString("amount").isEmpty()) {
                            JSONObject direct = new JSONObject(String.valueOf(financingMode.get("direct")));
                            String amount = direct.getString("amount");
                            Double amountINt = Double.parseDouble(amount);
//                            Log.e(TAG, "DirectJsonObject: " + direct);
//                            Log.e(TAG, "DirectAmount: " + amount);
                            if (amountINt > 0.0) {
//                                Log.e(TAG, "DirectAmount: " + amountINt);
                            } else {
                                errorMessage.append(", Direct amount must be greater than 0");
                                isInValid = true;
                            }
                        }else {
                            errorMessage.append(", Please enter Direct Amount");
                            isInValid = true;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

//            customer_number = config.getString("customer_number");
//            Log.e(TAG, "customer_number: " + customer_number);
            if (config.has("fee_amount") && !config.getString("fee_amount").isEmpty()) {
                fee_amount = config.getString("fee_amount");
//                Log.e(TAG, "fee_amount: " + fee_amount);

                if (config.has("payable_amount") && !config.getString("payable_amount").isEmpty()) {
                
                    payable_amount = config.getString("payable_amount");
//                    Log.e(TAG, "payable_amount: " + payable_amount);
                } else {
                    errorMessage.append(", Payable Amount required");
                    isInValid = true;
                }
            } else {
                if (config.has("payable_amount")) {
                    payable_amount = config.getString("payable_amount");
//                    Log.e(TAG, "payable_amount: " + payable_amount);
                }
            }

            if (config.has("customization")) {
//                Log.e(TAG, "Customization: " + config.getString("customization"));
                String jsonCustomization = config.getString("customization");
                GQPaymentSDK.customizationJSON = new JSONObject(jsonCustomization);

            } else {

//                Log.e(TAG, "No Customization");
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

                    Intent intent = new Intent(context, WebActivity.class);
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

//    private boolean isMatchDate(String date){
//        String regex = "[1-31]";
//        Pattern pattern = Pattern.compile()
//    }

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

                            Intent intent = new Intent(context, WebActivity.class);
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
