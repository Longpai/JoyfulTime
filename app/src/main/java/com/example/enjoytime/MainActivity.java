package com.example.enjoytime;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View section1 = findViewById(R.id.view_section_1);
        View section2 = findViewById(R.id.view_section_2);
        View section3 = findViewById(R.id.view_section_3);

        if (section1 != null) {
            section1.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, SectionOneActivity.class);
                startActivity(intent);
            });
        }

        if (section2 != null) {
            section2.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, SectionTwoActivity.class);
                startActivity(intent);
            });
        }

        if (section3 != null) {
            section3.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, SectionThreeActivity.class);
                startActivity(intent);
            });
        }
    }
}