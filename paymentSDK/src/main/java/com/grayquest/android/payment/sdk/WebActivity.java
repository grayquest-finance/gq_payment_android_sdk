package com.grayquest.android.payment.sdk;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class WebActivity extends AppCompatActivity {

    private static final String TAG = WebActivity.class.getSimpleName();
    WebView webSdk;
    String jsonOp, jsonCon;
    JSONObject optionsJSON, configJSON;

//    String url = "https://erp-sdk.graydev.tech/instant-eligibility?gapik=9db4fc333d8bcf7fee98804105d9fc0c85199d77&abase=MzU0NTk4ZmQtNTc1YS00YzFmLWE2ZTMtZTA4ZmM1ZWEwNmQzOjJlYjM0OTczMjU5NGZlNzc3YmUwNzlmYjNjN2U1NTcxOTRmNTVhMTQ=&cid=23960&ccode=e4589ac0-46ee-42de-9eb4-b0094e1a0b0b&sid=Studnet_51w121&pc=734858&fedit=true&famt=&pamt=&s=erp&user=existing";
//    String url = "https://erp-sdk.graydev.tech/instant-eligibility?m=7794653261&gapik=9db4fc333d8bcf7fee98804105d9fc0c85199d77&abase=MzU0NTk4ZmQtNTc1YS00YzFmLWE2ZTMtZTA4ZmM1ZWEwNmQzOjJlYjM0OTczMjU5NGZlNzc3YmUwNzlmYjNjN2U1NTcxOTRmNTVhMTQ=&cid=23960&ccode=e4589ac0-46ee-42de-9eb4-b0094e1a0b0b&sid=Studnet_51w121&pc=734858&fedit=true&famt=&pamt=&s=erp&user=existing";
    String loadURL;
    String clientId, secretKey;
    String gapik, abase, sid, m, famt, pamt, env, ccode, pc, s="asdk", user;
    int cid;
    boolean fedit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        webSdk = (WebView) findViewById(R.id.web_sdk);

        if (getIntent()!=null){
            try {
                if (getIntent().getStringExtra("options")!=null){
                    jsonOp = getIntent().getStringExtra("options");
                    optionsJSON = new JSONObject(jsonOp);
                    Log.e(TAG, "OptionsJSON: "+optionsJSON.toString());
                }
                jsonCon = getIntent().getStringExtra("config");
                configJSON = new JSONObject(jsonCon);
                Log.e(TAG, "ConfigJSON: "+configJSON.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            cid = getIntent().getIntExtra("customer_id", 0);
            ccode = getIntent().getStringExtra("customer_code");
            user = getIntent().getStringExtra("user");

            Log.e(TAG, "Options: "+jsonOp);
            Log.e(TAG, "Config: "+jsonCon);
            Log.e(TAG, "CustomerId: "+cid);
            Log.e(TAG, "CustomerCode: "+ccode);

            try {
                clientId = configJSON.getString("client_id");
                secretKey = configJSON.getString("client_secret_key");
                gapik = configJSON.getString("gq_api_key");
                sid = configJSON.getString("student_id");
                m = configJSON.getString("customer_number");
                famt = configJSON.getString("fee_amount");
                pamt = configJSON.getString("payable_amount");
                env = configJSON.getString("env");
                fedit = configJSON.getBoolean("fee_editable");
                pc = configJSON.getString("theme_color");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String base = clientId+":"+secretKey;

            byte[] data = new byte[0];
            data = base.getBytes(StandardCharsets.UTF_8);
            abase = Base64.encodeToString(data, Base64.DEFAULT);

            Log.e(TAG, "gapik: "+gapik);
            Log.e(TAG, "abase: "+abase);
            Log.e(TAG, "sid: "+sid);
            Log.e(TAG, "m: "+m);
            Log.e(TAG, "famt: "+famt);
            Log.e(TAG, "pamt: "+pamt);
            Log.e(TAG, "env: "+env);
            Log.e(TAG, "fedit: "+fedit);
            Log.e(TAG, "cid: "+cid);
            Log.e(TAG, "ccode: "+ccode);
            Log.e(TAG, "pc: "+pc);
            Log.e(TAG, "s: "+s);
            Log.e(TAG, "user: "+user);
            Log.e(TAG, "optional: "+optionsJSON.toString());

            loadURL = API_Client.WEB_LOAD_URL+"instant-eligibility?gapik="+gapik+"&abase="+abase+"&sid="+sid+"&m="+m+"&famt="+famt+"&pamt="+pamt+"&env="+env+"&fedit="+fedit+"&cid="+cid+"&ccode="+ccode+"&pc="+pc+"&s="+s+"&user="+user+"&optional="+optionsJSON.toString();
            Log.e(TAG, "LoadURL: "+loadURL);
        }

//        GQPaymentSDK.showProgress();
        webSdk.getSettings().setJavaScriptEnabled(true);
        webSdk.getSettings().setDomStorageEnabled(true);
        webSdk.setWebViewClient(new WebViewClient(){
            public void onPageFinished(WebView view, String url) {
                // do your stuff here
//                GQPaymentSDK.hideProgress();
                Log.e(TAG, "LoadedUrl: "+url);
            }
        });
        webSdk.addJavascriptInterface(new GQPaymentSDKInterface(WebActivity.this), "Gqsdk");
//        webSdk.loadUrl("https://erp-sdk.graydev.tech/instant-eligibility?optional="+jsonObject.toString());
        webSdk.loadUrl(loadURL);
//        Log.e(TAG, "URL: "+"https://erp-sdk.graydev.tech/instant-eligibility?optional="+jsonObject.toString());
        Log.e(TAG, "URL: "+loadURL);
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

    public void sdkSuccess(JSONObject jsonObject){
        Log.e(TAG, "Success: "+jsonObject.toString());
//        webSdk.clearCache(true);
        GQPaymentSDK.successSDK(jsonObject);
        WebActivity.this.finish();
    }

    public void sdkFailed(JSONObject jsonObject){
        Log.e(TAG, "Failed: "+jsonObject.toString());
//        webSdk.clearCache(true);
        GQPaymentSDK.failedSDK(jsonObject);
        WebActivity.this.finish();
    }

    public void sdkCancel(JSONObject jsonObject){
        Log.e(TAG, "Cancel: "+jsonObject.toString());
//        webSdk.clearCache(true);
        GQPaymentSDK.cancelSDK(jsonObject);
        WebActivity.this.finish();
    }
}