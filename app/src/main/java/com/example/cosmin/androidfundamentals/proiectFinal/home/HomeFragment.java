package com.example.cosmin.androidfundamentals.proiectFinal.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.cosmin.androidfundamentals.R;
import com.example.cosmin.androidfundamentals.proiectFinal.Trip;
import com.example.cosmin.androidfundamentals.proiectFinal.TripAdapter;
import com.example.cosmin.androidfundamentals.proiectFinal.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView mRecyclerViewTrips;
    private Button mButtonSignout;
    private Button mBtnEditTrips;
    //firebase
    private FirebaseDatabase database;
    private DatabaseReference ref;
    private DatabaseReference userReference;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String userId;

    //public List<Trip> trips = new ArrayList<>();

    //public TripAdapter tripAdapter;

    private static final String TAG = "MyTripsActivity";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_my_trips, container, false);

        database = FirebaseDatabase.getInstance();
        initView(view);
        return view;

    }

    private void initView(View view){
        mRecyclerViewTrips = view.findViewById(R.id.recyclerview_trips);
        mRecyclerViewTrips.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerViewTrips.setLayoutManager(layoutManager);

        //tripAdapter = new TripAdapter(trips);
        // mRecyclerViewTrips.setAdapter(tripAdapter);
        //getDataFromFirebase();
        //get a reference
        ref = database.getReference("MyTrips");
        setupFirebaseAuth();

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Trip> updatedTrips = new ArrayList<>();
                for (DataSnapshot ds:dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getChildren()){

                    //Log.d("nmmflc", "getUserAccountSettings: datasnapshot: "+ dsnap.getValue());
                    Trip trip = ds.getValue(Trip.class);
                    updatedTrips.add(trip);

                    //trips.add(trip);


                }


                TripAdapter tripAdapter = new TripAdapter(updatedTrips, getContext());
                mRecyclerViewTrips.setAdapter(tripAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


     /*
    ------------------------------------ Firebase ---------------------------------------------
     */

    /**
     * checks to see if the @param 'user' is logged in
     * @param user
     */
    private void checkCurrentUser(FirebaseUser user){
        Log.d(TAG, "verifica daca userul este logat sau nu");

        if(user == null){
            Intent intent = new Intent(getActivity(), LoginActivity.class);
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

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                //check if the user is logged in
                checkCurrentUser(user);

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
        checkCurrentUser(mAuth.getCurrentUser());
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


}
