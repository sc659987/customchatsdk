package com.braunster.androidchatsdk.firebaseplugin.firebase.listener;

import android.util.Log;

import com.braunster.chatsdk.network.BPath;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.HashMap;

/**
 * This class is listening to any changes to thread
 * <p>
 * Created by Android on 9/26/2017.
 */
public class FirebaseThreadDetailsChangeListener implements ChildEventListener {

    private static final String TAG = "ThreadDetailsChange";

    // this call back say firebase lost the authentication and needs to be done
    @Override
    public void onCancelled(DatabaseError databaseError) {
        // TODO log as error occurred
    }

    /***
     * This function will be called back for each thread associated with user,
     * Each thread has three sub key named 'details','message','user' ,
     * so if there is total 5 thread associated with user then 15
     * times call back will be invoked. Save that in db.
     * Fetch the thread from db and save it.
     * @param dataSnapshot
     * @param s
     */
    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        //Log.e(TAG, "firebase thread child added");
        BPath dataSnapshotRefPath = BPath.pathWithPath(dataSnapshot.getRef().toString());
        Log.i(TAG, "onChildAdded" + dataSnapshot.getRef().toString());
        Log.i(TAG, "onChildAdded" + dataSnapshotRefPath.idForIndex(0));
    }

    /***
     *  This is called subsequent when user gets a new message after reading the data
     *  initially. This function too is called three time
     *  for each sub key named 'details','messages' and 'users'
     * @param dataSnapshot
     * @param s
     */
    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        BPath dataSnapshotRefPath = BPath.pathWithPath(dataSnapshot.getRef().toString());
        Log.i(TAG, "onChildChanged" + dataSnapshot.getRef().toString());
        Log.i(TAG, "onChildChanged" + dataSnapshotRefPath.idForIndex(0));
        //handleMessage(dataSnapshotRefPath.idForIndex(0), dataSnapshot);
        // save in db
    }


    private void handleDetails(String threadId, HashMap snapshotData) {


    }

    /***
     * TODO later decide what to do
     * @param dataSnapshot
     */
    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        Log.i(TAG, "child removed");
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
        Log.i(TAG, "child moved !");
    }
}
