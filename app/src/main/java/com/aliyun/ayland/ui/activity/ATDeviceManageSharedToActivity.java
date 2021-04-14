package com.aliyun.ayland.ui.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.ayland.base.ATBaseActivity;
import com.aliyun.ayland.base.autolayout.util.ATAutoUtils;
import com.aliyun.ayland.contract.ATMainContract;
import com.aliyun.ayland.data.ATDeviceManageBean;
import com.aliyun.ayland.data.ATHouseBean;
import com.aliyun.ayland.global.ATGlobalApplication;
import com.aliyun.ayland.global.ATConstants;
import com.aliyun.ayland.presenter.ATMainPresenter;
import com.aliyun.ayland.ui.adapter.ATDeviceManageSharedToRVAdapter;
import com.aliyun.ayland.widget.ATRecycleViewItemDecoration;
import com.aliyun.ayland.widget.titlebar.ATMyTitleBar;
import com.anthouse.wyzhuoyue.R;
import com.google.gson.reflect.TypeToken;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import static com.aliyun.ayland.ui.activity.ATDeviceManageToActivity.REQUEST_CODE_CHANGE;

public class ATDeviceManageSharedToActivity extends ATBaseActivity implements ATMainContract.View {
    private ATDeviceManageSharedToRVAdapter mDeviceManageSharedToRVAdapter;
    private Dialog dialog;
    private ATMainPresenter mPresenter;
    private SwipeMenuBridge swipeMenuBridge;
    private ArrayList<ATDeviceManageBean.SharedUsersBean> mSharedUsersList;
    private String iotId;
    private int position, type;
    private List<ATDeviceManageBean> mDeviceManageList;
    private ATMyTitleBar titlebar;
    private SwipeMenuRecyclerView swipeMenuRecyclerView;

    @Override
    protected int getLayoutId() {
        return R.layout.at_activity_device_manage_share_to;
    }

    @Override
    protected void findView() {
        titlebar = findViewById(R.id.titlebar);
        swipeMenuRecyclerView = findViewById(R.id.swipe_menu_recyclerView);

        init();
    }

    @Override
    protected void initPresenter() {
        mPresenter = new ATMainPresenter(this);
        mPresenter.install(this);
    }

    private void findDevices() {
        ATHouseBean houseBean = gson.fromJson(ATGlobalApplication.getHouse(), ATHouseBean.class);
        if (type == 2 && houseBean == null)
            return;
        showBaseProgressDlg();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", type);
        JSONObject operator = new JSONObject();
        operator.put("hid", ATGlobalApplication.getHid());
        operator.put("hidType", "OPEN");
        jsonObject.put("operator", operator);
        jsonObject.put("iotToken", ATGlobalApplication.getIoTToken());
        jsonObject.put("iotSpaceId", houseBean.getIotSpaceId());
        mPresenter.request(ATConstants.Config.SERVER_URL_FINDDEVICES, jsonObject);
    }

    private void unsharedDevice() {
        showBaseProgressDlg();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", mSharedUsersList.get(swipeMenuBridge.getAdapterPosition()).getUsername());
        JSONObject operator = new JSONObject();
        operator.put("hid", ATGlobalApplication.getHid());
        operator.put("hidType", "OPEN");
        jsonObject.put("operator", operator);
        jsonObject.put("iotToken", ATGlobalApplication.getIoTToken());
        jsonObject.put("iotId", iotId);
        mPresenter.request(ATConstants.Config.SERVER_URL_UNSHAREDDEVICE, jsonObject);
    }

    private void init() {
        mSharedUsersList = getIntent().getParcelableArrayListExtra("sharedUsers");
        iotId = getIntent().getStringExtra("iotId");
        position = getIntent().getIntExtra("position", 0);
        type = getIntent().getIntExtra("type", 0);

        titlebar.setTitle(getString(R.string.at_device_manage));
        titlebar.setRightButtonText(getString(R.string.at_add_share));

        ArrayList<String> iotIdList = new ArrayList<>();
        iotIdList.add(iotId);
        titlebar.setRightClickListener(() ->
                startActivityForResult(new Intent(this, ATDeviceManageMineToActivity.class)
                        .putExtra("iotIdList", iotIdList), REQUEST_CODE_CHANGE)
        );
        swipeMenuRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mDeviceManageSharedToRVAdapter = new ATDeviceManageSharedToRVAdapter(this);
        mDeviceManageSharedToRVAdapter.setList(mSharedUsersList);
        swipeMenuRecyclerView.setNestedScrollingEnabled(false);
        swipeMenuRecyclerView.addItemDecoration(new ATRecycleViewItemDecoration(ATAutoUtils.getPercentHeightSize(24)));
        swipeMenuRecyclerView.setSwipeMenuCreator(mSwipeMenuCreator);
        swipeMenuRecyclerView.setSwipeMenuItemClickListener(mMenuItemClickListener); // Item的Menu点击。
        swipeMenuRecyclerView.setLongPressDragEnabled(false);
        swipeMenuRecyclerView.setItemViewSwipeEnabled(false);
        swipeMenuRecyclerView.setAdapter(mDeviceManageSharedToRVAdapter);

        initDialog();
    }

    @SuppressLint("InflateParams")
    private void initDialog() {
        dialog = new Dialog(this, R.style.nameDialog);
        View view = LayoutInflater.from(this).inflate(R.layout.at_dialog_855px546px_sure_or_no, null, false);
        TextView tvTitle = view.findViewById(R.id.tv_title);
        TextView tvLeft = view.findViewById(R.id.tv_cancel);
        TextView tvRight = view.findViewById(R.id.tv_sure);
        tvTitle.setText(getString(R.string.at_sure_to_unbind_));
        tvLeft.setOnClickListener(v -> dialog.dismiss());
        tvRight.setOnClickListener(v -> unsharedDevice());
        dialog.setContentView(view);
    }

    /**
     * 菜单创建器。
     */
    public SwipeMenuCreator mSwipeMenuCreator = (swipeLeftMenu, swipeRightMenu, viewType) -> {
        // 1. MATCH_PARENT 自适应高度，保持和Item一样高;
        // 2. 指定具体的高，比如80;
        // 3. WRAP_CONTENT，自身高度，不推荐;
        int width = ATAutoUtils.getPercentWidthSize(228);
        int height = ViewGroup.LayoutParams.MATCH_PARENT;

        SwipeMenuItem closeItem = new SwipeMenuItem(this)
                .setBackground(getResources().getDrawable(R.drawable.at_shape_12px_e83434_right))
                .setText(getString(R.string.at_delete))
                .setTextColor(Color.WHITE)
                .setWidth(width)
                .setHeight(height);
        swipeRightMenu.addMenuItem(closeItem); // 添加一个按钮到右侧菜单。
    };
    /**
     * RecyclerView的Item的Menu点击监听。
     */
    private SwipeMenuItemClickListener mMenuItemClickListener = new SwipeMenuItemClickListener() {
        @Override
        public void onItemClick(SwipeMenuBridge menuBridge) {
            swipeMenuBridge = menuBridge;
            dialog.show();
        }
    };

    @Override
    public void requestResult(String result, String url) {
        try {
            org.json.JSONObject jsonResult = new org.json.JSONObject(result);
            if ("200".equals(jsonResult.getString("code"))) {
                switch (url) {
                    case ATConstants.Config.SERVER_URL_UNSHAREDDEVICE:
                        showToast(getString(R.string.at_unbind_success));
                        dialog.dismiss();
                        swipeMenuBridge.closeMenu();
                        mDeviceManageSharedToRVAdapter.removeItem(swipeMenuBridge.getAdapterPosition());
                        setResult(RESULT_OK);
                        if (mSharedUsersList.size() == 0)
                            finish();
                        break;
                    case ATConstants.Config.SERVER_URL_FINDDEVICES:
                        mDeviceManageList = gson.fromJson(jsonResult.getString("data"), new TypeToken<List<ATDeviceManageBean>>() {
                        }.getType());
                        mDeviceManageSharedToRVAdapter.setList(mDeviceManageList.get(position).getSharedUsers());
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        dialog.dismiss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHANGE && resultCode == RESULT_OK)
            finish();
    }
}