package com.jjthedev.pilly;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PillViewAdapter extends RecyclerView.Adapter<PillViewAdapter.PillViewHolder> {
    public List<Pill> pills;
    private Context context;
    public PillViewAdapter(Context context, List<Pill> pills) {
        this.context = context;
        this.pills = pills;
    }
    public static class PillViewHolder extends RecyclerView.ViewHolder{
        TextView nametxt;
        //TextView idtxt;
        Button delButton;
        Button addTimeButton;
        RecyclerView timings;

        String[] dosages;
        Spinner dosage_spinner;
        public PillViewHolder(View view)
        {
            super(view);
            nametxt = view.findViewById(R.id.pillname);
            timings = view.findViewById(R.id.timings);
            delButton = view.findViewById(R.id.delete_pill);
            addTimeButton = view.findViewById(R.id.addTime);
            //idtxt = view.findViewById(R.id.idText);
            dosages = view.getResources().getStringArray(R.array.dosages);
            dosage_spinner = view.findViewById(R.id.dosage_select_spinner);
        }
    }

    @Override
    public PillViewAdapter.PillViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.pillitem, parent, false);
        return new PillViewAdapter.PillViewHolder(v);
    }

    public void onBindViewHolder(PillViewAdapter.PillViewHolder holder, int position) {
        Pill pill = pills.get(position);
        holder.nametxt.setText(pill.name);

        holder.delButton.setOnClickListener(v -> {
            pills.remove(position);
            this.notifyDataSetChanged();
        });
        holder.addTimeButton.setOnClickListener(v->{

        });


    }

    @Override
    public int getItemCount() {
        if (pills != null)
        {
            return pills.size();
        }
        return 0;
    }
}
