package com.aliyun.ayland.ui.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
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
import com.aliyun.ayland.ui.adapter.ATFamilyManageMemberRoomRVAdapter;
import com.aliyun.ayland.utils.ATResourceUtils;
import com.aliyun.ayland.widget.titlebar.ATMyTitleBar;
import com.anthouse.wyzhuoyue.R;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class ATFamilyManageMemberActivity extends ATBaseActivity implements ATMainContract.View {
    private static final int REQUEST_CODE_CHANGED = 0x1001;
    private ATMainPresenter mPresenter;
    private ATHouseBean houseBean;
    private ATFamilyManageMemberRoomRVAdapter mFamilyManageRoomRVAdapter;
    private List<ATFamilyManageRoomBean> mAllRoomList = new ArrayList<>();
    private boolean ifAdmin;
    private int current_position, status;
    private Dialog dialog;
    private ATFamilyMemberBean mFamilyMemberBean;
    private TextView tvName, tvIdentity;
    private ImageView imgUser, imgEdit;
    private RecyclerView rvRoom;
    private LinearLayout llEdit;
    private SmartRefreshLayout smartRefreshLayout;
    private ATMyTitleBar titleBar;

    @Override
    protected int getLayoutId() {
        return R.layout.at_activity_family_manage_member;
    }

    @Override
    protected void findView() {
        titleBar = findViewById(R.id.titlebar);
        llEdit = findViewById(R.id.ll_edit);
        tvName = findViewById(R.id.tv_name);
        tvIdentity = findViewById(R.id.tv_identity);
        imgUser = findViewById(R.id.img_user);
        imgEdit = findViewById(R.id.img_edit);
        rvRoom = findViewById(R.id.rv_room);
        smartRefreshLayout = findViewById(R.id.smartRefreshLayout);
        init();
    }

    @Override
    protected void initPresenter() {
        mPresenter = new ATMainPresenter(this);
        mPresenter.install(this);
        memberRoom();
    }

    private void changeView() {
        showBaseProgressDlg();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("buildingCode", houseBean.getBuildingCode());
        jsonObject.put("memberPersonCode", mFamilyMemberBean.getPersonCode());
        jsonObject.put("roomCode", mAllRoomList.get(current_position).getRoomCode());
        jsonObject.put("villageId", houseBean.getVillageId());
        JSONObject operator = new JSONObject();
        operator.put("hid", ATGlobalApplication.getHid());
        operator.put("hidType", "OPEN");
        jsonObject.put("operator", operator);
        jsonObject.put("iotToken", ATGlobalApplication.getIoTToken());
        jsonObject.put("status", status);
        mPresenter.request(ATConstants.Config.SERVER_URL_CHANGEVIEW, jsonObject);
    }

    private void deleteMember() {
        showBaseProgressDlg();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("memberPersonCode", mFamilyMemberBean.getPersonCode());
        jsonObject.put("buildingCode", houseBean.getBuildingCode());
        jsonObject.put("villageId", houseBean.getVillageId());
        JSONObject operator = new JSONObject();
        operator.put("hid", ATGlobalApplication.getHid());
        operator.put("hidType", "OPEN");
        jsonObject.put("operator", operator);
        jsonObject.put("iotToken", ATGlobalApplication.getIoTToken());
        mPresenter.request(ATConstants.Config.SERVER_URL_DELETEMEMBER, jsonObject);
    }

    private void memberRoom() {
        showBaseProgressDlg();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("buildingCode", houseBean.getBuildingCode());
        jsonObject.put("villageId", houseBean.getVillageId());
        jsonObject.put("memberPersonCode", mFamilyMemberBean.getPersonCode());
        mPresenter.request(ATConstants.Config.SERVER_URL_MEMBERROOM, jsonObject);
    }

    private void init() {
        mFamilyMemberBean = getIntent().getParcelableExtra("FamilyMemberBean");
        ifAdmin = getIntent().getBooleanExtra("ifAdmin", false);
        houseBean = gson.fromJson(ATGlobalApplication.getHouse(), ATHouseBean.class);

        tvName.setText(mFamilyMemberBean.getNickname());
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.ico_touxiang_mr)
                .diskCacheStrategy(DiskCacheStrategy.ALL);
//        Glide.with(this).load(mFamilyMemberBean.get()).apply(options).into(imgUser);

        tvIdentity.setText(TextUtils.isEmpty(mFamilyMemberBean.getHouseholdtype()) ? R.string.at_other : ATResourceUtils.getResIdByName(String.format(getString(R.string.at_householdtype_), mFamilyMemberBean.getHouseholdtype()), ATResourceUtils.ResourceType.STRING));

        rvRoom.setLayoutManager(new LinearLayoutManager(this));
        mFamilyManageRoomRVAdapter = new ATFamilyManageMemberRoomRVAdapter(this);
        rvRoom.setAdapter(mFamilyManageRoomRVAdapter);
        if (ifAdmin) {
            titleBar.setSendText(getString(R.string.at_delete));
            titleBar.setRightClickListener(() -> dialog.show());
            mFamilyManageRoomRVAdapter.setOnItemClickListener((view, o, position) -> {
                current_position = position;
                status = (int) o;
                changeView();
            });
            imgEdit.setVisibility(View.VISIBLE);
            llEdit.setVisibility(View.VISIBLE);
            llEdit.setOnClickListener(view -> startActivityForResult(new Intent(this, ATFamilyManageAdviceActivity.class)
                    .putExtra("edit", false).putExtra("FamilyMemberBean", mFamilyMemberBean), REQUEST_CODE_CHANGED));
        } else {
            imgEdit.setVisibility(View.GONE);
            llEdit.setVisibility(View.GONE);
        }
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            refreshLayout.finishRefresh(2000);
            memberRoom();
        });

        initDialog();
    }

    @SuppressLint("InflateParams")
    private void initDialog() {
        dialog = new Dialog(this, R.style.nameDialog);
        View view = LayoutInflater.from(this).inflate(R.layout.at_dialog_855px546px_sure_or_no, null, false);
        ((TextView) view.findViewById(R.id.tv_title)).setText(getString(R.string.at_sure_to_delete));
        view.findViewById(R.id.tv_cancel).setOnClickListener(v -> dialog.dismiss());
        view.findViewById(R.id.tv_sure).setOnClickListener(v -> deleteMember());
        dialog.setContentView(view);
    }

    @Override
    public void requestResult(String result, String url) {
        try {
            org.json.JSONObject jsonResult = new org.json.JSONObject(result);
            if ("200".equals(jsonResult.getString("code"))) {
                switch (url) {
                    case ATConstants.Config.SERVER_URL_DELETEMEMBER:
                        showToast(getString(R.string.at_delete_success));
                        finish();
                        break;
                    case ATConstants.Config.SERVER_URL_CHANGEVIEW:
                        mAllRoomList.get(current_position).setCanSee(status);
                        mFamilyManageRoomRVAdapter.notifyItem(current_position, status);
                        break;
                    case ATConstants.Config.SERVER_URL_MEMBERROOM:
                        List<ATFamilyManageRoomBean> mNotSeeRoomList = gson.fromJson(jsonResult.getJSONObject("room").getString("notSee"), new TypeToken<List<ATFamilyManageRoomBean>>() {
                        }.getType());
                        List<ATFamilyManageRoomBean> mCanSeeRoomList = gson.fromJson(jsonResult.getJSONObject("room").getString("canSee"), new TypeToken<List<ATFamilyManageRoomBean>>() {
                        }.getType());
                        for (ATFamilyManageRoomBean familyManageRoomBean : mCanSeeRoomList) {
                            familyManageRoomBean.setCanSee(1);
                            mAllRoomList.add(familyManageRoomBean);
                        }
                        for (ATFamilyManageRoomBean familyManageRoomBean : mNotSeeRoomList) {
                            familyManageRoomBean.setCanSee(0);
                            mAllRoomList.add(familyManageRoomBean);
                        }
                        mFamilyManageRoomRVAdapter.setLists(mAllRoomList, ifAdmin);
                        break;
                }
            } else {
                showToast(jsonResult.getString("message"));
            }
            closeBaseProgressDlg();
            smartRefreshLayout.finishRefresh();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_CHANGED) {
            finish();
        }
    }
}