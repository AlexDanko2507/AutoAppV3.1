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

import com.example.autoappv31.Activity.CategoryListActivity;
import com.example.autoappv31.Models.Category;
import com.example.autoappv31.R;

import java.util.List;

public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.ViewHolder> {

    private LayoutInflater layoutInflater;
    private List<Category> listCat;
    AppCompatActivity activity;

    public CategoryListAdapter(Context context, List<Category> listCat, AppCompatActivity activity)
    {
        this.layoutInflater = LayoutInflater.from(context);
        this.listCat = listCat;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.view_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String name = listCat.get(position).getName();

        holder.textName.setText(name);

    }

    @Override
    public int getItemCount() {
        return listCat.size();
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
            textName = itemView.findViewById(R.id.nameCategory);
            delBtn = itemView.findViewById(R.id.deleteCategory);
            delBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String t = listCat.get(getAdapterPosition()).getId();
                    ((CategoryListActivity)activity).alertDel(t);
                }
            });
        }
    }
}
