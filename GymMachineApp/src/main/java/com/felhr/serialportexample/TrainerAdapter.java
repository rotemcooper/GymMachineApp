package com.felhr.serialportexample;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class TrainerAdapter extends RecyclerView.Adapter<TrainerAdapter.MyViewHolder> {
    private String[] mDataset;

    // Provide a reference to the views for each data item
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView mImageView;
        public TextView mTextView;

        public MyViewHolder(View v) {
            super(v);
            mImageView =  (ImageView) v.findViewById(R.id.trainerImage);
            mTextView =  (TextView) v.findViewById(R.id.trainerText);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public TrainerAdapter(String[] myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public TrainerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trainer_recycler_row, parent, false);

        return new MyViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mImageView.setImageResource( R.drawable.trainer01 );
        holder.mTextView.setText( "Brandon " + position /*mDataset[position]*/);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return 40 /*mDataset.length*/;
    }
}