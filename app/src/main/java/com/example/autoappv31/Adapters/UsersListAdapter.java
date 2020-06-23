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

import com.example.autoappv31.Activity.AddMoneyActivity;
import com.example.autoappv31.Activity.CategoryListActivity;
import com.example.autoappv31.Activity.EditUserControlActivity;
import com.example.autoappv31.Activity.UserControlActivity;
import com.example.autoappv31.Fragments.MoneyFragment;
import com.example.autoappv31.Models.Category;
import com.example.autoappv31.Models.User;
import com.example.autoappv31.R;
import com.example.autoappv31.Util.NetworkService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsersListAdapter extends RecyclerView.Adapter<UsersListAdapter.ViewHolder> {

    private LayoutInflater layoutInflater;
    private List<User> listUsers;
    AppCompatActivity activity;

    public UsersListAdapter(Context context, List<User> listUsers, AppCompatActivity activity)
    {
        this.layoutInflater = LayoutInflater.from(context);
        this.listUsers = listUsers;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.view_users, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String name = listUsers.get(position).getUsername();
        String role = listUsers.get(position).getRole().getName();

        holder.textName.setText(name);
        holder.textRole.setText(role);
        if(listUsers.get(position).getRole().getName().equals("admin"))
        {
            holder.delBtn.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return listUsers.size();
    }

    public class  ViewHolder extends RecyclerView.ViewHolder{

        TextView textName;
        TextView textRole;
        ImageView delBtn;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(v.getContext(), EditUserControlActivity.class);
                    i.putExtra("user", listUsers.get(getAdapterPosition()).getId());
                    v.getContext().startActivity(i);
                }
            });
            textName = itemView.findViewById(R.id.nameUser);
            textRole = itemView.findViewById(R.id.roleUser);
            delBtn = itemView.findViewById(R.id.deleteUser);
            delBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NetworkService.getInstance(itemView.getContext())
                            .getJSONApi()
                            .deleteUser(listUsers.get(getAdapterPosition()).getId())
                            .enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                    ((UserControlActivity)activity).loadUsers();
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
