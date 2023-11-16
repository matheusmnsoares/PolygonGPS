package com.example.polygongps;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;

import android.content.Intent;
import android.os.Bundle;
import android.transition.Fade;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Activity Main");

        Fade fade = new Fade();
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);

        getWindow().setEnterTransition(fade);
        getWindow().setExitTransition(fade);

        final ImageView imageView = findViewById(R.id.mainInfo);
        Button infoButton = findViewById(R.id.buttonInfo);
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, InfoActivity.class);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, imageView, ViewCompat.getTransitionName(imageView));
                startActivity(intent, options.toBundle());
            }
        });
    }
    public void slide(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }
}
