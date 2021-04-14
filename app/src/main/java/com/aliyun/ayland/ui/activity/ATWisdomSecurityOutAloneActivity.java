package com.aliyun.ayland.ui.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.aliyun.ayland.data.ATFindFamilyMemberForOutAloneBean;
import com.aliyun.ayland.data.ATHouseBean;
import com.aliyun.ayland.global.ATGlobalApplication;
import com.aliyun.ayland.global.ATConstants;
import com.aliyun.ayland.presenter.ATMainPresenter;
import com.aliyun.ayland.ui.adapter.ATWisdomSecurityOutAloneMemberRVAdapter;
import com.aliyun.ayland.widget.ATSwitchButton;
import com.aliyun.ayland.widget.titlebar.ATMyTitleBar;
import com.anthouse.wyzhuoyue.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class ATWisdomSecurityOutAloneActivity extends ATBaseActivity implements ATMainContract.View, View.OnClickListener {
    private ATMainPresenter mPresenter;
    private ATHouseBean houseBean;
    private boolean had_one_subcribed;
    private int appId, status, button_status;
    private RecyclerView recyclerView;
    private Button button;
    private TextView tvStatement, tvSubscriber, tvState, tvCount;
    private ATSwitchButton switchButton;
    private SmartRefreshLayout smartRefreshLayout;
    private RelativeLayout rlCountFromTheCommunity;
    private ATMyTitleBar titleBar;
    private LinearLayout llHas, llNone;
    private ATWisdomSecurityOutAloneMemberRVAdapter mWisdomSecurityOutAloneMemberRVAdapter;
    private List<ATFindFamilyMemberForOutAloneBean.MembersBean> mFamilyMenberList = new ArrayList<>();
    private Dialog dialog;
    private ATFindFamilyMemberForOutAloneBean mFindFamilyMemberForOutAloneBean;

    @Override
    protected int getLayoutId() {
        return R.layout.at_activity_wisdom_security_out_alone;
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
    }

    private void setOutAlone() {
        showBaseProgressDlg();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("buildingCode", houseBean.getBuildingCode());
        jsonObject.put("villageId", houseBean.getVillageId());
        jsonObject.put("appId", appId);
        jsonObject.put("operator", Integer.parseInt(ATGlobalApplication.getATLoginBean().getPersonCode()));
        jsonObject.put("propertyStatus", switchButton.isChecked() ? 1 : 0);
        JSONArray personCodeList = new JSONArray();
        for (ATFindFamilyMemberForOutAloneBean.MembersBean findFamilyMemberForOutAloneBean : mFamilyMenberList) {
            JSONObject object = new JSONObject();
            object.put("personCode", findFamilyMemberForOutAloneBean.getPersonCode());
            object.put("status", findFamilyMemberForOutAloneBean.getStatus());
            personCodeList.add(object);
        }
        jsonObject.put("personCodeList", personCodeList);
        mPresenter.request(ATConstants.Config.SERVER_URL_SETOUTALONE, jsonObject);
    }

    private void cancelOutAlone() {
        showBaseProgressDlg();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("buildingCode", houseBean.getBuildingCode());
        jsonObject.put("villageId", houseBean.getVillageId());
        jsonObject.put("appId", appId);
        jsonObject.put("operator", ATGlobalApplication.getATLoginBean().getPersonCode());
        mPresenter.request(ATConstants.Config.SERVER_URL_CANCELOUTALONE, jsonObject);
    }

    private void findFamilyMemberForOutAlone() {
        showBaseProgressDlg();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("buildingCode", houseBean.getBuildingCode());
        jsonObject.put("villageId", houseBean.getVillageId());
        jsonObject.put("personCode", ATGlobalApplication.getATLoginBean().getPersonCode());
        jsonObject.put("appId", appId);
        mPresenter.request(ATConstants.Config.SERVER_URL_FINDFAMILYMEMBERFOROUTALONE, jsonObject);
    }

    private void init() {
        appId = getIntent().getIntExtra("appId", 0);
        status = getIntent().getIntExtra("status", 0);
        houseBean = gson.fromJson(ATGlobalApplication.getHouse(), ATHouseBean.class);

        recyclerView.setLayoutManager(new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        mWisdomSecurityOutAloneMemberRVAdapter = new ATWisdomSecurityOutAloneMemberRVAdapter(this);
        recyclerView.setAdapter(mWisdomSecurityOutAloneMemberRVAdapter);
        mWisdomSecurityOutAloneMemberRVAdapter.setOnItemClickListener((view, o, position) -> mFamilyMenberList.get(position).setStatus((int) o));

        tvStatement.setSelected(true);
        tvStatement.setOnClickListener(view -> {
            tvStatement.setSelected(!tvStatement.isSelected());
        });
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            refreshLayout.finishRefresh(2000);
            findFamilyMemberForOutAlone();
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
            titleBar.setRightClickListener(this::cancelOutAlone);
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
        view.findViewById(R.id.tv_sure).setOnClickListener(v -> cancelOutAlone());
        dialog.setContentView(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        smartRefreshLayout.autoRefresh();
    }

    @Override
    public void onClick(View view) {
        if (R.id.button == view.getId()) {
            switch (button_status) {
                case 0:
                case 2:
                    Log.e("onClick: ",
                            (switchButton.isChecked() ? 1 : 0) + "--");
                    if (!tvStatement.isSelected()) {
                        showToast(getString(R.string.at_please_check_agreement));
                        return;
                    }
                    //订阅
                    had_one_subcribed = false;
                    for (ATFindFamilyMemberForOutAloneBean.MembersBean findFamilyMemberForOutAloneBean : mFamilyMenberList) {
                        if (findFamilyMemberForOutAloneBean.getStatus() == 1) {
                            had_one_subcribed = true;
                            break;
                        }
                    }
                    if (had_one_subcribed)
                        setOutAlone();
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
                        tvSubscriber.setText(String.format(getString(R.string.at_subscriber_), mFindFamilyMemberForOutAloneBean.getOperatorName()));
                        rlCountFromTheCommunity.setVisibility(View.VISIBLE);
                        button.setText(R.string.at_edit);
                        titleBar.setRightButtonText(getString(R.string.at_unsubscribe));
                        titleBar.setRightClickListener(this::cancelOutAlone);
                        button_status = 1;
                        tvState.setVisibility(View.VISIBLE);
                        tvState.setText(mFindFamilyMemberForOutAloneBean.getPropertyStatus() == 0 ? R.string.at_not_open : R.string.at_has_been_open);
                        switchButton.setVisibility(View.GONE);
                        tvCount.setText(String.valueOf(mFindFamilyMemberForOutAloneBean.getSize()));
                    });
                    tvSubscriber.setText("");
                    rlCountFromTheCommunity.setVisibility(View.GONE);
                    tvState.setVisibility(View.GONE);
                    switchButton.setVisibility(View.VISIBLE);
                    switchButton.setChecked(mFindFamilyMemberForOutAloneBean.getPropertyStatus() == 1);
                    mWisdomSecurityOutAloneMemberRVAdapter.setStatus(2);
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
                    case ATConstants.Config.SERVER_URL_SETOUTALONE:
                        showToast(getString(R.string.at_subscribe_success));
                        status = 1;
                        button_status = 1;
                        smartRefreshLayout.autoRefresh();
                        break;
                    case ATConstants.Config.SERVER_URL_CANCELOUTALONE:
                        showToast(getString(R.string.at_unsubscribe_success));
                        status = 0;
                        button_status = 0;
                        dialog.dismiss();
                        smartRefreshLayout.autoRefresh();
                        break;
                    case ATConstants.Config.SERVER_URL_FINDFAMILYMEMBERFOROUTALONE:
                        mFindFamilyMemberForOutAloneBean = gson.fromJson(jsonResult.toString(), ATFindFamilyMemberForOutAloneBean.class);
                        List<ATFindFamilyMemberForOutAloneBean.MembersBean> familyMenberList = mFindFamilyMemberForOutAloneBean.getMembers();
                        mFamilyMenberList.clear();
                        mFamilyMenberList.addAll(familyMenberList);
                        if (button_status == 0) {
                            //未订阅
                            rlCountFromTheCommunity.setVisibility(View.GONE);
                            button.setText(R.string.at_subscribe);
                            titleBar.setRightButtonText("");
                            button_status = 0;
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
                            titleBar.setRightClickListener(this::cancelOutAlone);
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
                        mWisdomSecurityOutAloneMemberRVAdapter.setList(familyMenberList, button_status);
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