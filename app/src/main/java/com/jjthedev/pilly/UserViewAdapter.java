package com.jjthedev.pilly;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UserViewAdapter extends RecyclerView.Adapter<UserViewAdapter.UserViewHolder> {

    public List<UserDisplay> users;
    private Context context;
    public UserViewAdapter(Context context, List<UserDisplay> userList) {
        this.context = context;
        this.users = userList;
    }
    public static class UserViewHolder extends RecyclerView.ViewHolder{
        TextView nametxt;
        TextView idtxt;

        public UserViewHolder(View view)
        {
            super(view);
            nametxt = view.findViewById(R.id.nameText);
            idtxt = view.findViewById(R.id.idText);
        }
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.useritem, parent, false);
        return new UserViewHolder(v);
    }

    public void onBindViewHolder(UserViewHolder holder, int position) {
        UserDisplay user = users.get(position);
        holder.nametxt.setText(user.name);
        holder.idtxt.setText("ID: " + user.id);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, UserPageActivity.class);
            intent.putExtra("userid", user.id);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        if (users != null)
        {
            return users.size();
        }
        return 0;
    }

}
