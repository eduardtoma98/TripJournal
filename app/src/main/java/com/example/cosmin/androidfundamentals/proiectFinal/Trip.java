package com.example.cosmin.androidfundamentals.proiectFinal;

import android.os.Parcel;
import android.os.Parcelable;

public class Trip implements Parcelable {

    private double mRating;
    private String mName;
    private String mDestination;
    private double mPrice;
    private String mPicture;
    private String mStartDate;
    private String mEndDate;
    private int mKey;


    public Trip(double mRating, String mName, String mDestination, double mPrice, String mPicture, String mStartDate, String mEndDate) {
        this.mRating = mRating;
        this.mName = mName;
        this.mDestination = mDestination;
        this.mPrice = mPrice;
        this.mPicture = mPicture;
        this.mStartDate = mStartDate;
        this.mEndDate = mEndDate;

    }

    public Trip() {
    }


    public double getmRating() {
        return mRating;
    }

    public void setmRating(double mRating) {
        this.mRating = mRating;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmDestination() {
        return mDestination;
    }

    public void setmDestination(String mDestination) {
        this.mDestination = mDestination;
    }

    public double getmPrice() {
        return mPrice;
    }

    public void setmPrice(double mPrice) {
        this.mPrice = mPrice;
    }

    public String getmPicture() {
        return mPicture;
    }

    public void setmPicture(String mPicture) {
        this.mPicture = mPicture;
    }

    public void setmKey(int mKey){
        this.mKey = mKey;
    }

    public int getmKey(){
        return mKey;
    }

    public String getmStartDate(){
        return mStartDate;
    }

    public void setmStartDate(String mStartDate){
        this.mStartDate = mStartDate;}

    public String getmEndDate(){
        return mEndDate;
    }

    public void setmEndDate(String mEndDate){
        this.mEndDate = mEndDate;}

    //parcel part
    public Trip(Parcel parcel){
        String[] data = new String[7];
        parcel.readStringArray(data);
        this.mName = data[0];
        this.mDestination = data[1];
        this.mPrice = Double.parseDouble(data[2]);
        this.mRating = Double.parseDouble(data[3]);
        this.mKey = Integer.parseInt(data[4]);
        this.mStartDate = data[5];
        this.mEndDate = data[6];
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{this.mName,this.mDestination,String.valueOf(this.mPrice), String.valueOf(this.mRating), String.valueOf(this.mKey), this.mStartDate, this.mEndDate});
    }

    public static final Parcelable.Creator<Trip> CREATOR= new Parcelable.Creator<Trip>() {

        @Override
        public Trip createFromParcel(Parcel source) {
            // TODO Auto-generated method stub
            return new Trip(source);  //using parcelable constructor
        }

        @Override
        public Trip[] newArray(int size) {
            // TODO Auto-generated method stub
            return new Trip[size];
        }
    };
}
