package com.jjthedev.pilly;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TimeViewAdapter extends RecyclerView.Adapter<TimeViewAdapter.TimeViewHolder> {

    public List<Integer> timings;
    private Context context;
    public TimeViewAdapter(Context context, List<Integer> timings)
    {
        this.timings = timings;
        this.context = context;
    }
    public static class TimeViewHolder extends RecyclerView.ViewHolder{
        TextView hour;
        TextView min;

        public TimeViewHolder(View view)
        {
            super(view);
            this.hour = view.findViewById(R.id.timeTextHr);
            this.min = view.findViewById(R.id.timeTextMin);

        }
    }

    @Override
    public TimeViewAdapter.TimeViewHolder onCreateViewHolder(ViewGroup parent, int viewtype)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.time_item, parent, false);
        return new TimeViewAdapter.TimeViewHolder(v);
    }


    public void onBindViewHolder(TimeViewAdapter.TimeViewHolder holder, int position) {
        Integer time = timings.get(position);

        String hour = String.format("%02d", time / 60);
        String min  = String.format("%02d", time % 60);

        holder.hour.setText(hour);
        holder.min.setText(min);

        holder.itemView.setOnClickListener(v->{
            timings.remove(position);
            notifyDataSetChanged();
        });

        Log.d("TimeViewAdapter", time.toString());
//        holder.nametxt.setText(pill.name);
//
//        holder.delButton.setOnClickListener(v -> {
//            pills.remove(position);
//            this.notifyDataSetChanged();
//        });
//
//        holder.addTimeButton.setOnClickListener(v->{
//
//        });


    }

    @Override
    public int getItemCount() {
        if (timings != null)
        {
            return timings.size();
        }
        return 0;
    }
}
