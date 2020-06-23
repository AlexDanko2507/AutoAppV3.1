package com.example.autoappv31.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.autoappv31.Activity.AddAutoActivity;
import com.example.autoappv31.Activity.AutoListActivity;
import com.example.autoappv31.Models.Auto;
import com.example.autoappv31.R;
import com.example.autoappv31.SessionManager;
import com.example.autoappv31.Util.NetworkService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AutoListAdapter extends RecyclerView.Adapter<AutoListAdapter.ViewHolder> {

    SessionManager sessionManager;

    private LayoutInflater layoutInflater;
    private List<Auto> listAuto;
    AppCompatActivity activity;

    public AutoListAdapter(Context context, List<Auto> listAuto, AppCompatActivity activity)
    {
        this.layoutInflater = LayoutInflater.from(context);
        this.listAuto = listAuto;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.view_auto, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String model = listAuto.get(position).getModel().getName();
        String mark = listAuto.get(position).getMark().getName();
        String year =  String.valueOf(listAuto.get(position).getYear());
        String run = String.valueOf(listAuto.get(position).getRun());
        if (listAuto.get(position).getImageUrl()!=null) {
            Glide.with(holder.itemView.getContext())
                    .load(listAuto.get(position).getImageUrl())
                    .into(holder.iconAuto);
        }

        holder.textModel.setText(model);
        holder.textMark.setText(mark);
        holder.textYear.setText("Год выпуска: "+year);
        holder.textRun.setText("Пробег: "+run);
    }

    @Override
    public int getItemCount() {
        return listAuto.size();
    }

    public class  ViewHolder extends RecyclerView.ViewHolder{

        TextView textMark, textModel, textYear, textRun;
        ImageView deleteAuto, iconAuto;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);

            sessionManager = new SessionManager(itemView.getContext());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(v.getContext(), AddAutoActivity.class);
                    i.putExtra("mode", 1);
                    i.putExtra("auto", listAuto.get(getAdapterPosition()).getId());
                    v.getContext().startActivity(i);
                }
            });
            textMark = itemView.findViewById(R.id.markAuto);
            textModel = itemView.findViewById(R.id.modelAuto);
            textYear = itemView.findViewById(R.id.yearAuto);
            textRun = itemView.findViewById(R.id.runAuto);
            deleteAuto = itemView.findViewById(R.id.deleteAuto);
            iconAuto = itemView.findViewById(R.id.iconAuto);

            deleteAuto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NetworkService.getInstance(itemView.getContext())
                            .getJSONApi()
                            .deleteAuto(listAuto.get(getAdapterPosition()).getId())
                            .enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                    Integer newCount = Integer.parseInt(sessionManager.getCountAuto())-1;
                                    sessionManager.setCountAuto(newCount.toString());
                                    ((AutoListActivity)activity).findAutos(sessionManager.getUser().getId());
                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {

                                }
                            });
                }
            });
        }
    }

}
