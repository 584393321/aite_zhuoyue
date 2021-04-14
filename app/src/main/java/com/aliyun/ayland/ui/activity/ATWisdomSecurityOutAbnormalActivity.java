package com.aliyun.ayland.ui.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.ayland.base.ATBaseActivity;
import com.aliyun.ayland.contract.ATMainContract;
import com.aliyun.ayland.data.ATFindFamilyMemberForOutAbnormalBean;
import com.aliyun.ayland.data.ATFindFamilyMemberForOutAbnormalBean.MembersBean;
import com.aliyun.ayland.data.ATFindFamilyMemberForOutAbnormalBean.MembersBean.CycleListBean;
import com.aliyun.ayland.data.ATHouseBean;
import com.aliyun.ayland.global.ATGlobalApplication;
import com.aliyun.ayland.global.ATConstants;
import com.aliyun.ayland.presenter.ATMainPresenter;
import com.aliyun.ayland.ui.adapter.ATWisdomSecurityOutAbnormalRVAdapter;
import com.aliyun.ayland.widget.ATSwitchButton;
import com.aliyun.ayland.widget.titlebar.ATMyTitleBar;
import com.anthouse.wyzhuoyue.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class ATWisdomSecurityOutAbnormalActivity extends ATBaseActivity implements ATMainContract.View, View.OnClickListener {
    public static final int REQUEST_CODE_TIMING = 0x1001;
    private ATMainPresenter mPresenter;
    private ATHouseBean houseBean;
    private int appId, status, button_status;
    private RecyclerView recyclerView;
    private Button button;
    private TextView tvStatement, tvSubscriber, tvState, tvCount;
    private ATSwitchButton switchButton;
    private SmartRefreshLayout smartRefreshLayout;
    private RelativeLayout rlCountFromTheCommunity;
    private ATMyTitleBar titleBar;
    private LinearLayout llHas, llNone;
    private ATWisdomSecurityOutAbnormalRVAdapter mWisdomSecurityOutAbnormalRVAdapter;
    private List<MembersBean> mFamilyMenberList = new ArrayList<>();
    private Dialog dialog;
    private ATFindFamilyMemberForOutAbnormalBean mFindFamilyMemberForOutAbnormalBean;

    @Override
    protected int getLayoutId() {
        return R.layout.at_activity_wisdom_security_out_unusual;
    }

    @Override
    protected void findView() {
        titleBar = findViewById(R.id.titlebar);
        recyclerView = findViewById(R.id.recyclerView);
        smartRefreshLayout = findViewById(R.id.smartRefreshLayout);
        button = findViewById(R.id.button);
        tvStatement = findViewById(R.id.tv_statement);
        switchButton = findViewById(R.id.switchButton);
        rlCountFromTheCommunity = findViewById(R.id.rl_count_from_the_community);
        tvSubscriber = findViewById(R.id.tv_subscriber);
        tvState = findViewById(R.id.tv_state);
        tvCount = findViewById(R.id.tv_count);
        llHas = findViewById(R.id.ll_has);
        llNone = findViewById(R.id.ll_none);
        findViewById(R.id.tv_family_manage).setOnClickListener(this);
        findViewById(R.id.tv_to_family_manage).setOnClickListener(this);
        button.setOnClickListener(this);
        init();
    }

    @Override
    protected void initPresenter() {
        mPresenter = new ATMainPresenter(this);
        mPresenter.install(this);

        smartRefreshLayout.autoRefresh();
    }

    private void addHabitAbnormal() {
        showBaseProgressDlg();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("appId", appId);
        jsonObject.put("buildingCode", houseBean.getBuildingCode());
        jsonObject.put("operator", Integer.parseInt(ATGlobalApplication.getATLoginBean().getPersonCode()));
        JSONArray personCodeList = new JSONArray();
        JSONArray timeList = new JSONArray();
        JSONObject object = new JSONObject();
        for (Object o : mWisdomSecurityOutAbnormalRVAdapter.getLists()) {
            if (o instanceof MembersBean) {
                object = new JSONObject();
                object.put("personCode", ((MembersBean) o).getPersonCode());
                object.put("status", ((MembersBean) o).getStatus());
                timeList = new JSONArray();
            }
            if (o instanceof CycleListBean) {
                JSONObject object2 = new JSONObject();
                object2.put("beginTime", ((CycleListBean) o).getBeginTime());
                object2.put("endTime", ((CycleListBean) o).getEndTime());
                object2.put("weekDay", ((CycleListBean) o).getWeekDay());
                timeList.add(object2);
                if (((CycleListBean) o).getWeekDay() == 6) {
                    if (object.getIntValue("status") == 0 && timeList.size() == 0) {
                        showToast("请为订阅人添加出行时间");
                        return;
                    } else {
                        object.put("timeList", timeList);
                        personCodeList.add(object);
                    }
                }
            }
        }
        jsonObject.put("personCodeList", personCodeList);
        jsonObject.put("propertyStatus", switchButton.isChecked() ? 1 : 0);
        jsonObject.put("villageId", houseBean.getVillageId());
        mPresenter.request(ATConstants.Config.SERVER_URL_ADDHABITABNORMAL, jsonObject);
    }

    private void cancelHabitabonrmality() {
        showBaseProgressDlg();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("buildingCode", houseBean.getBuildingCode());
        jsonObject.put("villageId", houseBean.getVillageId());
        jsonObject.put("appId", appId);
        jsonObject.put("operator", ATGlobalApplication.getATLoginBean().getPersonCode());
        mPresenter.request(ATConstants.Config.SERVER_URL_CANCELHABITABONRMALITY, jsonObject);
    }

    private void findFamilyMemberForHabitAbnormal() {
        showBaseProgressDlg();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("buildingCode", houseBean.getBuildingCode());
        jsonObject.put("villageId", houseBean.getVillageId());
        jsonObject.put("personCode", Integer.parseInt(ATGlobalApplication.getATLoginBean().getPersonCode()));
        jsonObject.put("appId", appId);
        mPresenter.request(ATConstants.Config.SERVER_URL_FINDFAMILYMEMBERFORHABITABNORMAL, jsonObject);
    }

    private void init() {
        appId = getIntent().getIntExtra("appId", 0);
        status = getIntent().getIntExtra("status", 0);
        houseBean = gson.fromJson(ATGlobalApplication.getHouse(), ATHouseBean.class);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mWisdomSecurityOutAbnormalRVAdapter = new ATWisdomSecurityOutAbnormalRVAdapter(this);
        recyclerView.setAdapter(mWisdomSecurityOutAbnormalRVAdapter);
        mWisdomSecurityOutAbnormalRVAdapter.setOnItemClickListener((view, o, position) -> mFamilyMenberList.get(position).setStatus((int) o));

        tvStatement.setSelected(true);
        tvStatement.setOnClickListener(view -> {
            tvStatement.setSelected(!tvStatement.isSelected());
        });
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            refreshLayout.finishRefresh(2000);
            findFamilyMemberForHabitAbnormal();
        });
        initDialog();

        if (status == 0) {
            //未订阅
            titleBar.setRightButtonText("");
            switchButton.setVisibility(View.VISIBLE);
            rlCountFromTheCommunity.setVisibility(View.GONE);
            button.setText(R.string.at_subscribe);
            button_status = 0;
        } else {
            //已订阅
            titleBar.setRightButtonText(getString(R.string.at_unsubscribe));
            titleBar.setRightClickListener(this::cancelHabitabonrmality);
            switchButton.setVisibility(View.GONE);
            rlCountFromTheCommunity.setVisibility(View.VISIBLE);
            button.setText(R.string.at_edit);
            button_status = 1;
        }
    }

    @SuppressLint("InflateParams")
    private void initDialog() {
        dialog = new Dialog(this, R.style.nameDialog);
        View view = LayoutInflater.from(this).inflate(R.layout.at_dialog_855px546px_sure_or_no, null, false);
        ((TextView) view.findViewById(R.id.tv_title)).setText(getString(R.string.at_no_person_subscribed));
        view.findViewById(R.id.tv_cancel).setOnClickListener(v -> dialog.dismiss());
        view.findViewById(R.id.tv_sure).setOnClickListener(v -> cancelHabitabonrmality());
        dialog.setContentView(view);
    }

    @Override
    public void onClick(View view) {
        if (R.id.button == view.getId()) {
            switch (button_status) {
                case 0:
                case 2:
                    if (!tvStatement.isSelected()) {
                        showToast(getString(R.string.at_please_check_agreement));
                        return;
                    }
                    //订阅
                    boolean had_one_subcribed = false;
                    for (MembersBean findFamilyMemberForOutAloneBean : mFamilyMenberList) {
                        if (findFamilyMemberForOutAloneBean.getStatus() == 1) {
                            had_one_subcribed = true;
                            break;
                        }
                    }
                    if (had_one_subcribed)
                        addHabitAbnormal();
                    else {
                        showToast(getString(R.string.at_subscribe_at_lease_one_person));
                    }
                    break;
                case 1:
                    //编辑
                    button_status = 2;
                    button.setText(R.string.at_done);
                    titleBar.setRightButtonText(getString(R.string.at_cancel));
                    titleBar.setRightClickListener(() -> {
                        //编辑
                        tvSubscriber.setText(String.format(getString(R.string.at_subscriber_), mFindFamilyMemberForOutAbnormalBean.getOperatorName()));
                        rlCountFromTheCommunity.setVisibility(View.VISIBLE);
                        button.setText(R.string.at_edit);
                        titleBar.setRightButtonText(getString(R.string.at_unsubscribe));
                        titleBar.setRightClickListener(this::cancelHabitabonrmality);
                        button_status = 1;
                        tvState.setVisibility(View.VISIBLE);
                        tvState.setText(mFindFamilyMemberForOutAbnormalBean.getPropertyStatus() == 0 ? R.string.at_not_open : R.string.at_has_been_open);
                        switchButton.setVisibility(View.GONE);
                        tvCount.setText(String.valueOf(mFindFamilyMemberForOutAbnormalBean.getSize()));
                    });
                    tvSubscriber.setText("");
                    rlCountFromTheCommunity.setVisibility(View.GONE);
                    tvState.setVisibility(View.GONE);
                    switchButton.setVisibility(View.VISIBLE);
                    switchButton.setChecked(mFindFamilyMemberForOutAbnormalBean.getPropertyStatus() == 1);
                    mWisdomSecurityOutAbnormalRVAdapter.setButton_status(2);
                    break;
            }
        } else if (R.id.tv_family_manage == view.getId())
            startActivity(new Intent(this, ATFamilyManageActivity.class));
        else if (R.id.tv_to_family_manage == view.getId())
            startActivity(new Intent(this, ATFamilyManageActivity.class));

    }

    @Override
    public void requestResult(String result, String url) {
        try {
            org.json.JSONObject jsonResult = new org.json.JSONObject(result);
            if ("200".equals(jsonResult.getString("code"))) {
                switch (url) {
                    case ATConstants.Config.SERVER_URL_ADDHABITABNORMAL:
                        showToast(getString(R.string.at_subscribe_success));
                        status = 1;
                        button_status = 1;
                        smartRefreshLayout.autoRefresh();
                        break;
                    case ATConstants.Config.SERVER_URL_CANCELHABITABONRMALITY:
                        showToast(getString(R.string.at_unsubscribe_success));
                        status = 0;
                        button_status = 0;
                        dialog.dismiss();
                        smartRefreshLayout.autoRefresh();
                        break;
                    case ATConstants.Config.SERVER_URL_FINDFAMILYMEMBERFORHABITABNORMAL:
                        mFindFamilyMemberForOutAbnormalBean = gson.fromJson(jsonResult.toString(), ATFindFamilyMemberForOutAbnormalBean.class);
                        List<MembersBean> familyMenberList = mFindFamilyMemberForOutAbnormalBean.getMembers();
                        mFamilyMenberList.clear();
                        mFamilyMenberList.addAll(familyMenberList);
                        if (button_status == 0) {
                            //未订阅
                            rlCountFromTheCommunity.setVisibility(View.GONE);
                            button.setText(R.string.at_subscribe);
                            titleBar.setRightButtonText("");
                            tvSubscriber.setText("");
                            tvState.setVisibility(View.GONE);
                            tvStatement.setVisibility(View.VISIBLE);
                            switchButton.setVisibility(View.VISIBLE);
                        } else {
                            //编辑
                            tvSubscriber.setText(String.format(getString(R.string.at_subscriber_), jsonResult.getString("operatorName")));
                            rlCountFromTheCommunity.setVisibility(View.VISIBLE);
                            button.setText(R.string.at_edit);
                            titleBar.setRightButtonText(getString(R.string.at_unsubscribe));
                            titleBar.setRightClickListener(this::cancelHabitabonrmality);
                            button_status = 1;
                            tvState.setVisibility(View.VISIBLE);
                            tvStatement.setVisibility(View.GONE);
                            tvState.setText(jsonResult.getInt("propertyStatus") == 0 ? R.string.at_not_open : R.string.at_has_been_open);
                            switchButton.setVisibility(View.GONE);
                            tvCount.setText(String.valueOf(jsonResult.getInt("size")));
                            if (familyMenberList.size() == 0)
                                dialog.show();
                        }
                        if (familyMenberList.size() == 0) {
                            llNone.setVisibility(View.VISIBLE);
                            llHas.setVisibility(View.GONE);
                        } else {
                            llNone.setVisibility(View.GONE);
                            llHas.setVisibility(View.VISIBLE);
                        }
                        mWisdomSecurityOutAbnormalRVAdapter.setLists(familyMenberList, button_status);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_TIMING:
                    int week_day = data.getIntExtra("week_day", 7);
                    int position = data.getIntExtra("position", 0);
                    int beginTime = data.getIntExtra("beginTime", 0);
                    int endTime = data.getIntExtra("endTime", 1439);
                    String cron_week = data.getStringExtra("cron_week");
                    if (week_day == 7) {
                        //添加
                        mWisdomSecurityOutAbnormalRVAdapter.replaceCondition(position, cron_week, beginTime, endTime);
                    } else {
                        //编辑
                        mWisdomSecurityOutAbnormalRVAdapter.replaceCondition(position, beginTime, endTime);
                    }
                    break;
            }
        }
    }
}