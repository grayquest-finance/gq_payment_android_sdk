package com.grayquest.android;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.grayquest.android.payment.sdk.GQPaymentSDK;
import com.grayquest.android.payment.sdk.GQPaymentSDKListener;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements GQPaymentSDKListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    EditText edtClientId, edtSecretKey, edtGQApi, edtStudentID, edtCustomerNumber,
            edtTheme, edtOptional, edtPPSlug, edtPPCard, edtLogoUrl, edtFeeHelper, edtEnvironment,
    edtPPConfig, edtFeeHeaderObj, edtCustomisation, edtReferenceID, edtEmiPlanID, edtUdfDetails,
    edtPaymentModes, edtFeeHeaderSplit;
    SwitchCompat switchPP, switchFeeHeader, switchCustomisation;
    RadioButton radioTest, radioLive, radioStage, radioPreProd;
    String clientId, secretKey, GQApi, studentId, env, customerNumber, themeColour,
            logo_url, fee_helper_text, optional = "", ppSlug, ppCard, referenceID, emiPlanID, udfDetails,
            paymentModes, feeHeadersSplit;
    TextView btnOptionPrefill, btnRemovePrefill;
    boolean isPPConfig, isFeeHeaders;

    LinearLayout llPPConfig, llFeeHeaders, llFeeLayout, llCustomisation;
    LinearLayout btnAddFee;

    Button btn_open, btnPrefill;

    JSONObject ppConfig;
    JSONObject feeHeaderObject;
    JSONObject udfDetailsObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtClientId = (EditText) findViewById(R.id.edt_client_id);
        edtSecretKey = (EditText) findViewById(R.id.edt_client_secret);
        edtGQApi = (EditText) findViewById(R.id.edt_gq_api);
        edtStudentID = (EditText) findViewById(R.id.edt_student_id);
        edtEnvironment = (EditText) findViewById(R.id.edt_environment);

        radioTest = (RadioButton) findViewById(R.id.rd_test);
        radioLive = (RadioButton) findViewById(R.id.rd_live);
        radioStage = (RadioButton) findViewById(R.id.rd_stage);
        radioPreProd = (RadioButton) findViewById(R.id.rd_preprod);
        edtCustomerNumber = (EditText) findViewById(R.id.edt_customer_number);
        edtEmiPlanID = (EditText) findViewById(R.id.edt_emi_plan);
        edtUdfDetails = (EditText) findViewById(R.id.edt_udf_details);

        edtPPConfig = (EditText)findViewById(R.id.edt_pp_config);
        edtFeeHeaderObj = (EditText)findViewById(R.id.edt_fee_header_obj);
        edtCustomisation = (EditText)findViewById(R.id.edt_customisation);

        switchCustomisation = (SwitchCompat) findViewById(R.id.switch_customisation);
        llCustomisation = (LinearLayout) findViewById(R.id.ll_customisation);
        edtTheme = (EditText) findViewById(R.id.edt_theme);
        edtLogoUrl = (EditText) findViewById(R.id.edt_logo_url);
        edtFeeHelper = (EditText) findViewById(R.id.edt_fee_helper);

        edtReferenceID = (EditText) findViewById(R.id.edt_reference_id);
        edtPaymentModes = (EditText) findViewById(R.id.edt_payment_modes);
        edtFeeHeaderSplit = (EditText) findViewById(R.id.edt_fee_headers_split);

        edtOptional = (EditText) findViewById(R.id.edt_optional);

        btnOptionPrefill = (TextView) findViewById(R.id.btn_option_prefill);
        btnRemovePrefill = (TextView) findViewById(R.id.btn_remove_prefill);

        switchPP = (SwitchCompat) findViewById(R.id.switch_pp);
        llPPConfig = (LinearLayout) findViewById(R.id.ll_pp_config);
        edtPPSlug = (EditText) findViewById(R.id.edt_pp_slug);
        edtPPCard = (EditText) findViewById(R.id.edt_pp_card);

        switchFeeHeader = (SwitchCompat) findViewById(R.id.switch_fee_header);
        llFeeHeaders = (LinearLayout) findViewById(R.id.ll_fee_headers);
        llFeeLayout = (LinearLayout) findViewById(R.id.ll_fee_layout);
        btnAddFee = (LinearLayout) findViewById(R.id.btn_add_fee);

        switchPP.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    llPPConfig.setVisibility(View.VISIBLE);
                    isPPConfig = true;
                } else {
                    llPPConfig.setVisibility(View.GONE);
                    isPPConfig = false;
                    ppConfig = null;
                    edtPPSlug.setText("");
                    edtPPCard.setText("");
                }
            }
        });

        switchFeeHeader.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    llFeeHeaders.setVisibility(View.VISIBLE);
                    addFeeHeadersLayout();
                    isFeeHeaders = true;
                } else {
                    llFeeHeaders.setVisibility(View.GONE);
                    llFeeLayout.removeAllViews();
                    feeHeaderObject = null;
                    isFeeHeaders = false;
                }
            }
        });

        switchCustomisation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    llCustomisation.setVisibility(View.VISIBLE);
                } else {
                    llCustomisation.setVisibility(View.GONE);
                    edtTheme.setText("");
                    edtFeeHelper.setText("");
                    edtLogoUrl.setText("");
                }
            }
        });

        btnAddFee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFeeHeadersLayout();
            }
        });

        radioTest.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    env = "test";
                    radioTest.setChecked(true);
                    radioLive.setChecked(false);
                    radioStage.setChecked(false);
                    radioPreProd.setChecked(false);
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
                    radioStage.setChecked(false);
                    radioPreProd.setChecked(false);
                }
            }
        });
        radioStage.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    env = "stage";
                    radioTest.setChecked(false);
                    radioLive.setChecked(false);
                    radioStage.setChecked(true);
                    radioPreProd.setChecked(false);
                }
            }
        });
        radioPreProd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    env = "preprod";
                    radioTest.setChecked(false);
                    radioLive.setChecked(false);
                    radioStage.setChecked(false);
                    radioPreProd.setChecked(true);
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
//                        JSONObject student = new JSONObject();
//                        student.put("student_first_name", "");
//                        student.put("student_last_name", "");
//                        student.put("student_type", "");
//
//                        JSONObject customer = new JSONObject();
//                        customer.put("customer_first_name", "");
//                        customer.put("customer_last_name", "");
//                        customer.put("customer_dob", "");
//                        customer.put("customer_gender", "");
//                        customer.put("customer_email", "");
//                        customer.put("customer_marital_status", "");
//
//                        JSONObject kyc = new JSONObject();
//                        kyc.put("pan_number", "");
//
//                        JSONObject residential = new JSONObject();
//                        residential.put("residential_addr_line_1", "");
//                        residential.put("residential_addr_line_2", "");
//                        residential.put("residential_type", "");
//                        residential.put("residential_period", "");
//                        residential.put("residential_pincode", "");
//                        residential.put("residential_city", "");
//                        residential.put("residential_state", "");
//
//                        JSONObject employment = new JSONObject();
//                        employment.put("income_type", "");
//                        employment.put("employer_name", "");
//                        employment.put("work_experience", "");
//                        employment.put("net_monthly_salary", "");
//                        employment.put("income_type", "");
//                        employment.put("business_name", "");
//                        employment.put("business_turnover", "");
//                        employment.put("business_annual_income", "");
//                        employment.put("business_category", "");
//                        employment.put("business_type", "");
//                        employment.put("business_description", "");
//                        employment.put("business_employee_count", "");
//                        employment.put("years_of_current_business", "");
//                        employment.put("same_as_residence_address", "");
//                        employment.put("addr_line_1", "");
//                        employment.put("addr_line_2", "");
//                        employment.put("city", "");
//                        employment.put("state", "");

                        JSONObject note = new JSONObject();
                        note.put("test_notes1", "test_notes1_value");
                        note.put("test_notes2", "test_notes2_value");
                        note.put("test_notes3", "test_notes3_value");
                        note.put("test_notes4", "test_notes4_value");

//                        prefill.put("student_details", student);
//                        prefill.put("customer_details", customer);
//                        prefill.put("kyc_details", kyc);
//                        prefill.put("residential_details", residential);
//                        prefill.put("employment_details", employment);
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
                referenceID = edtReferenceID.getText().toString();
                customerNumber = edtCustomerNumber.getText().toString();
                themeColour = edtTheme.getText().toString();
                logo_url = edtLogoUrl.getText().toString();
                fee_helper_text = edtFeeHelper.getText().toString();
                emiPlanID = edtEmiPlanID.getText().toString();
                udfDetails = edtUdfDetails.getText().toString();
                optional = edtOptional.getText().toString();
                Log.e(TAG, "Optional: " + optional);
                openSDk();
            }
        });

        btnPrefill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Test Credentials
                /*edtClientId.setText("YOUR_CLIENT_ID_HERE");
                edtSecretKey.setText("YOUR_CLIENT_SECRET_KEY_HERE");
                edtGQApi.setText("YOUR_GQ_API_KEY_HERE");*/

                //Test Credentials
                /*edtClientId.setText("YOUR_CLIENT_ID_HERE");
                edtSecretKey.setText("YOUR_CLIENT_SECRET_KEY_HERE");
                edtGQApi.setText("YOUR_GQ_API_KEY_HERE");*/

                /*edtClientId.setText("YOUR_CLIENT_ID_HERE");
                edtSecretKey.setText("YOUR_CLIENT_SECRET_KEY_HERE");
                edtGQApi.setText("YOUR_GQ_API_KEY_HERE");*/

                // CashFree - Stage
                /*edtClientId.setText("YOUR_CLIENT_ID_HERE");
                edtSecretKey.setText("YOUR_CLIENT_SECRET_KEY_HERE");
                edtGQApi.setText("YOUR_GQ_API_KEY_HERE");*/

                // EaseBuzz - Stage
                /*edtClientId.setText("YOUR_CLIENT_ID_HERE");
                edtSecretKey.setText("YOUR_CLIENT_SECRET_KEY_HERE");
                edtGQApi.setText("YOUR_GQ_API_KEY_HERE");*/

                edtClientId.setText("YOUR_CLIENT_ID_HERE");
                edtSecretKey.setText("YOUR_CLIENT_SECRET_KEY_HERE");
                edtGQApi.setText("YOUR_GQ_API_KEY_HERE");

                // CashFree - UAT
                /*edtClientId.setText("YOUR_CLIENT_ID_HERE");
                edtSecretKey.setText("YOUR_CLIENT_SECRET_KEY_HERE");
                edtGQApi.setText("YOUR_GQ_API_KEY_HERE");*/

                //UNIPG - UAT
                /*edtClientId.setText("YOUR_CLIENT_ID_HERE");
                edtSecretKey.setText("YOUR_CLIENT_SECRET_KEY_HERE");
                edtGQApi.setText("YOUR_GQ_API_KEY_HERE");*/

                //Live Credentials
                /*edtClientId.setText("YOUR_CLIENT_ID_HERE");
                edtSecretKey.setText("YOUR_CLIENT_SECRET_KEY_HERE");
                edtGQApi.setText("YOUR_GQ_API_KEY_HERE");*/

                edtStudentID.setText("std_1210001");
//                radioTest.setChecked(true);
//                radioLive.setChecked(false);
                edtCustomerNumber.setText("8425900001");
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

        viewHandler();

        handleDeepLink(getIntent());
    }

    private void addFeeHeadersLayout() {
        View layout = LayoutInflater.from(MainActivity.this).inflate(R.layout.fee_headers_layout, llFeeLayout, false);

        LinearLayout btnRemove = (LinearLayout) layout.findViewById(R.id.btn_remove);
        EditText edtLabel = (EditText) layout.findViewById(R.id.edt_label);

        if (llFeeLayout.getChildCount() > 0) {
            btnRemove.setVisibility(View.VISIBLE);
        } else {
            btnRemove.setVisibility(View.GONE);
        }

        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "ClickedPosition: " + layout.getTag());
                deleteFeeLayout(layout.getTag());
            }
        });

        layout.setTag(llFeeLayout.getChildCount() + 1);
        Log.e(TAG, "AddViewID: " + layout.getTag());
        llFeeLayout.addView(layout);
    }

    private void deleteFeeLayout(Object tag) {
        for (int i = 0; i < llFeeLayout.getChildCount(); i++) {
            if (tag == llFeeLayout.getChildAt(i).getTag()) {
                Log.e(TAG, "ClickedPos: " + i);
                Log.e(TAG, "POss: " + llFeeLayout.getChildAt(i).getTag());
                llFeeLayout.removeViewAt(i);
                llFeeLayout.invalidate();
            }
        }
    }

    private void getFeeLayoutValues() {
        feeHeaderObject = new JSONObject();
        if (llFeeLayout.getChildCount() > 0) {
            for (int i = 0; i < llFeeLayout.getChildCount(); i++) {
                EditText edtFeeLabel = (EditText) llFeeLayout.getChildAt(i).findViewById(R.id.edt_label);
                EditText edtFeeValue = (EditText) llFeeLayout.getChildAt(i).findViewById(R.id.edt_value);

                try {
                    feeHeaderObject.put(edtFeeLabel.getText().toString(), edtFeeValue.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void openSDk() {
        JSONObject config = new JSONObject();

        try {
            JSONObject auth = new JSONObject();

            auth.put("client_id", clientId);
            auth.put("client_secret_key", secretKey);
            auth.put("gq_api_key", GQApi);

            JSONObject customisation = new JSONObject();
            if (fee_helper_text != null) {
                customisation.put("fee_helper_text", fee_helper_text);
            }
            if (logo_url != null) {
                customisation.put("logo_url", logo_url);
            }
            if (themeColour != null) {
                customisation.put("theme_color", themeColour);
            }

            if(!edtCustomisation.getText().toString().isEmpty()){
                customisation = new JSONObject(edtCustomisation.getText().toString());
                config.put("customization", customisation);
            }

            if (isPPConfig) {
                createPPConfig();
                config.put("pp_config", ppConfig);
            }

            if(!edtPPConfig.getText().toString().isEmpty()) {
                ppConfig = new JSONObject((edtPPConfig.getText().toString()));
                config.put("pp_config", ppConfig);
            }

            if (isFeeHeaders) {
                getFeeLayoutValues();
                config.put("fee_headers", feeHeaderObject);
            }

            if(!edtFeeHeaderObj.getText().toString().isEmpty()){
                feeHeaderObject = new JSONObject(edtFeeHeaderObj.getText().toString());
                config.put("fee_headers", feeHeaderObject);
            }

            if(!edtUdfDetails.getText().toString().isEmpty()){
                udfDetailsObject = new JSONObject(edtUdfDetails.getText().toString());
                config.put("udf_details", udfDetails);
            }

            if (!edtReferenceID.getText().toString().isEmpty()){
                referenceID = edtReferenceID.getText().toString();
                config.put("reference_id", referenceID);
            }

            if (!edtEmiPlanID.getText().toString().isEmpty()){
                emiPlanID = edtEmiPlanID.getText().toString();
                config.put("emi_plan_id", emiPlanID);
            }

            if (!edtPaymentModes.getText().toString().isEmpty()){
                paymentModes = edtPaymentModes.getText().toString();
                config.put("payment_methods", paymentModes);
            }

            if(!edtFeeHeaderSplit.getText().toString().isEmpty()){
                feeHeadersSplit = edtFeeHeaderSplit.getText().toString();
                config.put("fee_headers_split", feeHeadersSplit);
            }

//            if (hasMonthlyEmi || hasAuto || hasDirect) {
//                JSONObject financing_config = new JSONObject();
//
//                if (hasMonthlyEmi) {
//                    JSONObject monthlyEmi = new JSONObject();
//                    monthlyEmi.put("amount", edtMonthlyAmount.getText().toString());
//                    financing_config.put("monthly_emi", monthlyEmi);
//                }
//
//                if (hasAuto) {
//                    JSONObject auto_debit = new JSONObject();
//                    auto_debit.put("amount", edtAutoAmount.getText().toString());
//                    if (schedule_lists.size() > 0) {
//                        auto_debit.put("schedule", createScheduleArray());
//                    }
//                    financing_config.put("auto_debit", auto_debit);
//                }
//
//                if (hasDirect) {
//                    JSONObject direct = new JSONObject();
//                    direct.put("amount", edtDirectAmount.getText().toString());
//                    financing_config.put("direct", direct);
//                }
//
//                config.put("financing_config", financing_config);
//            }

            config.put("auth", auth);
            config.put("student_id", studentId);
            config.put("env", env);// eny = "test" for testing
            config.put("customer_number", customerNumber);
//            config.put("fee_amount", feeAmount);
//            config.put("payable_amount", payableAmount);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject prefill = new JSONObject();
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
//                prefill.put("notes", notes);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        prefill = new JSONObject();

//        try {

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

//            prefill.put("student_details", student);
//            prefill.put("customer_details", customer);
//            prefill.put("kyc_details", kyc);
//            prefill.put("residential_details", residential);
//            prefill.put("employment_details", employment);
//            prefill.put("notes", note);

//            Log.e(TAG, "ConfigObject: "+config.toString());
//            Log.e(TAG, "PrefillObject: " + prefill.toString());
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        if (prefill != null) {
            Log.e(TAG, "PrefillObject: " + prefill.toString());
        }
        Log.e(TAG, "ConfigObject: "+config.toString());
        GQPaymentSDK.initiate(MainActivity.this, config, prefill);
    }

    private void createPPConfig() {
        ppConfig = new JSONObject();

        try {
            if (ppSlug != null) {
                ppConfig.put("slug", ppSlug);
            }
            if (ppCard != null && !ppCard.isEmpty()) {
                ppConfig.put("card_code", ppCard);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e(TAG, "PPObject: " + ppConfig.toString());
    }

    private void viewHandler() {
        edtPPSlug.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    ppSlug = edtPPSlug.getText().toString();
                } else {
                    ppSlug = "";
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edtPPCard.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    ppCard = edtPPCard.getText().toString();
                } else {
                    ppCard = "";
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edtTheme.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    themeColour = edtTheme.getText().toString();
                } else {
                    themeColour = "";
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edtLogoUrl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    logo_url = edtLogoUrl.getText().toString();
                } else {
                    logo_url = "";
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edtFeeHelper.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    fee_helper_text = edtFeeHelper.getText().toString();
                } else {
                    fee_helper_text = "";
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edtEnvironment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    env = edtEnvironment.getText().toString();
                } else {
                    env = "";
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        handleDeepLink(intent);
        // Handle the deep link
        Uri data = intent.getData();
        if (data != null) {
            Log.d("DeepLink", "URL: " + data.toString());

            // Example: Check if the path matches
            if ("/v1".equals(data.getPath())) {
                // Perform actions upon returning to the app
                Toast.makeText(this, "Welcome back to the app!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void handleDeepLink(Intent intent) {
        Uri data = intent.getData();
        if (data != null) {
            Log.d("DeepLink", "Deep link triggered: " + data.toString());

            // Example: Perform actions based on the deep link
            if ("/return".equals(data.getPath())) {
                Toast.makeText(this, "Welcome back via deep link!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.d("DeepLink", "No deep link data found");
        }
    }
}