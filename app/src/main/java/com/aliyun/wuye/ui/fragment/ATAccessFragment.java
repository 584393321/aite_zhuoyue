package com.aliyun.wuye.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.ayland.base.ATBaseFragment;
import com.aliyun.ayland.base.autolayout.util.ATAutoUtils;
import com.aliyun.ayland.contract.ATMainContract;
import com.aliyun.ayland.global.ATConstants;
import com.aliyun.ayland.global.ATGlobalApplication;
import com.aliyun.ayland.presenter.ATMainPresenter;
import com.aliyun.ayland.utils.ATQRCodeUtil;
import com.anthouse.wyzhuoyue.R;

import org.json.JSONException;

import java.io.File;
import java.util.Objects;

public class ATAccessFragment extends ATBaseFragment implements ATMainContract.View {
    private static final int MSG_REFRESH_QRCODE = 0x1001;
    private ImageView imgCode;
    private TextView tvRefresh;
    private ATMainPresenter mPresenter;
    private String baseString;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_REFRESH_QRCODE:
                    createQrcode();
                    handler.sendEmptyMessageDelayed(MSG_REFRESH_QRCODE, 180000);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.at_fragment_access;
    }

    @Override
    protected void findView(View view) {
        imgCode = view.findViewById(R.id.img_code);
        tvRefresh = view.findViewById(R.id.tv_refresh);
        init();
    }

    private void init() {
        baseString = "mnt/sdcard/" + Objects.requireNonNull(getActivity()).getApplicationInfo().packageName + "/";
        tvRefresh.setOnClickListener(v -> {
            handler.removeMessages(MSG_REFRESH_QRCODE);
            handler.sendEmptyMessage(MSG_REFRESH_QRCODE);
        });
    }

    @Override
    protected void initPresenter() {
        mPresenter = new ATMainPresenter(this);
        mPresenter.install(getActivity());
    }

    public void getQrcode() {
        showBaseProgressDlg();
        handler.removeMessages(MSG_REFRESH_QRCODE);
        handler.sendEmptyMessage(MSG_REFRESH_QRCODE);
    }

    private void createQrcode() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("personCode", ATGlobalApplication.getATLoginBean().getPersonCode());
        mPresenter.request(ATConstants.Config.SERVER_URL_CREATEQRCODE, jsonObject);
    }

    private String getPathString() {
        String nowFilePath = baseString + System.currentTimeMillis() + ".jpg";
        File file = new File(baseString);
        if (!file.exists()) {
            file.mkdir();
        }
        return nowFilePath;
    }

    @Override
    public void onStop() {
        super.onStop();
        handler.removeMessages(MSG_REFRESH_QRCODE);
    }

    @Override
    public void requestResult(String result, String url) {
        try {
            org.json.JSONObject jsonResult = new org.json.JSONObject(result);
            if ("200".equals(jsonResult.getString("code"))) {
                switch (url) {
                    case ATConstants.Config.SERVER_URL_CREATEQRCODE:
                        String qrcode = jsonResult.has("qrcode") ? jsonResult.getString("qrcode") : "";
                        if (!TextUtils.isEmpty(qrcode)) {
                            imgCode.setImageBitmap(ATQRCodeUtil.createQRImage(qrcode, ATAutoUtils.getPercentWidthSize(701)
                                    , ATAutoUtils.getPercentHeightSize(701), getPathString(), false, null));
                        }
                        break;
                }
            } else {
                showToast(jsonResult.getString("message"));
            }
            closeBaseProgressDlg();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
