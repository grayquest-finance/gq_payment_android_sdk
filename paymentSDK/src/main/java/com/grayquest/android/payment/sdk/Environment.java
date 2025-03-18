package com.grayquest.android.payment.sdk;

import com.cashfree.pg.core.api.CFSession;

public class Environment {

    public static boolean isProduction = false; // Under UAT
//    public static final boolean isProduction = true; // Live

    public static String environment;

    public static String BASE_URL;
    public static String WEB_LOAD_URL;
    public static CFSession.Environment CASH_FREE_ENVIRONMENT;

    public static String VERSION = "\"1.1\"";

    public static String baseUrl() {
        if (isProduction) {
            BASE_URL = "https://erp-api.grayquest.com/";// Base Url for Live
        } else {
            BASE_URL = "https://erp-api.graydev.tech/";// Base URL for UAT
        }
        return BASE_URL;
    }

    public static String webLoadUrl() {
        if (isProduction) {
            WEB_LOAD_URL = "https://erp-sdk.grayquest.com/";// Base Url for Live
        } else {
            WEB_LOAD_URL = "https://erp-sdk.graydev.tech/";// Base URL for UAT
        }
        return WEB_LOAD_URL;
    }

    public static CFSession.Environment cashFreeEnvironment() {
        if (environment.equals("live")) {
            CASH_FREE_ENVIRONMENT = CFSession.Environment.PRODUCTION;// CashFee Production
        } else {
            CASH_FREE_ENVIRONMENT = CFSession.Environment.SANDBOX;// CashFee UAT
        }
        return CASH_FREE_ENVIRONMENT;
    }

    public static String env(String env) {
        switch (env) {
            case "stage":
                BASE_URL = "https://erp-api-stage.graydev.tech/";// Base URL for STAGE Environment
                WEB_LOAD_URL = "https://erp-sdk-stage.graydev.tech/";// Base Web URL for STAGE Environment
                return environment = "stage";
            case "preprod":
                BASE_URL = "https://erp-api-preprod.graydev.tech/";// Base URL for PREPROD Environment
                WEB_LOAD_URL = "https://erp-sdk-preprod.graydev.tech/";// Base Web URL for PREPROD Environment
                return environment = "preprod";
            case "live":
                BASE_URL = "https://erp-api.grayquest.com/";// Base URL for PRODUCTION Environment
                WEB_LOAD_URL = "https://erp-sdk.grayquest.com/";// Base Web URL for PRODUCTION Environment
                return environment = "live";
            default:
                BASE_URL = "https://erp-api.graydev.tech/";// Base URL for UAT Environment
                WEB_LOAD_URL = "https://erp-sdk.graydev.tech/";// Base Web URL for UAT Environment
                return environment = "test";
        }
    }
}
