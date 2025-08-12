package com.grayquest.android.payment.sdk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class GQWebActivity_Sec extends AppCompatActivity {

    private static final String TAG = GQWebActivity_Sec.class.getSimpleName();
    WebView web_load, webview2;
    String urlLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_sec);

        web_load = (WebView) findViewById(R.id.web_load);
        webview2 = (WebView) findViewById(R.id.webview2);

        setupWebView2();

        urlLoad = getIntent().getStringExtra("urlload");

        web_load.getSettings().setJavaScriptEnabled(true);
        web_load.getSettings().setSupportMultipleWindows(true);
        web_load.getSettings().setDomStorageEnabled(true);
        web_load.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                Log.d(TAG, "URL Loadinggggg: " + url);

                if (isNewTabRequest(url)) {
                    // Handle new tab request
//                    Log.e(TAG, "New Web Open");
//                    Intent intent = new Intent(GQWebActivity_Sec.this, GQWebActivity_New.class);
//                    intent.putExtra("urlload", url);
//                    startActivity(intent);
                    openInWebView2(url);
                    return true; // Prevent loading in WebView2
                }else if (url.startsWith("upi://") || url.startsWith("tez://upi/")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                    return true; // Prevent WebView from loading this URL
                }else if (url.startsWith("phonepe://")){
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                    return true;
                }else if (url.startsWith("paytmmp://")){
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                    return true;
                }else if (url.startsWith("credpay://")){
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                    return true;
                }
                else {
                    // Continue loading in WebView2
                    view.loadUrl(url);
                    return true; // Prevent default behavior
                }
            }
//
            @Override
            public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.d(TAG, "Page Started Loading: " + url);
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

        web_load.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, android.os.Message resultMsg) {
//                Log.d(TAG, "New tab request detected");

                // Handle the new tab request: Use the existing WebView2
                WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                transport.setWebView(web_load); // Assign the current WebView2 for the new tab
                resultMsg.sendToTarget();

                return true; // Indicate that we handled the new tab request
            }
        });
        web_load.loadUrl(urlLoad);
    }

    private void setupWebView2() {
        webview2.getSettings().setJavaScriptEnabled(true);
        webview2.getSettings().setSupportMultipleWindows(true);
//        webview2.setWebViewClient(new CustomWebViewClient());
        webview2.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                Log.d(TAG, "URL Loadinggggg: " + url);

                // Detect UPI URL and open in UPI app
                if (url.startsWith("upi://") || url.startsWith("tez://upi/")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                    return true; // Prevent WebView from loading this URL
                } else {
                    // Continue loading in WebView2
                    view.loadUrl(url);
                    return true; // Prevent default behavior
                }
            }
//
            @Override
            public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                Log.d(TAG, "Page Started Loading: " + url);
//                if (url.contains("svc-dp.graydev.tech")){
//                    finish();
//                }
                // You can handle URL changes here as well
            }
            public void onPageFinished(WebView view, String url) {
                // do your stuff here
//                GQPaymentSDK.hideProgress();
                Log.e(TAG, "LoadedUrl: "+url);
                if (url.contains("cf-redirect")){
                    finish();
                }
                if (url.contains(Environment.REDIRECTION_URL)){
//                    Log.e(TAG, "true");
                    finish();
                }
            }
        });
    }

    // Custom WebViewClient to handle target="_blank" or redirections
    private class CustomWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String url = request.getUrl().toString();
//            Log.d(TAG, "URL LoadingggggNEW: " + url);

            webview2.setVisibility(View.GONE);

            if(url.contains(Environment.REDIRECTION_URL)){
                finish();
            }

//            if (isNewTabRequest(url)) {
//                // Handle new tab request
//                openInWebView2(url);
//                return true; // Prevent loading in WebView2
//            } else {
//                // Continue loading in WebView2
//                view.loadUrl(url);
                return true; // Prevent default behavior
//            }
        }
    }

    private void openInWebView2(String url) {
        webview2.setVisibility(View.VISIBLE);
        webview2.loadUrl(url);
    }

    // Check if URL is a new tab request (can be extended based on site behavior)
    private boolean isNewTabRequest(String url) {
        // Logic to detect new tab requests
        // E.g., if URLs contain specific patterns indicating new tabs
        return url.startsWith("https://"); // Replace with actual logic if needed
    }

}