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
        ImageButton ringtoneSelect = findViewById(R.id.button_ringtone);

        RecyclerView userlistview = findViewById(R.id.recycler_userlist);

        userlistview.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UserViewAdapter(this, userList, position -> {
            userList.remove(position);
            adapter.notifyDataSetChanged();
        });
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

        ringtoneSelect.setOnClickListener(v->ringtoneSelect());

    }

    void ringtoneSelect()
    {
        UserAPI api = RetrofitClient.getUserAPI();
        Call<List<String>> call = api.getRingtones();
        List<String> ringtoneList;
        call.enqueue(new Callback<List<String>>() {

            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {

                if (response.isSuccessful() && response.body() != null) {

                    // **SUCCESS! The raw list of strings is here:**
                    List<String> rawRingtones = response.body();

                    // --- Use the Raw List Directly ---

                    // Example 1: Print all the raw strings to the log
                    for (String ringtoneUrl : rawRingtones) {
                        Log.d("RingtoneFetch", "Raw URL: " + ringtoneUrl);
                    }
                    ringtoneSelectDialog(rawRingtones);
                    // Example 2: Check the size
                    //Log.i("RingtoneFetch", "Total Ringtones fetched: " + rawRingtones.size());

                    // Example 3: Use the list to populate a simple ArrayAdapter/ListView
                    // if (rawRingtones.size() > 0) {
                    //     Toast.makeText(MainActivity.this, "First Ringtone: " + rawRingtones.get(0), Toast.LENGTH_LONG).show();
                    // }

                } else {
                    // Handle non-200 HTTP codes (e.g., 404, 500)
                    Log.e("RingtoneFetch", "API Call Failed with code: " + response.code());
                    Toast.makeText(MainActivity.this, "Server Error: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                // Handle network failure (e.g., connection lost)
                Log.e("RingtoneFetch", "Network Failed: " + t.getMessage());
                Toast.makeText(MainActivity.this, "Network Failed: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    void ringtoneSelectDialog(List<String> ringtoneList)
    {
        final String[] options = ringtoneList.toArray(new String[0]);

        // 2. Build the Material Dialog
        new MaterialAlertDialogBuilder(MainActivity.this)
                .setTitle("Select a Ringtone")

                // 3. Set the items and the click listener
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // 'which' is the index (0, 1, 2, ...) of the selected item
                        String selectedRingtone = options[which];
                        setRingtone(selectedRingtone);
                        // --- Selection Logic ---
                        // This is where you use the user's selection
                        Toast.makeText(
                                MainActivity.this,
                                "Selected Ringtone: " + selectedRingtone,
                                Toast.LENGTH_LONG
                        ).show();

                        // The dialog is automatically dismissed after this block executes.
                    }
                })
                // 4. Show the dialog
                .show();
    }

    void setRingtone(String filename)
    {
        UserAPI api = RetrofitClient.getUserAPI();

        Call<Void> call = api.setRingtone(filename);  // userId is a String or int

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    //Log.d("UserAPI", "User deleted successfully");

                    // You can finish activity or refresh UI
                } else {
                    //Log.e("UserAPI", "Delete failed with code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("UserAPI", "open container failed " + t.getMessage());
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