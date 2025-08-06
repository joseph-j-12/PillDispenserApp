package com.jjthedev.pilly;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserAPI {

    @GET("userlist")
    Call<Map<Integer,String>> getUserList(); // Returns a list of user IDs

    @GET("users/{id}")
    Call<User> getUser(@Path("id") String id); // Returns a single user

    @PUT("users/{id}")
    Call<Void> updateUser(@Path("id") String id, @Body User user); // Updates user data
}
