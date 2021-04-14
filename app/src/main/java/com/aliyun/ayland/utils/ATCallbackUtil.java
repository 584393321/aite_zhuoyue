package com.aliyun.ayland.utils;

import com.aliyun.ayland.interfaces.ATCallBack;

public class ATCallbackUtil {
    private static ATCallBack mCallBack;

    public static void setCallBack(ATCallBack callBack) {
        mCallBack = callBack;
    }

    public static void doCallBackMethod(String authCode){
        mCallBack.auth(authCode);
    }
}