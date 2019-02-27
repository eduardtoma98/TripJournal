package com.example.cosmin.androidfundamentals.proiectFinal;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cosmin.androidfundamentals.R;

public class TripViewHolder extends RecyclerView.ViewHolder {
    public ImageView mImageView;
    public TextView mTextViewName;
    public TextView mTextViewDestination;
    public TextView mTextViewPriceAndRating;
    public CardView mParent;


    public TripViewHolder(@NonNull View itemView) {
        super(itemView);
        this.mImageView = itemView.findViewById(R.id.imageview_trip_picture);
        this.mTextViewName = itemView.findViewById(R.id.textview_trip_name);
        this.mTextViewDestination = itemView.findViewById(R.id.textview_trip_destination);
        this.mTextViewPriceAndRating = itemView.findViewById(R.id.textview_trip_price_and_rating);
        this.mParent = itemView.findViewById(R.id.parent);

    }
}
