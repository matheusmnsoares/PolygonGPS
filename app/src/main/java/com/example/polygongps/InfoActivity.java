package com.example.polygongps;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        setTitle("Activity Info");

        final ImageView imageView = findViewById(R.id.infoInfo);
        Button button = findViewById(R.id.infoButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent intent = new Intent(InfoActivity.this, MainActivity.class);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(InfoActivity.this, imageView, ViewCompat.getTransitionName(imageView));
                startActivity(intent, options.toBundle());
            }
        });
    }
}