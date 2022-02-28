package com.grayquest.android.payment.sdk;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiInterface {

    @POST("v1/customer/create-customer")
    Call<PaymentSDk_Contributor>createCustomer(
            @Body JsonObject jsonObject
    );
}
