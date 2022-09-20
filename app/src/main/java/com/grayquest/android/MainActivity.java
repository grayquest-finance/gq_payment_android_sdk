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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.grayquest.android.payment.sdk.GQPaymentSDK;
import com.grayquest.android.payment.sdk.GQPaymentSDKListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements GQPaymentSDKListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    EditText edtClientId, edtSecretKey, edtGQApi, edtStudentID, edtCustomerNumber, edtFeeAmount, edtPayableAmount, edtTheme, edtOptional;
    SwitchCompat edtFeeEditable, edtFinancing;
    RadioButton radioTest, radioLive;
    String clientId, secretKey, GQApi, studentId, env, customerNumber, feeAmount, payableAmount, themeColour, logo_url, fee_helper_text, optional = "";
    TextView btnOptionPrefill, btnRemovePrefill;
    boolean feeEditable, hasMonthlyEmi, hasAuto, hasDirect;

    LinearLayout llFinancing, llAutoDebit, llSchedule;
    CheckBox chkMonthlyEmi, chkAutoDebit, chkDirect;
    TextInputLayout inputMonthlyAmount, inputAutoAmount, inputDirectAmount;
    TextInputEditText edtMonthlyAmount, edtAutoAmount, edtDirectAmount;
    TextView btnAddSchedule;

    Button btn_open, btnPrefill;

    ArrayList<Schedule_List> schedule_lists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtClientId = (EditText) findViewById(R.id.edt_client_id);
        edtSecretKey = (EditText) findViewById(R.id.edt_client_secret);
        edtGQApi = (EditText) findViewById(R.id.edt_gq_api);
        edtStudentID = (EditText) findViewById(R.id.edt_student_id);
        edtFeeEditable = (SwitchCompat) findViewById(R.id.edt_fee_editable);
        edtFinancing = (SwitchCompat) findViewById(R.id.edt_financing);
        radioTest = (RadioButton) findViewById(R.id.rd_test);
        radioLive = (RadioButton) findViewById(R.id.rd_live);
        edtCustomerNumber = (EditText) findViewById(R.id.edt_customer_number);
        edtFeeAmount = (EditText) findViewById(R.id.edt_fee_amount);
        edtPayableAmount = (EditText) findViewById(R.id.edt_payable_amount);
        edtTheme = (EditText) findViewById(R.id.edt_theme);
        edtOptional = (EditText) findViewById(R.id.edt_optional);

        llFinancing = (LinearLayout) findViewById(R.id.ll_financing);
        llAutoDebit = (LinearLayout) findViewById(R.id.ll_auto_debit);
        llSchedule = (LinearLayout) findViewById(R.id.ll_schedule);

        chkMonthlyEmi = (CheckBox) findViewById(R.id.chk_monthly);
        chkAutoDebit = (CheckBox) findViewById(R.id.chk_auto);
        chkDirect = (CheckBox) findViewById(R.id.chk_direct);

        inputMonthlyAmount = (TextInputLayout) findViewById(R.id.input_monthly_amount);
        inputAutoAmount = (TextInputLayout) findViewById(R.id.input_auto_amount);
        inputDirectAmount = (TextInputLayout) findViewById(R.id.input_direct_amount);

        edtMonthlyAmount = (TextInputEditText) findViewById(R.id.edt_monthly_amount);
        edtAutoAmount = (TextInputEditText) findViewById(R.id.edt_auto_emi);
        edtDirectAmount = (TextInputEditText) findViewById(R.id.edt_direct_emi);

        btnAddSchedule = (TextView) findViewById(R.id.btn_add);

        btnOptionPrefill = (TextView) findViewById(R.id.btn_option_prefill);
        btnRemovePrefill = (TextView) findViewById(R.id.btn_remove_prefill);

        schedule_lists = new ArrayList<>();

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

        edtFinancing.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    llFinancing.setVisibility(View.VISIBLE);
                } else {
                    llFinancing.setVisibility(View.GONE);
                }
            }
        });

        chkMonthlyEmi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    hasMonthlyEmi = true;
                    inputMonthlyAmount.setVisibility(View.VISIBLE);
                } else {
                    hasMonthlyEmi = false;
                    inputMonthlyAmount.setVisibility(View.GONE);
                    edtMonthlyAmount.setText("");
                }
            }
        });

        chkAutoDebit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    llAutoDebit.setVisibility(View.VISIBLE);
                    llSchedule.setVisibility(View.VISIBLE);
                    hasAuto = true;
                } else {
                    llAutoDebit.setVisibility(View.GONE);
                    llSchedule.setVisibility(View.GONE);
                    hasAuto = false;
                    llSchedule.removeAllViews();
                    edtAutoAmount.setText("");
                }
            }
        });

        chkDirect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    inputDirectAmount.setVisibility(View.VISIBLE);
                    hasDirect = true;
                } else {
                    inputDirectAmount.setVisibility(View.GONE);
                    hasDirect = false;
                    edtDirectAmount.setText("");
                }
            }
        });

        btnAddSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addLayout();
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
                // Test Credentials
                /*edtClientId.setText("354598fd-575a-4c1f-a6e3-e08fc5ea06d3");
                edtSecretKey.setText("2eb349732594fe777be079fb3c7e557194f55a14");
                edtGQApi.setText("9db4fc333d8bcf7fee98804105d9fc0c85199d77");*/

                // Live Credentials
                edtClientId.setText("5dcd22af-13b3-4970-9293-55c3be282f93");
                edtSecretKey.setText("a8aa18f2e043e1313685d0324f38e8599f7c1fed");
                edtGQApi.setText("122eab04ddd7ad9df25bb77de7494a48afafddee");

                edtStudentID.setText("std_1212");
                edtFeeEditable.setChecked(false);
                radioTest.setChecked(true);
                radioLive.setChecked(false);
                edtCustomerNumber.setText("8425960118");
                edtFeeAmount.setText("96000");
                edtPayableAmount.setText("9600");
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

    private void addLayout() {
        View layout2 = LayoutInflater.from(this).inflate(R.layout.schedule, llSchedule, false);

        TextView btnRemove = (TextView) layout2.findViewById(R.id.btn_remove);

        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llSchedule.removeView(layout2);
            }
        });

        llSchedule.addView(layout2);
    }

    private void getValueAddedView() {
        schedule_lists.clear();
        if (llSchedule.getChildCount()>0) {
            for (int i = 0; i < llSchedule.getChildCount(); i++) {
                TextInputEditText edtDate = (TextInputEditText) llSchedule.getChildAt(i).findViewById(R.id.edt_date);
                TextInputEditText edtAmount = (TextInputEditText) llSchedule.getChildAt(i).findViewById(R.id.edt_amount);

                Schedule_List schedule_list = new Schedule_List();

                schedule_list.setDate(edtDate.getText().toString());
                schedule_list.setAmount(edtAmount.getText().toString());

                schedule_lists.add(schedule_list);
            }
        }
    }

    public void openSDk() {

//        JSONObject config = new JSONObject();
//
//        try {
//            config.put("client_id", "354598fd-575a-4c1f-a6e3-e08fc5ea06d3");
//            config.put("client_secret_key", "2eb349732594fe777be079fb3c7e557194f55a14");
//            config.put("gq_api_key", "9db4fc333d8bcf7fee98804105d9fc0c85199d77");
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
        getValueAddedView();
        JSONObject config = new JSONObject();

        try {
            JSONObject auth = new JSONObject();

            auth.put("client_id", clientId);
            auth.put("client_secret_key", secretKey);
            auth.put("gq_api_key", GQApi);

            JSONObject customisation = new JSONObject();
            customisation.put("fee_helper_text", "fee_helper_text");
            customisation.put("logo_url", logo_url);
            customisation.put("theme_color", themeColour);

            if (hasMonthlyEmi || hasAuto || hasDirect) {
                JSONObject financing_config = new JSONObject();

                if (hasMonthlyEmi) {
                    JSONObject monthlyEmi = new JSONObject();
                    monthlyEmi.put("amount", edtMonthlyAmount.getText().toString());
                    financing_config.put("monthly_emi", monthlyEmi);
                }

                if (hasAuto) {
                    JSONObject auto_debit = new JSONObject();
                    auto_debit.put("amount", edtAutoAmount.getText().toString());
                    if (schedule_lists.size() > 0) {
                        auto_debit.put("schedule", createScheduleArray());
                    }
                    financing_config.put("auto_debit", auto_debit);
                }

                if (hasDirect) {
                    JSONObject direct = new JSONObject();
                    direct.put("amount", edtDirectAmount.getText().toString());
                    financing_config.put("direct", direct);
                }

                config.put("financing_config", financing_config);
            }

            config.put("auth", auth);
            config.put("student_id", studentId);
            config.put("fee_editable", feeEditable);//default true
            config.put("env", env);// eny = "test" for testing
            config.put("customer_number", customerNumber);
            config.put("fee_amount", feeAmount);
            config.put("payable_amount", payableAmount);
            config.put("customization", customisation);

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

        GQPaymentSDK.initiate(MainActivity.this, config, prefill);
    }

    private JSONArray createScheduleArray() {
        JSONArray schedule = new JSONArray();

        for (int i = 0; i < schedule_lists.size(); i++) {
            JSONObject scheduleObject = new JSONObject();
            try {
                scheduleObject.put("date", schedule_lists.get(i).getDate());
                scheduleObject.put("amount", schedule_lists.get(i).getAmount());

                schedule.put(i, scheduleObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return schedule;
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