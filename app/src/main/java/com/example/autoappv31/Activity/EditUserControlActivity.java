package com.example.autoappv31.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.autoappv31.ActiveAutoManager;
import com.example.autoappv31.Models.Auto;
import com.example.autoappv31.Models.Role;
import com.example.autoappv31.Models.User;
import com.example.autoappv31.R;
import com.example.autoappv31.SessionManager;
import com.example.autoappv31.Util.NetworkService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditUserControlActivity extends AppCompatActivity {

    Intent intent;
    SessionManager sessionManager;
    NetworkService networkService;
    User editUser;

    TextView toolbarText;
    Toolbar toolbar;

    EditText username;
    EditText email;
    TextView role;
    TextView saveBtn;

    List<Role> roles;
    ArrayList<String> rolesName;
    AlertDialog setRoleDialog;
    DialogInterface.OnClickListener setRoleClickListener;
    CharSequence[] rolesString;
    String currentRoleId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_control);

        init();
        listClick();

    }

    private void listClick()
    {
        setRoleClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == Dialog.BUTTON_NEGATIVE)
                {
                    dialog.dismiss();
                }
                else
                {
                    role.setText(roles.get(which).getName());
                    currentRoleId = roles.get(which).getId();
                }
            }
        };

        role.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView userAutoName = new TextView(EditUserControlActivity.this);
                userAutoName.setText("Выбрать роль пользователя");
                userAutoName.setPadding(20, 30, 20, 30);
                userAutoName.setTextSize(20F);
                userAutoName.setBackgroundColor(Color.rgb(00,85,77));
                userAutoName.setTextColor(Color.WHITE);
                AlertDialog.Builder myBuilder = new AlertDialog.Builder(EditUserControlActivity.this);
                myBuilder.setCustomTitle(userAutoName);
                myBuilder.setNegativeButton("Отмена", setRoleClickListener);
                myBuilder.setItems(rolesString, setRoleClickListener);
                setRoleDialog = myBuilder.create();
                setRoleDialog.show();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usernameM = username.getText().toString();
                String emailM = email.getText().toString();
                update(editUser.getId(),usernameM,emailM, editUser.getImageUrl(), currentRoleId);
            }
        });
    }

    private void init()
    {
        networkService = NetworkService.getInstance(this);
        sessionManager = new SessionManager(this);

        toolbarText = findViewById(R.id.toolbarText);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarText.setText("Изменить данные пользователя");

        username = findViewById(R.id.getTitleUsernameControl);
        email = findViewById(R.id.getTitleEmailControl);
        role = findViewById(R.id.getTitleRoleControl);
        saveBtn = findViewById(R.id.saveUserControlBtn);

        intent = getIntent();
        getUserInformation(intent.getStringExtra("user"));
        loadRole();
    }

    private void loadRole()
    {
        networkService.getJSONApi()
                .getAllRole()
                .enqueue(new Callback<List<Role>>() {
                    @Override
                    public void onResponse(Call<List<Role>> call, Response<List<Role>> response) {
                        roles = response.body();
                        rolesName = new ArrayList<>();
                        for (Integer i =0;i<roles.size();i++)
                        {
                            rolesName.add(roles.get(i).getName());
                        }
                        rolesString = rolesName.toArray(new CharSequence[rolesName.size()]);
                    }

                    @Override
                    public void onFailure(Call<List<Role>> call, Throwable t) {

                    }
                });
    }

    private void getUserInformation(String userId)
    {
        networkService.getJSONApi()
                .getUserbyId(userId)
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        editUser = response.body();
                        username.setText(editUser.getUsername());
                        email.setText(editUser.getEmail());
                        role.setText(editUser.getRole().getName());
                        currentRoleId = editUser.getRole().getId();
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {

                    }
                });
    }

    private void update(String userId, String username, String email, String imageUrl, String roleId)
    {
        NetworkService.getInstance(this)
                .getJSONApi()
                .update(userId, username, email, imageUrl, roleId)
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        User user = response.body();
                        if (response.code() == 200) {
                            if(sessionManager.getUser().getId().equals(user.getId()))
                            {
                                sessionManager.createSession(user);
                            }

                            Intent intent = new Intent(EditUserControlActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else if (response.code() == 400) {
                            Toast.makeText(EditUserControlActivity.this,
                                    "No exist", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {

                    }
                });
    }
}
