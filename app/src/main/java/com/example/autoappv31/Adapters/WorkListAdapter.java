package com.example.autoappv31.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autoappv31.Activity.CategoryWorkListActivity;
import com.example.autoappv31.Models.CategoryWork;
import com.example.autoappv31.R;

import java.util.List;

public class WorkListAdapter extends RecyclerView.Adapter<WorkListAdapter.ViewHolder> {

    private LayoutInflater layoutInflater;
    private List<CategoryWork> listWork;
    AppCompatActivity activity;

    public WorkListAdapter(Context context, List<CategoryWork> listWork, AppCompatActivity activity)
    {
        this.layoutInflater = LayoutInflater.from(context);
        this.listWork = listWork;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.view_works, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String name = listWork.get(position).getName();

        holder.textName.setText(name);

    }

    @Override
    public int getItemCount() {
        return listWork.size();
    }

    public class  ViewHolder extends RecyclerView.ViewHolder{

        TextView textName;
        ImageView delBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            textName = itemView.findViewById(R.id.nameWork);
            delBtn = itemView.findViewById(R.id.deleteWorks);
            delBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String t = listWork.get(getAdapterPosition()).getId();
                    ((CategoryWorkListActivity)activity).alertDel(t);
                }
            });
        }
    }
}
