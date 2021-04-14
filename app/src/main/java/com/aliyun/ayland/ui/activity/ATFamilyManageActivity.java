package com.aliyun.ayland.ui.activity;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.ayland.base.ATBaseActivity;
import com.aliyun.ayland.contract.ATMainContract;
import com.aliyun.ayland.data.ATFamilyManageRoomBean;
import com.aliyun.ayland.data.ATFamilyMemberBean;
import com.aliyun.ayland.data.ATHouseBean;
import com.aliyun.ayland.global.ATGlobalApplication;
import com.aliyun.ayland.global.ATConstants;
import com.aliyun.ayland.presenter.ATMainPresenter;
import com.aliyun.ayland.ui.adapter.ATFamilyManageRVAdapter;
import com.aliyun.ayland.ui.adapter.ATFamilyManageRoomRVAdapter;
import com.aliyun.ayland.utils.ATResourceUtils;
import com.aliyun.ayland.widget.titlebar.ATMyTitleBar;
import com.anthouse.wyzhuoyue.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class ATFamilyManageActivity extends ATBaseActivity implements ATMainContract.View {
    private ATMainPresenter mPresenter;
    private ATHouseBean houseBean;
    private ATFamilyManageRVAdapter mFamilyManageRVAdapter;
    private ATFamilyManageRoomRVAdapter mFamilyManageRoomRVAdapter;
    private ArrayList<ATFamilyMemberBean> mOtherMemberList = new ArrayList<>();
    private ArrayList<ATFamilyMemberBean> mOtherMemberHasPhoneList = new ArrayList<>();
    private List<ATFamilyManageRoomBean> mNotSeeRoomList = new ArrayList<>();
    private ArrayList<ATFamilyManageRoomBean> mCanSeeRoomList = new ArrayList<>();
    private boolean ifAdmin;
    private TextView tvName, tvAdmin, tvStatus, tvNoOthers;
    private ImageView imgUser;
    private LinearLayout llMember, llOther;
    private RecyclerView rvMember, rvRoom;
    private SmartRefreshLayout smartRefreshLayout;
    private ATMyTitleBar titleBar;
    private ATFamilyMemberBean mFamilyMemberBean;

    @Override
    protected int getLayoutId() {
        return R.layout.at_activity_family_manage;
    }

    @Override
    protected void findView() {
        titleBar = findViewById(R.id.titlebar);
        llMember = findViewById(R.id.ll_member);
        llOther = findViewById(R.id.ll_other);
        tvStatus = findViewById(R.id.tv_status);
        tvName = findViewById(R.id.tv_name);
        tvAdmin = findViewById(R.id.tv_admin);
        imgUser = findViewById(R.id.img_user);
        tvNoOthers = findViewById(R.id.tv_no_others);
        smartRefreshLayout = findViewById(R.id.smartRefreshLayout);
        rvMember = findViewById(R.id.rv_member);
        rvRoom = findViewById(R.id.rv_room);

        init();
    }

    @Override
    protected void initPresenter() {
        mPresenter = new ATMainPresenter(this);
        mPresenter.install(this);
    }

    private void memberRoom() {
        showBaseProgressDlg();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("buildingCode", houseBean.getBuildingCode());
        jsonObject.put("villageId", houseBean.getVillageId());
        jsonObject.put("memberPersonCode", ATGlobalApplication.getATLoginBean().getPersonCode());
        mPresenter.request(ATConstants.Config.SERVER_URL_MEMBERROOM, jsonObject);
    }

    private void findFamilyMember() {
        showBaseProgressDlg();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("buildingCode", houseBean.getBuildingCode());
        jsonObject.put("villageId", houseBean.getVillageId());
        mPresenter.request(ATConstants.Config.SERVER_URL_FINDFAMILYMEMBER, jsonObject);
    }

    private void init() {
        houseBean = gson.fromJson(ATGlobalApplication.getHouse(), ATHouseBean.class);
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.ico_touxiang_mr)
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        Glide.with(this).load(ATGlobalApplication.getATLoginBean().getAvatarUrl()).apply(options).into(imgUser);
        tvAdmin.setOnClickListener(view -> {
            if (mFamilyMemberBean == null) {
                findFamilyMember();
                memberRoom();
            } else
                startActivity(new Intent(this, ATFamilyManageAdviceActivity.class)
                        .putExtra("edit", false).putExtra("FamilyMemberBean", mFamilyMemberBean)
                        .putExtra("mOtherMemberList", mOtherMemberHasPhoneList));
        });
        rvMember.setLayoutManager(new LinearLayoutManager(this));
        mFamilyManageRVAdapter = new ATFamilyManageRVAdapter(this);
        rvMember.setAdapter(mFamilyManageRVAdapter);
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            refreshLayout.finishRefresh(2000);
            findFamilyMember();
            memberRoom();
        });
        rvRoom.setLayoutManager(new LinearLayoutManager(this));
        mFamilyManageRoomRVAdapter = new ATFamilyManageRoomRVAdapter(this);
        rvRoom.setAdapter(mFamilyManageRoomRVAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        smartRefreshLayout.autoRefresh();
    }

    @Override
    public void requestResult(String result, String url) {
        try {
            org.json.JSONObject jsonResult = new org.json.JSONObject(result);
            if ("200".equals(jsonResult.getString("code"))) {
                switch (url) {
                    case ATConstants.Config.SERVER_URL_MEMBERROOM:
                        mNotSeeRoomList = gson.fromJson(jsonResult.getJSONObject("room").getString("notSee"), new TypeToken<List<ATFamilyManageRoomBean>>() {
                        }.getType());
                        mCanSeeRoomList = gson.fromJson(jsonResult.getJSONObject("room").getString("canSee"), new TypeToken<List<ATFamilyManageRoomBean>>() {
                        }.getType());
                        mFamilyManageRoomRVAdapter.setLists(mCanSeeRoomList, mNotSeeRoomList);
                        closeBaseProgressDlg();
                        smartRefreshLayout.finishRefresh();
                        break;
                    case ATConstants.Config.SERVER_URL_FINDFAMILYMEMBER:
                        List<ATFamilyMemberBean> familyMenberList = gson.fromJson(jsonResult.getString("members"), new TypeToken<List<ATFamilyMemberBean>>() {
                        }.getType());
                        mOtherMemberList.clear();
                        mOtherMemberHasPhoneList.clear();
                        for (ATFamilyMemberBean familyMemberBean : familyMenberList) {
                            if (!ATGlobalApplication.getATLoginBean().getPersonCode().equals(familyMemberBean.getPersonCode())) {
                                if (!TextUtils.isEmpty(familyMemberBean.getPhone()))
                                    mOtherMemberHasPhoneList.add(familyMemberBean);
                                mOtherMemberList.add(familyMemberBean);
                            } else {
                                mFamilyMemberBean = familyMemberBean;
                                tvName.setText(mFamilyMemberBean.getNickname());
                                if (ATResourceUtils.getResIdByName(String.format(getString(R.string.at_householdtype_), familyMemberBean.getHouseholdtype()), ATResourceUtils.ResourceType.STRING) != 0) {
                                    tvStatus.setVisibility(View.VISIBLE);
                                    tvStatus.setText(ATResourceUtils.getResIdByName(String.format(getString(R.string.at_householdtype_), familyMemberBean.getHouseholdtype())
                                            , ATResourceUtils.ResourceType.STRING));
                                } else
                                    tvStatus.setVisibility(View.GONE);

                                ifAdmin = familyMemberBean.getIfAdmin() == 1;
                                tvAdmin.setVisibility(ifAdmin ? View.VISIBLE : View.GONE);
                                if (ifAdmin) {
                                    //为管理员
                                    llMember.setVisibility(View.VISIBLE);
                                    llOther.setVisibility(View.GONE);
                                    titleBar.showRightButton(true);
                                    titleBar.setRightBtTextImage(R.drawable.gengduo_a);
                                    titleBar.setRightClickListener(() -> startActivity(new Intent(this, ATFamilyManageRegistPhoneActivity.class)
                                            .putExtra("edit", true)));
                                } else {
                                    titleBar.showRightButton(false);
                                    if ("104".equals(familyMemberBean.getHouseholdtype())) {
                                        llMember.setVisibility(View.GONE);
                                        llOther.setVisibility(View.VISIBLE);
                                        return;
                                    } else {
                                        llMember.setVisibility(View.VISIBLE);
                                        llOther.setVisibility(View.GONE);
                                    }
                                }
                            }
                        }
                        if (mOtherMemberList.size() == 0) {
                            tvNoOthers.setVisibility(View.VISIBLE);
                        } else {
                            tvNoOthers.setVisibility(View.GONE);
                        }
                        closeBaseProgressDlg();
                        smartRefreshLayout.finishRefresh();
                        mFamilyManageRVAdapter.setLists(mOtherMemberList, ifAdmin);
                        break;
                }
            } else {
                closeBaseProgressDlg();
                smartRefreshLayout.finishRefresh();
                showToast(jsonResult.getString("message"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
