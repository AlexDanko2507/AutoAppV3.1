package com.example.autoappv31.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.autoappv31.Models.User;
import com.example.autoappv31.R;
import com.example.autoappv31.Util.NetworkService;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import android.util.Base64;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    String TAG = "RegisterActivity";

    TextView toPageLogin;
    TextView registerBtn;
    CircleImageView userImage;
    AutoCompleteTextView username;
    AutoCompleteTextView email;
    EditText password;
    EditText re_password;

    ProgressBar mProgressBar;
    Uri mImageUri;
    StorageReference mStorageRef;
    String imageUrlR;

    static final int GALLERY_REQUEST = 0;
    InputStream imageStream;
    Bitmap bitmap = null;

    StorageTask mUploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
        listClick();
    }

    private void init()
    {
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");

        toPageLogin = findViewById(R.id.toLoginPage);
        registerBtn = findViewById(R.id.registerBtn);
        userImage = findViewById(R.id.userImage);
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        re_password = findViewById(R.id.re_password);
    }

    private void listClick()
    {
        toPageLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String usernameR = username.getText().toString();
                final String emailR = email.getText().toString();
                final String passwordR = password.getText().toString();
                String re_passwordR = re_password.getText().toString();

                if (passwordR.equals(re_passwordR)) {

                    if (mImageUri != null) {
                        final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(mImageUri));
                        mUploadTask = fileReference.putFile(mImageUri)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                imageUrlR = uri.toString();
                                                sendRequest(usernameR,emailR, passwordR);
                                            }
                                        });

                                    }
                                });
                    } else {
                        sendRequest(usernameR,emailR, passwordR);
                        //Toast.makeText(RegisterActivity.this, "No file selected", Toast.LENGTH_SHORT).show();
                    }
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


    private void sendRequest(String usernameR, String emailR, String passwordR)
    {
        NetworkService.getInstance(RegisterActivity.this)
                .getJSONApi()
                .signup( usernameR, emailR, passwordR, imageUrlR)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.code() == 200) {
                            Toast.makeText(RegisterActivity.this, "Регистрация прошла успешно", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
//                            Toast.makeText(RegisterActivity.this,
//                                    "Signed up successfully", Toast.LENGTH_LONG).show();
                        } else if (response.code() == 400) {
                            Toast.makeText(RegisterActivity.this,
                                    "Пользователь уже зарегистрирован", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Log.w(TAG, "NO SUCCESS!!!!!!!!!!!!!!!!!");
                        Log.e(TAG, t.getMessage());
                        t.printStackTrace();
                    }
                });
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

    private void uploadFile() {
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
                                    imageUrlR = fileReference.getDownloadUrl().toString();
                                }
                            });

                        }
                    });
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

}
