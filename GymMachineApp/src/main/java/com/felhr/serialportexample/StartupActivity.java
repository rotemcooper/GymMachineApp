package com.felhr.serialportexample;

import android.graphics.drawable.ColorDrawable;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class StartupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        android.support.v7.app.ActionBar bar = getSupportActionBar();
        if( bar != null ) {
            bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
        }
    }

    public void buttonSelfWorkout(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void buttonGuidedWorkout(View view) {
        Intent intent = new Intent(this, WorkoutSelectorActivity.class);
        startActivity(intent);
    }
}
