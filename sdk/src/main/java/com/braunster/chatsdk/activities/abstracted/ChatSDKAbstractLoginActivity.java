/*
 * Created by Itzik Braun on 12/3/2015.
 * Copyright (c) 2015 deluge. All rights reserved.
 *
 * Last Modification at: 3/12/15 4:27 PM
 */

package com.braunster.chatsdk.activities.abstracted;

import android.content.Intent;
import android.widget.EditText;

import com.braunster.chatsdk.R;
import com.braunster.chatsdk.Utils.Debug;
import com.braunster.chatsdk.activities.ChatSDKBaseActivity;
import com.braunster.chatsdk.activities.ChatSDKMainActivity;
import com.braunster.chatsdk.dao.BUser;
import com.braunster.chatsdk.network.BDefines;
import com.braunster.chatsdk.network.BNetworkManager;
import com.braunster.chatsdk.object.BError;

import org.apache.commons.lang3.StringUtils;
import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;

import java.util.Map;

import timber.log.Timber;

/**
 * Created by itzik on 6/8/2014.
 */
public class ChatSDKAbstractLoginActivity extends ChatSDKBaseActivity {

    private static final String TAG = ChatSDKAbstractLoginActivity.class.getSimpleName();
    private static boolean DEBUG = Debug.LoginActivity;

    private boolean exitOnBackPressed = false;

    protected EditText etEmail, etPass;

    /** Passed to the activity in the intent extras, Indicates that the activity was called after the user press the logout button,
     * That means the activity wont try to authenticate in inResume. */
    public static final String FLAG_LOGGED_OUT = "LoggedOut";

    protected void initViews(){
    }

    @Override
    protected void onResume() {
        super.onResume();
        // If there is preferences saved dont check auth ot the info does not contain AccountType.
        Map<String, ?> loginInfo =BNetworkManager.sharedManager().getNetworkAdapter().getLoginInfo();
        if (loginInfo != null && loginInfo.containsKey(BDefines.Prefs.AccountTypeKey))
            if (getIntent() == null || getIntent().getExtras() == null || !getIntent().getExtras().containsKey(FLAG_LOGGED_OUT)) {

                showProgDialog(getString(R.string.authenticating));
                authenticate().done(new DoneCallback<BUser>() {
                    @Override
                    public void onDone(BUser bUser) {
                        if (DEBUG) Timber.d("Authenticated");
                        dismissProgDialog();
                        afterLogin();
                    }
                })
                .fail(new FailCallback<BError>() {
                    @Override
                    public void onFail(BError bError) {
                        dismissProgDialog();
                        if (DEBUG) Timber.d("Auth Failed");
                    }
                });
            }
    }

    /* Dismiss dialog and open main activity.*/
    protected void afterLogin(){
        // Indexing the user.
        BUser currentUser = getNetworkAdapter().currentUserModel();
        if(getNetworkAdapter().currentUserModel() != null) {
            getNetworkAdapter().pushUser();
        }

        Intent logout = new Intent(ChatSDKMainActivity.Action_clear_data);
        sendBroadcast(logout);

        dismissProgDialog();
    }





    /* Exit Stuff*/
    @Override
    public void onBackPressed() {
        if (exitOnBackPressed)
        {
            // Exit the app.
            // If logged out from the main activity pressing back in the LoginActivity will get me back to the Main so this have to be done.
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        else super.onBackPressed();

    }

    public void toastErrorMessage(BError error, boolean login){
        String errorMessage = "";

        if (StringUtils.isNotBlank(error.message))
            errorMessage = error.message;
        else if (login)
            errorMessage = getString(R.string.login_activity_failed_to_login_toast);
        else
            errorMessage = getString(R.string.login_activity_failed_to_register_toast);


        showAlertToast(errorMessage);
    }

    protected boolean checkFields(){
        if (etEmail.getText().toString().isEmpty()) {
            showAlertToast(getString(R.string.login_activity_no_mail_toast));
            return false;
        }

        if (etPass.getText().toString().isEmpty()) {
            showAlertToast( getString(R.string.login_activity_no_password_toast) );
            return false;
        }

        return true;
    }
    protected void setExitOnBackPressed(boolean exitOnBackPressed) {
        this.exitOnBackPressed = exitOnBackPressed;
    }
}
