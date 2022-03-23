# gq_payment_android_sdk
GQ Android Payment SDK



Add the following code to the apps build.gradle file:

# Gradle Config 
	allprojects {
		repositories {
				maven {
 			           url "https://jitpack.io"
 	       			}
			}
		}
    
    dependencies {
    
		  Implementation 'com.github.grayquest-finance:gq_payment_android_sdk:1.0.3
      
	  }

# Add the following code to initiate the Android SDK:

    GQPaymentSDK.initiate(MainActivity.this, config, prefill);
    
    
# Add the following code to callback:

    public class MainActivity extends AppCompatActivity implements GQPaymentSDKListener {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
    }

    @Override
    public void onSuccess(JSONObject message) {
        Toast.makeText(MainActivity.this, message.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFailed(JSONObject message) {
        Toast.makeText(MainActivity.this, message.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCancel(JSONObject message) {
        Toast.makeText(MainActivity.this, message.toString(), Toast.LENGTH_SHORT).show();
    }
    
    
# Options
Available options that can be set while initiating the sdk.

Must Have
   
	 

|Sr. No.| Option | Description | Mandatory |
|--|--|--|--|
| 1 | env | **Values:** test or live | Yes
|||**Possible Scenarios:**
|||1.  If value is test then SDK will connect with Testing environment | 
||| 2. 1.  If value is live then SDK will connect with Live/Production environment|
|2|client_id|Authentication credential|Yes
|3|client_secret_key|Authentication credential|Yes
|4|gq_api_key|Authentication credential|Yes
|5|student_id|Unique id of the student used to create the gq application with reference to customer mobile number.|Yes
|6|customer_mobile|Used to create a customer in the GrayQuest System|No
|7|fee_amount|Pending Annual Fee amount, Fee Amount should be greater than or equal to Rs. 10,000.|No
|||**Note:** If you leave this option blank, be sure you set the fee editable to true, or else the SDK will give an error that can be seen in the inspect tab's console area.|
|8|payable_amount|Current Payable Amount (E.g. Current Term Fee)|No
|||**Note:** If this amount is not available please pass the same value as ‘fee_amount’.|
|||If you leave this option blank, be sure you set the fee editable to true, or else the SDK will give an error that can be seen in the inspect tab's console area.|
|9|fee_editable|**Values**: true or false (Boolean)|Yes
|||If **true**: SDK will allow the customer to edit the fee details.|
|||If **false**: SDK will not allow customers to edit the fee detail.|
|||**Possible Scenarios:**|
|||1.  If a fee amount is provided then the fee editable can be true or false.|
|||2.  If fee amount is not provided the fee editable should be true otherwise sdk will throw an error that can be seen in the network tab's console area.|

# Additional Options that can be used to ease the journey for your customers / parents / users.

# Student Details


|Sr. No.| Option | Description | Mandatory |
|--|--|--|--|
|1|student_first_name|First name of student (First name should contain only alphabets and space)|No|
|2|student_middle_name|Middle name of student (Middle name should contain only alphabets and space)|No|
|3|student_last_name|Last name of student (Last name should contain only alphabets and space)|No|
|4|student_type|Type of Student|No
|||**Values:**|
|||1.  EXISTING|
|||2.  NEW|

# Applicant Details



|Sr. No.| Option | Description | Mandatory |
|--|--|--|--|
|1|customer_first_name|First name of customer (First name should contain only alphabets and space)|No|
|2|customer_middle_name|Middle name of customer (Middle name should contain only alphabets and space)|No|
|3|customer_last_name|Last name of customer (Last name should contain only alphabets and space)|No|
|4|customer_dob|Date of birth of customer (yyyy-mm-dd)|No|
|5|customer_gender|Gender of customer|No|
|||**Values:**|
|||1.  MALE|
|||2.  FEMALE|
|6|customer_email|Email address of customer|No|
|7|customer_marital_status|Marital Status of Customer|No|
|||**Values:**|
|||1.  MARRIED|
|||2.  DIVORCED|
|||3.  OTHERS|
|8|pan_number|Customer's PAN card number|No|

# Residential Details

|Sr. No.| Option | Description | Mandatory |
|--|--|--|--|
|1|residential_addr_line_1|Customer's residential address 1|No|
|||(Min length = 5 & Max length = 40)||
|2|residential_addr_line_2|Customer's residential address 2|No|
|||(Min length = 5 & Max length = 40)||
|3|residential_type|Type of Residence|No|
|||**Values:**||
|||1.  SELF OWNED|
|||2.  RENTED|
|||3.  COMPANY PROVIDED|
|||4.  PAYING GUEST|
|4|residential_period|Period of Residence|No|
|||**alues:**||
|||1.  LESS THAN 2 YEARS|
|||2.  2-5 YEARS|
|||3.  5-10 YEARS|
|||4.  GREATER THAN 10 YEARS|
|5|residential_pincode|Pincode (6 digits pincode)|No|
|6|residential_city|City|No
|7|residential_state|State|No|

# Employment Details - Salaried

|Sr. No.| Option | Description | Mandatory |
|--|--|--|--|
|1|income_type|Type of income|No|
|||**Value**: SALARIED|
|2|employer_nam|Name of employer|No|
|3|work_experience|Years of experience|No|
|||**Values:**|
|||1.  LESS THAN 2 YEARS|
|||2.  2-5 YEARS|
|||3.  5-10 YEARS|
|||4.  GREATER THAN 10 YEARS|
|4|net_monthly_salary|Monthly salary (Salary can only contain numeric values)|No|

# Employment Details - Self Employed

|Sr. No.| Option | Description | Mandatory |
|--|--|--|--|
|1|income_type|Type of income|No|
|||**Value**: SELF EMPLOYED||
|2|business_name|Name of the business|No|
|3|business_turnover|Annual Business Turnover|No
|||**Values:**|
|||1.  0 LAKHS TO 5 LAKHS||
|||2.  5 LAKHS TO 10 LAKHS||
|||3.  10 LAKHS TO 50 LAKHS||
|||4.  GREATER THAN 50 LAKHS||
|4|business_annual_income|Total annual income of business (Annual income can only contain numeric values)|No|
|5|business_category|1.RETAILER|No|
|||2.TRADER||
|||3.MANUFACTURER||
|||4.SERVICE PROVIDER||
|||5.OTHERS||
|6|business_type|Type of business|No|
|||**Values:**||
|||1.  PROPRIETORSHIP||
|||2.  PARTNERSHIP||
|||3.  PRIVATE LIMITED||
|||4.  PUBLIC LIMITED||
|||5.  LIMITED LIABILITY PARTNERSHIP (LLP)||
|7|business_description|Business Details|No|
|8|business_employee_count|Total employees (Employee count can only contain numeric values)|No|
|9|years_of_current_business|Total number of years since business has been established (Current business years can only contain numeric values)|No|
|10|same_as_residence_address|Enter '1' if business and residence address is same|No|
|11|addr_line_1|[If not same as Residence Address]|No|
|||Business Address line 1||
|||(Min length = 5 & Max length = 90)||
|12|addr_line_2|[If not same as Residence Address]|No|
|||Business Address line 2||
|||(Min length = 5 & Max length = 90)||
|13|pincode|[If not same as Residence Address]|No|
|||Business Pincode (6 digits pincode)||
|14|city|[If not same as Residence Address]|No|
|||Business city||
|15|state|[If not same as Residence Address]|No|
|||Business state||
|16|notes|Notes provided by customer|No|

# Customizations
Texts

|Sr. No.| Option | Description | Mandatory |
|--|--|--|--|
|1|fee_helper_text|To add a custom fee helper text which is shown on the fee details section on the popup/sdk.|No|

# Logo

|Sr. No.| Option | Description | Mandatory |
|--|--|--|--|
|1|logo_url|To add a custom institute logo which is shown on the popup/sdk.|No|
|||**Ideal Image Size:** **100 X 100 px**||

# Theme

|Sr. No.| Option | Description | Mandatory |
|--|--|--|--|
|1|theme_color|Add/Override custom theme color of the SDK/Popup|No|
|||**Note:** 6 digit hex color code is required.||
|||ex.#2C3E50||
