package com.grayquest.android.payment.sdk;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.cashfree.pg.api.CFPaymentGatewayService;
import com.cashfree.pg.core.api.CFSession;
import com.cashfree.pg.core.api.CFTheme;
import com.cashfree.pg.core.api.callback.CFCheckoutResponseCallback;
import com.cashfree.pg.core.api.exception.CFException;
import com.cashfree.pg.core.api.utils.CFErrorResponse;
import com.cashfree.pg.ui.api.CFDropCheckoutPayment;
import com.cashfree.pg.ui.api.CFPaymentComponent;
import com.easebuzz.payment.kit.PWECouponsActivity;
import com.razorpay.Checkout;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;

import org.json.JSONException;
import org.json.JSONObject;

public class WebActivity extends AppCompatActivity implements PaymentResultWithDataListener, CFCheckoutResponseCallback {

    private static final String TAG = WebActivity.class.getSimpleName();
    WebView webSdk;
    String jsonOp, jsonCon, jsonAuth, jsonCustomization, jsonPPConfig, jsonFeeHeader;
    JSONObject optionsJSON, configJSON, authJSON, customizationJSON, ppConfigJSON, feeHeaderJSON;

    //    String url = "https://erp-sdk.graydev.tech/instant-eligibility?gapik=9db4fc333d8bcf7fee98804105d9fc0c85199d77&abase=MzU0NTk4ZmQtNTc1YS00YzFmLWE2ZTMtZTA4ZmM1ZWEwNmQzOjJlYjM0OTczMjU5NGZlNzc3YmUwNzlmYjNjN2U1NTcxOTRmNTVhMTQ=&cid=23960&ccode=e4589ac0-46ee-42de-9eb4-b0094e1a0b0b&sid=Studnet_51w121&pc=734858&fedit=true&famt=&pamt=&s=erp&user=existing";
//    String url = "https://erp-sdk.graydev.tech/instant-eligibility?m=7794653261&gapik=9db4fc333d8bcf7fee98804105d9fc0c85199d77&abase=MzU0NTk4ZmQtNTc1YS00YzFmLWE2ZTMtZTA4ZmM1ZWEwNmQzOjJlYjM0OTczMjU5NGZlNzc3YmUwNzlmYjNjN2U1NTcxOTRmNTVhMTQ=&cid=23960&ccode=e4589ac0-46ee-42de-9eb4-b0094e1a0b0b&sid=Studnet_51w121&pc=734858&fedit=true&famt=&pamt=&s=erp&user=existing";
    String loadURL;
    StringBuilder urlLoad;
    String clientId, secretKey, name;
    String gapik, abase, sid, m, famt, pamt, env, ccode, pc, s = "asdk", user, callback_url, _gqfc;
    int cid;
    boolean fedit, redirect;

    ActivityResultLauncher<Intent> paymentLauncher;

    private ActivityResultLauncher<Intent> pweActivityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        webSdk = (WebView) findViewById(R.id.web_sdk);
//        urlLoad = new StringBuilder(API_Client.WEB_LOAD_URL + "instant-eligibility?");

        Checkout.preload(getApplicationContext());
        onResultPG();

        if (getIntent() != null) {
            try {
                if (getIntent().hasExtra("options") && getIntent().getStringExtra("options") != null) {
                    jsonOp = getIntent().getStringExtra("options");
                    optionsJSON = new JSONObject(jsonOp);
//                    Log.e(TAG, "OptionsJSON: " + optionsJSON.toString());
                }
                jsonCon = getIntent().getStringExtra("config");
                configJSON = new JSONObject(jsonCon);

                jsonAuth = configJSON.getString("auth");
                authJSON = new JSONObject(jsonAuth);
//                Log.e(TAG, "ConfigJSON: " + configJSON.toString());

                cid = getIntent().getIntExtra("customer_id", 0);
                ccode = getIntent().getStringExtra("customer_code");
                user = getIntent().getStringExtra("user");

//                Log.e(TAG, "Options: " + jsonOp);
//                Log.e(TAG, "Config: " + jsonCon);
//                Log.e(TAG, "CustomerId: " + cid);
//                Log.e(TAG, "CustomerCode: " + ccode);

                clientId = authJSON.getString("client_id");
                secretKey = authJSON.getString("client_secret_key");
                gapik = authJSON.getString("gq_api_key");
//                urlLoad.append("gapik=").append(gapik);
                sid = configJSON.getString("student_id");
                if (configJSON.has("customer_number")) {
                    m = configJSON.getString("customer_number");
                } else {
                    m = "";
                }
//                if (configJSON.has("fee_amount")) {
//                    famt = configJSON.getString("fee_amount");
//                } else {
//                    famt = "";
//                }
//                if (configJSON.has("payable_amount")) {
//                    pamt = configJSON.getString("payable_amount");
//                } else {
//                    pamt = "";
//                }
                env = configJSON.getString("env");
//                fedit = configJSON.getBoolean("fee_editable");

//                if (configJSON.has("financing_config")) {
//                    jsonFinancingConfig = configJSON.getString("financing_config");
//                    financingConfigJSON = new JSONObject(jsonFinancingConfig);
//                }

                if (configJSON.has("customization")) {
                    jsonCustomization = configJSON.getString("customization");
                    customizationJSON = new JSONObject(jsonCustomization);
//                    Log.e(TAG, "Customization: " + customizationJSON);
                    if (customizationJSON.has("theme_color")) {
                        pc = customizationJSON.getString("theme_color");
                    } else {
                        pc = "";
                    }
                }

                if (configJSON.has("pp_config")){
                    jsonPPConfig = configJSON.getString("pp_config");
                    ppConfigJSON = new JSONObject(jsonPPConfig);
                }

                if (configJSON.has("fee_headers")){
                    jsonFeeHeader = configJSON.getString("fee_headers");
                    feeHeaderJSON = new JSONObject(jsonFeeHeader);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String base = clientId + ":" + secretKey;

            abase = Base64.encodeToString(base.getBytes(), Base64.NO_WRAP);

//            Log.e(TAG, "gapik: " + gapik);
//            Log.e(TAG, "abase: " + abase);
//            Log.e(TAG, "sid: " + sid);
//            Log.e(TAG, "m: " + m);
//            Log.e(TAG, "famt: " + famt);
//            Log.e(TAG, "pamt: " + pamt);
//            Log.e(TAG, "env: " + env);
//            Log.e(TAG, "fedit: " + fedit);
//            Log.e(TAG, "cid: " + cid);
//            Log.e(TAG, "ccode: " + ccode);
//            Log.e(TAG, "pc: " + pc);
//            Log.e(TAG, "s: " + s);
//            Log.e(TAG, "user: " + user);

            urlLoad = new StringBuilder(Environment.WEB_LOAD_URL + "instant-eligibility?gapik=" + gapik + "&abase=" + abase + "&sid=" + sid + "&m=" + m + "&env=" + env + "&cid=" + cid + "&ccode=" + ccode + "&pc=" + pc + "&s=" + s + "&user=" + user);

            if (optionsJSON != null && optionsJSON.length() != 0) {
//                Log.e(TAG, "optional: " + optionsJSON.toString());
//                loadURL = API_Client.WEB_LOAD_URL + "instant-eligibility?gapik=" + gapik + "&abase=" + abase + "&sid=" + sid + "&m=" + m + "&famt=" + famt + "&pamt=" + pamt + "&env=" + env + "&fedit=" + fedit + "&cid=" + cid + "&ccode=" + ccode + "&pc=" + pc + "&s=" + s + "&user=" + user + "&optional=" + optionsJSON.toString();
                urlLoad.append("&optional=").append(optionsJSON.toString());
            }
//            if (financingConfigJSON != null && financingConfigJSON.length() != 0) {
////                Log.e(TAG, "financingConfig: "+financingConfigJSON.toString());
////                loadURL = API_Client.WEB_LOAD_URL + "instant-eligibility?gapik=" + gapik + "&abase=" + abase + "&sid=" + sid + "&m=" + m + "&famt=" + famt + "&pamt=" + pamt + "&env=" + env + "&fedit=" + fedit + "&cid=" + cid + "&ccode=" + ccode + "&pc=" + pc + "&s=" + s + "&user=" + user + "&_gqfc=" + financingConfigJSON.toString() + "&optional=" + optionsJSON.toString();
//                urlLoad.append("&_gqfc=").append(financingConfigJSON.toString());
//            }
            if (ppConfigJSON != null && ppConfigJSON.length() != 0){
                urlLoad.append("&_pp_config=").append(ppConfigJSON.toString());
            }
            if (feeHeaderJSON !=null && feeHeaderJSON.length() != 0){
                urlLoad.append("&_fee_headers=").append(feeHeaderJSON.toString());
            }
            urlLoad.append("&_v=").append(Environment.VERSION);
//            else {
//                loadURL = API_Client.WEB_LOAD_URL + "instant-eligibility?gapik=" + gapik + "&abase=" + abase + "&sid=" + sid + "&m=" + m + "&famt=" + famt + "&pamt=" + pamt + "&env=" + env + "&fedit=" + fedit + "&cid=" + cid + "&ccode=" + ccode + "&pc=" + pc + "&s=" + s + "&user=" + user;
//            }
//            Log.e(TAG, "LoadURL: " + urlLoad);
        }

//        GQPaymentSDK.showProgress();
        webSdk.getSettings().setJavaScriptEnabled(true);
        webSdk.getSettings().setDomStorageEnabled(true);
        webSdk.getSettings().setSupportMultipleWindows(true);
        webSdk.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

//        webSdk.setWebViewClient(new WebViewClient(){
//            public void onPageFinished(WebView view, String url) {
//                // do your stuff here
////                GQPaymentSDK.hideProgress();
//                Log.e(TAG, "LoadedUrl: "+url);
//            }
//        });

        webSdk.getSettings().setDatabaseEnabled(true);
        webSdk.getSettings().setSaveFormData(true);
        webSdk.getSettings().setAllowContentAccess(true);
        webSdk.getSettings().setAllowFileAccess(true);
        webSdk.getSettings().setAllowFileAccessFromFileURLs(true);
        webSdk.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webSdk.getSettings().setSupportZoom(true);

//        webSdk.setWebViewClient(new WebViewClient());
        webSdk.setClickable(true);
        webSdk.setOverScrollMode(WebView.OVER_SCROLL_NEVER);
//        webSdk.setWebViewClient(new MyWebViewClient());
        webSdk.setWebChromeClient(new MyWebChromeClient());
//        webSdk.setWebChromeClient(new WebChromeClient());

        webSdk.addJavascriptInterface(new GQPaymentSDKInterface(WebActivity.this), "Gqsdk");
//        webSdk.loadUrl("https://erp-sdk.graydev.tech/instant-eligibility?optional="+jsonObject.toString());
        webSdk.loadUrl(String.valueOf(urlLoad));
//        Log.e(TAG, "URL: "+"https://erp-sdk.graydev.tech/instant-eligibility?optional="+jsonObject.toString());
//        Log.e(TAG, "URL: " + loadURL);

        paymentLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {

//                    Log.e(TAG, "PaymentData: " + result.getData().getStringExtra("payment_data"));

                }
            }
        });
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("cancelled", "User Close SDk");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        GQPaymentSDK.closeSDK(jsonObject);
//    }

    public void sdkSuccess(JSONObject jsonObject) {
//        Log.e(TAG, "Success: " + jsonObject.toString());
//        webSdk.clearCache(true);
        GQPaymentSDK.successSDK(jsonObject);
//        WebActivity.this.finish();
    }

    public void sdkFailed(JSONObject jsonObject) {
//        Log.e(TAG, "Failed: " + jsonObject.toString());
//        webSdk.clearCache(true);
        GQPaymentSDK.failedSDK(jsonObject);
//        WebActivity.this.finish();
    }

    public void sdkCancel(JSONObject jsonObject) {
//        Log.e(TAG, "Cancel: " + jsonObject.toString());
//        webSdk.clearCache(true);
        GQPaymentSDK.cancelSDK(jsonObject);
        WebActivity.this.finish();
    }

    public void ADOptions(String jsonObject) {
//        Log.e(TAG, "ADOptions: " + jsonObject);

        JSONObject ADOptionsObject = null;
        try {
            ADOptionsObject = new JSONObject(jsonObject);

//            Log.e(TAG, "key: " + ADOptionsObject.getString("key"));
//            Log.e(TAG, "notes: " + ADOptionsObject.getString("notes"));
//            Log.e(TAG, "order_id: " + ADOptionsObject.getString("order_id"));
//            Log.e(TAG, "recurring: " + ADOptionsObject.getString("recurring"));

            if (ADOptionsObject.has("redirect")) {
                redirect = ADOptionsObject.getBoolean("redirect");
//                Log.e(TAG, "redirect: " + ADOptionsObject.getBoolean("redirect"));
            }

            if (ADOptionsObject.has("callback_url")) {
                callback_url = ADOptionsObject.getString("callback_url");
            }

//            Log.e(TAG, "customer_id: " + ADOptionsObject.getString("customer_id"));
//            Log.e(TAG, "callback_url: " + ADOptionsObject.getString("callback_url"));
            int recurring = 0;

            if (ADOptionsObject.has("recurring")) {
                recurring = ADOptionsObject.getInt("recurring");
            }
            String customer_id = null;

            if (ADOptionsObject.has("customer_id")) {
                customer_id = ADOptionsObject.getString("customer_id");
            }

            JSONObject prefill = null;
            if (ADOptionsObject.has("prefill")) {
                String pre = ADOptionsObject.getString("prefill");
                prefill = new JSONObject(pre);
            }

            startPayment(ADOptionsObject.getString("key"), ADOptionsObject.getString("notes"),
                    ADOptionsObject.getString("order_id"), recurring,
                    redirect, customer_id, prefill);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void startPayment(String key, String notes, String order_id, int recurring, boolean redirect, String customer_id, JSONObject prefill) {
        /**
         * Instantiate Checkout
         */
        Checkout checkout = new Checkout();
        checkout.setKeyID(key);
        /**
         * Set your logo here
         */
//        checkout.setImage(R.drawable.logo);

        /**
         * Reference to current activity
         */
        final Activity activity = this;

        /**
         * Pass your payment options to the Razorpay Checkout as a JSONObject
         */
        try {
            JSONObject options = new JSONObject();

//            options.put("name", "Merchant Name");
//            options.put("description", "Reference No. #123456");
//            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            options.put("order_id", order_id);//from response of step 3.

            JSONObject notesObject = new JSONObject(notes);

//            Log.e(TAG, "NotesObject: " + notesObject.toString());

            options.put("notes", notesObject);//from response of step 3.
            if (recurring == 1) {
                options.put("recurring", true);//from response of step 3.
            } else {
                options.put("recurring", false);//from response of step 3.
            }
            options.put("redirect", redirect);
            if (customer_id != null) {
                options.put("customer_id", customer_id);
            } else {
                String name = prefill.getString("name");
                String email = prefill.getString("email");
                String contact = prefill.getString("contact");

                options.put("prefill.name", name);
                options.put("prefill.email", email);
                options.put("prefill.contact", contact);
            }
//            options.put("theme.color", "#3399cc");
//            options.put("currency", "INR");
//            options.put("amount", "50000");//pass amount in currency subunits
//            options.put("prefill.email", "gaurav.kumar@example.com");
//            options.put("prefill.contact","9988776655");
//            JSONObject retryObj = new JSONObject();
//            retryObj.put("enabled", true);
//            retryObj.put("max_count", 2);
//            options.put("retry", retryObj);

            checkout.open(activity, options);


        } catch (Exception e) {
            Log.e(TAG, "Error in starting Razorpay Checkout", e);
        }
    }

    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {

//        Log.e(TAG, "PaymentSuccess: " + s.toString());
//        Log.e(TAG, "PaymentSuccess: " + paymentData.getData().toString());

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(paymentData.getData().toString());

//            Log.e(TAG, "JSONObject: " + jsonObject.toString());
            if (name != null && name.equals("UNIPG")) {
//                Log.e(TAG, "UNIPG Success: "+jsonObject.toString());
                webSdk.evaluateJavascript("javascript:sendPGPaymentResponse(" + jsonObject + ");", null);
            } else {
                jsonObject.put("callback_url", callback_url);
//                Log.e(TAG, "AD Success: "+jsonObject.toString());
                webSdk.evaluateJavascript("javascript:sendADPaymentResponse(" + jsonObject + ");", null);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "JSONError; "+e.getCause());
            Log.e(TAG, "JSONError; "+e.getMessage());
        }
//        Intent intent = new Intent();
//        intent.putExtra("payment_data", paymentData.getData().toString());
//        setResult(RESULT_OK, intent);
//        finish();
    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {

//        Log.e(TAG, "PaymentError: " + s.toString());
//        Log.e(TAG, "PaymentError: " + paymentData.getData().toString());
        if (name.equals("UNIPG")) {
            webSdk.evaluateJavascript("javascript:sendPGPaymentResponse('" + paymentData.getData().toString() + "');", null);
        } else {
            webSdk.evaluateJavascript("javascript:sendADPaymentResponse('" + callback_url + "," + paymentData.getData().toString() + "');", null);
        }

//        Intent intent = new Intent();
//        intent.putExtra("payment_data", paymentData.getData().toString());
//        setResult(RESULT_OK, intent);
//        finish();
    }

    public void PGOptions(String jsonObject) {
//        Log.e(TAG, "PGOptions: " + jsonObject);

//        Intent intentProceed = new Intent(WebActivity.this, PWECouponsActivity.class);
//        intentProceed.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT); // This is mandatory flag
//        intentProceed.putExtra("access_key","33b320cf06c78cbab0ea6d49467dbb29743d782fe0b280ef15c2edde41269142");// "Access key generated by the Initiate Payment API"
//        intentProceed.putExtra("pay_mode","production");//"This will either be "test" or "production""
//        pweActivityResultLauncher.launch(intentProceed);

        try {
            JSONObject pgOptionsObject = new JSONObject(jsonObject);

            name = pgOptionsObject.getString("name");
//            Log.e(TAG, "Name: " + name);
            String pgOption = pgOptionsObject.getString("pgOptions");
            JSONObject pgDetailsObject = new JSONObject(pgOption);
            if (name.equals("CASHFREE")) {
                String order_code = pgDetailsObject.getString("order_code");
                String payment_session_id = pgDetailsObject.getString("payment_session_id");

//                Log.e(TAG, "order_code: " + order_code);
//                Log.e(TAG, "payment_session_id: " + payment_session_id);

                doDropCheckoutPayment(payment_session_id, order_code);
            } else if (name.equals("UNIPG")) {
                ADOptions(pgOption);
            }else if (name.equals("EASEBUZZ")){
                String access_key = pgDetailsObject.getString("access_key");
                Log.e(TAG, "access_key: "+access_key);
                ezPGCheckout(access_key);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            CFPaymentGatewayService.getInstance().setCheckoutCallback(this);
        } catch (CFException e) {
            e.printStackTrace();
        }
    }

    public void doDropCheckoutPayment(String payment_session_id, String order_id) {
        try {
//            Log.e(TAG, "CashFeeEnvironment: " + Environment.cashFreeEnvironment());

            CFSession cfSession = new CFSession.CFSessionBuilder()
                    .setEnvironment(Environment.cashFreeEnvironment())
                    .setPaymentSessionID(payment_session_id)
                    .setOrderId(order_id)
                    .build();

            CFPaymentComponent cfPaymentComponent = new CFPaymentComponent.CFPaymentComponentBuilder()
                    .add(CFPaymentComponent.CFPaymentModes.CARD)
                    .add(CFPaymentComponent.CFPaymentModes.UPI)
                    .add(CFPaymentComponent.CFPaymentModes.NB)
                    .add(CFPaymentComponent.CFPaymentModes.WALLET)
                    .build();

            CFTheme cfTheme = new CFTheme.CFThemeBuilder()
                    .setNavigationBarBackgroundColor("#4563cb")
                    .setNavigationBarTextColor("#FFFFFF")
                    .setButtonBackgroundColor("#4563cb")
                    .setButtonTextColor("#FFFFFF")
                    .setPrimaryTextColor("#000000")
                    .setSecondaryTextColor("#000000")
                    .build();

            CFDropCheckoutPayment cfDropCheckoutPayment = new CFDropCheckoutPayment.CFDropCheckoutPaymentBuilder()
                    .setSession(cfSession)
                    .setCFUIPaymentModes(cfPaymentComponent)
                    .setCFNativeCheckoutUITheme(cfTheme)
                    .build();
            CFPaymentGatewayService gatewayService = CFPaymentGatewayService.getInstance();
            gatewayService.doPayment(WebActivity.this, cfDropCheckoutPayment);
        } catch (CFException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPaymentVerify(String s) {

        JSONObject paymentVerify = new JSONObject();
        try {
            paymentVerify.put("status", "SUCCESS");
            paymentVerify.put("order_code", s);
//            Log.e(TAG, "PaymentFailure: " + paymentVerify.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        webSdk.evaluateJavascript("javascript:sendPGPaymentResponse('" + paymentVerify + "');", null);
    }

    @Override
    public void onPaymentFailure(CFErrorResponse cfErrorResponse, String s) {
        JSONObject paymentFailure = new JSONObject();
        try {
            paymentFailure.put("order_code", s);
            paymentFailure.put("status", cfErrorResponse.getStatus());
            paymentFailure.put("message", cfErrorResponse.getMessage());
            paymentFailure.put("code", cfErrorResponse.getCode());
            paymentFailure.put("type", cfErrorResponse.getType());
//            Log.e(TAG, "PaymentFailure: " + paymentFailure.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(WebActivity.this);
// ...Irrelevant code for customizing the buttons and title
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.common_alert_dialog, null);
        dialogBuilder.setView(dialogView);
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);

        TextView txt_message = (TextView) dialogView.findViewById(R.id.txt_message);
        TextView btn_ok = (TextView) dialogView.findViewById(R.id.btn_ok);

        txt_message.setText(cfErrorResponse.getMessage());

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webSdk.evaluateJavascript("javascript:sendPGPaymentResponse('" + paymentFailure + "');", null);
                alertDialog.cancel();

            }
        });

        if (!alertDialog.isShowing()) {
            alertDialog.show();
//            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    private void ezPGCheckout(String access_key){
        Intent intentProceed = new Intent(WebActivity.this, PWECouponsActivity.class);
        intentProceed.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT); // This is mandatory flag
        intentProceed.putExtra("access_key",access_key);// "Access key generated by the Initiate Payment API"
        intentProceed.putExtra("pay_mode","production");//"This will either be "test" or "production""
        pweActivityResultLauncher.launch(intentProceed);
    }

    private void onResultPG(){
        pweActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                Intent data = result.getData();
                if (data != null) {
                    String payment_result = data.getStringExtra("result");
                    String payment_response = data.getStringExtra("payment_response");

//                    Log.e(TAG, "PaymentResult: "+payment_result);
//                    Log.e(TAG, "PaymentResponse: "+payment_response);
                    try {
                        // Handle response here

                        JSONObject jsonObject = new JSONObject(payment_response);

                        JSONObject paymentStatus = new JSONObject();
                        if (payment_result.equals("payment_successfull")) {
                            paymentStatus.put("status", "SUCCESS");
                        }else {
                            paymentStatus.put("status", "FAILED");
                        }
                        paymentStatus.put("payment_response", jsonObject);
//                        Log.e(TAG, "paymentStatus: "+paymentStatus );

                        webSdk.evaluateJavascript("javascript:sendPGPaymentResponse('" + paymentStatus + "');", null);
                    }catch (Exception e){
                        // Handle exception here
                    }
                }
            }
        });
    }

    public void getADPaymentResponse(String data) {
        Log.e(TAG, "ResponseData: " + data);

    }

    class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//            Log.e(TAG, "ChangeUrl: " + url);
            if (url.startsWith("tel:")) {
                Intent tel = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                startActivity(tel);
                return true;
            } else if (url.contains("mailto:")) {
                view.getContext().startActivity(
                        new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                return true;

            } else {

                Toast.makeText(WebActivity.this, url, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(WebActivity.this, WebActivity_Sec.class);
                intent.putExtra("urlload", url);
                startActivity(intent);

                return true;
            }
        }
    }

    class MyWebChromeClient extends WebChromeClient {


        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog,
                                      boolean isUserGesture, Message resultMsg) {

            WebView.HitTestResult result = view.getHitTestResult();
            String data = result.getExtra();
            Context context = view.getContext();
//            Log.e(TAG, "ChangeUrl: " + data);
//            Log.e(TAG, "ChangeUrl: " + resultMsg.getData());

//            Toast.makeText(WebActivity.this, data, Toast.LENGTH_SHORT).show();

//            Intent intent = new Intent(WebActivity.this, WebActivity_Sec.class);
//            intent.putExtra("urlload", data);
//            startActivity(intent);

            WebView newWebView = new WebView(WebActivity.this);
            view.addView(newWebView);
            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
            transport.setWebView(newWebView);
            resultMsg.sendToTarget();
//
            newWebView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                    Log.e(TAG, "ChangeUrl: " + url);
                    if (url.startsWith("tel:")) {
                        Intent tel = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                        startActivity(tel);
                        return true;
                    } else if (url.contains("mailto:")) {
                        view.getContext().startActivity(
                                new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                        return true;

                    } else if (url.contains("cashfree")) {
//                        Toast.makeText(WebActivity.this, url, Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(WebActivity.this, WebActivity_Sec.class);
                        intent.putExtra("urlload", url);
                        startActivity(intent);
                        return true;
                    } else {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                        browserIntent.setData(Uri.parse(url));
                        startActivity(browserIntent);
                        return true;
                    }
                }
            });

//            newWebView.setWebViewClient(new WebViewClient() {
//                @Override
//                public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                    Log.e(TAG, "ChangeUrl: " + url);
//
////                    Toast.makeText(WebActivity.this, url, Toast.LENGTH_SHORT).show();
////
////                    Intent intent = new Intent(WebActivity.this, WebActivity_Sec.class);
////                    intent.putExtra("urlload", url);
////                    startActivity(intent);
//                    return true;
//                }
//            });
            return true;
        }
    }
}