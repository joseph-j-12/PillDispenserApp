package com.jjthedev.pilly;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

public class MainActivity extends AppCompatActivity {
    UserViewAdapter adapter;
    List<UserDisplay> userList = new ArrayList<>();

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
        Button refButton = findViewById(R.id.button_refresh_userlist);
        RecyclerView userlistview = findViewById(R.id.recycler_userlist);

        userlistview.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UserViewAdapter(this, userList);
        userlistview.setAdapter(adapter);

        refButton.setOnClickListener(v->fetchUserList());

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
            }
        });

    }
}