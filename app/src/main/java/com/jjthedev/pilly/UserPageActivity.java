package com.jjthedev.pilly;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
    Button saveUser, canceledit;
    User userdata;
    Button newPill;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        Intent intent = getIntent();
        String userId = intent.getStringExtra("userid");
        Log.d("UserData", userId);
        setContentView(R.layout.activity_user_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        username_txtview = findViewById(R.id.userpage_username);
        userid_txtview = findViewById(R.id.userpage_userid);

        canceledit = findViewById(R.id.userpage_back);
        deleteUser = findViewById(R.id.userpage_delete);
        saveUser = findViewById(R.id.userpage_save);
        newPill = findViewById(R.id.userpage_newpill);
        if (userid_txtview == null)
        {
            Log.d("UserData", "null userid");
        }
        fetchUserData(userId);

        RecyclerView pill_list = findViewById(R.id.pill_list);
        pill_list.setLayoutManager(new LinearLayoutManager(this));
        pilladapter = new PillViewAdapter(this, pill_display);

        pill_list.setAdapter(pilladapter);
        canceledit.setOnClickListener(v->{
            finish();
        });
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

        saveUser.setOnClickListener(v->{
            Map<String, List<List<Integer>>> pill = new HashMap<>();;
            for (int i = 0; i < pill_display.size(); i++)
            {

                List<Integer> vals = new ArrayList<>();
                List<Integer> timings = new ArrayList<>();
                List<List<Integer>> pilldata = new ArrayList<>();
                for(int j = 0; j < pill_display.get(i).timings.size(); j++)
                {
                    Log.e("usrdata", "test");
                    timings.add(pill_display.get(i).timings.get(j));
                }
                vals.add(pill_display.get(i).count);
                vals.add(pill_display.get(i).dosage);
                pilldata.add(vals);
                pilldata.add(timings);

                pill.put(pill_display.get(i).name, pilldata);
            }
            userdata.pill = pill;

            UserAPI api = RetrofitClient.getUserAPI();
            api.updateUser(userdata.id.toString(), userdata).enqueue((new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        // Update successful
                        Toast.makeText(UserPageActivity.this, "User updated successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        // Server returned an error
                        Toast.makeText(UserPageActivity.this, "Update failed: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    // Network or conversion error
                    Toast.makeText(UserPageActivity.this, "Request failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }));
        });

        newPill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View view1 = LayoutInflater.from(UserPageActivity.this).inflate(R.layout.newpill_dialog,null);
                TextInputEditText nameinp = view1.findViewById(R.id.pillname_input);
                AlertDialog al1 = new MaterialAlertDialogBuilder(UserPageActivity.this)
                        .setTitle("Enter new Pill Name")
                        .setView(view1)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String name = nameinp.getText().toString();
                                Log.d("dialog", name);

                                Pill p = new Pill();
                                p.name = name;
                                List<Integer> vals = Arrays.asList(0,0);
                                List<Integer> timings = new ArrayList<>();
                                p.timings = timings;
                                p.count = vals.get(0);
                                p.dosage = vals.get(1);
                                pill_display.add(p);

                                pilladapter.notifyDataSetChanged();
                                dialog.dismiss();
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();
                al1.show();
            }
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


                    for (Map.Entry<String, List<List<Integer>>> entry : user.pill.entrySet()) {
                        Log.d("UserData", entry.getKey() + ": " + entry.getValue());
                        Pill p = new Pill();
                        p.name = entry.getKey();
                        List<Integer> vals = entry.getValue().get(0);
                        List<Integer> timings = entry.getValue().get(1);
                        p.timings = timings;
                        p.count = vals.get(0);
                        p.dosage = vals.get(1);
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