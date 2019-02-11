package com.felhr.serialportexample;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class WorkoutSelectorActivity extends AppCompatActivity implements TrainerAdapter.ItemClickListener {

    private Spinner activityTypeSpinner;
    private Spinner workoutAreaSpinner;
    private RecyclerView trainerRecyclerView;
    private TrainerAdapter trainerAdapter;
    private RecyclerView.LayoutManager trainerLayoutManager;

    @Override
    public void onItemClick(View view, int position) {

        //Toast.makeText(this, "You clicked " + position, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("TrainerID",  TrainerAdapter.trainerImages[position]);

        String brandon1 = new String("android.resource://" + getPackageName() + "/" + R.raw.brandon1);
        String brandon2 = new String("android.resource://" + getPackageName() + "/" + R.raw.brandon2);
        String brandon3 = new String("android.resource://" + getPackageName() + "/" + R.raw.brandon3);

        ArrayList<WorkoutPrf> workout = new ArrayList<WorkoutPrf>();
        workout.add( new WorkoutPrf("Weight", brandon1, 0,0,1,2, 4, WorkoutPrf.WEIGHT_TBL ) );
        workout.add( new WorkoutPrf("Weight", brandon2, 0,0,4,6, 4, WorkoutPrf.WEIGHT_TBL ) );
        workout.add( new WorkoutPrf("Spring", brandon3, 100,100,3,4, 4, WorkoutPrf.SPRING_TBL ) );
        intent.putExtra("Workout", workout);

        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_selector);

        Toast.makeText(this, "WorkoutSelectorActivity", Toast.LENGTH_LONG).show();

        android.support.v7.app.ActionBar bar = getSupportActionBar();
        if( bar != null ) {
            bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
        }

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

        //-----------------------------------------------------------------------

        // Trainer RecyclerView
        trainerRecyclerView = (RecyclerView) findViewById(R.id.trainerRecycler);

        // Improve performance (if changes in content do not change the layout
        // size of the RecyclerView)
        trainerRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        trainerLayoutManager = new LinearLayoutManager(this);
        trainerRecyclerView.setLayoutManager(trainerLayoutManager);

        // Dpecify an adapter
        trainerAdapter = new TrainerAdapter();
        trainerRecyclerView.setAdapter(trainerAdapter);

        // Set onClickListener
        trainerAdapter.setClickListener(this);
    }


}
