package com.grayquest.android;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.grayquest.android.payment.sdk.GQPaymentSDK;
import com.grayquest.android.payment.sdk.GQPaymentSDKListener;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements GQPaymentSDKListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    Button btn_open;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_open = (Button) findViewById(R.id.btn_open);

        btn_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSDk();
            }
        });
    }

    public void openSDk() {

        JSONObject options = new JSONObject();

        try {
            options.put("client_id", "YOUR_CLIENT_ID_HERE");
            options.put("client_secret_key", "YOUR_CLIENT_SECRET_KEY_HERE");
            options.put("gq_api_key", "YOUR_GQ_API_KEY_HERE");
            options.put("student_id", "Studnet_51w128");
            options.put("fee_editable", true);//default true
            options.put("env", "test");// eny = "local" for testing
            options.put("customer_mobile", "");// optional

            GQPaymentSDK.initiate(MainActivity.this, options);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSuccess(String message) {
        Log.e(TAG, "Message: "+message);
    }

    @Override
    public void onFailed(String message) {

    }

    @Override
    public void onCancel(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
        Log.e(TAG, "Message: "+message);
    }
}