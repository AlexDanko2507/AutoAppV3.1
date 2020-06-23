package com.example.autoappv31.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autoappv31.ActiveAutoManager;
import com.example.autoappv31.Activity.AddBuildActivity;
import com.example.autoappv31.Activity.AllBuildActivity;
import com.example.autoappv31.Fragments.BuildFragment;
import com.example.autoappv31.Models.TechnicalWork;
import com.example.autoappv31.R;
import com.example.autoappv31.Util.NetworkService;

import java.text.SimpleDateFormat;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllBuildListAdapter extends RecyclerView.Adapter<AllBuildListAdapter.ViewHolder> {

    ActiveAutoManager activeAutoManager;

    private LayoutInflater layoutInflater;
    private List<TechnicalWork> listWork;
    AppCompatActivity activity;

    public AllBuildListAdapter(Context context, List<TechnicalWork> listWork, AppCompatActivity activity)
    {
        this.layoutInflater = LayoutInflater.from(context);
        this.listWork = listWork;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.view_build, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String name = listWork.get(position).getName();
        String comments = listWork.get(position).getComments();
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        String date = formatter.format(listWork.get(position).getDate());


        holder.textName.setText(name);
        holder.textComments.setText(comments);
        if(listWork.get(position).isStatus())
        holder.textDate.setText("Запланировано на: "+date);
        else holder.textDate.setText("Завершено: "+date);
    }

    @Override
    public int getItemCount() {
        return listWork.size();
    }

    public class  ViewHolder extends RecyclerView.ViewHolder{

        TextView textName, textComments, textDate;
        ImageView delBtn;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);

            activeAutoManager = new ActiveAutoManager(itemView.getContext());


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent i = new Intent(v.getContext(), AddBuildActivity.class);
//                    i.putExtra("mode", 1);
//                    i.putExtra("build", listWork.get(getAdapterPosition()).getId());
//                    v.getContext().startActivity(i);
                }
            });
            textName = itemView.findViewById(R.id.nameBuild);
            textComments = itemView.findViewById(R.id.commentsBuild);
            textDate = itemView.findViewById(R.id.dateBuild);
            delBtn = itemView.findViewById(R.id.deleteBuild);

            delBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NetworkService.getInstance(itemView.getContext())
                            .getJSONApi()
                            .deleteTechnicalWork(listWork.get(getAdapterPosition()).getId())
                            .enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                    ((AllBuildActivity)activity).loadTechnicalWorkAll(activeAutoManager.getActiveAutoId());
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

