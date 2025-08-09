package com.jjthedev.pilly;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    UserViewAdapter adapter;
    List<UserDisplay> userList = new ArrayList<>();
    boolean firstlaunch = false;
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        fetchUserList();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView statusView = findViewById(R.id.textView_connectedto);
        ImageButton refButton = findViewById(R.id.button_refresh_userlist);
        ImageButton newUserButton = findViewById(R.id.button_newUser);
        RecyclerView userlistview = findViewById(R.id.recycler_userlist);

        userlistview.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UserViewAdapter(this, userList);
        userlistview.setAdapter(adapter);

        fetchUserList();
        refButton.setOnClickListener(v->fetchUserList());

        //TextView tinput = findViewById(R.id.)
        newUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View view1 = LayoutInflater.from(MainActivity.this).inflate(R.layout.username_dialog,null);
                TextInputEditText nameinp = view1.findViewById(R.id.name_input_user);
                AlertDialog al1 = new MaterialAlertDialogBuilder(MainActivity.this)
                        .setTitle("Enter new username")
                        .setView(view1)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String name = nameinp.getText().toString();
                                User newuser = new User();
                                newuser.name = name;
                                newuser.pill = new HashMap<>();
                                Integer id = 0;
                                for (int i = 0; i < userList.size(); i++)
                                {
                                    if (Objects.equals(userList.get(i).id, id.toString()))
                                    {
                                        id += 1;
                                    }
                                }
                                newuser.id = id;
                                newUserCreate(newuser);

                                Log.d("dialog", name);
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

    private void newUserCreate(User user)
    {
        UserAPI api = RetrofitClient.getUserAPI();
        api.newUser(user).enqueue((new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Update successful
                    Toast.makeText(MainActivity.this, "User created successfully", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(MainActivity.this, UserPageActivity.class);
                    intent.putExtra("userid", user.id.toString());
                    //Log.d("acswitch", user.id.toString());
                    startActivity(intent);

                } else {
                    // Server returned an error
                    Toast.makeText(MainActivity.this, "New User Create failed: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Network or conversion error
                Toast.makeText(MainActivity.this, "Request failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }));;
    }
    private void fetchUserList()
    {
        UserAPI api = RetrofitClient.getUserAPI();
        Call<Map<Integer, String>> call = api.getUserList();

        api.getUserList().enqueue(new Callback<Map<Integer, String>>() {
            @Override
            public void onResponse(Call<Map<Integer, String>> call, Response<Map<Integer, String>> response) {
                if(response.isSuccessful() && response.body() != null)
                {
                    userList.clear();
                    for(Map.Entry<Integer,String> entry : response.body().entrySet())
                    {
                        userList.add(new UserDisplay(entry.getKey().toString(), entry.getValue()));
                        //Toast.makeText(MainActivity.this, "added: " + entry.getValue(), Toast.LENGTH_LONG).show();
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<Map<Integer, String>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Failed: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("usrdata","Failed: " + t.getMessage());
            }
        });

    }
}