package com.aliyun.ayland.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Vibrator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.ayland.base.ATBaseActivity;
import com.aliyun.ayland.base.autolayout.util.ATAutoUtils;
import com.aliyun.ayland.contract.ATMainContract;
import com.aliyun.ayland.data.ATHouseBean;
import com.aliyun.ayland.data.ATSceneManualAutoBean;
import com.aliyun.ayland.global.ATGlobalApplication;
import com.aliyun.ayland.global.ATConstants;
import com.aliyun.ayland.presenter.ATMainPresenter;
import com.aliyun.ayland.ui.adapter.ATLinkageCustomRVAdapter;
import com.aliyun.ayland.ui.adapter.ATLinkageRecommendRVAdapter;
import com.aliyun.ayland.widget.ATRecycleViewItemDecoration;
import com.aliyun.ayland.widget.titlebar.ATMyTitleBar;
import com.anthouse.wyzhuoyue.R;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by fr on 2017/12/19.
 */

public class ATLinkageActivity extends ATBaseActivity implements ATMainContract.View {
    private ATMainPresenter mPresenter;
    private int current_position = 0;
    private ATHouseBean houseBean;
    private ATLinkageCustomRVAdapter mLinkageCustomRVAdapter;
    private List<ATSceneManualAutoBean> mSceneManualAutoBeanList = new ArrayList<>();
    private Vibrator mVibrator;
    private ATMyTitleBar titleBar;
    private RecyclerView rvRecommend;
    private TextView tvTip, tvAddLinkage;
    private RelativeLayout rlScene;
    private SwipeMenuRecyclerView swipeMenuRecyclerView;
    private SmartRefreshLayout smartRefreshLayout;
    private LinearLayout llEmpty;

    @Override
    protected int getLayoutId() {
        return R.layout.at_activity_linkage;
    }

    @Override
    protected void findView() {
        titleBar = findViewById(R.id.titlebar);
        rvRecommend = findViewById(R.id.rv_recommend);
        tvTip = findViewById(R.id.tv_tip);
        tvAddLinkage = findViewById(R.id.tv_add_linkage);
        rlScene = findViewById(R.id.rl_scene);
        swipeMenuRecyclerView = findViewById(R.id.swipeMenuRecyclerView);
        smartRefreshLayout = findViewById(R.id.smartRefreshLayout);
        llEmpty = findViewById(R.id.ll_empty);

        init();
    }

    @Override
    protected void initPresenter() {
        mPresenter = new ATMainPresenter(this);
        mPresenter.install(this);
        getHouseDevice();
    }

    private void getHouseDevice() {
        JSONObject operator = new JSONObject();
        operator.put("hid", ATGlobalApplication.getHid());
        operator.put("hidType", "OPEN");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("operator", operator);
        jsonObject.put("iotSpaceId", houseBean.getIotSpaceId());
        jsonObject.put("iotToken", ATGlobalApplication.getIoTToken());
        jsonObject.put("username", ATGlobalApplication.getAccount());
        mPresenter.request(ATConstants.Config.SERVER_URL_HOUSEDEVICE, jsonObject);
    }

    public void sceneDelete() {
        showBaseProgressDlg();
        JSONObject operator = new JSONObject();
        operator.put("hid", ATGlobalApplication.getHid());
        operator.put("hidType", "OPEN");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("sceneId", mSceneManualAutoBeanList.get(current_position).getSceneId());
        jsonObject.put("operator", operator);
        jsonObject.put("personCode", ATGlobalApplication.getATLoginBean().getPersonCode());
        jsonObject.put("sceneType", mSceneManualAutoBeanList.get(current_position).getSceneType());
        jsonObject.put("iotToken", ATGlobalApplication.getIoTToken());
        mPresenter.request(ATConstants.Config.SERVER_URL_SCENEDELETE, jsonObject);
    }

    private void sceneDeployRevoke(String sceneId, boolean enable, int sceneType) {
        showBaseProgressDlg();
        JSONObject operator = new JSONObject();
        operator.put("hid", ATGlobalApplication.getHid());
        operator.put("hidType", "OPEN");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("sceneId", sceneId);
        jsonObject.put("sceneType", sceneType);
        jsonObject.put("operator", operator);
        jsonObject.put("personCode", ATGlobalApplication.getATLoginBean().getPersonCode());
        jsonObject.put("enable", enable);
        jsonObject.put("iotToken", ATGlobalApplication.getIoTToken());
        mPresenter.request(ATConstants.Config.SERVER_URL_SCENEDEPLOYREVOKE, jsonObject);
    }

    private void sceneInstanceRun(String sceneId) {
        JSONObject operator = new JSONObject();
        operator.put("hid", ATGlobalApplication.getHid());
        operator.put("hidType", "OPEN");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("operator", operator);
        jsonObject.put("sceneId", sceneId);
        jsonObject.put("iotToken", ATGlobalApplication.getIoTToken());
        mPresenter.request(ATConstants.Config.SERVER_URL_SCENEINSTANCERUN, jsonObject);
    }

    private void getLocalUserSceneList() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("openId", ATGlobalApplication.getHid());
        jsonObject.put("sceneType", 4);
        jsonObject.put("villageId", houseBean.getVillageId());
        jsonObject.put("buildingCode", houseBean.getBuildingCode());
        mPresenter.request(ATConstants.Config.SERVER_URL_GETLOCALUSERSCENELIST, jsonObject);
    }

    private void init() {
        houseBean = gson.fromJson(ATGlobalApplication.getHouse(), ATHouseBean.class);
        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        titleBar.setTitle("");
        titleBar.setTitleStrings(getString(R.string.at_recommend), getString(R.string.at_mine));
        titleBar.setRightBtTextImage(R.drawable.gengduo_a);
        titleBar.setRightToClickListener(() -> startActivity(new Intent(this, ATLinkageLogActivity.class)));
        titleBar.setCeneterClick(who -> {
            if (who == 0) {
                rvRecommend.setVisibility(View.VISIBLE);
                tvTip.setVisibility(View.GONE);
                rlScene.setVisibility(View.GONE);
            } else {
                rvRecommend.setVisibility(View.GONE);
                tvTip.setVisibility(View.VISIBLE);
                rlScene.setVisibility(View.VISIBLE);
            }
        });
        rvRecommend.setLayoutManager(new LinearLayoutManager(this));
        ATLinkageRecommendRVAdapter linkageRecommendRVAdapter = new ATLinkageRecommendRVAdapter(this);
        rvRecommend.setAdapter(linkageRecommendRVAdapter);
        rvRecommend.addItemDecoration(new ATRecycleViewItemDecoration(ATAutoUtils.getPercentHeightSize(42)));

        swipeMenuRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mLinkageCustomRVAdapter = new ATLinkageCustomRVAdapter(this);
        swipeMenuRecyclerView.setNestedScrollingEnabled(false);
        swipeMenuRecyclerView.addItemDecoration(new ATRecycleViewItemDecoration(ATAutoUtils.getPercentHeightSize(42)));
        swipeMenuRecyclerView.setSwipeMenuCreator(mSwipeMenuCreator);
        swipeMenuRecyclerView.setSwipeMenuItemClickListener(mMenuItemClickListener); // Item的Menu点击。
        swipeMenuRecyclerView.setLongPressDragEnabled(false);
        swipeMenuRecyclerView.setItemViewSwipeEnabled(false);
        swipeMenuRecyclerView.setLongClickable(true);
        swipeMenuRecyclerView.setSwipeItemLongClickListener((itemView, position) -> {
            if (mVibrator != null)
                mVibrator.vibrate(200);
            current_position = position;
            mLinkageCustomRVAdapter.setShowing(position, ATSceneManualAutoBean.SHOWING);
            sceneInstanceRun(mSceneManualAutoBeanList.get(position).getSceneId());
        });
        swipeMenuRecyclerView.setAdapter(mLinkageCustomRVAdapter);
        mLinkageCustomRVAdapter.setOnItemClickListener((position, check) -> sceneDeployRevoke(mSceneManualAutoBeanList.get(position).getSceneId(), check,
                mSceneManualAutoBeanList.get(position).getSceneType()));

        titleBar.setRightClickListener(() -> {
            if (TextUtils.isEmpty(ATGlobalApplication.getHouse())) {
                showToast(getString(R.string.at_can_not_create_scene));
                return;
            }
            startActivity(new Intent(this, ATLinkageAddActivity.class));
        });
        tvAddLinkage.setOnClickListener(view -> {
            if (TextUtils.isEmpty(ATGlobalApplication.getHouse())) {
                showToast(getString(R.string.at_can_not_create_scene));
                return;
            }
            startActivity(new Intent(this, ATLinkageAddActivity.class));
        });
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            refreshLayout.finishRefresh(2000);
            getLocalUserSceneList();
        });
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
                .setBackground(getResources().getDrawable(R.drawable.at_shape_18px_6pxffffff_999999))
                .setText(getString(R.string.at_delete))
                .setTextColor(Color.WHITE)
                .setWidth(width)
                .setHeight(height);
        swipeRightMenu.addMenuItem(closeItem); // 添加一个按钮到右侧菜单。
    };

    /**
     * RecyclerView的Item的Menu点击监听。
     */
    private SwipeMenuItemClickListener mMenuItemClickListener = menuBridge -> {
        menuBridge.closeMenu();
        current_position = menuBridge.getAdapterPosition();
        sceneDelete();
    };

    @Override
    public void requestResult(String result, String url) {
        try {
            org.json.JSONObject jsonResult = new org.json.JSONObject(result);
            if (ATConstants.Config.SERVER_URL_SCENEINSTANCERUN.equals(url)) {
                mLinkageCustomRVAdapter.setShowing(current_position, ATSceneManualAutoBean.NOTSHOW);
                if ("200".equals(jsonResult.getString("code"))) {
                    showToast(getString(R.string.at_perform_scene_success));
                }
            }
            if ("200".equals(jsonResult.getString("code"))) {
                switch (url) {
                    case ATConstants.Config.SERVER_URL_HOUSEDEVICE:
                        String allDeviceData = jsonResult.getString("data");
                        ATGlobalApplication.setAllDeviceData(allDeviceData);
                        break;
                    case ATConstants.Config.SERVER_URL_SCENEDELETE:
                        closeBaseProgressDlg();
                        mSceneManualAutoBeanList.remove(current_position);
                        mLinkageCustomRVAdapter.remove(current_position);
                        showToast(getString(R.string.at_delete_scene_success));
                        break;
                    case ATConstants.Config.SERVER_URL_SCENEDEPLOYREVOKE:
//                        showToast(getString(R.string.perform_scene_success));
                        break;
                    case ATConstants.Config.SERVER_URL_GETLOCALUSERSCENELIST:
                        mSceneManualAutoBeanList = gson.fromJson(jsonResult.getString("data"), new TypeToken<List<ATSceneManualAutoBean>>() {
                        }.getType());
                        llEmpty.setVisibility(mSceneManualAutoBeanList.size() == 0 ? View.VISIBLE : View.GONE);
                        mLinkageCustomRVAdapter.setLists(mSceneManualAutoBeanList);
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
    protected void onResume() {
        smartRefreshLayout.autoRefresh();
        super.onResume();
    }
}