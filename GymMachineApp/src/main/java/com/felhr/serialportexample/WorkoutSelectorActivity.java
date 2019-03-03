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

        String brandon1 = new String("android.resource://" + getPackageName() + "/" + R.raw.brandon1_warmup1);
        String brandon2 = new String("android.resource://" + getPackageName() + "/" + R.raw.brandon2_warmup2);
        String brandon3 = new String("android.resource://" + getPackageName() + "/" + R.raw.brandon3_warmup3);
        String brandon4 = new String("android.resource://" + getPackageName() + "/" + R.raw.brandon4_warmup4);
        String brandon5 = new String("android.resource://" + getPackageName() + "/" + R.raw.brandon5_sqtest_tricep);
        String brandon6 = new String("android.resource://" + getPackageName() + "/" + R.raw.brandon6_sqtest_back);
        String brandon7 = new String("android.resource://" + getPackageName() + "/" + R.raw.brandon7_chest_press);
        String brandon8 = new String("android.resource://" + getPackageName() + "/" + R.raw.brandon8_tricep);
        String brandon9 = new String("android.resource://" + getPackageName() + "/" + R.raw.brandon9_lat_pulldown);
        String brandon10 = new String("android.resource://" + getPackageName() + "/" + R.raw.brandon10_sqtest_bicep);
        String brandon11 = new String("android.resource://" + getPackageName() + "/" + R.raw.brandon11_bicep_curls_standing);
        String brandon12 = new String("android.resource://" + getPackageName() + "/" + R.raw.brandon12_squat_staggered);
        String brandon13 = new String("android.resource://" + getPackageName() + "/" + R.raw.brandon13_squat_inplace);
        String brandon14 = new String("android.resource://" + getPackageName() + "/" + R.raw.brandon14_deadlift);

        ArrayList<WorkoutPrf> workout = new ArrayList<WorkoutPrf>();
        workout.add( new WorkoutPrf("Warm-up 1", brandon1, 0,0,1,1, 1, WorkoutPrf.WEIGHT_TBL ) );
        workout.add( new WorkoutPrf("Warm-up 2", brandon2, 0,0,1,1, 1, WorkoutPrf.WEIGHT_TBL ) );
        workout.add( new WorkoutPrf("Warm-up 3", brandon3, 0,0,1,1, 1, WorkoutPrf.WEIGHT_TBL ) );
        workout.add( new WorkoutPrf("Warm-up 4", brandon4, 0,0,1,1, 1, WorkoutPrf.WEIGHT_TBL ) );
        workout.add( new WorkoutPrf("SQ Triceps", brandon5, 0,0,8,8, 2, WorkoutPrf.STRENGTH_TEST_TBL ) );
        workout.add( new WorkoutPrf("SQ Back", brandon6, 0,0,8,8, 2, WorkoutPrf.STRENGTH_TEST_TBL ) );

        workout.add( new WorkoutPrf("Chest Press", brandon7, 0,0,2,2, 15, WorkoutPrf.WEIGHT_TBL ) );
        workout.add( new WorkoutPrf("Standing Tricep", brandon8, 0,0,2,2, 15, WorkoutPrf.WEIGHT_TBL ) );
        workout.add( new WorkoutPrf("Lat Pulldown", brandon9, 0,0,2,2, 15, WorkoutPrf.WEIGHT_TBL ) );
        workout.add( new WorkoutPrf("Chest Press", brandon7, 0,0,2,2, 15, WorkoutPrf.WEIGHT_TBL ) );
        workout.add( new WorkoutPrf("Standing Tricep", brandon8, 0,0,2,2, 15, WorkoutPrf.WEIGHT_TBL ) );
        workout.add( new WorkoutPrf("Lat Pulldown", brandon9, 0,0,2,2, 15, WorkoutPrf.WEIGHT_TBL ) );
        workout.add( new WorkoutPrf("Chest Press", brandon7, 0,0,2,2, 15, WorkoutPrf.WEIGHT_TBL ) );
        workout.add( new WorkoutPrf("Standing Tricep", brandon8, 0,0,2,2, 15, WorkoutPrf.WEIGHT_TBL ) );
        workout.add( new WorkoutPrf("Lat Pulldown", brandon9, 0,0,2,2, 15, WorkoutPrf.WEIGHT_TBL ) );

        workout.add( new WorkoutPrf("SQ Bicep", brandon10, 0,0,8,8, 2, WorkoutPrf.STRENGTH_TEST_TBL ) );

        workout.add( new WorkoutPrf("Bicep Curls", brandon11, 0,0,2,2, 15, WorkoutPrf.WEIGHT_TBL ) );
        workout.add( new WorkoutPrf("Staggered Squat", brandon12, 0,0,2,2, 15, WorkoutPrf.WEIGHT_TBL ) );
        workout.add( new WorkoutPrf("In-place Squat", brandon13, 0,0,2,2, 15, WorkoutPrf.WEIGHT_TBL ) );
        workout.add( new WorkoutPrf("Dead-lift", brandon14, 0,0,2,2, 15, WorkoutPrf.WEIGHT_TBL ) );
        workout.add( new WorkoutPrf("Bicep Curls", brandon11, 0,0,2,2, 15, WorkoutPrf.WEIGHT_TBL ) );
        workout.add( new WorkoutPrf("Staggered Squat", brandon12, 0,0,2,2, 15, WorkoutPrf.WEIGHT_TBL ) );
        workout.add( new WorkoutPrf("In-place Squat", brandon13, 0,0,2,2, 15, WorkoutPrf.WEIGHT_TBL ) );
        workout.add( new WorkoutPrf("Dead-lift", brandon14, 0,0,2,2, 15, WorkoutPrf.WEIGHT_TBL ) );

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

        // Specify an adapter
        trainerAdapter = new TrainerAdapter();
        trainerRecyclerView.setAdapter(trainerAdapter);

        // Set onClickListener
        trainerAdapter.setClickListener(this);
    }


}
