package com.aliyun.ayland.utils;

import android.text.TextUtils;

import com.aliyun.ayland.global.ATGlobalApplication;
import com.aliyun.iot.aep.sdk.login.ILoginCallback;
import com.aliyun.iot.aep.sdk.login.ILogoutCallback;
import com.aliyun.iot.aep.sdk.login.LoginBusiness;

public class ATLoginUtils {
    /**
     * 登录
     *
     * @param account password loginCallback
     */
    public static void login(String account, String password, ATLoginCallBack loginCallback) {
        if (LoginBusiness.isLogin() && account.equals(ATGlobalApplication.getAccount()) && !TextUtils.isEmpty(ATGlobalApplication.getLoginBeanStr())) {
            loginCallback.onSuccess();
        } else {
            LoginBusiness.login(new ILoginCallback() {
                @Override
                public void onLoginSuccess() {
                    loginCallback.onSuccess();
                }

                @Override
                public void onLoginFailed(int code, String error) {
                    loginCallback.onFailure(code, error);
                }
            });
        }
    }

    /**
     * 退出登录
     */
    public static void logout() {
        LoginBusiness.logout(new ILogoutCallback() {
            @Override
            public void onLogoutSuccess() {
                ATGlobalApplication.setAllDeviceData("");
                ATGlobalApplication.setAccount("");
                ATGlobalApplication.setLoginBeanStr("");
            }

            @Override
            public void onLogoutFailed(int code, String error) {
            }
        });
    }
}