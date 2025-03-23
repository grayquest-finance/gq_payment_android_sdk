package com.grayquest.android.payment.sdk;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class GQWebActivity_New extends AppCompatActivity {

    private static final String TAG = GQWebActivity_New.class.getSimpleName();
    WebView webLoadNew;
    String urlLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gqweb_new);

        webLoadNew = (WebView) findViewById(R.id.web_load_new);

        urlLoad = getIntent().getStringExtra("urlload");

        webLoadNew.getSettings().setJavaScriptEnabled(true);
        webLoadNew.getSettings().setSupportMultipleWindows(true);
        webLoadNew.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
//                Log.d(TAG, "URL Loadinggggg: " + url);

//                if (isNewTabRequest(url)) {
//                    // Handle new tab request
//                    Log.e(TAG, "New Web Open");
//                    Intent intent = new Intent(GQWebActivity_Sec.this, GQWebActivity_New.class);
//                    intent.putExtra("urlload", url);
//                    startActivity(intent);
////                    openInWebView2(url);
//                    return true; // Prevent loading in WebView2
//                } else {
//                    // Continue loading in WebView2
//                    view.loadUrl(url);
                    return true; // Prevent default behavior
//                }
            }
            //
            @Override
            public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
//                Log.d(TAG, "Page Started Loading: " + url);
//                if (url.contains("svc-dp.graydev.tech")){
//                    finish();
//                }
                // You can handle URL changes here as well
            }
            public void onPageFinished(WebView view, String url) {
                // do your stuff here
//                GQPaymentSDK.hideProgress();
//                Log.e(TAG, "LoadedUrl: "+url);
                if (url.contains("cf-redirect")){
                    finish();
                }
                if (url.contains(Environment.REDIRECTION_URL)){
//                    Log.e(TAG, "true");
                    finish();
                }
            }
        });

        webLoadNew.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, android.os.Message resultMsg) {
//                Log.d(TAG, "New tab request detected");

                // Handle the new tab request: Use the existing WebView2
                WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                transport.setWebView(webLoadNew); // Assign the current WebView2 for the new tab
                resultMsg.sendToTarget();

                return true; // Indicate that we handled the new tab request
            }
        });
        webLoadNew.loadUrl(urlLoad);

    }
}