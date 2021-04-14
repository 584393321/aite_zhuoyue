package com.aliyun.ayland.ui.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
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
import java.util.ArrayList;
import java.util.List;

public class ATVisitorAppointResultActivity extends ATBaseActivity implements ATMainContract.View {
    private ATMainPresenter mPresenter;
    private ATVisitorResultBean mVisitorReservateBean;
    private String baseString, qrcodeUrl;
    private ATMyTitleBar titleBar;
    private TextView tvHttp, tvOffLine, tvName, tvVisiteRoom, tvVisiteTime, tvLeaveTime, tvPlateNumber, tvPhone;
    private RelativeLayout rlOffLine;
    private ImageView imgOffLine, img;

    @Override
    protected int getLayoutId() {
        return R.layout.at_activity_visitor_appoint_result;
    }

    @Override
    protected void findView() {
        titleBar = findViewById(R.id.titlebar);
        tvHttp = findViewById(R.id.tv_http);
        rlOffLine = findViewById(R.id.rl_off_line);
        imgOffLine = findViewById(R.id.img_off_line);
        img = findViewById(R.id.img);
        tvOffLine = findViewById(R.id.tv_off_line);
        tvName = findViewById(R.id.tv_name);
        tvVisiteRoom = findViewById(R.id.tv_visite_room);
        tvVisiteTime = findViewById(R.id.tv_visite_time);
        tvLeaveTime = findViewById(R.id.tv_leave_time);
        tvPlateNumber = findViewById(R.id.tv_plate_number);
        tvPhone = findViewById(R.id.tv_phone);
        init();
    }

    @Override
    protected void initPresenter() {
        mPresenter = new ATMainPresenter(this);
        mPresenter.install(this);
        getVisitorReservation();
    }

    private void getVisitorReservationQRCode() {
        showBaseProgressDlg();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("visitorReservationId", getIntent().getStringExtra("id"));
        mPresenter.request(ATConstants.Config.SERVER_URL_GETVISITORRESERVATIONQRCODE, jsonObject);
    }

    private void getVisitorReservation() {
        showBaseProgressDlg();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", getIntent().getStringExtra("id"));
        mPresenter.request(ATConstants.Config.SERVER_URL_GETVISITORRESERVATION, jsonObject);
    }

    private void init() {
        baseString = "mnt/sdcard/" + getApplicationInfo().packageName + "/";
        titleBar.setTitle(getString(R.string.at_appoint_result));
        titleBar.setRightBtTextImage(R.drawable.icon_s_fkyy_fenxiang);
        titleBar.setRightButtonEnable(true);
        titleBar.setRightClickListener(this::shareView);
        tvHttp.setOnLongClickListener(v -> {
            if (mVisitorReservateBean == null)
                return false;
            //获取剪贴板管理器：
            ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData mClipData = ClipData.newPlainText("Label", mVisitorReservateBean.getFaceUrl());
            cm.setPrimaryClip(mClipData);
            tvHttp.setTextColor(getResources().getColor(R.color._999999));
            showToast("复制成功");
            return false;
        });
        rlOffLine.setOnClickListener(view -> {
            if (TextUtils.isEmpty(qrcodeUrl))
                getVisitorReservationQRCode();
        });
    }

    private void shareView() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, "点击链接查看访客预约结果："+mVisitorReservateBean.getFaceUrl());
        intent.setType("text/*");
        //        intent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
//        intent.setType("image/*");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        PackageManager pm = getPackageManager();
        List<ResolveInfo> resInfo = pm.queryIntentActivities(intent, 0);
        if (resInfo.isEmpty())
            return;

        List<Intent> targetIntents = new ArrayList<>();
        for (ResolveInfo resolveInfo : resInfo) {
            ActivityInfo activityInfo = resolveInfo.activityInfo;
            if (activityInfo.packageName.contains("com.tencent.mm") || activityInfo.packageName.contains("com.tencent.mobileqq")
                    || activityInfo.packageName.contains("com.android.mms")) {
                //过滤掉qq收藏
                if (resolveInfo.loadLabel(pm).toString().contains("QQ收藏") || resolveInfo.loadLabel(pm).toString().contains("微信收藏")
                        || resolveInfo.loadLabel(pm).toString().contains("朋友圈") || resolveInfo.loadLabel(pm).toString().contains("我的电脑")
                        || resolveInfo.loadLabel(pm).toString().contains("面对面快传")) {
                    continue;
                }
                Intent target = new Intent();
                target.setAction(Intent.ACTION_SEND);
                target.setComponent(new ComponentName(activityInfo.packageName, activityInfo.name));

                target.putExtra(Intent.EXTRA_TEXT, "点击链接查看访客预约结果："+mVisitorReservateBean.getFaceUrl());
                target.setType("text/*");
//                target.putExtra(Intent.EXTRA_STREAM, bitmapUri);
//                target.setType("image/*");
                targetIntents.add(new LabeledIntent(target, activityInfo.packageName, resolveInfo.loadLabel(pm), resolveInfo.icon));
            }
        }
        if (targetIntents.size() <= 0)
            return;
        Intent chooser = Intent.createChooser(targetIntents.remove(targetIntents.size() - 1), "");
        if (chooser == null) return;
        LabeledIntent[] labeledIntents = targetIntents.toArray(new LabeledIntent[targetIntents.size()]);
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, labeledIntents);
        startActivity(chooser);
    }

    private String getPathString() {
        String nowFilePath = baseString + System.currentTimeMillis() + ".jpg";
        File file = new File(baseString);
        if (!file.exists())
            file.mkdir();
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
                            img.setImageBitmap(ATQRCodeUtil.createQRImage(mVisitorReservateBean.getQrcodeUrl(), ATAutoUtils.getPercentWidthSize(701)
                                    , ATAutoUtils.getPercentHeightSize(701), getPathString(), false, null));
                        }
                        break;
                    case ATConstants.Config.SERVER_URL_GETVISITORRESERVATION:
                        mVisitorReservateBean = gson.fromJson(jsonResult.getString("data"), new TypeToken<ATVisitorResultBean>() {
                        }.getType());
                        qrcodeUrl = mVisitorReservateBean.getQrcodeUrl();
                        if (TextUtils.isEmpty(qrcodeUrl)) {
                            imgOffLine.setVisibility(View.VISIBLE);
                            tvOffLine.setVisibility(View.VISIBLE);
                        } else {
                            imgOffLine.setVisibility(View.GONE);
                            tvOffLine.setVisibility(View.GONE);
                            img.setImageBitmap(ATQRCodeUtil.createQRImage(mVisitorReservateBean.getQrcodeUrl(), ATAutoUtils.getPercentWidthSize(701)
                                    , ATAutoUtils.getPercentHeightSize(701), getPathString(), false, null));
                        }
                        tvName.setText(mVisitorReservateBean.getVisitorName());
                        tvVisiteRoom.setText(mVisitorReservateBean.getVisitorHouse());
                        tvVisiteTime.setText(String.format(getString(R.string.at_visit_time_), mVisitorReservateBean.getReservationStartTime()));
                        tvLeaveTime.setText(String.format(getString(R.string.at_leave_time_), mVisitorReservateBean.getReservationEndTime()));
                        tvPlateNumber.setText(String.format(getString(R.string.at_plate_number_),
                                TextUtils.isEmpty(mVisitorReservateBean.getCarNumber()) ? "无" :mVisitorReservateBean.getCarNumber()));
                        tvPhone.setText(String.format(getString(R.string.at_phone_),
                                TextUtils.isEmpty(mVisitorReservateBean.getVisitorTel()) ? "无" : mVisitorReservateBean.getVisitorTel()));
                        if (mVisitorReservateBean.getHasCar() != -1) {
                            tvPlateNumber.setVisibility(View.VISIBLE);
//                                tvPlateNumber.setText(mJsonObject.getString("carNumber"));
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