package com.jjthedev.pilly;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserViewAdapter extends RecyclerView.Adapter<UserViewAdapter.UserViewHolder> {

    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }

    public OnDeleteClickListener delListener;
    public List<UserDisplay> users;
    private Context context;
    public UserViewAdapter(Context context, List<UserDisplay> userList, OnDeleteClickListener listener) {
        this.context = context;
        this.users = userList;
        this.delListener = listener;
    }
    public static class UserViewHolder extends RecyclerView.ViewHolder{
        TextView nametxt;
        TextView idtxt;

        ImageButton deleteuser;

        public UserViewHolder(View view)
        {
            super(view);
            nametxt = view.findViewById(R.id.nameText);
            idtxt = view.findViewById(R.id.idText);
            deleteuser = view.findViewById(R.id.mainpage_deleteuser);
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

        holder.deleteuser.setOnClickListener(v->{
            UserAPI api = RetrofitClient.getUserAPI();

            Call<Void> call = api.deleteUser(user.id);  // userId is a String or int

            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Log.d("UserAPI", "User deleted successfully");
                        if (delListener != null)
                        {
                            delListener.onDeleteClick(position);
                        }

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
