package com.felhr.serialportexample;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.MyViewHolder> {

    private static WorkoutAdapter.ItemClickListener mClickListener;
    private ArrayList<WorkoutPrf> workoutList;

    WorkoutAdapter(Context parent, ArrayList<WorkoutPrf> workoutList ) {
        this.workoutList = workoutList;
        Toast.makeText(parent, "List size " + workoutList.size() , Toast.LENGTH_LONG).show();
    }

    // Parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    // Allows clicks events to be caught
    public void setClickListener(WorkoutAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // Provide a reference to the views for each data item (row)
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Views of each row
        public GraphView graph;
        public MyViewHolder(View v) {
            super(v);
            //text = (TextView) v.findViewById(R.id.textView8);
            //photo =  (ImageView) v.findViewById(R.id.workoutImage);

            graph = (GraphView) v.findViewById(R.id.frameGraph);

            // Set the onClickListener
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (WorkoutAdapter.mClickListener != null) {
                WorkoutAdapter.mClickListener.onItemClick(view, getAdapterPosition());
            }
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public WorkoutAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.workout_recycler_frame, parent, false);

        return new MyViewHolder(v);
    }

    private double torqueToPound( int x ) {
        //double y = 0.0707x + 15.886
        double y = 0.09*x + 5.0;
        //double y = -(8E-05)*x*x + 0.1494*x - 1.1517;
        return y;
    }

    private DataPoint[] prfDataPointsPull( WorkoutPrf prf ) {
        DataPoint[] dp = new DataPoint[200];
        for (int i = 0; i < 200; i++) {
            int len = i;
            if (len >= prf.tbl.length) {
                len = prf.tbl.length - 1;
            }
            dp[i] = new DataPoint(i, torqueToPound(prf.tbl[len] * prf.multPull));
        }
        return dp;
    }

    private DataPoint[] prfDataPointsRel( WorkoutPrf prf ) {
        DataPoint[] dp = new DataPoint[200];
        for (int i = 0; i < 200; i++) {
            int len = i;
            if (len >= prf.tbl.length) {
                len = prf.tbl.length - 1;
            }
            dp[i] = new DataPoint(i, torqueToPound(prf.tbl[len] * prf.multRel));
        }
        return dp;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(WorkoutAdapter.MyViewHolder holder, int position) {
/*
        holder.text.setText( "Workout this is a lot of text" );
        holder.photo.setImageResource( R.drawable.trainer01 );
*/
        // Get element from your dataset at this position
        WorkoutPrf prf = workoutList.get( position%(workoutList.size()) );

        // Create and title the graph
        holder.graph.setTitle( prf.name );
        holder.graph.setTitleTextSize( 60 );
        holder.graph.setTitleColor( Color.BLACK );
        holder.graph.getGridLabelRenderer().setGridColor(0xFFFF7900);
        holder.graph.getGridLabelRenderer().setHorizontalLabelsColor(0xFFFF7900);
        holder.graph.getGridLabelRenderer().setVerticalLabelsColor(0xFFFF7900);
        //holder.graph.getGridLabelRenderer().setTextSize(50);
        //graph.getGridLabelRenderer().setHorizontalAxisTitleColor(Color.WHITE);

        // Draw workout profile line for pull
        LineGraphSeries<DataPoint> pointsPull = new LineGraphSeries<DataPoint>(prfDataPointsPull(prf));
        holder.graph.addSeries(pointsPull);
        pointsPull.setDrawBackground(true);

        // Draw workout profile line for release
        LineGraphSeries<DataPoint> pointsRel = new LineGraphSeries<DataPoint>(prfDataPointsRel(prf));
        holder.graph.addSeries(pointsRel);
        pointsRel.setDrawBackground(true);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return 10;//workoutList.size();
    }


}

