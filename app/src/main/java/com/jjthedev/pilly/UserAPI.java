package com.jjthedev.pilly;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserAPI {

    @GET("userlist")
    Call<Map<Integer,String>> getUserList(); // Returns a list of user IDs

    @GET("empty_containers")
    Call<List<Integer>> getEmptyContainers(); // Returns a list of user IDs

    @GET("users/{id}")
    Call<User> getUser(@Path("id") String id); // Returns a single user

    @PUT("users/{id}")
    Call<Void> updateUser(@Path("id") String id, @Body User user); // Updates user data

    //containers
    @PUT("containers/{id}/open")
    Call<Void> openContainer(@Path("id") String id); // Updates user data

    @PUT("containers/close")
    Call<Void> closeContainer(); // Updates user data

    @DELETE("users/{id}")
    Call<Void> deleteUser(@Path("id") String id);

    @POST("adduser")
    Call<Void> newUser(@Body User user);

    @POST("user/{id}/edit_pill/{pill}")
    Call<Void> editPill();

    @PUT("users/{id}/save_face")
    Call<Void> saveFace(@Path("id") String id);

    @GET("ringtone")
    Call<List<String>> getRingtones();

    @POST("ringtone/set")
    Call<Void> setRingtone(@Body String filename);
    //@PUT("adduser")
    //Call<Void> newUser()
}
