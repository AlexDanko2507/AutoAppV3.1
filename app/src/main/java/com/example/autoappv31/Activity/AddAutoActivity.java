package com.example.autoappv31.Activity;

import androidx.annotation.NonNull;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.autoappv31.ActiveAutoManager;
import com.example.autoappv31.BuildConfig;
import com.example.autoappv31.Models.Auto;
import com.example.autoappv31.Models.Mark;
import com.example.autoappv31.Models.Model;
import com.example.autoappv31.Models.RecognizeModel;
import com.example.autoappv31.R;
import com.example.autoappv31.SessionManager;
import com.example.autoappv31.Util.NetworkService;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.whiteelephant.monthpicker.MonthPickerDialog;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddAutoActivity extends AppCompatActivity {

    static final int GALLERY_REQUEST = 0;
    private static final int CAMERA_REQUEST = 1;

    ProgressBar progressBar;

    Intent intent;
    Integer mode;

    AlertDialog dialogScanAuto;
    File mPhotoFile;
    private String pictureFilePath;
    boolean checkCamera;

    Uri mImageUri;
    StorageReference mStorageRef;
    String imageUrlR;
    InputStream imageStream;
    Bitmap bitmap = null;

    StorageTask mUploadTask;


    SessionManager sessionManager;
    ActiveAutoManager activeAutoManager;
    Auto editAuto;

    TextView toolbarText;
    Toolbar toolbar;

    ImageView autoImage;
    ImageView listMark;
    ImageView listModel;
    TextView titleMark;
    EditText titleModel;
    EditText titleFuel;
    EditText titleRun;
    TextView titleYear;
    EditText titleComments;
    LinearLayout btnMark;
    TextView saveBtn;

    NetworkService networkService;
    List<Mark> marks;
    List<Model> models;
    Mark currentMark;
    Model currentModel;

    SpinnerDialog spinnerDialogMark;
    SpinnerDialog spinnerDialogModels;
    ArrayList<String> stringMarks;
    ArrayList<String> stringModels;
    int choosenYear = 2017;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_auto);
        init();
        listClick();
    }

    private void listClick() {
        autoImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Integer run = Integer.parseInt(titleRun.getText().toString());
                Integer year = Integer.parseInt(titleYear.getText().toString());
                Integer fuel = Integer.parseInt((titleFuel.getText().toString()));
                String comments = titleComments.getText().toString();
                String imgUrl = currentMark.getImageUrl();
                String mark = currentMark.getId();
                String user = sessionManager.getUser().getId();
                String newModel = titleModel.getText().toString();
                String model, modelName;
                if (!stringModels.contains(newModel))
                {
                    model = null;
                    modelName = newModel;
                }else {
                    model = currentModel.getId();
                    modelName = currentModel.getName();
                }
                if(mode == 1)
                {
                    updateAuto(editAuto.getId(),run,year,fuel,comments,imgUrl,model,mark,user,modelName);
                }else
                {
                    createAuto(run,year,fuel,comments,imgUrl,model,mark,user,modelName);
                }


            }
        });

        btnMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnerDialogMark.showSpinerDialog();
            }
        });

        spinnerDialogMark.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
                titleMark.setText(item);
                titleModel.setText("");
                currentMark = marks.get(position);
                Glide.with(AddAutoActivity.this).load(currentMark.getImageUrl()).into(autoImage);
                stringModels.clear();
                loadModels(currentMark.getId());
            }
        });

        spinnerDialogModels.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
                titleModel.setText(item);
                currentModel = models.get(position);
            }
        });

        titleYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(AddAutoActivity.this, new MonthPickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(int selectedMonth, int selectedYear) {
                        titleYear.setText(Integer.toString(selectedYear));
                        choosenYear = selectedYear;
                    }
                }, choosenYear, 0);

                builder.showYearOnly()
                        .setYearRange(1990, 2030)
                        .build()
                        .show();
            }
        });

        listModel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentMark != null)
                {
                    spinnerDialogModels.showSpinerDialog();
                }
            }
        });
    }

    private void init()
    {
        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        sessionManager = new SessionManager(this);
        activeAutoManager = new ActiveAutoManager(this);
        networkService = NetworkService.getInstance(this);
        loadMarks();

        toolbarText = findViewById(R.id.toolbarText);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar = findViewById(R.id.progress_barRecognizeCreateAuto);
        autoImage = findViewById(R.id.autoImage);
        listMark = findViewById(R.id.listMark);
        listModel = findViewById(R.id.listModel);
        titleMark = findViewById(R.id.getTitleMark);
        titleModel = findViewById(R.id.getTitleModel);
        titleFuel = findViewById(R.id.getTitleFuel);
        titleRun = findViewById(R.id.getTitleRun);
        titleYear = findViewById(R.id.getTitleYear);
        titleComments = findViewById(R.id.getTitleComments);
        btnMark = findViewById(R.id.markBtn);
        saveBtn = findViewById(R.id.saveAutoBtn);

        stringMarks = new ArrayList<>();
        stringModels = new ArrayList<>();

        spinnerDialogMark=new SpinnerDialog(AddAutoActivity.this,stringMarks,"Выберите марку","Отмена");
        spinnerDialogMark.setCancellable(true); // for cancellable
        spinnerDialogMark.setShowKeyboard(false);// for open keyboard by default

        spinnerDialogModels=new SpinnerDialog(AddAutoActivity.this, stringModels,"Выберите модель","Отмена");
        spinnerDialogModels.setCancellable(true); // for cancellable
        spinnerDialogModels.setShowKeyboard(false);// for open keyboard by default

        intent = getIntent();
        mode = intent.getIntExtra("mode",0);
        if (mode == 1)
        {
            toolbarText.setText("Редактировать автомобиль");
            loadEditAuto();
        }
        else
        {
            toolbarText.setText("Добавить автомобиль");
        }

    }

    private void loadEditAuto()
    {
        networkService.getJSONApi()
                .getAutobyId(intent.getStringExtra("auto"))
                .enqueue(new Callback<Auto>() {
                    @Override
                    public void onResponse(Call<Auto> call, Response<Auto> response) {
                        editAuto = response.body();
                        titleMark.setText(editAuto.getMark().getName());
                        titleModel.setText(editAuto.getModel().getName());
                        titleFuel.setText(editAuto.getFuel().toString());
                        titleRun.setText(editAuto.getRun().toString());
                        titleYear.setText(editAuto.getYear().toString());
                        titleComments.setText(editAuto.getComments());
                        Glide.with(AddAutoActivity.this)
                                .load(editAuto.getImageUrl())
                                .into(autoImage);
                        currentMark = editAuto.getMark();
                        currentModel = editAuto.getModel();
                        loadModels(currentMark.getId());
                    }

                    @Override
                    public void onFailure(Call<Auto> call, Throwable t) {

                    }
                });
    }

    private void updateAuto(String editAutoId ,Integer run, Integer year, Integer fuel, String comments, String imageUrl, String modelId, String markId, String userId, String modelName)
    {
        networkService.getJSONApi()
                .updateAuto(editAutoId,run,year,fuel,comments,imageUrl,modelId,markId,userId,modelName)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        Intent intent = new Intent(AddAutoActivity.this, AutoListActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {

                    }
                });
    }

    private void createAuto(Integer run, Integer year, Integer fuel, String comments, String imageUrl, String modelId, String markId, String userId, String modelName){
        networkService.getJSONApi()
                .createAuto(run,year,fuel,comments,imageUrl,modelId,markId,userId,modelName)
                .enqueue(new Callback<Auto>() {
                    @Override
                    public void onResponse(Call<Auto> call, Response<Auto> response) {
                        Auto auto = response.body();

                        if (response.code() == 200) {
                            if(sessionManager.getCountAuto().equals("0"))
                            {
                                activeAutoManager.setActiveAutoId(auto.getId());
                                Integer newCount = Integer.parseInt(sessionManager.getCountAuto())+1;
                                sessionManager.setCountAuto(newCount.toString());
                                Intent intent = new Intent(AddAutoActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else
                            {
                                Integer newCount = Integer.parseInt(sessionManager.getCountAuto())+1;
                                sessionManager.setCountAuto(newCount.toString());
                                Intent intent = new Intent(AddAutoActivity.this, AutoListActivity.class);
                                startActivity(intent);
                                finish();
                            }

//                            Toast.makeText(RegisterActivity.this,
//                                    "Signed up successfully", Toast.LENGTH_LONG).show();
                        } else if (response.code() == 400) {
                            Toast.makeText(AddAutoActivity.this,
                                    "ERROR CREATE AUTO", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Auto> call, Throwable t) {

                    }
                });
    }

    private void loadMarks()
    {
        networkService.getJSONApi()
                .getAllMarks()
                .enqueue(new Callback<List<Mark>>() {
                    @Override
                    public void onResponse(Call<List<Mark>> call, Response<List<Mark>> response) {
                        marks = response.body();
                        //Toast.makeText(AddAutoActivity.this, marks.size()+"", Toast.LENGTH_SHORT).show();
                        for(Integer i =0; i<marks.size();i++)
                        {
                            stringMarks.add(marks.get(i).getName());
                        }

                    }

                    @Override
                    public void onFailure(Call<List<Mark>> call, Throwable t) {

                    }
                });
    }

    private void loadModels(String markId)
    {
        networkService.getJSONApi()
                .getAllModel(markId)
                .enqueue(new Callback<List<Model>>() {
                    @Override
                    public void onResponse(Call<List<Model>> call, Response<List<Model>> response) {
                        models = response.body();
                        for(Integer i =0; i<models.size();i++)
                        {
                            stringModels.add(models.get(i).getName());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Model>> call, Throwable t) {

                    }
                });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.addauto_fragment_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.recognizeAutoMenu:
                AlertDialog.Builder myBuilder = new AlertDialog.Builder(this);
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
                dialogScanAuto = myBuilder.create();
                dialogScanAuto.show();
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadScanModel(String modelId)
    {
        networkService.getJSONApi()
                .getModelbyId(modelId)
                .enqueue(new Callback<Model>() {
                    @Override
                    public void onResponse(Call<Model> call, Response<Model> response) {
                        currentModel = response.body();
                        titleModel.setText(currentModel.getName());
                        progressBar.setVisibility(ProgressBar.INVISIBLE);
                        autoImage.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onFailure(Call<Model> call, Throwable t) {

                    }
                });
    }

    private void loadScanMark(String markId, final String modelId)
    {
        networkService.getJSONApi()
                .getMarkbyId(markId)
                .enqueue(new Callback<Mark>() {
                    @Override
                    public void onResponse(Call<Mark> call, Response<Mark> response) {
                        currentMark = response.body();
                        titleMark.setText(currentMark.getName());
                        Glide.with(AddAutoActivity.this)
                                .load(currentMark.getImageUrl())
                                .into(autoImage);
                        loadModels(currentMark.getId());
                        loadScanModel(modelId);
                    }

                    @Override
                    public void onFailure(Call<Mark> call, Throwable t) {

                    }
                });
    }

    private void scanAuto(String img)
    {
        networkService.getJSONApi()
                .recognizeCreateAuto(img)
                .enqueue(new Callback<RecognizeModel>() {
                    @Override
                    public void onResponse(Call<RecognizeModel> call, final Response<RecognizeModel> response) {
                        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrlR);
                        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                if(response.code() == 200)
                                {
                                    RecognizeModel recognizeModel = response.body();
                                    loadScanMark(recognizeModel.getMarkId(), recognizeModel.getModelId());
                                }
                                else
                                {
                                    Toast.makeText(AddAutoActivity.this, "Не удалось рапознать автомобиль", Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility(ProgressBar.INVISIBLE);
                                    autoImage.setVisibility(View.VISIBLE);
                                }
                            }
                        });

                    }

                    @Override
                    public void onFailure(Call<RecognizeModel> call, Throwable t) {

                    }
                });
    }

    private void uploadFile() {
        progressBar.setVisibility(ProgressBar.VISIBLE);
        autoImage.setVisibility(View.INVISIBLE);
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
                uploadFile();
            }
        } else if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK)
        {
            checkCamera = false;
            mImageUri = data.getData();
            uploadFile();
        }
    }
}
