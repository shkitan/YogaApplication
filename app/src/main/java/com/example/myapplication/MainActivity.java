package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    Button buttonW, buttonD, buttonC, buttonT;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menue_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.item2:
                Intent intent = new Intent(MainActivity.this, guideActivity.class);
                startActivity(intent);
                return true;
            case R.id.item4:
                Intent intent1 = new Intent(MainActivity.this, AboutUs.class);
                startActivity(intent1);
                return true;
            case R.id.item5:
                Intent intent2 = new Intent(MainActivity.this, AboutApp.class);
                startActivity(intent2);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 0);
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 0);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        buttonC = findViewById(R.id.cobraView);
        buttonW = findViewById(R.id.warriorView);
        buttonD = findViewById(R.id.dogView);
        buttonT = findViewById(R.id.treeView);
//        buttonS = findViewById(R.id.startP);
        FloatingActionButton mFab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, guideActivity.class);
                startActivity(intent);
            }
        });

        buttonC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, cobra.class);
                startActivity(intent);
            }
        });

        buttonD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Dog.class);
                startActivity(intent);
            }
        });

        buttonT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Tree.class);
                startActivity(intent);
            }
        });

        buttonW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WarriorTwo.class);
                startActivity(intent);
            }
        });

    }

    public void treeBtn(View view) {
        Intent intent = new Intent(MainActivity.this, Tree.class);
        startActivity(intent);
    }

    public void cobraBtn(View view) {
        Intent intent = new Intent(MainActivity.this, cobra.class);
        startActivity(intent);
    }

    public void dogBtn(View view) {
        Intent intent = new Intent(MainActivity.this, Dog.class);
        startActivity(intent);
    }

    public void warriorBtn(View view) {
        Intent intent = new Intent(MainActivity.this, WarriorTwo.class);
        startActivity(intent);
    }

    public void AboutAppBtn(View view) {
        Intent intent = new Intent(MainActivity.this, AboutApp.class);
        startActivity(intent);
    }

    public void AboutUsBtn(View view) {
        Intent intent = new Intent(MainActivity.this, AboutUs.class);
        startActivity(intent);
    }


}