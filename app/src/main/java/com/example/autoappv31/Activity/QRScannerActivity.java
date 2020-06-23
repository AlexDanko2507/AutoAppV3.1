package com.example.autoappv31.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.autoappv31.R;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QRScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    ZXingScannerView zXingScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscanner);
        scan();
    }

    public void scan()
    {
        zXingScannerView = new ZXingScannerView(getApplicationContext());
        setContentView(zXingScannerView);
        zXingScannerView.setResultHandler(this);
        zXingScannerView.startCamera();

    }

    @Override
    protected void onPause() {
        super.onPause();
        zXingScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result result) {
        //Toast.makeText(getApplicationContext(), result.getText(), Toast.LENGTH_SHORT).show();
        if (result.getText().startsWith("t=")) {
            Intent i = new Intent(QRScannerActivity.this, AddMoneyActivity.class);
            i.putExtra("QRString", result.getText());
            startActivity(i);
        } else {
            Toast.makeText(getApplicationContext(), "Пожалуйста, отсканируйте чек!", Toast.LENGTH_SHORT).show();
            zXingScannerView.resumeCameraPreview(this);
        }
    }
}