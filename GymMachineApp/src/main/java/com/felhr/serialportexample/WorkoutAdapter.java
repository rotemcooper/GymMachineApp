package com.felhr.serialportexample;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
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
    int clickedListPosition=0;
    View clickedView;
    boolean isCalled = false;
    Context parent;

    WorkoutAdapter(Context parent, ArrayList<WorkoutPrf> workoutList ) {
        this.workoutList = workoutList;
        this.parent = parent;
        //Toast.makeText(parent, "List size " + workoutList.size() , Toast.LENGTH_LONG).show();
    }

    // Parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    // Allows clicks events to be caught
    public void setClickListener(WorkoutAdapter.ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
        //Toast.makeText(parent, "setClickListener1", Toast.LENGTH_LONG).show();
    }

    // Provide a reference to the views for each data item (row)
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Views of each row
        public View frameView;
        public TextView segNum;
        public TextView checkMark;
        public GraphView graph;
        public MyViewHolder(View v) {
            super(v);
            frameView = v;
            segNum = (TextView) v.findViewById(R.id.textSegNum);
            checkMark = (TextView) v.findViewById(R.id.textCheckMark);
            //photo =  (ImageView) v.findViewById(R.id.workoutImage);

            graph = (GraphView) v.findViewById(R.id.frameGraph);

            // Set the onClickListener for graph view
            //v.setOnClickListener(this);
            graph.setOnClickListener( this );
        }

        @Override
        public void onClick(View view) {
            // Clear background of previously clicked view
            if( clickedView != null ) {
                clickedView.setBackgroundColor( view.getResources().getColor(R.color.ColorBackground));
            }
            clickedView = view;

            // Highlight currently clicked view
            //graph.setBackgroundColor( view.getResources().getColor(R.color.ColorBackgroundHighlight));
            graph.setBackground( view.getResources().getDrawable(R.drawable.btn_rounded));

            clickedListPosition = getAdapterPosition();
            if (WorkoutAdapter.mClickListener != null) {
                //Toast.makeText(parent, "onClick " + getAdapterPosition(), Toast.LENGTH_LONG).show();
                WorkoutAdapter.mClickListener.onItemClick(frameView, getAdapterPosition());
            }
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public WorkoutAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new view
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

        holder.setIsRecyclable(false);

        // Get element from data-set at this position
        WorkoutPrf prf = workoutList.get( position );

        // Indicate segment number and whether it is completed
        holder.segNum.setText( " " + (position+1) );
        if( prf.reps > prf.repsMax ) {
            holder.checkMark.setVisibility( View.VISIBLE );
        }

        // Create and title the graph
        holder.graph.setTitle( prf.name );
        holder.graph.setTitleTextSize( 40 );
        holder.graph.setTitleColor( Color.WHITE );
        holder.graph.getGridLabelRenderer().setGridColor(0xFFFF7900);
        holder.graph.getGridLabelRenderer().setHorizontalLabelsColor(0xFFFF7900);
        holder.graph.getGridLabelRenderer().setVerticalLabelsColor(0xFFFF7900);
        //holder.graph.getGridLabelRenderer().setTextSize(50);
        //graph.getGridLabelRenderer().setHorizontalAxisTitleColor(Color.WHITE);

        holder.graph.removeAllSeries();
        // Draw workout profile line for pull
        LineGraphSeries<DataPoint> pointsPull = new LineGraphSeries<DataPoint>(prfDataPointsPull(prf));
        holder.graph.addSeries(pointsPull);
        pointsPull.setDrawBackground(true);

        // Draw workout profile line for release
        LineGraphSeries<DataPoint> pointsRel = new LineGraphSeries<DataPoint>(prfDataPointsRel(prf));
        holder.graph.addSeries(pointsRel);
        pointsRel.setDrawBackground(true);

        // Set x and y range
        holder.graph.getViewport().setMinX(0);
        holder.graph.getViewport().setMaxX(200);
        holder.graph.getViewport().setMinY(0);
        holder.graph.getViewport().setMaxY(80);
        holder.graph.getViewport().setYAxisBoundsManual(true);
        holder.graph.getViewport().setXAxisBoundsManual(true);

        // Remember and highlight the last clicked view
        if( position == clickedListPosition ) {
            clickedView = holder.graph;
            //clickedView.setBackgroundColor( clickedView.getResources().getColor(R.color.ColorBackgroundHighlight));
            clickedView.setBackground( clickedView.getResources().getDrawable(R.drawable.btn_rounded));

            // If this is the first time we are here, call onClick()
            if( !isCalled && position == 0 ) {
                isCalled = true;
                //Toast.makeText(parent, "onBindViewHolder", Toast.LENGTH_LONG).show();
                holder.onClick( holder.graph );
            }
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return workoutList.size();
    }
}

