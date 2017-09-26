package com.braunster.androidchatsdk.firebaseplugin.firebase.listener;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Observable;

/**
 * This class is listening to any changes to thread
 * <p>
 * Created by Android on 9/26/2017.
 */
public class FirebaseThreadDetailsChangeListener extends Observable implements ValueEventListener, ChildEventListener {

    public FirebaseThreadDetailsChangeListener(String userId) {

    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {

    }

    // this call back say firebase lost the authentication and needs to be done
    @Override
    public void onCancelled(DatabaseError databaseError) {
        // TODO log as error occurred
    }



    // This call back is for thread
    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }
}
