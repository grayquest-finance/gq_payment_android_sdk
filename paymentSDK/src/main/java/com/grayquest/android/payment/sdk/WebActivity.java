package com.grayquest.android.payment.sdk;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.json.JSONException;
import org.json.JSONObject;

public class WebActivity extends AppCompatActivity {

    private static final String TAG = WebActivity.class.getSimpleName();
    WebView webSdk;
    String json;
    JSONObject jsonObject;
    GQPaymentSDKListener gqPaymentSDKListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        webSdk = (WebView) findViewById(R.id.web_sdk);

        if (getIntent()!=null){
            json = getIntent().getStringExtra("options");

            Log.e(TAG, "Options: "+json);

            try {
                jsonObject = new JSONObject(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.e(TAG, "JSONObject: "+jsonObject.toString());
        }

        GQPaymentSDK.showProgress();
        webSdk.getSettings().setJavaScriptEnabled(true);
        webSdk.getSettings().setDomStorageEnabled(true);
//        webSdk.setWebViewClient(new WebViewClient(){
//            public void onPageFinished(WebView view, String url) {
//                // do your stuff here
//                GQPaymentSDK.hideProgress();
//            }
//        });
//        webSdk.loadUrl("https://erp-sdk.graydev.tech/instant-eligibility?optional="+jsonObject.toString());
        webSdk.loadUrl("https://erp-sdk.graydev.tech/instant-eligibility?m=7894653261&gapik=9db4fc333d8bcf7fee98804105d9fc0c85199d77&abase=MzU0NTk4ZmQtNTc1YS00YzFmLWE2ZTMtZTA4ZmM1ZWEwNmQzOjJlYjM0OTczMjU5NGZlNzc3YmUwNzlmYjNjN2U1NTcxOTRmNTVhMTQ=&cid=23960&ccode=e4589ac0-46ee-42de-9eb4-b0094e1a0b0b&sid=Studnet_51w121&pc=734858&fedit=true&famt=&pamt=&s=erp&user=existing");
        Log.e(TAG, "URL: "+"https://erp-sdk.graydev.tech/instant-eligibility?optional="+jsonObject.toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GQPaymentSDK.closeSDK("User Close SDk");
    }
}