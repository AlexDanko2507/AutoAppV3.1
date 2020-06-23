package com.example.autoappv31.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.autoappv31.BuildConfig;
import com.example.autoappv31.Models.RecognizeModel;
import com.example.autoappv31.R;
import com.example.autoappv31.Util.NetworkService;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecognizeAutoActivity extends AppCompatActivity {

    static final int GALLERY_REQUEST = 0;
    private static final int CAMERA_REQUEST = 1;
    AlertDialog dialogAutoImage;
    ProgressBar progressBar;

    TextView toolbarText;
    Toolbar toolbar;

    File mPhotoFile;
    private String pictureFilePath;
    private ImageView autoImage;
    TextView sendToScan;
    TextView markScan;
    TextView modelScan;
    TextView propScan;
    boolean checkCamera;

    Uri mImageUri;
    StorageReference mStorageRef;
    String imageUrlR;
    InputStream imageStream;
    Bitmap bitmap = null;

    StorageTask mUploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognize_auto);

        init();
        listClick();

    }

    private void init()
    {
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");

        toolbarText = findViewById(R.id.toolbarText);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarText.setText("Распознавание автомобиля");

        progressBar= findViewById(R.id.progress_barRecognizeAuto);
        markScan = findViewById(R.id.markScan);
        modelScan = findViewById(R.id.modelScan);
        propScan = findViewById(R.id.propScan);
        sendToScan = findViewById(R.id.recognizeAutoScan);
        autoImage = findViewById(R.id.imageView);
        checkCamera = false;
    }

    private void scanAuto(String img)
    {
        NetworkService.getInstance(this)
                .getJSONApi()
                .recognizeAuto(img)
                .enqueue(new Callback<RecognizeModel>() {
                    @Override
                    public void onResponse(Call<RecognizeModel> call, Response<RecognizeModel> response) {
                        if(response.code() == 200)
                        {
                            final RecognizeModel auto = response.body();
                            StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrlR);
                            storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    markScan.setText("Марка: "+auto.getMark());
                                    modelScan.setText("Модель: "+auto.getModel());
                                    propScan.setText("Точность распознавания: "+auto.getProp());
                                    progressBar.setVisibility(ProgressBar.INVISIBLE);
                                }
                            });
                        }
                        else
                        {
                            Toast.makeText(RecognizeAutoActivity.this, "Не удалось распознать автомобиль", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(ProgressBar.INVISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(Call<RecognizeModel> call, Throwable t) {

                    }
                });
    }

    private void listClick()
    {
        autoImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder myBuilder = new AlertDialog.Builder(v.getContext());
                final CharSequence[] test = {"Галерея", "Фото"};
                myBuilder.setItems(test, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which)
                        {
                            case GALLERY_REQUEST:
                                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                                photoPickerIntent.setType("image/*");
                                startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
                                break;
                            case CAMERA_REQUEST:
                                requestPermissions();
                                break;
                        }
                    }
                });
                dialogAutoImage = myBuilder.create();
                dialogAutoImage.show();
            }
        });

        sendToScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mImageUri == null)
                {
                    Toast.makeText(RecognizeAutoActivity.this, "Выберите фото автомобиля!", Toast.LENGTH_LONG).show();
                }else
                {
                    markScan.setText("");
                    modelScan.setText("");
                    propScan.setText("");
                    progressBar.setVisibility(ProgressBar.VISIBLE);
                    uploadFile();
                }
            }
        });
    }

    private void captureImage() {

        PackageManager pm = getPackageManager();

        if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    ex.printStackTrace();
                    // Error occurred while creating the File
                }
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this,
                            BuildConfig.APPLICATION_ID + ".provider",
                            photoFile);
                    mPhotoFile = photoFile;
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, CAMERA_REQUEST);
                }
            }

        } else {

            Toast.makeText(getBaseContext(), "Camera is not available", Toast.LENGTH_LONG).show();
        }
    }

    private void uploadFile() {
        if (mImageUri != null) {
            final StorageReference fileReference;
            if(checkCamera)
            {
                fileReference = mStorageRef.child(System.currentTimeMillis() + ".jpg");
            }
            else{
                fileReference = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(mImageUri));
            }
            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    imageUrlR = uri.toString();
                                    scanAuto(imageUrlR);
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

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                CAMERA_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    captureImage();
                } else {
                    Toast.makeText(this, "We need next permissions: Camera", Toast.LENGTH_LONG).show();
                }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String mFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File mFile = File.createTempFile(mFileName, ".jpg", storageDir);
        pictureFilePath = mFile.getAbsolutePath();
        return mFile;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            File imgFile = new  File(pictureFilePath);
            if(imgFile.exists())            {
                mImageUri = Uri.fromFile(imgFile);
                checkCamera = true;
                try {
                    imageStream = getContentResolver().openInputStream(mImageUri);
                    bitmap = BitmapFactory.decodeStream(imageStream);
                    bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth()/2, bitmap.getHeight()/2, true);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                autoImage.setImageBitmap(bitmap);
            }
        } else if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK)
        {
            checkCamera = false;
            mImageUri = data.getData();
            try {
                imageStream = getContentResolver().openInputStream(mImageUri);
                bitmap = BitmapFactory.decodeStream(imageStream);
                bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth()/2, bitmap.getHeight()/2, true);

            } catch (IOException e) {
                e.printStackTrace();
            }
            autoImage.setImageBitmap(bitmap);
        }
    }
}
