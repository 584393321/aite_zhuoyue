package com.aliyun.ayland.ui.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.ayland.base.ATBaseActivity;
import com.aliyun.ayland.base.autolayout.util.ATAutoUtils;
import com.aliyun.ayland.contract.ATMainContract;
import com.aliyun.ayland.data.ATApplicationBean;
import com.aliyun.ayland.data.ATHouseBean;
import com.aliyun.ayland.global.ATGlobalApplication;
import com.aliyun.ayland.global.ATConstants;
import com.aliyun.ayland.presenter.ATMainPresenter;
import com.aliyun.ayland.ui.adapter.ATWisdomPublicAreaRVAdapter;
import com.aliyun.ayland.utils.ATQRCodeUtil;
import com.aliyun.ayland.widget.titlebar.ATMyTitleBar;
import com.anthouse.wyzhuoyue.R;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import org.json.JSONException;

import java.io.File;
import java.util.List;
import java.util.Objects;

public class ATWisdomPublicAreaActivity extends ATBaseActivity implements ATMainContract.View {
    private static final int MSG_REFRESH_QRCODE = 1001;
    private ATMainPresenter mPresenter;
    private ATWisdomPublicAreaRVAdapter mWisdomPublicAreaRVAdapter;
    private Dialog mDialogQRCode;
    private ImageView mImgQR;
    private String qrcode, baseString;
    private ATHouseBean houseBean;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_REFRESH_QRCODE:
                    createQrcode();
                    mHandler.sendEmptyMessageDelayed(MSG_REFRESH_QRCODE, 180000);
                    break;
                default:
                    break;
            }
        }
    };
    private ATMyTitleBar titleBar;
    private RecyclerView recyclerView;
    private SmartRefreshLayout smartRefreshLayout;

    @Override
    protected int getLayoutId() {
        return R.layout.at_activity_recycleview_smr1;
    }

    @Override
    protected void findView() {
        titleBar = findViewById(R.id.titlebar);
        recyclerView = findViewById(R.id.recyclerView);
        smartRefreshLayout = findViewById(R.id.smartRefreshLayout);

        init();
    }

    @Override
    protected void initPresenter() {
        mPresenter = new ATMainPresenter(this);
        mPresenter.install(this);

        findAllApplication();
    }

    private void createQrcode() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("personCode", ATGlobalApplication.getATLoginBean().getPersonCode());
        mPresenter.request(ATConstants.Config.SERVER_URL_CREATEQRCODE, jsonObject);
    }

    private void findAllApplication() {
        showBaseProgressDlg();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("openId", ATGlobalApplication.getHid());
        jsonObject.put("villageCode", houseBean.getVillageId());
        jsonObject.put("typeCode", 233);
        mPresenter.request(ATConstants.Config.SERVER_URL_FINDALLAPPLICATION, jsonObject);
    }

    private void init() {
        houseBean = gson.fromJson(ATGlobalApplication.getHouse(), ATHouseBean.class);

        titleBar.setTitle(getString(R.string.at_wisdom_public_area));
        baseString = "mnt/sdcard/" + getApplicationInfo().packageName + "/";

        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        mWisdomPublicAreaRVAdapter = new ATWisdomPublicAreaRVAdapter(this);
        recyclerView.setAdapter(mWisdomPublicAreaRVAdapter);

        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            refreshLayout.finishRefresh(2000);
            findAllApplication();
        });

        mWisdomPublicAreaRVAdapter.setOnItemClickListener((view, o, position) -> {
            switch ((String) o) {
                case "app_access_record":
                    //通行记录
                    startActivity(new Intent(this, ATVehiclePassageActivity.class));
//                    startActivity(new Intent(this, ATAccessRecordActivity.class));
                    break;
                case "app_face_access":
                    //人脸通行
                    startActivity(new Intent(this, ATUserFaceActivity.class));
                    break;
                case "app_qrcode_access":
                    //二维码通行
                    showBaseProgressDlg();
                    mHandler.removeMessages(MSG_REFRESH_QRCODE);
                    mHandler.sendEmptyMessage(MSG_REFRESH_QRCODE);
                    break;
                case "app_visitor_appointment":
                    //访客预约
                    startActivity(new Intent(this, ATVisitorRecordActivity.class));
                    break;
                case "app_video_intercom":
                    //可视对讲
                    startActivity(new Intent(this, ATVisualIntercomActivity.class));
                    break;
                case "app_public_security":
                    //公区监控
                    startActivity(new Intent(this, ATFamilySecurityActivity.class));
                    break;
                case "app_my_car":
                    //我家的车
                    startActivity(new Intent(this, ATVehiclePassageActivity.class));
                    break;
                case "app_community_invite":
                    //社区邀访
                    startActivity(new Intent(this, ATVisiteRecordActivity.class));
                    break;
                case "app_alarm_notification":
                    //报警通知
                    this.startActivity(new Intent(this, ATWarningNoticeActivity.class));
                    break;
                case "app_space_appointment":
                    //空间预约
                    startActivity(new Intent(this, ATShareSpaceActivity.class));
                    break;
            }
        });
        initQRCodeDialog();
    }

    @SuppressLint("InflateParams")
    private void initQRCodeDialog() {
        mDialogQRCode = new Dialog(Objects.requireNonNull(this), R.style.nameDialog);
        View view = LayoutInflater.from(this).inflate(R.layout.at_dialog_qrcode, null, false);
        mImgQR = view.findViewById(R.id.img);
        view.findViewById(R.id.tv_refresh).setOnClickListener(v -> {
            mHandler.removeMessages(MSG_REFRESH_QRCODE);
            mHandler.sendEmptyMessage(MSG_REFRESH_QRCODE);
        });
        view.findViewById(R.id.img_close).setOnClickListener(v -> mDialogQRCode.dismiss());
        mDialogQRCode.setContentView(view);
        mDialogQRCode.setOnDismissListener(dialogInterface -> mHandler.removeMessages(MSG_REFRESH_QRCODE));
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
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeMessages(MSG_REFRESH_QRCODE);
    }

    @Override
    public void requestResult(String result, String url) {
        try {
            org.json.JSONObject jsonResult = new org.json.JSONObject(result);
            if ("200".equals(jsonResult.getString("code"))) {
                switch (url) {
                    case ATConstants.Config.SERVER_URL_CREATEQRCODE:
                        qrcode = jsonResult.has("qrcode") ? jsonResult.getString("qrcode") : "";
                        if (!TextUtils.isEmpty(qrcode)) {
                            mImgQR.setImageBitmap(ATQRCodeUtil.createQRImage(qrcode, ATAutoUtils.getPercentWidthSize(701)
                                    , ATAutoUtils.getPercentHeightSize(701), getPathString(), false, null));
                            if (!mDialogQRCode.isShowing())
                                mDialogQRCode.show();
                        }
                        break;
                    case ATConstants.Config.SERVER_URL_FINDALLAPPLICATION:
                        List<ATApplicationBean> applicationList = gson.fromJson(jsonResult.getString("data"), new TypeToken<List<ATApplicationBean>>() {
                        }.getType());
                        mWisdomPublicAreaRVAdapter.setLists(applicationList);
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