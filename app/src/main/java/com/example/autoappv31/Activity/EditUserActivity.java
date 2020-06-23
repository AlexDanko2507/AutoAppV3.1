package com.example.autoappv31.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.autoappv31.Models.User;
import com.example.autoappv31.R;
import com.example.autoappv31.SessionManager;
import com.example.autoappv31.Util.NetworkService;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditUserActivity extends AppCompatActivity {

    SessionManager sessionManager;

    TextView toolbarText;
    Toolbar toolbar;

    Uri mImageUri;
    StorageReference mStorageRef;
    String imageUrlR;
    StorageTask mUploadTask;

    CircleImageView userImage;
    AutoCompleteTextView username;
    AutoCompleteTextView email;
    EditText oldPassword;
    EditText newPassword;
    EditText reNewPassword;
    TextView saveUserEditBtn;
    TextView saveUserEditBtnUsername;

    static final int GALLERY_REQUEST = 0;
    InputStream imageStream;
    Bitmap bitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);
        init();
        listClick();
    }

    private void listClick() {
        saveUserEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePassword(sessionManager.getUser().getId(), oldPassword.getText().toString(), newPassword.getText().toString());
            }
        });

        saveUserEditBtnUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mImageUri != null) {
                    final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                            + "." + getFileExtension(mImageUri));
                    mUploadTask = fileReference.putFile(mImageUri)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            imageUrlR = uri.toString();
                                            update(sessionManager.getUser().getId(), username.getText().toString(), email.getText().toString(), imageUrlR, sessionManager.getRole().getId());
                                        }
                                    });

                                }
                            });
                } else {
                    update(sessionManager.getUser().getId(), username.getText().toString(), email.getText().toString(), null, sessionManager.getRole().getId());
                    //Toast.makeText(RegisterActivity.this, "No file selected", Toast.LENGTH_SHORT).show();
                }


            }
        });

        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
            }
        });
    }

    private void init()
    {
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        sessionManager = new SessionManager(this);

        toolbarText = findViewById(R.id.toolbarText);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarText.setText("Редактировать профиль");

        userImage = findViewById(R.id.editUserImage);
        username = findViewById(R.id.editUsername);
        email = findViewById(R.id.editEmail);
        oldPassword = findViewById(R.id.oldPassword);
        newPassword = findViewById(R.id.newPassword);
        reNewPassword = findViewById(R.id.re_newPassword);
        saveUserEditBtn = findViewById(R.id.saveUserEditBtn);
        saveUserEditBtnUsername = findViewById(R.id.saveUserEditBtnUsername);

        username.setText(sessionManager.getUser().getUsername());
        email.setText(sessionManager.getUser().getEmail());
        if (sessionManager.getUser().getImageUrl()!=null)
        Glide.with(this)
                .load(sessionManager.getUser().getImageUrl())
                .into(userImage);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case GALLERY_REQUEST:
                if(resultCode == RESULT_OK){
                    mImageUri = data.getData();
                    try {
                        imageStream = getContentResolver().openInputStream(mImageUri);
                        bitmap = BitmapFactory.decodeStream(imageStream);
                        bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth()/2, bitmap.getHeight()/2, true);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    userImage.setImageBitmap(bitmap);
                }
        }
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
                            Toast.makeText(EditUserActivity.this,
                                    "Success", Toast.LENGTH_LONG).show();
                            sessionManager.createSession(user);
                            Intent intent = new Intent(EditUserActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else if (response.code() == 400) {
                            Toast.makeText(EditUserActivity.this,
                                    "No exist", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {

                    }
                });
    }

    private void updatePassword(String userId, String oldpassword, String password)
    {
        NetworkService.getInstance(this)
                .getJSONApi()
                .updatePassword(userId, oldpassword, password)
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        User user = response.body();
                        if (response.code() == 200) {
                            Toast.makeText(EditUserActivity.this,
                                    "Success", Toast.LENGTH_LONG).show();
                            sessionManager.createSession(user);
                            Intent intent = new Intent(EditUserActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else if (response.code() == 400) {
                            Toast.makeText(EditUserActivity.this,
                                    "No exist", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {

                    }
                });
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }
}
