package com.jjthedev.pilly;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class UserPageActivity extends AppCompatActivity {

    PillViewAdapter pilladapter;

    List<Pill> pill_display = new ArrayList<>();

    String username;
    Integer userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        RecyclerView pill_list = findViewById(R.id.pill_list);
        pill_list.setLayoutManager(new LinearLayoutManager(this));
        pilladapter = new PillViewAdapter(this, pill_display);

        pill_list.setAdapter(pilladapter);
    }

    void fetchUserData()
    {

    }
}