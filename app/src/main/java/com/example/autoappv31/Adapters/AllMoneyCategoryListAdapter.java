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
import androidx.recyclerview.widget.RecyclerView;

import com.example.autoappv31.Activity.AddBuildActivity;
import com.example.autoappv31.Activity.AddMoneyActivity;
import com.example.autoappv31.Activity.AllMoneyActivity;
import com.example.autoappv31.Activity.AllMoneyCategoryActivity;
import com.example.autoappv31.Models.Expense;
import com.example.autoappv31.R;
import com.example.autoappv31.Util.NetworkService;

import java.text.SimpleDateFormat;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllMoneyCategoryListAdapter extends RecyclerView.Adapter<AllMoneyCategoryListAdapter.ViewHolder> {


    private LayoutInflater layoutInflater;
    private List<Expense> listMoney;
    AppCompatActivity activity;

    public AllMoneyCategoryListAdapter(Context context, List<Expense> listMoney, AppCompatActivity activity)
    {
        this.layoutInflater = LayoutInflater.from(context);
        this.listMoney = listMoney;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.view_money, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String name = listMoney.get(position).getAuto().getMark().getName() + " " + listMoney.get(position).getAuto().getModel().getName();
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
        String date = formatter.format(listMoney.get(position).getDate());
        String price =  String.valueOf(listMoney.get(position).getSum());

        if(listMoney.get(position).getCategory().getName().equals("ТО"))
        {
            holder.buildMoney.setVisibility(View.VISIBLE);
        }

        holder.textName.setText(name);
        holder.textComment.setText(date);
        holder.textPrice.setText(price+" руб. ");

    }

    @Override
    public int getItemCount() {
        return listMoney.size();
    }

    public class  ViewHolder extends RecyclerView.ViewHolder{

        TextView textName, textComment, textPrice;
        ImageView deleteMoney, buildMoney;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(v.getContext(), AddMoneyActivity.class);
                    i.putExtra("mode", 1);
                    i.putExtra("expense", listMoney.get(getAdapterPosition()).getId());
                    v.getContext().startActivity(i);
                }
            });
            textName = itemView.findViewById(R.id.nameMoney);
            textComment = itemView.findViewById(R.id.dateMoney);
            textPrice = itemView.findViewById(R.id.priceMoney);
            deleteMoney = itemView.findViewById(R.id.deleteMoney);
            buildMoney = itemView.findViewById(R.id.buildMoney);
            deleteMoney.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NetworkService.getInstance(itemView.getContext())
                            .getJSONApi()
                            .deleteExpense(listMoney.get(getAdapterPosition()).getId())
                            .enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                    ((AllMoneyCategoryActivity)activity).loadExpense();
                                }

                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {

                                }
                            });
                }
            });
            buildMoney.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(v.getContext(), AddBuildActivity.class);
                    i.putExtra("mode", 2);
                    i.putExtra("build", listMoney.get(getAdapterPosition()).getTechnicalWork().getId());
                    v.getContext().startActivity(i);
                }
            });
        }
    }
}
