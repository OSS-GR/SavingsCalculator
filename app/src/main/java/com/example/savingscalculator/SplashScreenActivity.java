package com.example.savingscalculator;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.savingscalculator.swipe.SwipeSelectionActivity;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        int SPLASH_TIME_OUT = 3100;
        new Handler().postDelayed(() -> {
            Intent homeIntent = new Intent(SplashScreenActivity.this, SwipeSelectionActivity.class);
            startActivity(homeIntent);
            finish();
        }, SPLASH_TIME_OUT);     //Displays the gif provided for the mentioned time and when the time is over an intent is used to move to the next screen.
        // This wasn't the best choice because depending on the device the gif might be sluggish and does not show as intended.

        ImageView gifView = findViewById(R.id.gifView);

        Glide.with(this).asGif().load(R.raw.golden_gates_trimmed).into(gifView);

    }


}
