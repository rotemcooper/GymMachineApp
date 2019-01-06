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

    private static final Integer[] trainerImages = {
            R.drawable.trainer01,
            R.drawable.trainer02,
            R.drawable.trainer03,
            R.drawable.trainer04,
            R.drawable.trainer05,
            R.drawable.trainer06,
            R.drawable.trainer07,
            R.drawable.trainer08,
            R.drawable.trainer09,
            R.drawable.trainer10,
            R.drawable.trainer11,
            R.drawable.trainer12,
            R.drawable.trainer13,
            R.drawable.trainer14,
            R.drawable.trainer15,
            R.drawable.trainer16,
            R.drawable.trainer17,
            R.drawable.trainer18,
            R.drawable.trainer19,
            R.drawable.trainer20,
            R.drawable.trainer21,
            R.drawable.trainer01
    };

    private static final String[] trainerText = {
            "Joe\n This is a great upper body workout routine." +
                    " It starts with warm-up and then get really intense."
    };

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mImageView.setImageResource( trainerImages[position] );
        holder.mTextView.setText( trainerText[0] );
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return trainerImages.length;
    }
}