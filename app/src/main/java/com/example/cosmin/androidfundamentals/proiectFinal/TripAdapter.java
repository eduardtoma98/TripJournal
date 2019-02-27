package com.example.cosmin.androidfundamentals.proiectFinal;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.cosmin.androidfundamentals.R;
import com.example.cosmin.androidfundamentals.proiectFinal.edit.EditTripsActivity;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.List;

public class TripAdapter extends RecyclerView.Adapter<TripViewHolder> {
    private static final String TAG = "TripAdapter";
    private static final String RECEIVED_TRIP_CODE = "RECEIVED";
    private List<Trip> mTrips;
    private Context mContext;



    public TripAdapter(List<Trip> trips, Context context) {
        this.mTrips = trips;
        this.mContext = context;
    }

    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View tripView = LayoutInflater.from(viewGroup.getContext()).
                inflate(R.layout.trip_item, viewGroup, false);

        return new TripViewHolder(tripView);
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder tripViewHolder, final int i) {
        Trip trip = mTrips.get(i);
        tripViewHolder.mTextViewName.setText(trip.getmName());
        tripViewHolder.mTextViewDestination.setText(trip.getmDestination());
        tripViewHolder.mTextViewPriceAndRating.setText(trip.getmPrice() + "/" + trip.getmRating());
        Log.d(TAG, "onBindViewHolder: URL POZA " + trip.getmPicture());
        if(trip.getmPicture() != null){
            Picasso.get().load(trip.getmPicture()).into(tripViewHolder.mImageView);
        }

        //add click listener so that we can see the details of trip
        tripViewHolder.mParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,String.valueOf(mTrips.get(i).getmPrice()), Toast.LENGTH_SHORT).show();
            }
        });


        //add long click listener so we can edit a trip
       tripViewHolder.mParent.setOnLongClickListener(new View.OnLongClickListener() {
           @Override
           public boolean onLongClick(View v) {
               Intent intent=new Intent(mContext,EditTripsActivity.class);
               intent.putExtra(RECEIVED_TRIP_CODE,mTrips.get(i));
               mContext.startActivity(intent);


               return true;
           }
       });


    }

    @Override
    public int getItemCount() {
        return mTrips.size();
    }


}
