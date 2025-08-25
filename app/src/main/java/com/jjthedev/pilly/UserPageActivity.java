package com.jjthedev.pilly;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
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
    ImageButton saveUser, canceledit;
    User userdata;
    ImageButton newPill;
    List<Integer> empty_containers = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        Intent intent = getIntent();
        String userId = intent.getStringExtra("userid");
        Log.d("UserData", userId);
        Log.d("UserData", "1AAAAAAAAAAAAAAA");
        setContentView(R.layout.activity_user_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        username_txtview = findViewById(R.id.userpaage_username);
        //userid_txtview = findViewById(R.id.userpage_userid);

        canceledit = findViewById(R.id.userpage_cancel);
        saveUser = findViewById(R.id.userpage_save);
        newPill = findViewById(R.id.userpage_newpill);
        Log.d("UserData", "2AAAAAAAAAAAAAAA");
        if (saveUser == null)
        {
            Log.d("UserData", "NULLLLLL!!!!!!");
        }
        fetchUserData(userId);

        RecyclerView pill_list = findViewById(R.id.pill_list);
        pill_list.setLayoutManager(new LinearLayoutManager(this));
        pilladapter = new PillViewAdapter(this, pill_display);

        pill_list.setAdapter(pilladapter);
        canceledit.setOnClickListener(v->{
            finish();
        });

        saveUser.setOnClickListener(v->{
            update_user();
        });
        newPill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newPill(view);
            }
        });

    }

    void update_user()
    {
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
            vals.add(pill_display.get(i).containerid);
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
    }
    void newPill(View view)
    {
        Pill p = new Pill();
        View view1 = LayoutInflater.from(UserPageActivity.this).inflate(R.layout.newpill_dialog,null);
        TextInputEditText nameinp = view1.findViewById(R.id.pillname_input);
        Spinner dosagesp = view1.findViewById(R.id.dosage_select);
        TextInputEditText countinp = view1.findViewById(R.id.pillcount_input);

        AlertDialog al1 = new MaterialAlertDialogBuilder(UserPageActivity.this)
                .setTitle("Enter new Pill Name")
                .setView(view1)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = nameinp.getText().toString();
                        Integer dosage = dosagesp.getSelectedItemPosition();
                        Integer count = Integer.parseInt(countinp.getText().toString());
                        Log.d("dialog", name);

                        //check if containers are available
                        UserAPI api = RetrofitClient.getUserAPI();
                        Call<List<Integer>> call = api.getEmptyContainers();
                        boolean success = false;

                        call.enqueue(new Callback<List<Integer>>() {
                            @Override
                            public void onResponse(Call<List<Integer>> call, Response<List<Integer>> response) {
                                if(response.isSuccessful() && response.body() != null)
                                {
                                    empty_containers = response.body();
                                    Log.d("containerdata", "Received: " + empty_containers);
                                    if (empty_containers == null)
                                    {
                                        Log.d("containerdata", "container request failed");
                                    }
                                    else if (empty_containers.size() > 0) {
                                        //add new pill
                                        p.name = name;
                                        List<Integer> vals = Arrays.asList(count,dosage,empty_containers.get(0));
                                        List<Integer> timings = new ArrayList<>();
                                        p.timings = timings;
                                        p.count = vals.get(0);
                                        p.dosage = vals.get(1);
                                        p.containerid = vals.get(2);
                                        pill_display.add(p);
                                        Log.d("containerdata", "new pill added");

                                        pilladapter.notifyDataSetChanged();
                                        dialog.dismiss();
                                    }
                                    else
                                    {
                                        //all containers are used
                                        Log.d("containerdata", Integer.toString(empty_containers.size()));
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<List<Integer>> call, Throwable t) {
                                empty_containers = null;
                                Toast.makeText(UserPageActivity.this, "Failed: " + t.getMessage(), Toast.LENGTH_LONG).show();
                                Log.e("containerdata","Failed: " + t.getMessage());
                            }
                        });


                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        al1.show();
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
                    //userid_txtview.setText(user.id.toString());


                    for (Map.Entry<String, List<List<Integer>>> entry : user.pill.entrySet()) {
                        Log.d("UserData", entry.getKey() + ": " + entry.getValue());
                        Pill p = new Pill();
                        p.name = entry.getKey();
                        List<Integer> vals = entry.getValue().get(0);
                        List<Integer> timings = entry.getValue().get(1);
                        p.timings = timings;
                        p.count = vals.get(0);
                        p.dosage = vals.get(1);
                        p.containerid = vals.get(2);
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

    void get_empty_containers()
    {
        UserAPI api = RetrofitClient.getUserAPI();
        Call<List<Integer>> call = api.getEmptyContainers();
        boolean success = false;

        call.enqueue(new Callback<List<Integer>>() {
            @Override
            public void onResponse(Call<List<Integer>> call, Response<List<Integer>> response) {
                if(response.isSuccessful() && response.body() != null)
                {
                    empty_containers = response.body();
                    Log.d("containerdata", "Received: " + empty_containers);
                }
            }

            @Override
            public void onFailure(Call<List<Integer>> call, Throwable t) {
                empty_containers = null;
                Toast.makeText(UserPageActivity.this, "Failed: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("containerdata","Failed: " + t.getMessage());
            }
        });
    }
}