package com.braunster.androidchatsdk.firebaseplugin.firebase.listener;

import com.braunster.androidchatsdk.firebaseplugin.firebase.FirebaseEventsManager;
import com.braunster.chatsdk.dao.BThread;
import com.braunster.chatsdk.dao.core.DaoCore;
import com.braunster.chatsdk.network.BDefines;
import com.braunster.chatsdk.network.BPath;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.Map;

/**
 * This class is listening to any changes to thread
 * <p>
 * Created by Android on 9/26/2017.
 */
public class FirebaseThreadDetailsChangeListener implements ValueEventListener {

    private static final String TAG = "ThreadDetailsChange";


    // this call back say firebase lost the authentication and needs to be done
    @Override
    public void onCancelled(DatabaseError databaseError) {
        // TODO log as error occurred
    }

    @Override
    public void onDataChange(final DataSnapshot dataSnapshot) {
        FirebaseEventsManager.Executor.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
                BPath dataSnapshotRefPath = BPath.pathWithPath(dataSnapshot.getRef().toString());
                //Log.i(TAG, "onChildChanged" + dataSnapshot.getRef().toString());
                //Log.i(TAG, "onChildChanged" + dataSnapshotRefPath.idForIndex(0));
                String threadEntityId = dataSnapshotRefPath.idForIndex(0);
                handleDetailsChange(threadEntityId, dataSnapshot);
            }
        });
    }


    private void handleDetailsChange(String threadEntityId, DataSnapshot dataSnapshot) {
        final BThread thread = DaoCore.fetchOrCreateEntityWithEntityID(BThread.class, threadEntityId);
        this.deserialize(thread, (java.util.Map<String, Object>) dataSnapshot.getValue());

    }

    @SuppressWarnings("all")
    void deserialize(BThread bThread, Map<String, Object> value) {
        if (value == null)
            return;
        if (value.containsKey(BDefines.Keys.BCreationDate)) {
            if (value.get(BDefines.Keys.BCreationDate) instanceof Long) {
                Long data = (Long) value.get(BDefines.Keys.BCreationDate);
                if (data != null && data > 0)
                    bThread.setCreationDate(new Date(data));
            } else if (value.get(BDefines.Keys.BCreationDate) instanceof Double) {
                Double data = (Double) value.get(BDefines.Keys.BCreationDate);
                if (data != null && data > 0)
                    bThread.setCreationDate(new Date(data.longValue()));
            }
        }
        long type;
        if (value.containsKey(BDefines.Keys.BType)) {
            type = (Long) value.get(BDefines.Keys.BType);
            bThread.setType((int) type);
        }
        if (value.containsKey(BDefines.Keys.BName) && !value.get(BDefines.Keys.BName).equals(""))
            bThread.setName((String) value.get(BDefines.Keys.BName));
        Long lastMessageAdded = 0L;
        Object o = value.get(BDefines.Keys.BLastMessageAdded);
        if (o instanceof Long)
            lastMessageAdded = (Long) o;
        else if (o instanceof Double)
            lastMessageAdded = ((Double) o).longValue();
        if (lastMessageAdded != null && lastMessageAdded > 0) {
            Date date = new Date(lastMessageAdded);
            if (bThread.getLastMessageAdded() == null || date.getTime() > bThread.getLastMessageAdded().getTime())
                bThread.setLastMessageAdded(date);
        }
        bThread.setImageUrl((String) value.get(BDefines.Keys.BImageUrl));
        bThread.setCreatorEntityId((String) value.get(BDefines.Keys.BCreatorEntityId));
        DaoCore.updateEntity(bThread);
    }


}
