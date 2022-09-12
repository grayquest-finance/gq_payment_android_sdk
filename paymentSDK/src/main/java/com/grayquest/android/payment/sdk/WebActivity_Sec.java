package com.grayquest.android.payment.sdk;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebActivity_Sec extends AppCompatActivity {

    private static final String TAG = WebActivity_Sec.class.getSimpleName();
    WebView web_load;
    String urlLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_sec);

        web_load = (WebView) findViewById(R.id.web_load);

        urlLoad = getIntent().getStringExtra("urlload");

        web_load.getSettings().setJavaScriptEnabled(true);
        web_load.getSettings().setDomStorageEnabled(true);
        web_load.setWebViewClient(new WebViewClient(){
            public void onPageFinished(WebView view, String url) {
                // do your stuff here
//                GQPaymentSDK.hideProgress();
//                Log.e(TAG, "LoadedUrl: "+url);
                if (url.contains("cf-redirect")){
                    finish();
                }
            }
        });
        web_load.loadUrl(urlLoad);
    }
}