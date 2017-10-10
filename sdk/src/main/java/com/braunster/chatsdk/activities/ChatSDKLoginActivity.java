/*
 * Created by Itzik Braun on 12/3/2015.
 * Copyright (c) 2015 deluge. All rights reserved.
 *
 * Last Modification at: 3/12/15 4:27 PM
 */

package com.braunster.chatsdk.activities;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.braunster.chatsdk.R;
import com.braunster.chatsdk.Utils.Debug;
import com.braunster.chatsdk.activities.abstracted.ChatSDKAbstractLoginActivity;
import com.braunster.chatsdk.dao.BUser;
import com.braunster.chatsdk.dao.core.DaoCore;
import com.braunster.chatsdk.network.AbstractNetworkAdapter;
import com.braunster.chatsdk.network.BDefines;
import com.braunster.chatsdk.network.BNetworkManager;
import com.braunster.chatsdk.object.BError;
import com.studycopter.network.CourseType;
import com.studycopter.network.RetrofitInstance;
import com.studycopter.network.StudyCopterService;
import com.studycopter.network.Utils;
import com.studycopter.network.model.StudyCopterStudentDetail;

import org.apache.commons.lang3.StringUtils;
import org.jdeferred.DoneCallback;
import org.jdeferred.FailCallback;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by itzik on 6/8/2014.
 */
public class ChatSDKLoginActivity extends ChatSDKAbstractLoginActivity {

    private static final String TAG = ChatSDKLoginActivity.class.getSimpleName();
    private static boolean DEBUG = Debug.LoginActivity;

    private Button btnLogin;
    private ImageView appIconImage;

    private RetrofitInstance retrofitInstance;
    private StudyCopterService studyCopterService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_sdk_activty_login);
        setExitOnBackPressed(true);
        View view = findViewById(R.id.chat_sdk_root_view);
        setupTouchUIToDismissKeyboard(view);
        initViews();
        this.retrofitInstance = new RetrofitInstance("http://studycopter.com/");
        this.studyCopterService = this.retrofitInstance.getStudyCopterServiceInstance();
    }

    @Override
    protected void initViews() {
        super.initViews();

        btnLogin = (Button) findViewById(R.id.chat_sdk_btn_login);
        etEmail = (EditText) findViewById(R.id.chat_sdk_et_mail);
        etPass = (EditText) findViewById(R.id.chat_sdk_et_password);

        appIconImage = (ImageView) findViewById(R.id.app_icon);

        appIconImage.post(new Runnable() {
            @Override
            public void run() {
                appIconImage.setVisibility(View.VISIBLE);
            }
        });
    }

    private void initListeners() {
        etPass.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    btnLogin.callOnClick();
                }
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        initListeners();
    }

    /* Dismiss dialog and open main activity.*/
    @Override
    protected void afterLogin() {
        super.afterLogin();
        // Updating the version name.
        BUser curUser = getNetworkAdapter().currentUserModel();
        String version = BDefines.BAppVersion,
                metaVersion = curUser.metaStringForKey(BDefines.Keys.BVersion);
        if (StringUtils.isNotEmpty(version)) {
            if (StringUtils.isEmpty(metaVersion) || !metaVersion.equals(version)) {
                curUser.setMetadataString(BDefines.Keys.BVersion, version);
            }
            DaoCore.updateEntity(curUser);
        }
        startMainActivity();
    }

    public void onLoginClicked(View v) {
        if (!checkFields())
            return;
        showProgDialog(getString(R.string.connecting));
        this.studyCopterService.loginToStudyCopter(etEmail.getText().toString(),
                Utils.MD5(etPass.getText().toString()), "1", "2",
                CourseType.IBPS.toString())
                .enqueue(new Callback<StudyCopterStudentDetail>() {
                    @Override
                    public void onResponse(Call<StudyCopterStudentDetail> call,
                                           Response<StudyCopterStudentDetail> response) {
                        if (response.isSuccessful()) {
                            StudyCopterStudentDetail detail = response.body();
                            //TODO save in preference, find out why
                            if (detail != null) {
                                Map<String, Object> data = AbstractNetworkAdapter.getMap(
                                        new String[]{BDefines.Prefs.LoginTypeKey, BDefines.Prefs.TokenKey},
                                        BDefines.BAccountType.Custom, detail.getFirebaseToken());
                                BNetworkManager.sharedManager().getNetworkAdapter()
                                        .authenticateWithMap(data)
                                        .done(new DoneCallback<Object>() {
                                            @Override
                                            public void onDone(Object o) {
                                                afterLogin();
                                            }
                                        })
                                        .fail(new FailCallback<BError>() {
                                            @Override
                                            public void onFail(BError bError) {
                                                toastErrorMessage(bError, true);
                                                dismissProgDialog();
                                            }
                                        });
                            } else {
                                dismissProgDialog();
                            }
                        } else {
                            dismissProgDialog();
                        }
                    }
                    @Override
                    public void onFailure(Call<StudyCopterStudentDetail> call, Throwable t) {
                        dismissProgDialog();
                    }
                });
    }
}
