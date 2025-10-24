package com.jjthedev.pilly;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

import java.io.IOException;
import java.net.InetAddress;

public class RetrofitClient {
    private static Retrofit retrofit = null;

    public static UserAPI getUserAPI(){

        if(retrofit == null)
        {
            
            retrofit = new Retrofit.Builder().baseUrl("http://192.168.2.168:5000/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(UserAPI.class);
    }
}
