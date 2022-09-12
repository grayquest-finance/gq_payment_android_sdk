package com.grayquest.android.payment.sdk;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.razorpay.Checkout;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultListener;
import com.razorpay.PaymentResultWithDataListener;

import org.json.JSONException;
import org.json.JSONObject;

public class ADPayment extends AppCompatActivity implements PaymentResultWithDataListener {

    private static final String TAG = ADPayment.class.getSimpleName();

    String optionJson;
    JSONObject jsonObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adpayment);

        Checkout.preload(getApplicationContext());

        if (getIntent()!=null){
            optionJson = getIntent().getStringExtra("options_json");

//            Log.e(TAG, "OptionsJSon: "+optionJson);

            try {
                jsonObject = new JSONObject(optionJson);

//                Log.e(TAG, "key: "+jsonObject.getString("key"));
//                Log.e(TAG, "notes: "+jsonObject.getString("notes"));
//                Log.e(TAG, "order_id: "+jsonObject.getString("order_id"));
//                Log.e(TAG, "recurring: "+jsonObject.getString("recurring"));
//                Log.e(TAG, "redirect: "+jsonObject.getString("redirect"));
//                Log.e(TAG, "customer_id: "+jsonObject.getString("customer_id"));

                startPayment(jsonObject.getString("key"), jsonObject.getString("notes"),
                        jsonObject.getString("order_id"), jsonObject.getInt("recurring"),
                        jsonObject.getBoolean("redirect"), jsonObject.getString("customer_id"));
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

    public void startPayment(String key, String notes, String order_id, int recurring, boolean redirect, String customer_id) {
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
//            Log.e(TAG, "NotesObject: "+notesObject.toString());

            options.put("notes", notesObject);//from response of step 3.
            if (recurring==1) {
                options.put("recurring", true);//from response of step 3.
            }else {
                options.put("recurring", false);//from response of step 3.
            }
            options.put("redirect", redirect);
            options.put("customer_id", customer_id);
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

        } catch(Exception e) {
//            Log.e(TAG, "Error in starting Razorpay Checkout", e);
        }
    }

    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {
//        Log.e(TAG, "PaymentSuccess: "+s.toString());
//        Log.e(TAG, "PaymentSuccess: "+paymentData.getData().toString());
        Intent intent = new Intent();
        intent.putExtra("payment_data", paymentData.getData().toString());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {
//        Log.e(TAG, "PaymentError: "+s.toString());
//        Log.e(TAG, "PaymentError: "+paymentData.getData().toString());
        Intent intent = new Intent();
        intent.putExtra("payment_data", paymentData.getData().toString());
        setResult(RESULT_OK, intent);
        finish();
    }
}