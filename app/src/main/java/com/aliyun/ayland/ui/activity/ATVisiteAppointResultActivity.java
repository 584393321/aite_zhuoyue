package com.aliyun.ayland.ui.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.ayland.base.ATBaseActivity;
import com.aliyun.ayland.base.autolayout.util.ATAutoUtils;
import com.aliyun.ayland.contract.ATMainContract;
import com.aliyun.ayland.data.ATVisitorResultBean;
import com.aliyun.ayland.global.ATConstants;
import com.aliyun.ayland.presenter.ATMainPresenter;
import com.aliyun.ayland.utils.ATQRCodeUtil;
import com.aliyun.ayland.widget.titlebar.ATMyTitleBar;
import com.anthouse.wyzhuoyue.R;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;

import java.io.File;

public class ATVisiteAppointResultActivity extends ATBaseActivity implements ATMainContract.View {
    private ATMainPresenter mPresenter;
    private ATVisitorResultBean mVisitorReservateBean;
    private String baseString, qrcodeUrl;
    private ATMyTitleBar titleBar;
    private TextView tvOffLine, tvName, tvVisitAddress, tvVisitTime, tvLeaveTime, tvPlateNumber, tvPhone;
    private RelativeLayout rlOffLine;
    private LinearLayout llPlateNumber;
    private ImageView imgOffLine, img;

    @Override
    protected int getLayoutId() {
        return R.layout.at_activity_visited_appoint_result;
    }

    @Override
    protected void findView() {
        titleBar = findViewById(R.id.titlebar);
        rlOffLine = findViewById(R.id.rl_off_line);
        imgOffLine = findViewById(R.id.img_off_line);
        img = findViewById(R.id.img);
        tvOffLine = findViewById(R.id.tv_off_line);
        tvName = findViewById(R.id.tv_name);
        tvVisitAddress = findViewById(R.id.tv_visit_address);
        tvVisitTime = findViewById(R.id.tv_visit_time);
        tvLeaveTime = findViewById(R.id.tv_leave_time);
        tvPlateNumber = findViewById(R.id.tv_plate_number);
        llPlateNumber = findViewById(R.id.ll_plate_number);
        tvPhone = findViewById(R.id.tv_phone);

        init();
    }

    @Override
    protected void initPresenter() {
        mPresenter = new ATMainPresenter(this);
        mPresenter.install(this);
        getVisitedDetail();
    }

    private void getVisitorReservationQRCode() {
        showBaseProgressDlg();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("visitorReservationId", getIntent().getStringExtra("id"));
        mPresenter.request(ATConstants.Config.SERVER_URL_GETVISITORRESERVATIONQRCODE, jsonObject);
    }

    private void getVisitedDetail() {
        showBaseProgressDlg();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", getIntent().getStringExtra("id"));
        mPresenter.request(ATConstants.Config.SERVER_URL_GETVISITEDDETAIL, jsonObject);
    }

    private void init() {
        baseString = "mnt/sdcard/" + getApplicationInfo().packageName + "/";
        rlOffLine.setOnClickListener(view -> {
            if (TextUtils.isEmpty(qrcodeUrl)) {
                getVisitorReservationQRCode();
            }
        });
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
    public void requestResult(String result, String url) {
        try {
            org.json.JSONObject jsonResult = new org.json.JSONObject(result);
            if ("200".equals(jsonResult.getString("code"))) {
                switch (url) {
                    case ATConstants.Config.SERVER_URL_GETVISITORRESERVATIONQRCODE:
                        qrcodeUrl = jsonResult.has("data") ? jsonResult.getString("data") : "";
                        if (TextUtils.isEmpty(qrcodeUrl)) {
                            imgOffLine.setVisibility(View.VISIBLE);
                            tvOffLine.setVisibility(View.VISIBLE);
                            showToast("二维码走丢了，请稍后重试");
                        } else {
                            imgOffLine.setVisibility(View.GONE);
                            tvOffLine.setVisibility(View.GONE);
                            img.setImageBitmap(ATQRCodeUtil.createQRImage(mVisitorReservateBean.getQrcodeUrl(), ATAutoUtils.getPercentWidthSize(765)
                                    , ATAutoUtils.getPercentWidthSize(765), getPathString(), false, null));
                        }
                        break;
                    case ATConstants.Config.SERVER_URL_GETVISITEDDETAIL:
                        mVisitorReservateBean = gson.fromJson(jsonResult.getString("data"), new TypeToken<ATVisitorResultBean>() {
                        }.getType());
                        qrcodeUrl = mVisitorReservateBean.getQrcodeUrl();
                        if (TextUtils.isEmpty(qrcodeUrl)) {
                            imgOffLine.setVisibility(View.VISIBLE);
                            tvOffLine.setVisibility(View.VISIBLE);
                        } else {
                            imgOffLine.setVisibility(View.GONE);
                            tvOffLine.setVisibility(View.GONE);
                            img.setImageBitmap(ATQRCodeUtil.createQRImage(mVisitorReservateBean.getQrcodeUrl(), ATAutoUtils.getPercentWidthSize(765)
                                    , ATAutoUtils.getPercentWidthSize(765), getPathString(), false, null));
                        }
                        tvName.setText(String.format(getString(R.string.at_visited_person_), mVisitorReservateBean.getInviterName()));
                        if (TextUtils.isEmpty(mVisitorReservateBean.getAddress())) {
                            tvVisitAddress.setVisibility(View.GONE);
                        } else {
                            tvVisitAddress.setVisibility(View.VISIBLE);
                            tvVisitAddress.setText(mVisitorReservateBean.getAddress());
                        }
                        tvVisitTime.setText(mVisitorReservateBean.getReservationStartTime());
                        tvLeaveTime.setText(mVisitorReservateBean.getReservationEndTime());
                        if (TextUtils.isEmpty(mVisitorReservateBean.getCarNumber())) {
                            llPlateNumber.setVisibility(View.GONE);
                        } else {
                            llPlateNumber.setVisibility(View.VISIBLE);
                            tvPlateNumber.setText(mVisitorReservateBean.getCarNumber());
                        }
                        tvPhone.setText(TextUtils.isEmpty(mVisitorReservateBean.getInviterPhone()) ? "无" : mVisitorReservateBean.getInviterPhone());
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