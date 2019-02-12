package com.felhr.serialportexample;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jjoe64.graphview.GraphView;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.MyViewHolder> {

    private static WorkoutAdapter.ItemClickListener mClickListener;

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

        return new WorkoutAdapter.MyViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(WorkoutAdapter.MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return 0; //rotemc
    }


}

