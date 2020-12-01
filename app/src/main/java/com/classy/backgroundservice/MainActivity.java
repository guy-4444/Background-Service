package com.classy.backgroundservice;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.paz.accesstolib.GiveMe;
import com.paz.accesstolib.GrantListener;

import java.util.ArrayList;
import java.util.List;
import com.classy.backgroundservice.PathImageGenerator.LatLng;

public class MainActivity extends AppCompatActivity {

    MaterialButton main_BTN_permission1;
    MaterialButton main_BTN_permission2;
    MaterialButton main_BTN_start;
    MaterialButton main_BTN_pause;
    MaterialButton main_BTN_stop;
    GiveMe giveMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("pttt", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
        initViews();

        giveMe = new GiveMe(this);
        giveMe.setGrantListener(new GrantListener() {
            @Override
            public void onGranted(boolean allGranted) {

            }

            @Override
            public void onNotGranted(String[] permissions) {

            }

            @Override
            public void onNeverAskAgain(String[] permissions) {

            }
        });
    }

    @Override
    protected void onStart() {
        Log.d("pttt", "onStart");
        super.onStart();
    }

    private void initViews() {
        main_BTN_permission1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                giveMe.requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                });
            }
        });

        main_BTN_permission2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                giveMe.requestPermissions(new String[]{
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                });
            }
        });

        main_BTN_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionToService(LocationService.START_FOREGROUND_SERVICE);
            }
        });

        main_BTN_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionToService(LocationService.PAUSE_FOREGROUND_SERVICE);
            }
        });

        main_BTN_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionToService(LocationService.STOP_FOREGROUND_SERVICE);
            }
        });

        ((MaterialButton) findViewById(R.id.open)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isMyServiceRunning(LocationService.class);
//                generateMapPath();
            }
        });
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runs = manager.getRunningServices(Integer.MAX_VALUE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void actionToService(String action) {
        Intent startIntent = new Intent(MainActivity.this, LocationService.class);
        startIntent.setAction(action);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(startIntent);
            // or
            //ContextCompat.startForegroundService(this, startIntent);
        } else {
            startService(startIntent);
        }
    }

    private void generateMapPath() {
        ArrayList<LatLng> latLngs = new ArrayList<>();
        latLngs.add(new LatLng(32.10533959459135, 34.80692872947253));

        Bitmap bitmap = PathImageGenerator.generatePath(latLngs);
        ((ImageView) findViewById(R.id.imageView)).setImageBitmap(bitmap);
    }

    private void findViews() {
        main_BTN_permission1 = findViewById(R.id.main_BTN_permission1);
        main_BTN_permission2 = findViewById(R.id.main_BTN_permission2);
        main_BTN_start = findViewById(R.id.main_BTN_start);
        main_BTN_pause = findViewById(R.id.main_BTN_pause);
        main_BTN_stop = findViewById(R.id.main_BTN_stop);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        giveMe.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        giveMe.onActivityResult(requestCode, resultCode, data);
    }
}