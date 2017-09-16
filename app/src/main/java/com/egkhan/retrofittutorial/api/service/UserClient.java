package com.egkhan.retrofittutorial.api.service;

import com.egkhan.retrofittutorial.api.model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by EgK on 9/16/2017.
 */

public interface UserClient {

    @POST("user")
    Call<User>  createAccount(@Body User user);
}
