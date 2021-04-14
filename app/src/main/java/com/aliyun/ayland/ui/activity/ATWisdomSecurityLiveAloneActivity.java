package com.aliyun.ayland.ui.activity;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.ayland.base.ATBaseActivity;
import com.aliyun.ayland.contract.ATMainContract;
import com.aliyun.ayland.data.ATElderlyAloneCareInfoBean;
import com.aliyun.ayland.data.ATHouseBean;
import com.aliyun.ayland.global.ATGlobalApplication;
import com.aliyun.ayland.global.ATConstants;
import com.aliyun.ayland.interfaces.ATOnPopupItemClickListener;
import com.aliyun.ayland.presenter.ATMainPresenter;
import com.aliyun.ayland.widget.ATSwitchButton;
import com.aliyun.ayland.widget.popup.ATOptions1Popup;
import com.aliyun.ayland.widget.titlebar.ATMyTitleBar;
import com.anthouse.wyzhuoyue.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import org.json.JSONException;

public class ATWisdomSecurityLiveAloneActivity extends ATBaseActivity implements ATMainContract.View {
    private ATMainPresenter mPresenter;
    private ATHouseBean houseBean;
    private ATElderlyAloneCareInfoBean mElderlyAloneCareInfoBean;
    private ATOptions1Popup options1Popup;
    private ATMyTitleBar titleBar;
    private SmartRefreshLayout smartRefreshLayout;
    private TextView tvDayEdit, tvStatement, tvSubscriber, tvDay, tvState, tvRecord;
    private ATSwitchButton switchButton;
    private Button button;
    private RelativeLayout rlRecord;
    private LinearLayout llTop, llBottom;

    @Override
    protected int getLayoutId() {
        return R.layout.at_activity_wisdom_security_live_alone;
    }

    @Override
    protected void findView() {
        titleBar = findViewById(R.id.titlebar);
        smartRefreshLayout = findViewById(R.id.smartRefreshLayout);
        tvDayEdit = findViewById(R.id.tv_day_edit);
        switchButton = findViewById(R.id.switchButton);
        button = findViewById(R.id.button);
        tvStatement = findViewById(R.id.tv_statement);
        tvSubscriber = findViewById(R.id.tv_subscriber);
        tvDay = findViewById(R.id.tv_day);
        tvState = findViewById(R.id.tv_state);
        tvRecord = findViewById(R.id.tv_record);
        llTop = findViewById(R.id.ll_top);
        llBottom = findViewById(R.id.ll_bottom);
        rlRecord = findViewById(R.id.rl_record);
        init();
    }

    @Override
    protected void initPresenter() {
        mPresenter = new ATMainPresenter(this);
        mPresenter.install(this);
    }

    private void findElderlyAloneCareInfo() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("buildingCode", houseBean.getBuildingCode());
        jsonObject.put("villageId", houseBean.getVillageId());
        jsonObject.put("personCode", ATGlobalApplication.getATLoginBean().getPersonCode());
        mPresenter.request(ATConstants.Config.SERVER_URL_FINDELDERLYALONECAREINFO, jsonObject);
    }

    private void addElderlyAloneCare(int status) {
        showBaseProgressDlg();
        mElderlyAloneCareInfoBean = new ATElderlyAloneCareInfoBean();
        mElderlyAloneCareInfoBean.setStatus(status);
        mElderlyAloneCareInfoBean.setCareDays(tvDayEdit.getText().toString());
        mElderlyAloneCareInfoBean.setPropertyCareStatus(switchButton.isChecked() ? 1 : 0);
        mElderlyAloneCareInfoBean.setRecordNum("0");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("buildingCode", houseBean.getBuildingCode());
        jsonObject.put("personCode", ATGlobalApplication.getATLoginBean().getPersonCode());
        jsonObject.put("intelliFocusDayLimit", mElderlyAloneCareInfoBean.getCareDays()); //间隔天数
        jsonObject.put("NeedIntelliFocus", mElderlyAloneCareInfoBean.getPropertyCareStatus()); //是否需要物业关怀
        jsonObject.put("villageId", houseBean.getVillageId());
        jsonObject.put("status", mElderlyAloneCareInfoBean.getStatus()); //是否订阅1是0否
        mPresenter.request(ATConstants.Config.SERVER_URL_ADDELDERLYALONECARE, jsonObject);
    }

    private void init() {
        houseBean = gson.fromJson(ATGlobalApplication.getHouse(), ATHouseBean.class);

        button.setOnClickListener(view -> {
            switch (button.getText().toString()) {
                case "订阅":
                    if (!tvStatement.isSelected())
                        showToast(getString(R.string.at_please_check_agreement));
                    else {
                        addElderlyAloneCare(1);
                    }
                    break;
                case "编辑":
                    button.setText(R.string.at_done);
                    break;
                case "完成":
                    addElderlyAloneCare(1);
                    break;
            }
        });
        tvStatement.setOnClickListener(view -> tvStatement.setSelected(!tvStatement.isSelected()));
        tvStatement.setSelected(true);
        titleBar.setRightClickListener(() -> addElderlyAloneCare(0));
        tvDayEdit.setOnClickListener(v -> options1Popup.showPopupWindow());
        options1Popup = new ATOptions1Popup(this);
        options1Popup.setOnItemClickListener(new ATOnPopupItemClickListener() {
            @Override
            public void onItemClick(int i1, int i2) {

            }

            @Override
            public void onItemClick(String s1, String s2, String s3) {
                tvDayEdit.setText(s1);
            }
        });
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            refreshLayout.finishRefresh(2000);
            findElderlyAloneCareInfo();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        smartRefreshLayout.autoRefresh();
    }

    @Override
    public void requestResult(String result, String url) {
        try {
            org.json.JSONObject jsonResult = new org.json.JSONObject(result);
            if ("200".equals(jsonResult.getString("code"))) {
                switch (url) {
                    case ATConstants.Config.SERVER_URL_FINDELDERLYALONECAREINFO:
                        if (jsonResult.has("data")) {
                            mElderlyAloneCareInfoBean = gson.fromJson(jsonResult.getString("data"), ATElderlyAloneCareInfoBean.class);
                        } else {
                            mElderlyAloneCareInfoBean = new ATElderlyAloneCareInfoBean();
                            mElderlyAloneCareInfoBean.setStatus(0);
                        }
                        llTop.setVisibility(View.VISIBLE);
                        llBottom.setVisibility(View.VISIBLE);
                        if (mElderlyAloneCareInfoBean.getStatus() == 0) {
                            //未订阅
                            titleBar.setRightButtonText("");
                            tvSubscriber.setVisibility(View.GONE);
                            tvDayEdit.setVisibility(View.VISIBLE);
                            tvDayEdit.setText("3");
                            tvDay.setVisibility(View.GONE);
                            tvDay.setText("3");
                            tvState.setVisibility(View.GONE);
                            rlRecord.setVisibility(View.GONE);
                            switchButton.setVisibility(View.VISIBLE);
                            tvStatement.setVisibility(View.VISIBLE);
                            button.setText(getString(R.string.at_subscribe));
                        } else {
                            //已订阅
                            titleBar.setRightButtonText(getString(R.string.at_unsubscribe));
                            tvSubscriber.setVisibility(View.VISIBLE);
                            tvSubscriber.setText(String.format(getString(R.string.at_subscriber_), mElderlyAloneCareInfoBean.getCreatePersonName()));
                            tvDayEdit.setVisibility(View.GONE);
                            tvDayEdit.setText(mElderlyAloneCareInfoBean.getCareDays());
                            tvDay.setVisibility(View.VISIBLE);
                            tvDay.setText(mElderlyAloneCareInfoBean.getCareDays());
                            tvState.setVisibility(View.VISIBLE);
                            if (mElderlyAloneCareInfoBean.getPropertyCareStatus() == 0) {
                                tvState.setTextColor(getResources().getColor(R.color._999999));
                                tvState.setText(R.string.at_not_open);
                            } else {
                                tvState.setTextColor(getResources().getColor(R.color._1478C8));
                                tvState.setText(R.string.at_has_been_open);
                            }
                            rlRecord.setVisibility(View.VISIBLE);
                            if ("0".equals(mElderlyAloneCareInfoBean.getRecordNum())) {
                                tvRecord.setText(getString(R.string.at_the_service_object_has_no_reminder_record));
                                tvRecord.setTextColor(getResources().getColor(R.color._333333));
                            } else {
                                SpannableString s = new SpannableString(String.format(getString(R.string.at_count_from_the_elderly_care_), mElderlyAloneCareInfoBean.getRecordNum()));
                                s.setSpan(new ForegroundColorSpan(Color.parseColor("#FF333333")), 0, 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                s.setSpan(new ForegroundColorSpan(Color.parseColor("#FFFF5656")), 6, 6 + mElderlyAloneCareInfoBean.getRecordNum().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                s.setSpan(new ForegroundColorSpan(Color.parseColor("#FF333333")), 6 + mElderlyAloneCareInfoBean.getRecordNum().length(), s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                tvRecord.setText(s);
                            }
                            tvStatement.setVisibility(View.GONE);
                            switchButton.setVisibility(View.GONE);
                            button.setText(getString(R.string.at_edit));
                        }
                        break;
                    case ATConstants.Config.SERVER_URL_ADDELDERLYALONECARE:
                        if (mElderlyAloneCareInfoBean.getStatus() == 0) {
                            //未订阅
                            titleBar.setRightButtonText("");
                            tvSubscriber.setVisibility(View.GONE);
                            tvDayEdit.setVisibility(View.VISIBLE);
                            tvDayEdit.setText("3");
                            tvDay.setVisibility(View.GONE);
                            tvDay.setText("3");
                            tvState.setVisibility(View.GONE);
                            rlRecord.setVisibility(View.GONE);
                            switchButton.setVisibility(View.VISIBLE);
                            tvStatement.setVisibility(View.VISIBLE);
                            button.setText(getString(R.string.at_subscribe));
                        } else {
                            //已订阅
                            titleBar.setRightButtonText(getString(R.string.at_unsubscribe));
                            tvSubscriber.setVisibility(View.VISIBLE);
                            tvSubscriber.setText(String.format(getString(R.string.at_subscriber_), ATGlobalApplication.getATLoginBean().getNickName()));
                            tvDayEdit.setVisibility(View.GONE);
                            tvDayEdit.setText(mElderlyAloneCareInfoBean.getCareDays());
                            tvDay.setVisibility(View.VISIBLE);
                            tvDay.setText(mElderlyAloneCareInfoBean.getCareDays());
                            tvState.setVisibility(View.VISIBLE);

                            rlRecord.setVisibility(View.VISIBLE);
                            if ("0".equals(mElderlyAloneCareInfoBean.getRecordNum())) {
                                tvRecord.setText(getString(R.string.at_the_service_object_has_no_reminder_record));
                                tvRecord.setTextColor(getResources().getColor(R.color._333333));
                            } else {
                                SpannableString s = new SpannableString(String.format(getString(R.string.at_count_from_the_elderly_care_), mElderlyAloneCareInfoBean.getRecordNum()));
                                s.setSpan(new ForegroundColorSpan(Color.parseColor("#FF333333")), 0, 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                s.setSpan(new ForegroundColorSpan(Color.parseColor("#FFFF5656")), 6, 6 + mElderlyAloneCareInfoBean.getRecordNum().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                s.setSpan(new ForegroundColorSpan(Color.parseColor("#FF333333")), 6 + mElderlyAloneCareInfoBean.getRecordNum().length(), s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                                tvRecord.setText(s);
                            }
                            tvStatement.setVisibility(View.GONE);
                            switchButton.setVisibility(View.GONE);
                            button.setText(getString(R.string.at_edit));
                        }
                        break;
                }
            } else {
                showToast(jsonResult.getString("message"));
            }
            smartRefreshLayout.finishRefresh();
            closeBaseProgressDlg();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}