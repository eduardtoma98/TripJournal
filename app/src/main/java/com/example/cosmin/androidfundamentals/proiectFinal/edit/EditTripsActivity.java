package com.example.cosmin.androidfundamentals.proiectFinal.edit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.cosmin.androidfundamentals.R;
import com.example.cosmin.androidfundamentals.proiectFinal.Trip;
import com.example.cosmin.androidfundamentals.proiectFinal.home.MainTripActivity;
import com.example.cosmin.androidfundamentals.proiectFinal.utils.UploadImageMethods;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.FileNotFoundException;

public class EditTripsActivity extends AppCompatActivity {

    private static final String TAG = "EditTripsActivity";
    private static final int CAMERA_REQUEST_CODE = 3;
    private static final int GALLERY_ACCESS_CODE = 2;
    private static final String RECEIVED_TRIP_CODE = "RECEIVED";

    private EditText mName;
    private EditText mDestination;
    private SeekBar mPrice;
    private RatingBar mRating;
    private ImageView mPicture;
    private Button mGalleryPhotoBtn;
    private Button mTakePictureBtn;
    private Button mSaveTripBtn;
    private EditText mStartDate;
    private EditText mEndDate;

    private String destination, tripName, startDate, endDate;
    private double price, rating;


    //firebase
    private FirebaseDatabase database;
    private DatabaseReference ref;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String userId;
    private UploadImageMethods mUploadImageMethods;
    private Bitmap bm;
    private boolean isEdited;
    private  Trip receivedTrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isEdited = false;
        setContentView(R.layout.activity_edit_trips);

        init();

        //check if we received data from onLongClick
        receivedTrip = getIntent().getParcelableExtra(RECEIVED_TRIP_CODE);
        if(receivedTrip != null){
            //if we did, we edit the trip and update firebase
            setExistingTripData(receivedTrip);
            isEdited = true;

        }

        setupFirebaseAuth();
        addNewTrip();

    }


    private void init(){
        mName = findViewById(R.id.edittext_trip_name);
        mDestination = findViewById(R.id.edittext_trip_destination);
        mPrice = findViewById(R.id.seekbar_price);
        mRating = findViewById(R.id.trip_rating);
        mGalleryPhotoBtn = findViewById(R.id.btn_gallery_pic);
        mTakePictureBtn = findViewById(R.id.btn_take_pic);
        mSaveTripBtn = findViewById(R.id.btn_save_trip);
        mStartDate = findViewById(R.id.edit_start_date);
        mEndDate = findViewById(R.id.edit_end_date);

    }

    private void addNewTrip(){
      mSaveTripBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tripName = mName.getText().toString();
                destination = mDestination.getText().toString();
                rating = (double)mRating.getRating();
                startDate = mStartDate.getText().toString();
                endDate= mEndDate.getText().toString();

                Button btnGalleryPhotoBtn = mGalleryPhotoBtn.findViewById(R.id.btn_gallery_pic);
                choosePictureFromGalleryOnClick(btnGalleryPhotoBtn);

                Button btnLaunchCamera = mTakePictureBtn.findViewById(R.id.btn_take_pic);
                launchCameraOnBtnClick(btnLaunchCamera);


                mPrice.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        price = progress;

                        //TODO: de afisat progresul pe ecran intr un textview
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });


                if(checkInputs(tripName,destination,price,rating, startDate, endDate)){
                    //Log.d(TAG, "saving photo to firebase storageaiciiiiiiiiiiiiiiii=== " + receivedTrip.getmKey());
                    Trip newTrip;

                    //if our trip exists and we want to edit it
                    if(isEdited == true){
                        //create new trip object
                         newTrip = new Trip(rating, tripName, destination, price, null, startDate, endDate);

                        //and set the key to the one it had before our intention to edit it
                        newTrip.setmKey(receivedTrip.getmKey());

                    }else{
                        //create new trip object
                         newTrip = new Trip(rating, tripName, destination, price, null, startDate, endDate);
                    }


                    mUploadImageMethods = new UploadImageMethods(EditTripsActivity.this, newTrip);
                    mUploadImageMethods.uploadNewPhotoAndTripData(bm, isEdited);

                    //go back to main fragment after uploading trip
                    if(mAuth.getCurrentUser() != null){
                        Intent intent = new Intent(EditTripsActivity.this, MainTripActivity.class);
                        startActivity(intent);



                    }

                }
            }
        });
    }

    private boolean checkInputs(String name, String destination, double price, double rating, String startDate, String endDate){
        Log.d(TAG, "checkInputs: checking inputs for null values.");
        if(name.equals("") || destination.equals("") || startDate.equals("") || endDate.equals("") || price == 0 || rating == 0 || bm == null ){
            Toast.makeText(EditTripsActivity.this, "All fields must be filled out.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void setExistingTripData(Trip trip){
        mName.setText(trip.getmName());
        mDestination.setText(trip.getmDestination());
        mPrice.setProgress((int) trip.getmPrice());
        mRating.setRating((float) trip.getmPrice());
        mStartDate.setText(trip.getmStartDate());
        mEndDate.setText(trip.getmEndDate());

    }


    private void launchCameraOnBtnClick(Button btnLaunchCamera){
        btnLaunchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: starting camera");
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
            }
        });

    }

    private void choosePictureFromGalleryOnClick(Button btnGalleryPhotoBtn ){
        btnGalleryPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: entering gallery to choose pic");
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, GALLERY_ACCESS_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CAMERA_REQUEST_CODE){
            Log.d(TAG, "onActivityResult: took the photo");

            bm = (Bitmap) data.getExtras().get("data");//this is your bitmap image and now you can do whatever you want with this
            //todo:upload photo to firebase
        }else if (requestCode == GALLERY_ACCESS_CODE){
            Log.d(TAG, "onActivityResult: chose the photo from gallery");
            Uri targetUri = data.getData();
            try {
                bm = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetUri));
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /*
    ------------------------------------ Firebase ---------------------------------------------
     */


   /* private void checkCurrentUser(FirebaseUser user){
        Log.d(TAG, "verifica daca userul este logat sau nu");

        if(user == null){
            Intent intent = new Intent(MyTripsActivity.this, LoginActivity.class);
            startActivity(intent);
            Log.d(TAG, "userul NU este logaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaat");
        }else{
            Log.d(TAG, "userul este logaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaat");
        }
    }
    /**
     * Setup the firebase auth object
     */
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        ref = database.getReference("MyTrips");

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                //check if the user is logged in
                //checkCurrentUser(user);

                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    userId = user.getUid();
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        //checkCurrentUser(mAuth.getCurrentUser());
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
