package com.felhr.serialportexample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class WorkoutSelector extends AppCompatActivity {

    private Spinner activityTypeSpinner;
    private Spinner workoutAreaSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_selector);

        activityTypeSpinner = (Spinner) findViewById(R.id.activityTypeSpinner);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> activityTypeAdapter = ArrayAdapter.createFromResource(this,
                R.array.activityTypes, R.layout.workout_selector_spinner);

        // Specify the layout to use when the list of choices appears
        activityTypeAdapter.setDropDownViewResource(R.layout.workout_selector_spinner_dropdown);
        // Apply the adapter to the spinner
        activityTypeSpinner.setAdapter(activityTypeAdapter);

        //------------------------------

        workoutAreaSpinner = (Spinner) findViewById(R.id.workoutAreaSpinner);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> workoutAreaAdapter = ArrayAdapter.createFromResource(this,
                R.array.workoutAreas, R.layout.workout_selector_spinner);

        // Specify the layout to use when the list of choices appears
        workoutAreaAdapter.setDropDownViewResource(R.layout.workout_selector_spinner_dropdown);
        // Apply the adapter to the spinner
        workoutAreaSpinner.setAdapter(workoutAreaAdapter);

    }


}
