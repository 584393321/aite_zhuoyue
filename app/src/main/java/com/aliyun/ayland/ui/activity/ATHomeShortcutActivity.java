package com.aliyun.ayland.ui.activity;

import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.ayland.base.ATBaseActivity;
import com.aliyun.ayland.base.autolayout.util.ATAutoUtils;
import com.aliyun.ayland.contract.ATMainContract;
import com.aliyun.ayland.data.ATFamilyDeviceBean;
import com.aliyun.ayland.data.ATHouseBean;
import com.aliyun.ayland.data.ATPublicDeviceBean;
import com.aliyun.ayland.data.ATSceneBean1;
import com.aliyun.ayland.data.ATShortcutBean;
import com.aliyun.ayland.global.ATGlobalApplication;
import com.aliyun.ayland.global.ATConstants;
import com.aliyun.ayland.presenter.ATMainPresenter;
import com.aliyun.ayland.ui.adapter.ATHomeShortcutAddRVAdapter;
import com.aliyun.ayland.ui.adapter.ATHomeShortcutCommunityRVAdapter;
import com.aliyun.ayland.ui.adapter.ATHomeShortcutDeleteRVAdapter;
import com.aliyun.ayland.ui.adapter.ATHomeShortcutFamilyRVAdapter;
import com.aliyun.ayland.widget.ATRecycleViewItemDecoration;
import com.aliyun.ayland.widget.titlebar.ATMyTitleBar;
import com.anthouse.wyzhuoyue.R;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.touch.OnItemMoveListener;
import com.yanzhenjie.recyclerview.swipe.touch.OnItemStateChangedListener;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ATHomeShortcutActivity extends ATBaseActivity implements ATMainContract.View {
    private ATMainPresenter mPresenter;
    private ATHomeShortcutDeleteRVAdapter mHomeShortcutRVAdapter;
    private ATHomeShortcutAddRVAdapter mHomeShortcutAddRVAdapter;
    private List<ATShortcutBean> mShortcutList = new ArrayList<>();
    private List<ATSceneBean1> mSceneBeanList = new ArrayList<>();
    private ATHouseBean mHouseBean;
    private ATHomeShortcutFamilyRVAdapter mHomeShortcutFamilyRVAdapter;
    private ATHomeShortcutCommunityRVAdapter mHomeShortcutCommunityRVAdapter;
    private List<ATFamilyDeviceBean> mFamilyDeviceList = new ArrayList<>();
    private List<ATPublicDeviceBean> mPublicDeviceList = new ArrayList<>();
    private ATMyTitleBar titleBar;
    private SmartRefreshLayout smartRefreshLayout;
    private SwipeMenuRecyclerView smrvMyShortcut;
    private TextView tvNoFamilyDevice, tvNoLinkage, tvNoCommunityDevice;
    private RecyclerView rvFamily, rvCommunity, rvLinkage;

    @Override
    protected int getLayoutId() {
        return R.layout.at_activity_home_shortcut;
    }

    @Override
    protected void findView() {
        titleBar = findViewById(R.id.titlebar);
        smartRefreshLayout = findViewById(R.id.smartRefreshLayout);
        smrvMyShortcut = findViewById(R.id.smrv_my_shortcut);
        rvFamily = findViewById(R.id.rv_family);
        rvCommunity = findViewById(R.id.rv_community);
        rvLinkage = findViewById(R.id.rv_linkage);
        tvNoFamilyDevice = findViewById(R.id.tv_no_family_device);
        tvNoLinkage = findViewById(R.id.tv_no_linkage);
        tvNoCommunityDevice = findViewById(R.id.tv_no_community_device);
        init();
    }

    @Override
    protected void initPresenter() {
        mPresenter = new ATMainPresenter(this);
        mPresenter.install(this);
    }

    private void findPublicDevice() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("villageId", mHouseBean.getVillageId());
        jsonObject.put("iotToken", ATGlobalApplication.getIoTToken());
        mPresenter.request(ATConstants.Config.SERVER_URL_FINDPUBLICDEVICE, jsonObject);
    }

    private void findShortcutDevice() {
        JSONObject operator = new JSONObject();
        operator.put("hid", ATGlobalApplication.getHid());
        operator.put("hidType", "OPEN");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("operator", operator);
        jsonObject.put("iotSpaceId", mHouseBean.getIotSpaceId());
        jsonObject.put("iotToken", ATGlobalApplication.getIoTToken());
        mPresenter.request(ATConstants.Config.SERVER_URL_FINDSHORTCUTDEVICE, jsonObject);
    }

    private void shortcutUpdate() {
        showBaseProgressDlg();
        JSONArray shortcutList = new JSONArray();
        for (int i = 0; i < mShortcutList.size(); i++) {
            JSONObject shortcut = new JSONObject();
            shortcut.put("itemId", mShortcutList.get(i).getItemId());
            shortcut.put("operateType", mShortcutList.get(i).getOperateType());
            shortcut.put("shortcutType", mShortcutList.get(i).getShortcutType() == 0 ? 1 : mShortcutList.get(i).getShortcutType());
            shortcutList.add(shortcut);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("personCode", ATGlobalApplication.getATLoginBean().getPersonCode());
        jsonObject.put("shortcutList", shortcutList);
        jsonObject.put("villageId", mHouseBean.getVillageId());
        jsonObject.put("buildingCode", mHouseBean.getBuildingCode());
        mPresenter.request(ATConstants.Config.SERVER_URL_SHORTCUTUPDATE, jsonObject);
    }

    private void sceneList() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("personCode", ATGlobalApplication.getATLoginBean().getPersonCode());
        mPresenter.request(ATConstants.Config.SERVER_URL_SCENELIST, jsonObject);
    }

    private void init() {
        mHouseBean = gson.fromJson(ATGlobalApplication.getHouse(), ATHouseBean.class);
        titleBar.setRightButtonText(getString(R.string.at_done));
        titleBar.setRightClickListener(this::shortcutUpdate);

        mShortcutList = getIntent().getParcelableArrayListExtra("mShortcutList");
        smrvMyShortcut.setLayoutManager(new GridLayoutManager(this, 3) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        mHomeShortcutRVAdapter = new ATHomeShortcutDeleteRVAdapter(this);
        smrvMyShortcut.setAdapter(mHomeShortcutRVAdapter);
        setShortcutList();
        mHomeShortcutRVAdapter.setOnItemClickListener((view, position) -> {
            if (0 == mShortcutList.get(position).getShortcutType()) {
                for (int i = 0; i < mFamilyDeviceList.size(); i++) {
                    if (mShortcutList.get(position).getItemId().equals(mFamilyDeviceList.get(i).getIotId())) {
                        mFamilyDeviceList.get(i).setAdd(false);
                        mHomeShortcutFamilyRVAdapter.notifyItemChanged(i);
                    }
                }
            } else if (1 == mShortcutList.get(position).getShortcutType()) {
                for (int i = 0; i < mPublicDeviceList.size(); i++) {
                    if (mShortcutList.get(position).getItemId().equals(mPublicDeviceList.get(i).getDeviceId())) {
                        mPublicDeviceList.get(i).setAdd(false);
                        mHomeShortcutCommunityRVAdapter.notifyItemChanged(i);
                    }
                }
                for (int i = 0; i < mFamilyDeviceList.size(); i++) {
                    if (mShortcutList.get(position).getItemId().equals(mFamilyDeviceList.get(i).getIotId())) {
                        mFamilyDeviceList.get(i).setAdd(false);
                        mHomeShortcutFamilyRVAdapter.notifyItemChanged(i);
                    }
                }
            } else if (2 == mShortcutList.get(position).getShortcutType()) {
                for (int i = 0; i < mSceneBeanList.size(); i++) {
                    if (mShortcutList.get(position).getItemId().equals(mSceneBeanList.get(i).getSceneId())) {
                        mSceneBeanList.get(i).setAdd(false);
                        mHomeShortcutAddRVAdapter.notifyItemChanged(i);
                    }
                }
            }
            mShortcutList.remove(position);
        });
        smrvMyShortcut.addItemDecoration(new ATRecycleViewItemDecoration(ATAutoUtils.getPercentHeightSize(48)));
        smrvMyShortcut.setOnItemMoveListener(onItemMoveListener);// Item的拖拽/侧滑删除时，手指状态发生变化监听。
        smrvMyShortcut.setOnItemStateChangedListener(mOnItemStateChangedListener); // 监听Item上的手指状态。
        smrvMyShortcut.setLongPressDragEnabled(true);
        smrvMyShortcut.setItemViewSwipeEnabled(false); // 开启滑动删除。
        smrvMyShortcut.setAdapter(mHomeShortcutRVAdapter);

        rvFamily.setLayoutManager(new GridLayoutManager(this, 3));
        mHomeShortcutFamilyRVAdapter = new ATHomeShortcutFamilyRVAdapter(this);
        rvFamily.setAdapter(mHomeShortcutFamilyRVAdapter);
        rvFamily.setNestedScrollingEnabled(false);
        rvFamily.addItemDecoration(new ATRecycleViewItemDecoration(ATAutoUtils.getPercentHeightSize(45)));
        mHomeShortcutFamilyRVAdapter.setOnItemClickListener((add, position) -> {
            if (add) {
                if (mShortcutList.size() > 8) {
                    showToast(getString(R.string.at_most_nine_shortcut));
                    return;
                }
                ATShortcutBean shortcutBean = new ATShortcutBean();
                shortcutBean.setItemIcon(mFamilyDeviceList.get(position).getMyImage());
                shortcutBean.setItemName(mFamilyDeviceList.get(position).getProductName());
                shortcutBean.setItemId(mFamilyDeviceList.get(position).getIotId());
                if ("Camera".equals(mFamilyDeviceList.get(position).getCategoryKey()) || "FaceRecognitionCapabilityModel".equals(mFamilyDeviceList.get(position).getCategoryKey())) {
                    shortcutBean.setOperateType(1);
                } else {
                    shortcutBean.setOperateType(2);
                }
                shortcutBean.setShortcutType(0);
                mShortcutList.add(shortcutBean);
            } else {
                for (ATShortcutBean shortcutBean : mShortcutList) {
                    if (mFamilyDeviceList.get(position).getIotId().equals(shortcutBean.getItemId())) {
                        mShortcutList.remove(shortcutBean);
                        break;
                    }
                }
            }
            mFamilyDeviceList.get(position).setAdd(add);
            mHomeShortcutFamilyRVAdapter.setLists(mFamilyDeviceList);
            mHomeShortcutRVAdapter.setLists(mShortcutList);
        });

        rvCommunity.setLayoutManager(new GridLayoutManager(this, 3));
        mHomeShortcutCommunityRVAdapter = new ATHomeShortcutCommunityRVAdapter(this);
        rvCommunity.setAdapter(mHomeShortcutCommunityRVAdapter);
        rvCommunity.setNestedScrollingEnabled(false);
        rvCommunity.addItemDecoration(new ATRecycleViewItemDecoration(ATAutoUtils.getPercentHeightSize(45)));
        mHomeShortcutCommunityRVAdapter.setOnItemClickListener((add, position) -> {
            if (add) {
                if (mShortcutList.size() > 8) {
                    showToast(getString(R.string.at_most_nine_shortcut));
                    return;
                }
                ATShortcutBean shortcutBean = new ATShortcutBean();
                shortcutBean.setItemIcon(mPublicDeviceList.get(position).getImageUrl());
                shortcutBean.setItemName(mPublicDeviceList.get(position).getName());
                shortcutBean.setItemId(mPublicDeviceList.get(position).getDeviceId());
                if ("Camera".equals(mPublicDeviceList.get(position).getCategoryKey()) || "FaceRecognitionCapabilityModel".equals(mPublicDeviceList.get(position).getCategoryKey())) {
                    shortcutBean.setOperateType(1);
                } else {
                    shortcutBean.setOperateType(2);
                }
                shortcutBean.setShortcutType(1);
                mShortcutList.add(shortcutBean);
            } else {
                for (ATShortcutBean shortcutBean : mShortcutList) {
                    if (mPublicDeviceList.get(position).getDeviceId().equals(shortcutBean.getItemId())) {
                        mShortcutList.remove(shortcutBean);
                        break;
                    }
                }
            }
            mPublicDeviceList.get(position).setAdd(add);
            mHomeShortcutCommunityRVAdapter.setLists(mPublicDeviceList);
            mHomeShortcutRVAdapter.setLists(mShortcutList);
        });

        rvLinkage.setLayoutManager(new GridLayoutManager(this, 3));
        mHomeShortcutAddRVAdapter = new ATHomeShortcutAddRVAdapter(this);
        rvLinkage.setAdapter(mHomeShortcutAddRVAdapter);
        rvLinkage.setNestedScrollingEnabled(false);
        rvLinkage.addItemDecoration(new ATRecycleViewItemDecoration(ATAutoUtils.getPercentHeightSize(45)));
        mHomeShortcutAddRVAdapter.setOnItemClickListener((add, position) -> {
            if (add) {
                if (mShortcutList.size() > 8) {
                    showToast(getString(R.string.at_most_nine_shortcut));
                    return;
                }
                ATShortcutBean shortcutBean = new ATShortcutBean();
                shortcutBean.setItemIcon(mSceneBeanList.get(position).getSceneIcon());
                shortcutBean.setItemName(mSceneBeanList.get(position).getSceneName());
                shortcutBean.setItemId(mSceneBeanList.get(position).getSceneId());
                shortcutBean.setOperateType(2);
                shortcutBean.setShortcutType(2);
                mShortcutList.add(shortcutBean);
            } else {
                for (ATShortcutBean shortcutBean : mShortcutList) {
                    if (mSceneBeanList.get(position).getSceneId().equals(shortcutBean.getItemId())) {
                        mShortcutList.remove(shortcutBean);
                        break;
                    }
                }
            }
            mSceneBeanList.get(position).setAdd(add);
            mHomeShortcutAddRVAdapter.setLists(mSceneBeanList);
            mHomeShortcutRVAdapter.setLists(mShortcutList);
        });

        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            refreshLayout.finishRefresh(2000);
            sceneList();
            findPublicDevice();
            findShortcutDevice();
        });
    }

    /**
     * 监听拖拽和侧滑删除，更新UI和数据源。
     */
    private OnItemMoveListener onItemMoveListener = new OnItemMoveListener() {
        @Override
        public boolean onItemMove(RecyclerView.ViewHolder srcHolder, RecyclerView.ViewHolder targetHolder) {
            // 不同的ViewType不能拖拽换位置。
            if (srcHolder.getItemViewType() != targetHolder.getItemViewType())
                return false;

            int fromPosition = srcHolder.getAdapterPosition();
            int toPosition = targetHolder.getAdapterPosition();

            Collections.swap(mShortcutList, fromPosition, toPosition);
            mHomeShortcutRVAdapter.notifyItemMoved(fromPosition, toPosition);
            return true;// 返回true表示处理了并可以换位置，返回false表示你没有处理并不能换位置。
        }

        @Override
        public void onItemDismiss(RecyclerView.ViewHolder srcHolder) {
            mHomeShortcutRVAdapter.notifyItemRemoved(srcHolder.getAdapterPosition());
        }
    };

    /**
     * Item的拖拽/侧滑删除时，手指状态发生变化监听。
     */
    private OnItemStateChangedListener mOnItemStateChangedListener = (viewHolder, actionState) -> {
        if (actionState == OnItemStateChangedListener.ACTION_STATE_DRAG) {
//                当前状态（"状态：拖拽");
            // 拖拽的时候背景就透明了，这里我们可以添加一个特殊背景。
            viewHolder.itemView.setBackground(ContextCompat.getDrawable(ATHomeShortcutActivity.this, R.drawable.at_shape_18px_f2f0ef));
        } else if (actionState == OnItemStateChangedListener.ACTION_STATE_SWIPE) {
//                当前状态（"状态：滑动删除");
        } else if (actionState == OnItemStateChangedListener.ACTION_STATE_IDLE) {
//                当前状态（"状态：手指松开");
            // 在手松开的时候还原背景。
            ViewCompat.setBackground(viewHolder.itemView, ContextCompat.getDrawable(ATHomeShortcutActivity.this, R.drawable.at_selector_f5f5f5_eeeeee));
        }
    };

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
                    case ATConstants.Config.SERVER_URL_FINDSHORTCUTDEVICE:
                        mFamilyDeviceList = gson.fromJson(jsonResult.getString("data"), new TypeToken<List<ATFamilyDeviceBean>>() {
                        }.getType());
                        for (ATShortcutBean shortcutBean : mShortcutList) {
                            for (ATFamilyDeviceBean familyDeviceBean : mFamilyDeviceList) {
                                if (shortcutBean.getItemId().equals(familyDeviceBean.getIotId())) {
                                    familyDeviceBean.setAdd(true);
                                }
                            }
                        }
                        tvNoFamilyDevice.setVisibility(mFamilyDeviceList.size() == 0 ? View.VISIBLE : View.GONE);
                        mHomeShortcutFamilyRVAdapter.setLists(mFamilyDeviceList);
                        break;
                    case ATConstants.Config.SERVER_URL_FINDPUBLICDEVICE:
                        mPublicDeviceList = gson.fromJson(jsonResult.getString("data"), new TypeToken<List<ATPublicDeviceBean>>() {
                        }.getType());
                        for (ATShortcutBean shortcutBean : mShortcutList) {
                            for (ATPublicDeviceBean publicDeviceBean : mPublicDeviceList) {
                                if (shortcutBean.getItemId().equals(publicDeviceBean.getDeviceId())) {
                                    publicDeviceBean.setAdd(true);
                                }
                            }
                        }
                        tvNoCommunityDevice.setVisibility(mPublicDeviceList.size() == 0 ? View.VISIBLE : View.GONE);
                        mHomeShortcutCommunityRVAdapter.setLists(mPublicDeviceList);
                        break;
                    case ATConstants.Config.SERVER_URL_SCENELIST:
                        mSceneBeanList = gson.fromJson(jsonResult.getString("data"), new TypeToken<List<ATSceneBean1>>() {
                        }.getType());
                        for (ATShortcutBean shortcutBean : mShortcutList) {
                            for (ATSceneBean1 sceneBean1 : mSceneBeanList) {
                                if (shortcutBean.getItemId().equals(sceneBean1.getSceneId())) {
                                    sceneBean1.setAdd(true);
                                }
                            }
                        }
                        tvNoLinkage.setVisibility(mSceneBeanList.size() == 0 ? View.VISIBLE : View.GONE);
                        mHomeShortcutAddRVAdapter.setLists(mSceneBeanList);
                        break;
                    case ATConstants.Config.SERVER_URL_SHORTCUTUPDATE:
                        showToast(getString(R.string.at_shortcut_edit_success));
                        setResult(RESULT_OK);
                        finish();
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

    public void setShortcutList() {
        mHomeShortcutRVAdapter.setLists(mShortcutList);
    }
}