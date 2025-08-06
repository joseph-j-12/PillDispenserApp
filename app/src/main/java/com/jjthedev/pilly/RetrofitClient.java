package com.jjthedev.pilly;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public class RetrofitClient {
    private static Retrofit retrofit = null;

    public static UserAPI getUserAPI(){
        if(retrofit == null)
        {
            retrofit = new Retrofit.Builder().baseUrl("http://192.168.186.172:5000")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(UserAPI.class);
    }
}
