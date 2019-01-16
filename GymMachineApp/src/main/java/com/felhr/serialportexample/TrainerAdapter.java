package com.felhr.serialportexample;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class TrainerAdapter extends RecyclerView.Adapter<TrainerAdapter.MyViewHolder> {

    private static ItemClickListener mClickListener;

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // Provide a reference to the views for each data item
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        public ImageView trainerPhoto;
        public TextView trainerName;
        public TextView trainerText;

        public MyViewHolder(View v) {
            super(v);
            trainerPhoto =  (ImageView) v.findViewById(R.id.trainerImage);
            trainerName =  (TextView) v.findViewById(R.id.trainerName);
            trainerText =  (TextView) v.findViewById(R.id.trainerText);

            // Set the onClickListener
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (TrainerAdapter.mClickListener != null) {
                TrainerAdapter.mClickListener.onItemClick(view, getAdapterPosition());
            }
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public TrainerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = (View) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trainer_recycler_row, parent, false);

        return new MyViewHolder(v);
    }

    public static final Integer[] trainerImages = {
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

    private static final String[] trainerName = {
            "Ryan", "Maria", "David", "Patricia", "Brandon",
            "Andrew and Donna", "Jennifer", "James", "Nicole", "Jessica",
            "William", "Sarah", "Daniel and Karen", "Lisa", "Ashley",
            "Mark", "Robert", "Paul", "Kimberly", "Carol and Dylan",
            "Stephanie", "James"
    };

    private static final String[] trainerText = {
            "This is a great upper body workout routine. It starts with warm-up and then get really intense.",
            "HI there, are you ready for a fun upper-body workout? Come on and join me for a great 30 minutes session.",
            "Let's get together for a serious upper-body workout, where we will focus on your chest, back and arms.",
            "The Best Upper-Body Workout. Pack on lean size across your torso in just 16 workouts over the next four weeks with this laser-focused muscle-building programme.",
            "Do you want to add a serious amount of lean muscle mass in just 28 days? Then you’ve come to the right place because this four-week, 16-session training plan will do exactly that by pushing your body harder than it’s ever been pushed before.",
            "Are you ready to get out of your comfort zone? Let's do something you’ve not done before and make rapid progress towards stronger upper-body!",
            "Let's add significant muscle mass across your torso with this high-intensity upper-body workouts while also stripping away excess body fat.",
            "This workout designed to add as much lean muscle mass as possible over the next 28 days, while also stripping off body fat.",
            "This custom upper-body workout has been designed to tax your major muscle groups, especially your chest and back, to radically transform how you look shirtless.",
            "Work your major upper-body muscles either directly or indirectly twice a week, and it’s this big increase in training volume that will stimulate these muscles into growing bigger quickly.",
            "Upper-body workout that which will not only keep your muscles stimulated for longer but also keep your heart rate high to increase the rate of fat burn.",
            "Tempo training for upper-body. The accumulated time under tension increases your heart rate to burn body fat and break down muscle tissue so it’s rebuilt bigger and stronger.",

    };

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.trainerPhoto.setImageResource( trainerImages[position] );
        holder.trainerName.setText( trainerName[position] );
        holder.trainerText.setText( trainerText[position%trainerText.length] );
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return trainerImages.length;
    }
}