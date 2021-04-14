package com.aliyun.ayland.ui.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.ayland.base.ATBaseActivity;
import com.aliyun.ayland.contract.ATMainContract;
import com.aliyun.ayland.global.ATConstants;
import com.aliyun.ayland.presenter.ATMainPresenter;
import com.aliyun.ayland.utils.ATCacheDataManager;
import com.aliyun.iot.aep.sdk.apiclient.IoTAPIClientFactory;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTCallback;
import com.aliyun.iot.aep.sdk.apiclient.callback.IoTResponse;
import com.aliyun.iot.aep.sdk.apiclient.emuns.Scheme;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequest;
import com.aliyun.iot.aep.sdk.apiclient.request.IoTRequestBuilder;
import com.aliyun.iot.aep.sdk.threadpool.ThreadPool;
import com.anthouse.wyzhuoyue.R;

import org.json.JSONException;

import java.util.Map;

public class ATSoundAuthorizationActivity extends ATBaseActivity implements ATMainContract.View {
    private int REQUEST_CODE_AUTHORIZED = 1001;
    private ATMainPresenter mPresenter;
    private RelativeLayout rlTmall;
    private Dialog dialog;
    private TextView mTvTilte, mTvLeft, mTvRight, tvStorage;

    @Override
    protected int getLayoutId() {
        return R.layout.at_activity_sound_authorization;
    }

    @Override
    protected void findView() {
        rlTmall = findViewById(R.id.rl_tmall);
        init();
    }

    @Override
    protected void initPresenter() {
        mPresenter = new ATMainPresenter(this);
        mPresenter.install(this);
    }

    private void init() {
        rlTmall.setOnClickListener(v -> getBind());
        initDialog();
    }

    @SuppressLint("InflateParams")
    private void initDialog() {
        dialog = new Dialog(this, R.style.nameDialog);
        View view = LayoutInflater.from(this).inflate(R.layout.at_dialog_clean, null, false);
        mTvTilte = view.findViewById(R.id.tv_title);
        mTvLeft = view.findViewById(R.id.tv_cancel);
        mTvRight = view.findViewById(R.id.tv_sure);
        mTvLeft.setOnClickListener(v -> dialog.dismiss());
        mTvRight.setOnClickListener(v -> {
            if (getString(R.string.at_cancel).equals(mTvLeft.getText().toString()))
                new Thread(() -> {
                    try {
                        ATCacheDataManager.clearAllCache(this);
                        if (ATCacheDataManager.getTotalCacheSize(this).startsWith("0")) {
                            runOnUiThread(() -> {
                                showToast(getString(R.string.at_clear_finish));
                                dialog.dismiss();
                                try {
                                    tvStorage.setText(ATCacheDataManager.getTotalCacheSize(this));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });
                        }
                    } catch (Exception e) {
                        Log.e("onClick: ", e.getMessage());
                    }
                }).start();
            else
                unbind();
        });
        dialog.setContentView(view);
    }

    @Override
    public void requestResult(String result, String url) {
        try {
            org.json.JSONObject jsonResult = new org.json.JSONObject(result);
            if ("200".equals(jsonResult.getString("code"))) {
                switch (url) {
                    case ATConstants.Config.SERVER_URL_FACEVILLAGELIST:
                        break;
                    case ATConstants.Config.SERVER_URL_GETFACE:
                        break;
                }
            } else {
                showToast(jsonResult.getString("message"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getBind() {
        IoTRequest ioTRequest = new IoTRequestBuilder()
                .setAuthType("iotAuth")
                .setApiVersion("1.0.5")
                .setPath("/account/thirdparty/get")
                .addParam("accountType", "TAOBAO")
                .setScheme(Scheme.HTTPS)
                .build();
        new IoTAPIClientFactory().getClient().send(ioTRequest, new IoTCallback() {
            @Override
            public void onFailure(IoTRequest ioTRequest, Exception e) {
                Log.e("onResponse: ", e.getMessage());
            }

            @Override
            public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
                ThreadPool.MainThreadHandler.getInstance().post(() -> {
                    if (TextUtils.isEmpty(ioTResponse.getData().toString()))
                        startActivityForResult(new Intent(ATSoundAuthorizationActivity.this, ATTianMaoWebViewActivity.class)
                                , REQUEST_CODE_AUTHORIZED);
                    else {
                        mTvTilte.setText(getString(R.string.at_tmall_wizard_authorization_have_been_bind));
                        mTvLeft.setText(getString(R.string.at_back));
                        mTvRight.setText(getString(R.string.at_cancel_bind));
                        dialog.show();
                    }
                });
                Log.e("onResponse: ", ioTResponse.getCode() + "---" + ioTResponse.getMessage() + "---" + ioTResponse.getLocalizedMsg());
            }
        });
    }

    public void unbind() {
        IoTRequest ioTRequest = new IoTRequestBuilder()
                .setAuthType("iotAuth")
                .setApiVersion("1.0.5")
                .setPath("/account/thirdparty/unbind")
                .addParam("accountType", "TAOBAO")
                .setScheme(Scheme.HTTPS)
                .build();
        new IoTAPIClientFactory().getClient().send(ioTRequest, new IoTCallback() {
            @Override
            public void onFailure(IoTRequest ioTRequest, Exception e) {
                ThreadPool.MainThreadHandler.getInstance().post(() -> showToast(e.getMessage()));
            }

            @Override
            public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
                ThreadPool.MainThreadHandler.getInstance().post(() -> {
                    if (ioTResponse.getCode() == 200) {
                        dialog.dismiss();
                        showToast(getString(R.string.at_unbind_success));
                    } else {
                        showToast(ioTResponse.getMessage());
                    }
                });
            }
        });
    }

    public void bindAccount(String authCode) {
        JSONObject params = new JSONObject();
        if (null != authCode) {
            params.put("authCode", authCode);
        }
        Map<String, Object> requestMap = params.getInnerMap();
        IoTRequest ioTRequest = new IoTRequestBuilder()
                .setAuthType("iotAuth")
                .setApiVersion("1.0.5")
                .setPath("/account/taobao/bind")
                .setParams(requestMap)
                .setScheme(Scheme.HTTPS)
                .build();
        new IoTAPIClientFactory().getClient().send(ioTRequest, new IoTCallback() {
            @Override
            public void onFailure(IoTRequest ioTRequest, Exception e) {
                Log.e("onResponse: ", e.getMessage());
            }

            @Override
            public void onResponse(IoTRequest ioTRequest, IoTResponse ioTResponse) {
                if (200 != ioTResponse.getCode()) {
                    Log.e("onResponse: ", ioTResponse.getCode() + "---" + ioTResponse.getMessage() + "---" + ioTResponse.getLocalizedMsg());
                    return;
                }
                ThreadPool.MainThreadHandler.getInstance().post(() -> showToast(getString(R.string.at_authorization_success)));
                Log.e("onResponse: ", ioTResponse.getData().toString());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_AUTHORIZED) {
            bindAccount(data.getStringExtra("AuthCode"));
        }
    }
}