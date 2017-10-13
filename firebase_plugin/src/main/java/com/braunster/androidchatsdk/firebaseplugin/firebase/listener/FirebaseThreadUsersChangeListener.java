package com.braunster.androidchatsdk.firebaseplugin.firebase.listener;

import com.braunster.androidchatsdk.firebaseplugin.firebase.FirebaseEventsManager;
import com.braunster.chatsdk.dao.BThread;
import com.braunster.chatsdk.dao.BUser;
import com.braunster.chatsdk.dao.core.DaoCore;
import com.braunster.chatsdk.network.BNetworkManager;
import com.braunster.chatsdk.network.BPath;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Android on 10/13/2017.
 */
public class FirebaseThreadUsersChangeListener implements ValueEventListener {

    private String TAG = "FBThreadUsersChange";

    @Override
    public void onDataChange(final DataSnapshot dataSnapshot) {
        FirebaseEventsManager.Executor.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
                BUser currentUser = BNetworkManager.sharedManager().getNetworkAdapter().currentUserModel();
                BPath path = BPath.pathWithPath(dataSnapshot.getRef().toString());
                final String threadEntityId = path.idForIndex(0);
                final BThread thread = DaoCore.fetchOrCreateEntityWithEntityID(BThread.class, threadEntityId);
                Set<String> userEntityIds = fetchUserEntityIdExceptCurrent((Map<String, Object>) dataSnapshot.getValue(),
                        currentUser.getEntityID());
                for (String userEntityId : userEntityIds) {
                    BUser bUser = DaoCore.fetchOrCreateEntityWithEntityID(BUser.class, userEntityId);
                    if (!thread.hasUser(bUser))
                        DaoCore.connectUserAndThread(bUser, thread);
                }
            }
        });
    }


    private Set<String> fetchUserEntityIdExceptCurrent(Map<String, Object> data, String currentUserEntityId) {
        if (data != null) {
            Set<String> userEntityIds = data.keySet();
            userEntityIds.remove(currentUserEntityId);
            return data.keySet();
        }
        return new HashSet<>();
    }


    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
