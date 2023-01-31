package com.grayquest.android.payment.sdk;

import com.cashfree.pg.core.api.CFSession;

public class Environment {

    public static final boolean isProduction = false; // Under UAT
//    public static final boolean isProduction = true; // Live

    public static String BASE_URL;
    public static String WEB_LOAD_URL;
    public static CFSession.Environment CASH_FREE_ENVIRONMENT;

    public static String VERSION = "\"1.1\"";

    public static String baseUrl(){
        if (isProduction){
            BASE_URL = "https://erp-api.grayquest.com/";// Base Url for Live
        }else {
            BASE_URL = "https://erp-api.graydev.tech/";// Base URL for UAT
        }
        return BASE_URL;
    }

    public static String webLoadUrl(){
        if (isProduction){
            WEB_LOAD_URL = "https://erp-sdk.grayquest.com/";// Base Url for Live
        }else {
            WEB_LOAD_URL = "https://erp-sdk-beta.graydev.tech/";// Base URL for UAT
        }
        return WEB_LOAD_URL;
    }

    public static CFSession.Environment cashFreeEnvironment(){
        if (isProduction){
            CASH_FREE_ENVIRONMENT = CFSession.Environment.PRODUCTION;// CashFee Production
        }else {
            CASH_FREE_ENVIRONMENT = CFSession.Environment.SANDBOX;// CashFee UAT
        }

        return CASH_FREE_ENVIRONMENT;
    }
}
