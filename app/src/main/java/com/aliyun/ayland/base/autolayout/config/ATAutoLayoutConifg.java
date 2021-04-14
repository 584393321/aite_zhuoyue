package com.aliyun.ayland.base.autolayout.config;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.aliyun.ayland.base.autolayout.util.ATScreenUtils;
import com.aliyun.ayland.utils.ATL;

/**
 * Created by zhy on 15/11/18.
 */
public class ATAutoLayoutConifg {
    private static ATAutoLayoutConifg sIntance = new ATAutoLayoutConifg();

    private static final String KEY_DESIGN_WIDTH = "design_width";
    private static final String KEY_DESIGN_HEIGHT = "design_height";

    private int mScreenWidth;
    private int mScreenHeight;

    private int mDesignWidth;
    private int mDesignHeight;

    private ATAutoLayoutConifg(){
    }

    public void checkParams(){
        if(mDesignHeight <=0 || mDesignWidth <= 0 ){
            throw new RuntimeException(
                    "you must set " + KEY_DESIGN_WIDTH + " and " + KEY_DESIGN_HEIGHT + "  in your manifest file.");
        }
    }

    public static ATAutoLayoutConifg getInstance()
    {
        return sIntance;
    }

    public int getScreenWidth()
    {
        return mScreenWidth;
    }

    public int getScreenHeight()
    {
        return mScreenHeight;
    }

    public int getDesignWidth()
    {
        return mDesignWidth;
    }

    public int getDesignHeight()
    {
        return mDesignHeight;
    }

    public void init(Context context) {
        getMetaData(context);
        int[] screenSize = ATScreenUtils.getScreenSize(context);
        mScreenWidth = screenSize[0];
        mScreenHeight = screenSize[1];
        ATL.e(" screenWidth =" + mScreenWidth + " ,screenHeight = " + mScreenHeight);
    }

    private void getMetaData(Context context){
        PackageManager packageManager = context.getPackageManager();
        ApplicationInfo applicationInfo;
        try{
            applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (applicationInfo != null && applicationInfo.metaData != null){
                mDesignWidth = (Integer) applicationInfo.metaData.get(KEY_DESIGN_WIDTH);
                mDesignHeight = (Integer) applicationInfo.metaData.get(KEY_DESIGN_HEIGHT);
            }
        } catch (PackageManager.NameNotFoundException e){
            throw new RuntimeException(
                    "you must set " + KEY_DESIGN_WIDTH + " and " + KEY_DESIGN_HEIGHT + "  in your manifest file.", e);
        }
        ATL.e(" designWidth =" + mDesignWidth + " , designHeight = " + mDesignHeight);
    }
}