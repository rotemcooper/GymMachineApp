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

        //-----------------------------------------------------------------------

        // Load activity type data with ArrayAdapter using the string array and a spinner layout
        ArrayAdapter<CharSequence> activityTypeAdapter = ArrayAdapter.createFromResource(this,
                R.array.activityTypes, R.layout.workout_selector_spinner);

        // Specify the layout to use when the list of choices appears
        activityTypeAdapter.setDropDownViewResource(R.layout.workout_selector_spinner_dropdown);

        // Apply the adapter to the spinner
        activityTypeSpinner = (Spinner) findViewById(R.id.activityTypeSpinner);
        activityTypeSpinner.setAdapter(activityTypeAdapter);


        //-----------------------------------------------------------------------

        // Load workout data with ArrayAdapter using the string array and a spinner layout
        ArrayAdapter<CharSequence> workoutAreaAdapter = ArrayAdapter.createFromResource(this,
                R.array.workoutAreas, R.layout.workout_selector_spinner);

        // Specify the layout to use when the list of choices appears
        workoutAreaAdapter.setDropDownViewResource(R.layout.workout_selector_spinner_dropdown);

        // Apply the adapter to the spinner
        workoutAreaSpinner = (Spinner) findViewById(R.id.workoutAreaSpinner);
        workoutAreaSpinner.setAdapter(workoutAreaAdapter);

    }


}
