package com.jjthedev.pilly;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class UserPageActivity extends AppCompatActivity {

    PillViewAdapter pilladapter;

    List<Pill> pill_display = new ArrayList<>();

    String username;
    Integer userid;

    TextView username_txtview;
    TextView userid_txtview;

    Button deleteUser;
    User userdata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        Intent intent = getIntent();
        String userId = intent.getStringExtra("userid");

        setContentView(R.layout.activity_user_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        username_txtview = findViewById(R.id.userpage_username);
        userid_txtview = findViewById(R.id.userpage_userid);

        deleteUser = findViewById(R.id.userpage_delete);
        if (userid_txtview == null)
        {
            Log.d("UserData", "null userid");
        }
        fetchUserData(userId);

        RecyclerView pill_list = findViewById(R.id.pill_list);
        pill_list.setLayoutManager(new LinearLayoutManager(this));
        pilladapter = new PillViewAdapter(this, pill_display);

        pill_list.setAdapter(pilladapter);

        deleteUser.setOnClickListener(v->{
            UserAPI api = RetrofitClient.getUserAPI();

            Call<Void> call = api.deleteUser(userId);  // userId is a String or int

            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Log.d("UserAPI", "User deleted successfully");
                        // You can finish activity or refresh UI
                    } else {
                        Log.e("UserAPI", "Delete failed with code: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e("UserAPI", "Delete failed: " + t.getMessage());
                }
            });

            finish();

        });


    }

    void fetchUserData(String id)
    {
        UserAPI api = RetrofitClient.getUserAPI();
        Call<User> call = api.getUser(id);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    userdata = user;
                    Log.d("UserData", "ID: " + user.id + ", Name: " + user.name);

                    username_txtview.setText(user.name);
                    userid_txtview.setText(user.id.toString());


                    for (Map.Entry<String, List<Integer>> entry : user.pill.entrySet()) {
                        Log.d("UserData", entry.getKey() + ": " + entry.getValue());
                        Pill p = new Pill();
                        p.name = entry.getKey();
                        List<Integer> vals = entry.getValue();
                        p.timings = vals;
                        pill_display.add(p);
                    }
                    pilladapter.notifyDataSetChanged();

                    // You can now update UI fields with user.name etc.
                } else {
                    Log.e("UserData", "Response error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("UserData", "Network error: " + t.getMessage());
            }
        });


    }
}