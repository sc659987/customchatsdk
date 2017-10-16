/*
 * Created by Itzik Braun on 12/3/2015.
 * Copyright (c) 2015 deluge. All rights reserved.
 *
 * Last Modification at: 3/12/15 4:27 PM
 */

package com.braunster.chatsdk.fragments.abstracted;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TimingLogger;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.braunster.chatsdk.R;
import com.braunster.chatsdk.Utils.Debug;
import com.braunster.chatsdk.activities.abstracted.ChatSDKAbstractChatActivity;
import com.braunster.chatsdk.adapter.ChatSDKThreadsListAdapter;
import com.braunster.chatsdk.adapter.abstracted.ChatSDKAbstractThreadsListAdapter;
import com.braunster.chatsdk.dao.BMessage;
import com.braunster.chatsdk.dao.BThread;
import com.braunster.chatsdk.dao.BUser;
import com.braunster.chatsdk.dao.core.DaoCore;
import com.braunster.chatsdk.dao.entities.Entity;
import com.braunster.chatsdk.fragments.ChatSDKBaseFragment;
import com.braunster.chatsdk.network.BNetworkManager;
import com.braunster.chatsdk.network.events.BatchedEvent;
import com.braunster.chatsdk.network.events.Event;
import com.braunster.chatsdk.object.Batcher;
import com.braunster.chatsdk.object.UIUpdater;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.braunster.chatsdk.dao.entities.BMessageEntity.Type.IMAGE;
import static com.braunster.chatsdk.dao.entities.BMessageEntity.Type.LOCATION;
import static com.braunster.chatsdk.dao.entities.BMessageEntity.Type.TEXT;

/**
 * Created by itzik on 6/17/2014.
 */
public class ChatSDKAbstractConversationsFragment extends ChatSDKBaseFragment {

    private static final String TAG = ChatSDKAbstractConversationsFragment.class.getSimpleName();
    private static boolean DEBUG = Debug.ConversationsFragment;
    public static final String APP_EVENT_TAG = "ConverstaionFragment";

    protected RecyclerView recyclerView;
    protected ConversionAdapter conversionAdapter;

    protected ProgressBar progressBar;

    protected TimingLogger timings;
    protected UIUpdater uiUpdater;

    protected boolean inflateMenuItems = true;

    protected AdapterView.OnItemLongClickListener onItemLongClickListener;
    protected AdapterView.OnItemClickListener onItemClickListener;

    private int form = 0, limit = 20;

    protected static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm dd/MM/yy");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().registerReceiver(receiver, new IntentFilter(ChatSDKAbstractChatActivity.ACTION_CHAT_CLOSED));
    }

    @Override
    public void initViews() {
        //listThreads = (ListView) mainView.findViewById(R.id.list_threads);
        recyclerView = (RecyclerView) mainView.findViewById(R.id.chat_conversion_recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);

        this.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
        this.conversionAdapter = new ConversionAdapter();

        progressBar = (ProgressBar) mainView.findViewById(R.id.chat_sdk_progress_bar);
        initList();
    }

    private void initList() {
        this.recyclerView.setAdapter(conversionAdapter);
        //  if (onItemClickListener == null) {
//            onItemClickListener = new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    startChatActivityForID(adapter.getItem(position).getId());
//                }
//            };
//        }
//
//        //listThreads.setOnItemClickListener(onItemClickListener);
//
//        if (onItemLongClickListener == null) {
//            onItemLongClickListener = new AdapterView.OnItemLongClickListener() {
//                @Override
//                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                    showAlertDialog("", getResources().getString(R.string.alert_delete_thread), getResources().getString(R.string.delete),
//                            getResources().getString(R.string.cancel), null, new DeleteThread(adapter.getItem(position).getEntityId()));
//
//                    return true;
//                }
//            };
//        }
        //listThreads.setOnItemLongClickListener(onItemLongClickListener);
        loadData();
    }

    @Override
    public void loadData() {
        super.loadData();
        if (mainView == null)
            return;
        conversionAdapter.setData(BNetworkManager
                .sharedManager()
                .getNetworkAdapter()
                .getPrivateThreadWithLimit(form, limit));
        conversionAdapter.notifyDataSetChanged();
        recyclerView.invalidate();
    }

    @Override
    public void loadDataOnBackground() {
        uiUpdater = new UIUpdater() {
            @Override
            public void run() {
                List list = BNetworkManager.sharedManager()
                        .getNetworkAdapter().getPrivateThreadWithLimit(form, limit);
                if (DEBUG)
                    timings.addSplit("Loading threads");
                uiUpdater = null;
                Message message = new Message();
                message.obj = list;
                message.what = 1;
                handler.sendMessageAtFrontOfQueue(message);

                if (DEBUG) timings.addSplit("Sending message to handler.");
            }
        };

        ChatSDKAbstractConversationsFragmentChatSDKThreadPool.getInstance().scheduleExecute(uiUpdater, 10);
    }

    @Override
    public void refreshForEntity(Entity entity) {
        super.refreshForEntity(entity);
//        if (adapter.getCount() == 0)
//            return;
//
//
//        adapter.replaceOrAddItem((BThread) entity);
        if (progressBar.getVisibility() == View.VISIBLE) {
            progressBar.setVisibility(View.GONE);
            //listThreads.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void clearData() {
//        if (adapter != null) {
//            if (uiUpdater != null)
//                uiUpdater.setKilled(true);
//
//            adapter.getThreadItems().clear();
//            adapter.notifyDataSetChanged();
//        }
    }

    private class UpdateHandler extends Handler {

        public UpdateHandler(Looper mainLooper) {
            super(mainLooper);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case 1:
                    conversionAdapter.clearData();
                    conversionAdapter.setData((List<BThread>) msg.obj);
                    conversionAdapter.notifyDataSetChanged();
                    loadDataOnBackground();
//                    //adapter.setThreadItems((List<ChatSDKThreadsListAdapter.ThreadListItem>) msg.obj);
//                    if (progressBar.getVisibility() == View.VISIBLE) {
//                        progressBar.setVisibility(View.INVISIBLE);
//                        //listThreads.setVisibility(View.VISIBLE);
//                    }
//                    if (DEBUG) {
//                        timings.addSplit("Updating UI");
//                        timings.dumpToLog();
//                        timings.reset(TAG.substring(0, 21), "loadDataOnBackground");
//                    }loadDataOnBackground()
                    break;
            }
        }
    }

    private UpdateHandler handler = new UpdateHandler(Looper.getMainLooper());

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (!inflateMenuItems)
            return;

        super.onCreateOptionsMenu(menu, inflater);
        MenuItem item =
                menu.add(Menu.NONE, R.id.action_chat_sdk_add, 10, "Add Conversation");
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        item.setIcon(R.drawable.ic_plus);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (!inflateMenuItems)
            return super.onOptionsItemSelected(item);

        /* Cant use switch in the library*/
        int id = item.getItemId();

        if (id == R.id.action_chat_sdk_add) {
            startPickFriendsActivity();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadDataOnBackground();


    }

    @Override
    public void onPause() {
        super.onPause();
        ChatSDKAbstractConversationsFragmentChatSDKThreadPool.getInstance().removeSchedule(uiUpdater);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ChatSDKAbstractChatActivity.ACTION_CHAT_CLOSED)) {
                loadDataOnBackground();
            }
        }
    };

    /**
     * FIXME not sure if needed.
     * Created by braunster on 18/08/14.
     */
    private static class ChatSDKAbstractConversationsFragmentChatSDKThreadPool {
        // Sets the amount of time an idle thread waits before terminating
        private static final int KEEP_ALIVE_TIME = 3;
        // Sets the Time Unit to seconds
        private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;

        private LinkedBlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>();
        /*
         * Gets the number of available cores
         * (not always the same as the maximum number of cores)
         */
        private static int NUMBER_OF_CORES =
                Runtime.getRuntime().availableProcessors();

        private ThreadPoolExecutor threadPool;
        private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;

        private static ChatSDKAbstractConversationsFragmentChatSDKThreadPool instance;

        public static ChatSDKAbstractConversationsFragmentChatSDKThreadPool getInstance() {
            if (instance == null)
                instance = new ChatSDKAbstractConversationsFragmentChatSDKThreadPool();
            return instance;
        }

        private ChatSDKAbstractConversationsFragmentChatSDKThreadPool() {

            if (NUMBER_OF_CORES <= 0)
                NUMBER_OF_CORES = 2;

            // Creates a thread pool manager
            threadPool = new ThreadPoolExecutor(
                    NUMBER_OF_CORES,       // Initial pool size
                    NUMBER_OF_CORES,       // Max pool size
                    KEEP_ALIVE_TIME,
                    KEEP_ALIVE_TIME_UNIT,
                    workQueue);

            scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(NUMBER_OF_CORES);

        }

        public void execute(Runnable runnable) {
            threadPool.execute(runnable);
        }

        public void scheduleExecute(Runnable runnable, long delay) {
            scheduledThreadPoolExecutor.schedule(runnable, delay, TimeUnit.SECONDS);
        }

        public boolean removeSchedule(Runnable runnable) {
            return scheduledThreadPoolExecutor.remove(runnable);
        }
    }

    public class ConversionAdapter extends RecyclerView.Adapter<ConversionAdapter.ViewHolder> {

        private List<ConversionListItem> conversionListItems = new LinkedList<>();

        public class ViewHolder extends RecyclerView.ViewHolder {
            private CircleImageView image;
            private TextView name;
            private TextView lastMessageText;
            private TextView lastMessageDate;
            private TextView noUnreadMessage;


            public ViewHolder(View view) {
                super(view);
                this.image = (CircleImageView) view.findViewById(R.id.img_thread_image);
                this.name = (TextView) view.findViewById(R.id.chat_sdk_txt);
                this.lastMessageText = (TextView) view.findViewById(R.id.txt_last_message);
                this.lastMessageDate = (TextView) view.findViewById(R.id.txt_last_message_date);
                this.noUnreadMessage = (TextView) view.findViewById(R.id.txt_unread_messages);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_sdk_row_threads, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            ConversionListItem conversionListItem = this.conversionListItems.get(position);
            holder.image.setImageResource(R.drawable.ic_profile);
            holder.name.setText(conversionListItem.name);
            holder.lastMessageText.setText(conversionListItem.lastMessageText);
            holder.lastMessageDate.setText(conversionListItem.lastMessageDate);
            holder.noUnreadMessage.setText(conversionListItem.noUnreadMessage);
        }

        @Override
        public int getItemCount() {
            return conversionListItems.size();
        }

        public void setData(List<BThread> bThreads) {
            for (BThread thread : bThreads) {
                String[] data = new String[2];
                getLastMessageTextAndDate(thread, data);
                conversionListItems.add(new ConversionListItem(thread.getEntityID(),
                        thread.displayName(),
                        thread.threadImageUrl(),
                        data[1], data[0],
                        thread.getUnreadMessagesAmount()));
            }
        }

        public void clearData() {
            conversionListItems.clear();
        }
    }

    public class ConversionListItem {

        public String entityId;
        public String url;
        public String name;
        public String lastMessageText;
        public String lastMessageDate;
        public String noUnreadMessage;

        public ConversionListItem(String entityId,
                                  String name,
                                  String imageUrl,
                                  String lastMessageDate,
                                  String lastMessageText,
                                  int unreadMessagesAmount) {
            this.entityId = entityId;
            this.url = imageUrl;
            this.name = name;
            this.lastMessageText = lastMessageText;
            this.lastMessageDate = lastMessageDate;
            this.noUnreadMessage = String.valueOf(unreadMessagesAmount);
        }
    }

    public static String[] getLastMessageTextAndDate(BThread thread, String[] data) {
        List<BMessage> messages = thread.getMessagesWithOrder(DaoCore.ORDER_DESC);

        // If no message create dummy message.
        if (messages.size() == 0) {
//                if (DEBUG) Log.d(TAG, "No messages");
//            message = new BMessage();
//            message.setText("No Messages...");
//            message.setType(bText.ordinal());
            data[0] = "No Message";
            data[1] = "";
            return data;
        }
//            if (DEBUG) Log.d(TAG, "Message text: " + messages.get(0).getText());
        if (messages.get(0).getType() == null)
            data[0] = "Bad Data";
        else
            switch (messages.get(0).getType()) {
                case TEXT:
                    // TODO cut string if needed.
                    //http://stackoverflow.com/questions/3630086/how-to-get-string-width-on-android
                    data[0] = messages.get(0).getText();
                    break;

                case IMAGE:
                    data[0] = "Image message";
                    break;

                case LOCATION:
                    data[0] = "Location message";
                    break;
            }
        data[1] = simpleDateFormat.format(messages.get(0).getDate());
        return data;
    }

}
