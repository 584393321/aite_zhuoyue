package com.aliyun.ayland.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.alibaba.sdk.android.openaccount.OpenAccountSDK;
import com.alibaba.sdk.android.openaccount.OpenAccountService;
import com.alibaba.sdk.android.openaccount.callback.LoginCallback;
import com.aliyun.ayland.ui.activity.ATLoginActivity;
import com.aliyun.ayland.utils.ATCallbackUtil;
import com.aliyun.iot.aep.sdk.login.ILoginCallback;
import com.aliyun.iot.aep.sdk.login.oa.OALoginAdapter;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class ATMyOALoginAdapter2 extends OALoginAdapter{
    private Context context;
    private String TAG = "MyOALoginAdapter";

    public ATMyOALoginAdapter2(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public void login(ILoginCallback callback) {
        //打开三方登录页面
        Intent intent = new Intent();
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK);
        intent.setClass(context, ATLoginActivity.class);
        context.startActivity(intent);

        ATCallbackUtil.setCallBack(authCode -> authLogin(authCode, new OALoginCallback(callback)));
    }

    public void authLogin(String authCode, LoginCallback mLoginCallback) {
        Log.e(TAG, "authLogin: "+authCode + mLoginCallback);
        //authCode 是三方登录页面返回的code
        OpenAccountService service = OpenAccountSDK.getService(OpenAccountService.class);
        service.authCodeLogin(context, authCode, mLoginCallback);
    }
}