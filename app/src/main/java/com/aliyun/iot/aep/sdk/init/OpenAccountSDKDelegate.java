package com.aliyun.iot.aep.sdk.init;

import android.app.Application;
import android.content.res.Resources;
import android.text.TextUtils;

import com.alibaba.sdk.android.openaccount.ui.LayoutMapping;
import com.alibaba.sdk.android.openaccount.ui.ui.LoginActivity;
import com.alibaba.sdk.android.openaccount.ui.ui.MobileCountrySelectorActivity;
import com.aliyun.ayland.ui.adapter.ATMyOALoginAdapter;
import com.aliyun.iot.aep.oa.OAUIInitHelper;
import com.aliyun.iot.aep.oa.page.data.ALiYunAuthConfigData;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClientImpl;
import com.aliyun.iot.aep.sdk.apiclient.adapter.APIGatewayHttpAdapterImpl;
import com.aliyun.iot.aep.sdk.apiclient.hook.IoTAuthProvider;
import com.aliyun.iot.aep.sdk.credential.IoTCredentialProviderImpl;
import com.aliyun.iot.aep.sdk.credential.IotCredentialManager.IoTCredentialManageImpl;
import com.aliyun.iot.aep.sdk.framework.sdk.SDKConfigure;
import com.aliyun.iot.aep.sdk.framework.sdk.SimpleSDKDelegateImp;
import com.aliyun.iot.aep.sdk.log.ALog;
import com.aliyun.iot.aep.sdk.login.LoginBusiness;
import com.aliyun.iot.aep.sdk.login.oa.OALoginAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by wuwang on 2017/10/30.
 */

public final class OpenAccountSDKDelegate extends SimpleSDKDelegateImp {
    static final private String TAG = "OpenAccountSDKDelegate";

    public static final String ENV_KEY_API_CLIENT_API_ENV = "KEY_API_CLIENT_API_ENV";

    /* API: ISDKDelegate */

    @Override
    public int init(Application app, SDKConfigure configure, Map<String, String> args) {
        String env = args == null ? "TEST" : args.get("env");
        String securityIndex = args == null ? "114d" : args.get("securityIndex");
        String region = "China";
        if (null != args) {
            String r = args.get("region");
            if (!TextUtils.isEmpty(r)) {
                region = r;
            }
        }
        String appKey = APIGatewayHttpAdapterImpl.getAppKey(app, securityIndex);

        //??????SDK???????????????
        JSONObject opts = configure.opts;

//        //1--- ???????????????OALoginAdapter??????ILoginAdapter????????????
//        OALoginAdapter loginAdapter = new OALoginAdapter(app);
//        if (args != null && region.equalsIgnoreCase("Singapore")) {
//            if (opts != null && opts.has("sgp_host")) {
//                try {//???????????????Host
//                    loginAdapter.setDefaultOAHost(opts.getString("sgp_host"));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        loginAdapter.init(env, securityIndex, new OALoginAdapter.OALoginAdapterInitResultCallback() {
//            @Override
//            public void onInitSuccess() {
//                Log.i(TAG, "onInitSuccess");
//            }
//
//            @Override
//            public void onInitFailed(int i, String s) {
//                Log.i(TAG, "onInitFailed");
//            }
//        });
//        loginAdapter.setIsDebuggable(true);
//
//        //2--- ???????????????????????????SDK?????????loginAdapter??????????????????ILoginAdapter??????????????????????????????
//        LoginBusiness.init(app, loginAdapter, true, env);

        ATMyOALoginAdapter adapter = new ATMyOALoginAdapter(app);
        adapter.init("online", "114d");
        LoginBusiness.init(app, adapter, "online");

        //3--- ????????????UI(?????????????????????)
        boolean supportALiYun = false;
        ALiYunAuthConfigData configData = null;
        try {
            if (opts != null) {
                supportALiYun = opts.has("supportAliYun") && "true".equalsIgnoreCase(opts.getString("supportAliYun"));
                configData = new ALiYunAuthConfigData();
                configData.oauth_consumer_key_test = opts.getString("oauth_consumer_key_test");
                configData.oauth_consumer_key_online = opts.getString("oauth_consumer_key_online");
                configData.oauth_consumer_secret_test = opts.getString("oauth_consumer_secret_test");
                configData.oauth_consumer_secret_online = opts.getString("oauth_consumer_secret_online");
            }
        } catch (Exception e) {
        }
        OAUIInitHelper.initConfig(adapter, supportALiYun, configData);


        //4--- ?????????????????????????????????(???sdk_config.json?????????)
        try {
            if (!supportALiYun) {//????????????????????????????????????UI
                injectCustomUIConfig(app, adapter, opts);
            }
        } catch (Exception e) {
            ALog.i(TAG, "Inject CustomUIConfig failed:" + e.toString());
        }

        //5--- ????????????????????????
        try {
            if (opts != null && opts.has("disable_screen_protrait")) {
                boolean disableScreenProtrait = "true".equalsIgnoreCase(opts.getString("disable_screen_protrait"));
                if (disableScreenProtrait) {
                    OAUIInitHelper.disableScreenLandscape();
                }
            }
        } catch (Exception e) {

        }

        //6--- ?????????????????????????????????
        try {
            if (opts != null && opts.has("disable_foreign_mobile_number")) {
                boolean disableForeignMobileNumber = "true".equalsIgnoreCase(opts.getString("disable_foreign_mobile_number"));
                if (disableForeignMobileNumber) {
                    OAUIInitHelper.disableForeignMobileNumbers();
                }
            }
        } catch (Exception e) {

        }

        //7--- ?????????????????????SDK
        IoTCredentialManageImpl.init(appKey);

        //8--- ?????????????????????IoTtoken??????
        IoTAuthProvider provider = new IoTCredentialProviderImpl(IoTCredentialManageImpl.getInstance(app));
        IoTAPIClientImpl.getInstance().registerIoTAuthProvider("iotAuth", provider);

        try {//?????????????????????????????????
            String aliyunDailyCreateIotTokenHost = "";
            if (opts != null) {
                aliyunDailyCreateIotTokenHost = opts.getString("aliyun_daily_create_iottoken_host");
            }
            if (!TextUtils.isEmpty(aliyunDailyCreateIotTokenHost)) {
                IoTCredentialManageImpl.DefaultDailyALiYunCreateIotTokenRequestHost = aliyunDailyCreateIotTokenHost;
            }
        } catch (Exception e) {

        }

        return 0;
    }

    private static void injectCustomUIConfig(Application app, OALoginAdapter loginAdapter, JSONObject customUIData) throws Exception {
        JSONArray array = customUIData.getJSONArray("ui_config");
        if (array == null || array.length() == 0) {
            return;
        }
        String activity, layout;
        boolean isSelectMobileCountry;

        for (int i = 0; i < array.length(); i++) {
            activity = ((JSONObject) array.get(i)).getString("activity");
            layout = ((JSONObject) array.get(i)).getString("layout");
            isSelectMobileCountry = false;
            if (((JSONObject) array.get(i)).has("is_select_mobile_country")) {
                isSelectMobileCountry = "true".equalsIgnoreCase(((JSONObject) array.get(i)).getString("is_select_mobile_country"));
            }
            Class activityClazz = Class.forName(activity);
            if (LoginActivity.class.isAssignableFrom(activityClazz)) {//?????????LoginActivity??????????????????????????????????????????
                loginAdapter.setDefaultLoginClass(activityClazz);
                //??????????????????
                JSONObject params = ((JSONObject) array.get(i)).getJSONObject("params");
                Map<String, String> loginActivityParams = new HashMap<>();
                if (params != null) {
                    Iterator it = params.keys();
                    while (it.hasNext()) {
                        String key = (String) it.next();
                        loginActivityParams.put(key, params.getString(key));
                    }
                }
                if (!loginActivityParams.isEmpty()) {
                    loginAdapter.setDefaultLoginParams(loginActivityParams);
                }
            } else if (MobileCountrySelectorActivity.class.isAssignableFrom(activityClazz) || isSelectMobileCountry) {//???????????????????????????
                OAUIInitHelper.setCustomSelectCountryActivityUIConfig(activityClazz);
            }

            if (!TextUtils.isEmpty(layout)) {
                LayoutMapping.put(activityClazz, getLayoutIdFromR(app, layout));
            }
        }
    }

    private static int getLayoutIdFromR(Application app, String idName) {
        Resources resources = app.getResources();
        return resources.getIdentifier(idName, "layout", app.getPackageName());
    }
}
