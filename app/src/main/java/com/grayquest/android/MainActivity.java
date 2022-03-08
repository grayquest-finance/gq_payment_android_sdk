package com.grayquest.android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.grayquest.android.payment.sdk.GQPaymentSDK;
import com.grayquest.android.payment.sdk.GQPaymentSDKListener;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements GQPaymentSDKListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    EditText edtClientId, edtSecretKey, edtGQApi, edtStudentID, edtCustomerNumber, edtFeeAmount, edtPayableAmount, edtTheme, edtOptional;
    SwitchCompat edtFeeEditable;
    RadioButton radioTest, radioLive;
    String clientId, secretKey, GQApi, studentId, env, customerNumber, feeAmount, payableAmount, themeColour, optional = "";
    TextView btnOptionPrefill, btnRemovePrefill;
    boolean feeEditable;

    Button btn_open, btnPrefill;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtClientId = (EditText) findViewById(R.id.edt_client_id);
        edtSecretKey = (EditText) findViewById(R.id.edt_client_secret);
        edtGQApi = (EditText) findViewById(R.id.edt_gq_api);
        edtStudentID = (EditText) findViewById(R.id.edt_student_id);
        edtFeeEditable = (SwitchCompat) findViewById(R.id.edt_fee_editable);
        radioTest = (RadioButton) findViewById(R.id.rd_test);
        radioLive = (RadioButton) findViewById(R.id.rd_live);
        edtCustomerNumber = (EditText) findViewById(R.id.edt_customer_number);
        edtFeeAmount = (EditText) findViewById(R.id.edt_fee_amount);
        edtPayableAmount = (EditText) findViewById(R.id.edt_payable_amount);
        edtTheme = (EditText) findViewById(R.id.edt_theme);
        edtOptional = (EditText) findViewById(R.id.edt_optional);

        btnOptionPrefill = (TextView) findViewById(R.id.btn_option_prefill);
        btnRemovePrefill = (TextView) findViewById(R.id.btn_remove_prefill);

        edtFeeEditable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    feeEditable = true;
                } else {
                    feeEditable = false;
                }
            }
        });

        radioTest.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    env = "test";
                    radioTest.setChecked(true);
                    radioLive.setChecked(false);
                }
            }
        });
        radioLive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    env = "live";
                    radioTest.setChecked(false);
                    radioLive.setChecked(true);
                }
            }
        });

        btn_open = (Button) findViewById(R.id.btn_open);
        btnPrefill = (Button) findViewById(R.id.btn_prefill);

        btnOptionPrefill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnOptionPrefill.setVisibility(View.GONE);
                btnRemovePrefill.setVisibility(View.VISIBLE);
                JSONObject prefill = null;
                try {
                    if (!optional.equals("")) {
                        prefill = new JSONObject(optional);
                    } else {
                        prefill = new JSONObject();
                        JSONObject student = new JSONObject();
                        student.put("student_first_name", "");
                        student.put("student_last_name", "");
                        student.put("student_type", "");

                        JSONObject customer = new JSONObject();
                        customer.put("customer_first_name", "");
                        customer.put("customer_last_name", "");
                        customer.put("customer_dob", "");
                        customer.put("customer_gender", "");
                        customer.put("customer_email", "");
                        customer.put("customer_marital_status", "");

                        JSONObject kyc = new JSONObject();
                        kyc.put("pan_number", "");

                        JSONObject residential = new JSONObject();
                        residential.put("residential_addr_line_1", "");
                        residential.put("residential_addr_line_2", "");
                        residential.put("residential_type", "");
                        residential.put("residential_period", "");
                        residential.put("residential_pincode", "");
                        residential.put("residential_city", "");
                        residential.put("residential_state", "");

                        JSONObject employment = new JSONObject();
                        employment.put("income_type", "");
                        employment.put("employer_name", "");
                        employment.put("work_experience", "");
                        employment.put("net_monthly_salary", "");
                        employment.put("income_type", "");
                        employment.put("business_name", "");
                        employment.put("business_turnover", "");
                        employment.put("business_annual_income", "");
                        employment.put("business_category", "");
                        employment.put("business_type", "");
                        employment.put("business_description", "");
                        employment.put("business_employee_count", "");
                        employment.put("years_of_current_business", "");
                        employment.put("same_as_residence_address", "");
                        employment.put("addr_line_1", "");
                        employment.put("addr_line_2", "");
                        employment.put("city", "");
                        employment.put("state", "");

                        JSONObject note = new JSONObject();
                        note.put("notes", "");

                        prefill.put("student_details", student);
                        prefill.put("customer_details", customer);
                        prefill.put("kyc_details", kyc);
                        prefill.put("residential_details", residential);
                        prefill.put("employment_details", employment);
                        prefill.put("notes", note);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (prefill != null) {
                    edtOptional.setText(prefill.toString());
                }
            }
        });

        btn_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clientId = edtClientId.getText().toString();
                secretKey = edtSecretKey.getText().toString();
                GQApi = edtGQApi.getText().toString();
                studentId = edtStudentID.getText().toString();
                customerNumber = edtCustomerNumber.getText().toString();
                feeAmount = edtFeeAmount.getText().toString();
                payableAmount = edtPayableAmount.getText().toString();
                themeColour = edtTheme.getText().toString();
                optional = edtOptional.getText().toString();
                Log.e(TAG, "Optional: " + optional);
                openSDk();
            }
        });

        btnPrefill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtClientId.setText("YOUR_CLIENT_ID_HERE");
                edtSecretKey.setText("YOUR_CLIENT_SECRET_KEY_HERE");
                edtGQApi.setText("YOUR_GQ_API_KEY_HERE");
//                edtStudentID.setText("Studnet_51w128");
//                edtFeeEditable.setChecked(true);
//                radioTest.setChecked(true);
//                radioLive.setChecked(false);
            }
        });

        btnRemovePrefill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnRemovePrefill.setVisibility(View.GONE);
                btnOptionPrefill.setVisibility(View.VISIBLE);
                edtOptional.setText("");
                optional = "";
            }
        });
    }

    public void openSDk() {

//        JSONObject config = new JSONObject();
//
//        try {
//            config.put("client_id", "YOUR_CLIENT_ID_HERE");
//            config.put("client_secret_key", "YOUR_CLIENT_SECRET_KEY_HERE");
//            config.put("gq_api_key", "YOUR_GQ_API_KEY_HERE");
//            config.put("student_id", "Studnet_51w128");
//            config.put("fee_editable", true);//default true
//            config.put("env", "test");// eny = "test" for testing
//            config.put("customer_number", "");// eny = "test" for testing
//            config.put("fee_amount", "9999999999");// eny = "test" for testing
//            config.put("payable_amount", "9999999999");// eny = "test" for testing
//            config.put("theme_color", "");// eny = "test" for testing
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        JSONObject config = new JSONObject();

        try {
            config.put("client_id", clientId);
            config.put("client_secret_key", secretKey);
            config.put("gq_api_key", GQApi);
            config.put("student_id", studentId);
            config.put("fee_editable", feeEditable);//default true
            config.put("env", env);// eny = "test" for testing
            config.put("customer_number", customerNumber);// eny = "test" for testing
            config.put("fee_amount", feeAmount);// eny = "test" for testing
            config.put("payable_amount", payableAmount);// eny = "test" for testing
            config.put("theme_color", themeColour);// eny = "test" for testing

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject prefill = null;
        try {
            if (!optional.equals("")) {
                prefill = new JSONObject(optional);
            } else {
//                prefill = new JSONObject();
//                JSONObject student = new JSONObject();
//                student.put("student_first_name", "");
//                student.put("student_last_name", "");
//                student.put("student_type", "");
//
//                JSONObject customer = new JSONObject();
//                customer.put("customer_first_name", "");
//                customer.put("customer_last_name", "");
//                customer.put("customer_dob", "");
//                customer.put("customer_gender", "");
//                customer.put("customer_email", "");
//                customer.put("customer_marital_status", "");
//
//                JSONObject kyc = new JSONObject();
//                kyc.put("pan_number", "");
//
//                JSONObject residential = new JSONObject();
//                residential.put("residential_addr_line_1", "");
//                residential.put("residential_addr_line_2", "");
//                residential.put("residential_type", "");
//                residential.put("residential_period", "");
//                residential.put("residential_pincode", "");
//                residential.put("residential_city", "");
//                residential.put("residential_state", "");
//
//                JSONObject employment = new JSONObject();
//                employment.put("income_type", "");
//                employment.put("employer_name", "");
//                employment.put("work_experience", "");
//                employment.put("net_monthly_salary", "");
//                employment.put("income_type", "");
//                employment.put("business_name", "");
//                employment.put("business_turnover", "");
//                employment.put("business_annual_income", "");
//                employment.put("business_category", "");
//                employment.put("business_type", "");
//                employment.put("business_description", "");
//                employment.put("business_employee_count", "");
//                employment.put("years_of_current_business", "");
//                employment.put("same_as_residence_address", "");
//                employment.put("addr_line_1", "");
//                employment.put("addr_line_2", "");
//                employment.put("city", "");
//                employment.put("state", "");
//
//                JSONObject note = new JSONObject();
//                note.put("notes", "");
//
//                prefill.put("student_details", student);
//                prefill.put("customer_details", customer);
//                prefill.put("kyc_details", kyc);
//                prefill.put("residential_details", residential);
//                prefill.put("employment_details", employment);
//                prefill.put("notes", note);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        JSONObject prefill = new JSONObject();
//
//        try {
//
//            JSONObject student = new JSONObject();
//            student.put("student_first_name", "sdsfsf");
//            student.put("student_last_name", "sdsfsf");
//            student.put("student_type", "sdsfsf");
//
//            JSONObject customer = new JSONObject();
//            customer.put("customer_first_name", "sdsfsf");
//            customer.put("customer_last_name", "sdsfsf");
//            customer.put("customer_dob", "sdsfsf");
//            customer.put("customer_gender", "sdsfsf");
//            customer.put("customer_email", "sdsfsf");
//            customer.put("customer_marital_status", "sdsfsf");
//
//            JSONObject kyc = new JSONObject();
//            kyc.put("pan_number", "AAAPA0000A");
//
//            JSONObject residential = new JSONObject();
//            residential.put("residential_addr_line_1", "residential_addr_line_1");
//            residential.put("residential_addr_line_2", "residential_addr_line_2");
//            residential.put("residential_type", "residential_type");
//            residential.put("residential_period", "residential_period");
//            residential.put("residential_pincode", "residential_pincode");
//            residential.put("residential_city", "residential_city");
//            residential.put("residential_state", "residential_state");
//
//            JSONObject employment = new JSONObject();
//            employment.put("income_type", "income_type");
//            employment.put("employer_name", "employer_name");
//            employment.put("work_experience", "work_experience");
//            employment.put("net_monthly_salary", "net_monthly_salary");
//            employment.put("income_type", "income_type");
//            employment.put("business_name", "business_name");
//            employment.put("business_turnover", "business_turnover");
//            employment.put("business_annual_income", "business_annual_income");
//            employment.put("business_category", "business_category");
//            employment.put("business_type", "business_type");
//            employment.put("business_description", "business_description");
//            employment.put("business_employee_count", "business_employee_count");
//            employment.put("years_of_current_business", "years_of_current_business");
//            employment.put("same_as_residence_address", "same_as_residence_address");
//            employment.put("addr_line_1", "addr_line_1");
//            employment.put("addr_line_2", "addr_line_2");
//            employment.put("city", "city");
//            employment.put("state", "state");
//
//            JSONObject note = new JSONObject();
//            note.put("notes", "notes");
//
//            prefill.put("student_details", student);
//            prefill.put("customer_details", customer);
//            prefill.put("kyc_details", kyc);
//            prefill.put("residential_details", residential);
//            prefill.put("employment_details", employment);
//            prefill.put("notes", note);
//
//            Log.e(TAG, "ConfigObject: "+config.toString());
//            Log.e(TAG, "PrefillObject: " + prefill.toString());
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        GQPaymentSDK.initiate(MainActivity.this, config, prefill);
    }

    @Override
    public void onSuccess(JSONObject message) {
        Log.e(TAG, "Message: " + message);
    }

    @Override
    public void onFailed(JSONObject message) {
        Log.e(TAG, "Message: " + message);
        CommonDialog(message.toString());
    }

    @Override
    public void onCancel(JSONObject message) {
        Toast.makeText(MainActivity.this, message.toString(), Toast.LENGTH_SHORT).show();
        Log.e(TAG, "Message: " + message.toString());
    }

    private void CommonDialog(String message) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
// ...Irrelevant code for customizing the buttons and title
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.common_alert_dialog, null);
        dialogBuilder.setView(dialogView);
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);

        TextView txt_message = (TextView) dialogView.findViewById(R.id.txt_message);
        TextView btn_ok = (TextView) dialogView.findViewById(R.id.btn_ok);

        txt_message.setText(message);

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();

            }
        });

        if (!alertDialog.isShowing()) {
            alertDialog.show();
//            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }
}