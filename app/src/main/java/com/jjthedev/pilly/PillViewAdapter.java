package com.jjthedev.pilly;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        ImageButton delButton;
        ImageButton addTimeButton;
        RecyclerView timings;

        ImageButton editPill;
        String[] dosages;
        Spinner dosage_spinner;

        TimeViewAdapter timeadapter;

        List<Integer> time_display;
        public PillViewHolder(View view)
        {
            super(view);
            nametxt = view.findViewById(R.id.pillname);
            timings = view.findViewById(R.id.timings);
            delButton = view.findViewById(R.id.delete_pill);
            addTimeButton = view.findViewById(R.id.addTime);
            //idtxt = view.findViewById(R.id.idText);
            dosages = view.getResources().getStringArray(R.array.dosages);
            //dosage_spinner = view.findViewById(R.id.dosage_select_spinner);

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
            newTimingDialog(this.context, holder);
        });

        holder.time_display = pill.timings;

        holder.timeadapter = new TimeViewAdapter(this.context, holder.time_display);
        holder.timings.setLayoutManager(
                new LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
        );
        holder.timings.setAdapter(holder.timeadapter);

        Log.d("times", holder.time_display.toString());
        holder.timeadapter.notifyDataSetChanged();

    }

    void newTimingDialog(Context context, PillViewHolder holder)
    {
        Pill p = new Pill();
        View view1 = LayoutInflater.from(context).inflate(R.layout.new_timing_dialog,null);
        TextInputEditText hourInp = view1.findViewById(R.id.hour_input);
        TextInputEditText minInp = view1.findViewById(R.id.minute_input);

        AlertDialog al1 = new MaterialAlertDialogBuilder(context)
                .setTitle("New Time in 24H")
                .setView(view1)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            Integer hour = Integer.parseInt(hourInp.getText().toString());
                            Integer min = Integer.parseInt(minInp.getText().toString());
                            holder.time_display.add(hour * 60 + min);
                            holder.timeadapter.notifyDataSetChanged();
                            dialog.dismiss();
                        } catch (Exception e) {

                        }


                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        al1.show();
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
