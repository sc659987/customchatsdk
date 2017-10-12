package com.braunster.androidchatsdk.firebaseplugin.firebase.listener;

import android.util.Log;

import com.braunster.androidchatsdk.firebaseplugin.firebase.wrappers.BUserWrapper;
import com.braunster.chatsdk.dao.BMessage;
import com.braunster.chatsdk.dao.BThread;
import com.braunster.chatsdk.dao.BUser;
import com.braunster.chatsdk.dao.core.DaoCore;
import com.braunster.chatsdk.network.BDefines;
import com.braunster.chatsdk.network.BPath;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.Date;
import java.util.Map;

/**
 * Created by Android on 10/11/2017.
 */
public class FirebaseThreadMessageChangeListener implements ChildEventListener {

    private static final String TAG = "ThreadMessageChange";

    /***
     *
     * @param threadEntityId
     * @param snapshot
     */
    private void handleMessage(final String threadEntityId, DataSnapshot snapshot) {
        // retrieve the thread
        final BThread thread = DaoCore.fetchOrCreateEntityWithEntityID(BThread.class, threadEntityId);
        final BMessage message = DaoCore.fetchOrCreateEntityWithEntityID(BMessage.class, snapshot.getKey());
        deserializeMessageData(message, snapshot);
        message.setDelivered(BMessage.Delivered.Yes);
        if (message.getBUserSender() != null && !message.getBUserSender().isMe() &&
                System.currentTimeMillis() < message.getDate().getTime()) {
            message.setIsRead(false);
        }

        if (thread.isDeleted()) {
            return;
        }

        if (message.getThread() == null) {
            thread.setHasUnreadMessages(true);
        }

        DaoCore.updateEntity(thread);

        message.setThread(thread);
        DaoCore.updateEntity(message);
    }

    @SuppressWarnings("all")
    private void deserializeMessageData(final BMessage message,
                                        final DataSnapshot snapshot) {
        Map<String, Object> value = (Map<String, Object>) snapshot.getValue();
        if (value == null) return;
        if (value.containsKey(BDefines.Keys.BPayload) && !value.get(BDefines.Keys.BPayload).equals(""))
            message.setText((String) value.get(BDefines.Keys.BPayload));
        if (value.containsKey(BDefines.Keys.BType) && !value.get(BDefines.Keys.BType).equals(""))
            if (value.get(BDefines.Keys.BType) instanceof Integer)
                message.setType((Integer) value.get(BDefines.Keys.BType));
            else if (value.get(BDefines.Keys.BType) instanceof Long)
                message.setType(((Long) value.get(BDefines.Keys.BType)).intValue());
        if (value.containsKey(BDefines.Keys.BDate) &&
                !value.get(BDefines.Keys.BDate).equals(""))
            message.setDate(new Date((Long) value.get(BDefines.Keys.BDate)));
        if (value.containsKey(BDefines.Keys.BUserFirebaseId) &&
                !value.get(BDefines.Keys.BUserFirebaseId).equals("")) {
            String userEntityId = (String) value.get(BDefines.Keys.BUserFirebaseId);
            BUser user = DaoCore.fetchEntityWithEntityID(BUser.class, userEntityId);
            // If there is no user saved in the db for this entity id,
            // Create a new one and do a once on it to get all the details.
            if (user == null) {
                user = DaoCore.fetchOrCreateEntityWithEntityID(BUser.class, userEntityId);
                BUserWrapper.initWithModel(user).once();
            }
            message.setBUserSender(user);
        }
        DaoCore.updateEntity(message);
    }


    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        BPath dataSnapshotRefPath = BPath.pathWithPath(dataSnapshot.getRef().toString());
        //Log.i(TAG, "onChildAdded" + dataSnapshot.getRef().toString());
        Log.i(TAG, "onChildAdded related threadId: " + dataSnapshotRefPath.idForIndex(0));
        Log.i(TAG, "onChildAdded messageId: " + dataSnapshotRefPath.idForIndex(1));
        String relatedThreadId = dataSnapshotRefPath.idForIndex(0);
        String messageId = dataSnapshotRefPath.idForIndex(1);
        handleMessage(relatedThreadId, dataSnapshot);
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        BPath dataSnapshotRefPath = BPath.pathWithPath(dataSnapshot.getRef().toString());
        Log.i(TAG, "onChildChanged related threadId: " + dataSnapshotRefPath.idForIndex(0));
        Log.i(TAG, "onChildChanged messageId: " + dataSnapshotRefPath.idForIndex(1));
        String relatedThreadId = dataSnapshotRefPath.idForIndex(0);
        String messageId = dataSnapshotRefPath.idForIndex(1);
        handleMessage(relatedThreadId, dataSnapshot);
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
    }
}
