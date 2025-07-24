package com.grayquest.android.payment.sdk;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiInterface {

    @POST("v1/customer/create-customer")
    Call<PaymentSDk_Contributor>createCustomer(
            @Header("GQ-API-Key") String gqApiKey,
            @Header("Authorization")String abase,
            @Body JsonObject jsonObject
    );

    @GET("v1/pp/get-session-data")
    Call<PaymentSDk_Contributor>sessionCode(
            @Header("Authorization")String authorization
    );
}
