package com.aliyun.ayland.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.ayland.base.ATBaseActivity;
import com.aliyun.ayland.base.autolayout.util.ATAutoUtils;
import com.aliyun.ayland.contract.ATMainContract;
import com.aliyun.ayland.data.ATDeviceBean;
import com.aliyun.ayland.data.ATEventIntent;
import com.aliyun.ayland.data.ATHouseBean;
import com.aliyun.ayland.data.ATRoomBean1;
import com.aliyun.ayland.global.ATGlobalApplication;
import com.aliyun.ayland.global.ATConstants;
import com.aliyun.ayland.presenter.ATMainPresenter;
import com.aliyun.ayland.ui.adapter.ATManageRoomRVAdapter;
import com.aliyun.ayland.ui.adapter.ATManageRoomSMRVAdapter;
import com.aliyun.ayland.widget.titlebar.ATMyTitleBar;
import com.anthouse.wyzhuoyue.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.touch.OnItemMoveListener;
import com.yanzhenjie.recyclerview.swipe.touch.OnItemStateChangedListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ATManageRoomActivity extends ATBaseActivity implements ATMainContract.View {
    private ATMainPresenter mPresenter;
    private ATManageRoomRVAdapter mManageRoomRVAdapter;
    private ATManageRoomSMRVAdapter mManageRoomSMRVAdapter;
    private boolean mEditable = false;
    private List<ATRoomBean1> mRoomNameList = new ArrayList<>();
    private List<ATRoomBean1> mTempRoomList = new ArrayList<>();
    private JSONArray deleteList = new JSONArray();
    private JSONObject mAllDevice;
    private int mCurrentPosition = 0;
    private ATHouseBean houseBean;
    private String mAllDeviceData;
    private ATHouseBean mHouseBean;
    private ATMyTitleBar titleBar;
    private RecyclerView rvManageRoom;
    private SwipeMenuRecyclerView smrvManageRoom;

    @Override
    protected int getLayoutId() {
        return R.layout.at_activity_manage_room;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ATEventIntent eventIntent) {
        if ("ManageRoomActivity".equals(eventIntent.getClazz())) {
            mAllDeviceData = eventIntent.getData().getStringExtra("allDeviceData");
            mAllDevice = JSON.parseObject(mAllDeviceData);
            String room_name = eventIntent.getData().getStringExtra("room_name");
            if (!TextUtils.isEmpty(room_name)) {
                String iotSpaceId = eventIntent.getData().getStringExtra("iotSpaceId");
                String room_type = eventIntent.getData().getStringExtra("room_type");
                if (!TextUtils.isEmpty(iotSpaceId)) {
                    //????????????
                    ATRoomBean1 roomBean = new ATRoomBean1();
                    roomBean.setName(room_name);
                    roomBean.setType(room_type);
                    roomBean.setIotSpaceId(iotSpaceId);
                    roomBean.setOrderCode(mRoomNameList.size() + "");
                    mRoomNameList.add(roomBean);
                } else {
                    mRoomNameList.get(mCurrentPosition).setName(room_name);
                    mRoomNameList.get(mCurrentPosition).setType(room_type);
                }
                mTempRoomList.clear();
                mTempRoomList.addAll(mRoomNameList);
                mManageRoomRVAdapter.setLists(mRoomNameList);
                mManageRoomSMRVAdapter.setLists(mTempRoomList);
            }
        }
    }

    @Override
    protected void findView() {
        titleBar = findViewById(R.id.titlebar);
        rvManageRoom = findViewById(R.id.rv_manage_room);
        smrvManageRoom = findViewById(R.id.smrv_manage_room);
        EventBus.getDefault().register(this);
        init();
    }

    @Override
    protected void initPresenter() {
        mPresenter = new ATMainPresenter(this);
        mPresenter.install(this);

        mAllDeviceData = getIntent().getStringExtra("allDevice");
        if (TextUtils.isEmpty(mAllDeviceData)) {
            request();
        } else {
            mRoomNameList = (List<ATRoomBean1>) getIntent().getSerializableExtra("mRoomNameList");
            mAllDevice = JSON.parseObject(mAllDeviceData);
            mRoomNameList.remove(0);
            mTempRoomList.addAll(mRoomNameList);
            mManageRoomRVAdapter.setLists(mRoomNameList);
            mManageRoomSMRVAdapter.setLists(mTempRoomList);
        }
    }

    private void request() {
        mHouseBean = gson.fromJson(ATGlobalApplication.getHouse(), ATHouseBean.class);
        if (mHouseBean == null)
            return;
        showBaseProgressDlg();
        findOrderRoom();
        getHouseDevice();
    }

    private void findOrderRoom() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("spaceId", mHouseBean.getIotSpaceId());
        jsonObject.put("villageId", mHouseBean.getVillageId());
        jsonObject.put("iotToken", ATGlobalApplication.getIoTToken());
        jsonObject.put("personCode", ATGlobalApplication.getATLoginBean().getPersonCode());
        mPresenter.request(ATConstants.Config.SERVER_URL_FINDORDERROOM, jsonObject);
    }

    private void getHouseDevice() {
        JSONObject operator = new JSONObject();
        operator.put("hid", ATGlobalApplication.getHid());
        operator.put("hidType", "OPEN");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("operator", operator);
        jsonObject.put("iotSpaceId", mHouseBean.getIotSpaceId());
        jsonObject.put("iotToken", ATGlobalApplication.getIoTToken());
        jsonObject.put("username", ATGlobalApplication.getAccount());
        mPresenter.request(ATConstants.Config.SERVER_URL_HOUSEDEVICE, jsonObject);
    }

    private void init() {
        titleBar.setRightButtonText(getString(R.string.at_edit));
        titleBar.setRightClickListener(() -> {
            if (mEditable) {
                editRoom();
            } else {
                rvManageRoom.setVisibility(View.GONE);
                smrvManageRoom.setVisibility(View.VISIBLE);
                titleBar.setRightButtonText(getString(R.string.at_done));
            }
            mEditable = !mEditable;
        });

        titleBar.setTitleBarClickBackListener(() -> {
            if (mEditable) {
                mEditable = false;
//                llAddRoom.setVisibility(View.VISIBLE);
                rvManageRoom.setVisibility(View.VISIBLE);
                smrvManageRoom.setVisibility(View.GONE);
                titleBar.setRightButtonText(getString(R.string.at_edit));
                mManageRoomRVAdapter.setLists(mRoomNameList);
                mManageRoomSMRVAdapter.setLists(mRoomNameList);
                deleteList.clear();
                mTempRoomList.clear();
                mTempRoomList.addAll(mRoomNameList);
            } else {
                finish();
            }
        });
        rvManageRoom.setLayoutManager(new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        mManageRoomRVAdapter = new ATManageRoomRVAdapter(this);
        mManageRoomRVAdapter.setOnRVClickListener((view, position) -> {
            if(mAllDevice == null){
                request();
                return;
            }
            if (position == mRoomNameList.size()) {
                startActivity(new Intent(ATManageRoomActivity.this, ATRoomPicActivity.class)
                        .putExtra("allDeviceData", mAllDeviceData));
                return;
            }
            mCurrentPosition = position;
            Intent intent = new Intent();
            if (mAllDevice.containsKey(mRoomNameList.get(position).getIotSpaceId())) {
                Gson gson = new Gson();
                List<ATDeviceBean> deviceBeanList = gson.fromJson(mAllDevice.getString(mRoomNameList.get(position).getIotSpaceId()), new TypeToken<List<ATDeviceBean>>() {
                }.getType());
                intent.putExtra("deviceBeanList", (Serializable) deviceBeanList);
            }
            intent.putExtra("allDeviceData", mAllDeviceData);
            intent.putExtra("roombean", mTempRoomList.get(position));
            intent.setClass(ATManageRoomActivity.this, ATEditRoomActivity.class);
            startActivity(intent);
        });
        mManageRoomRVAdapter.setLists(mRoomNameList);
        rvManageRoom.setAdapter(mManageRoomRVAdapter);
        rvManageRoom.setNestedScrollingEnabled(false);

        mManageRoomSMRVAdapter = new ATManageRoomSMRVAdapter(smrvManageRoom, this);
        smrvManageRoom.setLayoutManager(new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        smrvManageRoom.setOnItemMoveListener(onItemMoveListener);// Item?????????/???????????????????????????????????????????????????
        smrvManageRoom.setOnItemStateChangedListener(mOnItemStateChangedListener); // ??????Item?????????????????????
        smrvManageRoom.setSwipeMenuCreator(mSwipeMenuCreator);
        smrvManageRoom.setSwipeMenuItemClickListener(mMenuItemClickListener); // Item???Menu?????????
        smrvManageRoom.setLongPressDragEnabled(false); // ???????????????
        smrvManageRoom.setItemViewSwipeEnabled(false); // ?????????????????????
        smrvManageRoom.setAdapter(mManageRoomSMRVAdapter);
        smrvManageRoom.setNestedScrollingEnabled(false);

//        llAddRoom.setOnClickListener(view -> {
//            startActivity(new Intent(this, RoomPicActivity.class)
//                    .putExtra("allDeviceData", mAllDeviceData));
//        });
    }

    private void editRoom() {
        if (!TextUtils.isEmpty(ATGlobalApplication.getHouse())) {
            houseBean = gson.fromJson(ATGlobalApplication.getHouse(), ATHouseBean.class);
        }
        if (houseBean == null)
            return;
        showBaseProgressDlg();

        JSONObject operator = new JSONObject();
        operator.put("hid", ATGlobalApplication.getHid());
        operator.put("hidType", "OPEN");

        JSONArray orderList = new JSONArray();
        for (ATRoomBean1 roomBean : mTempRoomList) {
            orderList.add(roomBean.getIotSpaceId());
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("operator", operator);
        jsonObject.put("deleteList", deleteList);
        jsonObject.put("rootSpaceId", houseBean.getRootSpaceId());
        jsonObject.put("orderList", orderList);
        jsonObject.put("iotToken", ATGlobalApplication.getIoTToken());
        mPresenter.request(ATConstants.Config.SERVER_URL_DELETEROOM, jsonObject);
    }

    /**
     * ????????????????????????????????????UI???????????????
     */
    private OnItemMoveListener onItemMoveListener = new OnItemMoveListener() {
        @Override
        public boolean onItemMove(RecyclerView.ViewHolder srcHolder, RecyclerView.ViewHolder targetHolder) {
            // ?????????ViewType????????????????????????
            if (srcHolder.getItemViewType() != targetHolder.getItemViewType()) return false;

            int fromPosition = srcHolder.getAdapterPosition();
            int toPosition = targetHolder.getAdapterPosition();

            Collections.swap(mTempRoomList, fromPosition, toPosition);
            mManageRoomSMRVAdapter.notifyItemMoved(fromPosition, toPosition);
            return true;// ??????true??????????????????????????????????????????false??????????????????????????????????????????
        }

        @Override
        public void onItemDismiss(RecyclerView.ViewHolder srcHolder) {
            mTempRoomList.remove(srcHolder.getAdapterPosition());
//            mManageRoomSMRVAdapter.notifyItemRemoved(srcHolder.getAdapterPosition());
        }
    };

    /**
     * Item?????????/???????????????????????????????????????????????????
     */
    private OnItemStateChangedListener mOnItemStateChangedListener = new OnItemStateChangedListener() {
        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
            if (actionState == OnItemStateChangedListener.ACTION_STATE_DRAG) {
//                ???????????????"???????????????");
                // ?????????????????????????????????????????????????????????????????????????????????
                viewHolder.itemView.setBackgroundColor(ContextCompat.getColor(ATManageRoomActivity.this, R.color._CFCFCF));
            } else if (actionState == OnItemStateChangedListener.ACTION_STATE_SWIPE) {
//                ???????????????"?????????????????????");
            } else if (actionState == OnItemStateChangedListener.ACTION_STATE_IDLE) {
//                ???????????????"?????????????????????");
                // ????????????????????????????????????
                ViewCompat.setBackground(viewHolder.itemView, ContextCompat.getDrawable(ATManageRoomActivity.this, R.drawable.at_selector_ffffff_cfcfcf));
            }
        }
    };

    /**
     * ??????????????????
     */
    public SwipeMenuCreator mSwipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int viewType) {
            int width = ATAutoUtils.getPercentWidthSize(204);
            // 1. MATCH_PARENT ???????????????????????????Item?????????;
            // 2. ???????????????????????????80;
            // 3. WRAP_CONTENT???????????????????????????;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            SwipeMenuItem closeItem = new SwipeMenuItem(ATManageRoomActivity.this)
                    .setBackground(R.color._E83434)
                    .setText(getString(R.string.at_delete))
                    .setTextColor(Color.WHITE)
                    .setWidth(width)
                    .setHeight(height);
            swipeRightMenu.addMenuItem(closeItem); // ????????????????????????????????????
        }
    };

    /**
     * RecyclerView???Item???Menu???????????????
     */
    private SwipeMenuItemClickListener mMenuItemClickListener = new SwipeMenuItemClickListener() {
        @Override
        public void onItemClick(SwipeMenuBridge menuBridge) {
            menuBridge.closeMenu();
            int adapterPosition = menuBridge.getAdapterPosition(); // RecyclerView???Item???position???
            deleteList.add(mTempRoomList.get(adapterPosition).getIotSpaceId());
            mTempRoomList.remove(adapterPosition);

//            mManageRoomRVAdapter.setLists(mTempRoomList);
            mManageRoomSMRVAdapter.removeItem(adapterPosition);
//            mManageRoomSMRVAdapter.notifyItemRemoved(adapterPosition);
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mEditable) {
                mEditable = false;
                rvManageRoom.setVisibility(View.VISIBLE);
                smrvManageRoom.setVisibility(View.GONE);
                titleBar.setRightButtonText(getString(R.string.at_edit));

                mManageRoomRVAdapter.setLists(mRoomNameList);
                deleteList.clear();
                mTempRoomList.clear();
                mTempRoomList.addAll(mRoomNameList);

                mManageRoomSMRVAdapter.setLists(mTempRoomList);
            } else {
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void requestResult(String result, String url) {
        try {
            org.json.JSONObject jsonResult = new org.json.JSONObject(result);
            if ("200".equals(jsonResult.getString("code"))) {
                switch (url) {
                    case ATConstants.Config.SERVER_URL_DELETEROOM:
                        mRoomNameList.clear();
                        mRoomNameList.addAll(mTempRoomList);
                        deleteList.clear();
                        mManageRoomRVAdapter.setLists(mRoomNameList);
                        rvManageRoom.setVisibility(View.VISIBLE);
                        smrvManageRoom.setVisibility(View.GONE);
                        titleBar.setRightButtonText(getString(R.string.at_edit));
                        break;
                    case ATConstants.Config.SERVER_URL_HOUSEDEVICE:
                        mAllDeviceData = jsonResult.getString("data");
                        mAllDevice = JSON.parseObject(mAllDeviceData);
                        break;
                    case ATConstants.Config.SERVER_URL_FINDORDERROOM:
                        mRoomNameList = gson.fromJson(jsonResult.getString("data"), new TypeToken<List<ATRoomBean1>>() {
                        }.getType());
                        mTempRoomList.clear();
                        mTempRoomList.addAll(mRoomNameList);
                        mManageRoomRVAdapter.setLists(mRoomNameList);
                        mManageRoomSMRVAdapter.setLists(mTempRoomList);
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
        EventBus.getDefault().unregister(this);
    }
}