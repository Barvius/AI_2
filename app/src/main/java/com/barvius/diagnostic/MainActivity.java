package com.barvius.diagnostic;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.v7.widget.Toolbar;
import android.widget.Button;

import com.barvius.diagnostic.ui.StatusBarTools;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        StatusBarTools.setStatusBarColor(getWindow(),getResources().getColor(R.color.backgroundAppBarDark));

        DBHandler.init(getApplicationContext());

        Button btn_diagnose_list = findViewById(R.id.btn_diagnose_list);
        btn_diagnose_list.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DiagnoseListActivity.class);
            startActivity(intent);
        });

        Button btn_symptom_list = findViewById(R.id.btn_symptom_list);
        btn_symptom_list.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SymptomListActivity.class);
            startActivity(intent);
        });

        Button btn_symptom_test_start = findViewById(R.id.btn_symptom_test_start);
        btn_symptom_test_start.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TestActivity.class);
            startActivity(intent);
        });
    }
}
