package com.example.cosmin.androidfundamentals.proiectFinal.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import com.example.cosmin.androidfundamentals.R;
import com.example.cosmin.androidfundamentals.proiectFinal.Trip;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Random;

import static java.lang.Math.toIntExact;

public class UploadImageMethods {
    private static final String TAG = "UploadImageMethods";
    private String STORE_IMAGE = "trip_photos/users/";
    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private StorageReference mStorageReference;
    private String userID;
    private long count;
    private Trip mTrip;
    private boolean mIsEdited;

    //vars
    private Context mContext;
    private double mPhotoUploadProgress = 0;

    public UploadImageMethods(Context context, Trip trip) {
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference("MyTrips");
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mContext = context;
        mTrip = trip;
        mIsEdited = false;

        if(mAuth.getCurrentUser() != null){
            userID = mAuth.getCurrentUser().getUid();
        }
    }

    public void uploadNewPhotoAndTripData(Bitmap bm, boolean mIsEdited){
        Log.d(TAG, "uploadNewPhoto: attempting to uplaod new photo.");

        this.mIsEdited = mIsEdited;

        String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Random ran = new Random();
        final int photoID = ran.nextInt(200) + 1;

        StorageReference storageReference = mStorageReference
                    .child(STORE_IMAGE + "/" + user_id + "/photo" + photoID);

        byte[] bytes = getBytesFromBitmap(bm, 100);

        UploadTask uploadTask = null;
        uploadTask = storageReference.putBytes(bytes);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    //get download url
                    mStorageReference.child(STORE_IMAGE + "/" + user_id + "/photo" + photoID).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            addTripToDatabase(task.getResult().toString(), mTrip, myRef);
                        }
                    });

                    Toast.makeText(mContext, "photo upload success", Toast.LENGTH_SHORT).show();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: Photo upload failed.");
                    Toast.makeText(mContext, "Photo upload failed ", Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                    if(progress - 25 > mPhotoUploadProgress){
                        Toast.makeText(mContext, "photo upload progress: " + String.format("%.0f", progress) + "%", Toast.LENGTH_SHORT).show();
                        mPhotoUploadProgress = progress;
                    }

                    Log.d(TAG, "onProgress: upload progress: " + progress + "% done");
                }
            });

        }


    private void addTripToDatabase(String url, Trip newTrip, DatabaseReference ref){
        Log.d(TAG, "addPhotoToDatabase: adding photo to database.");

        newTrip.setmPicture(url);
        Log.d(TAG, "addTripToDatabase: Image URL is: " + url);

        //save to firebase
        if(mIsEdited == true){
            //if it is being edited save to the same node to replace the data
            ref.child( FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(String.valueOf(newTrip.getmKey()))
                    .setValue(newTrip);

            Log.d(TAG, "addTripToDatabase: edit existing trip and replace in database at newTrip.getmKey() node"  + String.valueOf(newTrip.getmKey()));

        }else{

            Random r = new Random();
            int randomNumber = r.nextInt(100 - 1) + 1;
            newTrip.setmKey(randomNumber);
            ref.child( FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(String.valueOf(randomNumber))
                    .setValue(newTrip);
        }

    }


  private static byte[] getBytesFromBitmap(Bitmap bm, int quality){
      ByteArrayOutputStream stream = new ByteArrayOutputStream();
      bm.compress(Bitmap.CompressFormat.JPEG, quality, stream);
      return stream.toByteArray();
  }
}
