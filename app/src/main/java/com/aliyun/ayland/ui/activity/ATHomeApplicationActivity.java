package com.aliyun.ayland.ui.activity;

import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.ayland.base.ATBaseActivity;
import com.aliyun.ayland.base.autolayout.util.ATAutoUtils;
import com.aliyun.ayland.contract.ATMainContract;
import com.aliyun.ayland.data.ATApplicationBean;
import com.aliyun.ayland.global.ATGlobalApplication;
import com.aliyun.ayland.global.ATConstants;
import com.aliyun.ayland.presenter.ATMainPresenter;
import com.aliyun.ayland.ui.adapter.ATHomeShortcutEditRVAdapter;
import com.aliyun.ayland.widget.ATRecycleViewItemDecoration;
import com.aliyun.ayland.widget.titlebar.ATMyTitleBar;
import com.anthouse.wyzhuoyue.R;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.touch.OnItemMoveListener;
import com.yanzhenjie.recyclerview.swipe.touch.OnItemStateChangedListener;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class ATHomeApplicationActivity extends ATBaseActivity implements ATMainContract.View {
    private ATMainPresenter mPresenter;
    private List<ATApplicationBean> mApplicationList = new ArrayList<>();
    private List<ATApplicationBean> mTempRoomList = new ArrayList<>();
    private ATHomeShortcutEditRVAdapter mHomeShortcutEditRVAdapter, mHomeShortcutEditSMRVAdapter;
    private boolean mEditable = false;
    private ATMyTitleBar titleBar;
    private RecyclerView recyclerView;
    private SwipeMenuRecyclerView swipeMenuRecyclerView;

    @Override
    protected int getLayoutId() {
        return R.layout.at_activity_home_application;
    }

    @Override
    protected void findView() {
        titleBar = findViewById(R.id.titlebar);
        recyclerView = findViewById(R.id.recyclerView);
        swipeMenuRecyclerView = findViewById(R.id.swipeMenuRecyclerView);
        init();
    }

    @Override
    protected void initPresenter() {
        mPresenter = new ATMainPresenter(this);
        mPresenter.install(this);
    }

    private void init() {
        mApplicationList = getIntent().getParcelableArrayListExtra("mApplicationList");
        mTempRoomList.addAll(mApplicationList);
        titleBar.setSendText(getString(R.string.at_edit));
        titleBar.setPublishClickListener(() -> {
            if (mEditable) {
                mApplicationList.clear();
                mApplicationList.addAll(mHomeShortcutEditSMRVAdapter.getList());
                mHomeShortcutEditRVAdapter.setLists(mHomeShortcutEditSMRVAdapter.getList());
                updateAppSort();
            } else {
                recyclerView.setVisibility(View.GONE);
                swipeMenuRecyclerView.setVisibility(View.VISIBLE);
                titleBar.setSendText(getString(R.string.at_done));
            }
            mEditable = !mEditable;
        });
//
//        titleBar.setPublishClickListener(() -> {
//            if (mEditable) {
//                mEditable = false;
//                recyclerView.setVisibility(View.VISIBLE);
//                swipeMenuRecyclerView.setVisibility(View.GONE);
//                titleBar.setRightButtonText(getString(R.string.edit));
//
//                mHomeShortcutEditRVAdapter.setLists(mApplicationList);
//                mHomeShortcutEditSMRVAdapter.setLists(mApplicationList);
//                mTempRoomList.clear();
//                mTempRoomList.addAll(mApplicationList);
//            } else {
//                if (changed)
//                    setResult(RESULT_OK);
//                finish();
//            }
//        });

        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.addItemDecoration(new ATRecycleViewItemDecoration(ATAutoUtils.getPercentHeightSize(48)));
        mHomeShortcutEditRVAdapter = new ATHomeShortcutEditRVAdapter(swipeMenuRecyclerView, this, false);
        mHomeShortcutEditRVAdapter.setLists(mApplicationList);
        recyclerView.setAdapter(mHomeShortcutEditRVAdapter);

        swipeMenuRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mHomeShortcutEditSMRVAdapter = new ATHomeShortcutEditRVAdapter(swipeMenuRecyclerView, this, true);
        mHomeShortcutEditSMRVAdapter.setLists(mTempRoomList);

        swipeMenuRecyclerView.addItemDecoration(new ATRecycleViewItemDecoration(ATAutoUtils.getPercentHeightSize(48)));
        swipeMenuRecyclerView.setOnItemMoveListener(onItemMoveListener);// Item的拖拽/侧滑删除时，手指状态发生变化监听。
        swipeMenuRecyclerView.setOnItemStateChangedListener(mOnItemStateChangedListener); // 监听Item上的手指状态。
        swipeMenuRecyclerView.setLongPressDragEnabled(false); // 开启拖拽。
        swipeMenuRecyclerView.setItemViewSwipeEnabled(false); // 开启滑动删除。
        swipeMenuRecyclerView.setAdapter(mHomeShortcutEditSMRVAdapter);
    }

    private void updateAppSort() {
        showBaseProgressDlg();
        JSONArray applicationIdList = new JSONArray();
        for (ATApplicationBean applicationBean : mTempRoomList) {
            applicationIdList.add(applicationBean.getApplicationId());
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("personCode", ATGlobalApplication.getATLoginBean().getPersonCode());
        jsonObject.put("applicationIdList", applicationIdList);
        mPresenter.request(ATConstants.Config.SERVER_URL_UPDATEAPPSORT, jsonObject);
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
//            Collections.swap(mTempRoomList, fromPosition, toPosition);

            ATApplicationBean applicationBean = mTempRoomList.get(fromPosition);
            mTempRoomList.remove(fromPosition);
            mTempRoomList.add(toPosition, applicationBean);
            mHomeShortcutEditSMRVAdapter.notifyItemMoved(fromPosition, toPosition);
            return true;
        }

        @Override
        public void onItemDismiss(RecyclerView.ViewHolder srcHolder) {
            mApplicationList.remove(srcHolder.getAdapterPosition());
            mHomeShortcutEditSMRVAdapter.notifyItemRemoved(srcHolder.getAdapterPosition());
        }
    };

    /**
     * Item的拖拽/侧滑删除时，手指状态发生变化监听。
     */
    private OnItemStateChangedListener mOnItemStateChangedListener = (viewHolder, actionState) -> {
        if (actionState == OnItemStateChangedListener.ACTION_STATE_DRAG) {
//                当前状态（"状态：拖拽");
            // 拖拽的时候背景就透明了，这里我们可以添加一个特殊背景。
            viewHolder.itemView.setBackground(ContextCompat.getDrawable(ATHomeApplicationActivity.this, R.drawable.at_shape_18px_f2f0ef));
        } else if (actionState == OnItemStateChangedListener.ACTION_STATE_SWIPE) {
//                当前状态（"状态：滑动删除");
        } else if (actionState == OnItemStateChangedListener.ACTION_STATE_IDLE) {
//                当前状态（"状态：手指松开");
            // 在手松开的时候还原背景。
            ViewCompat.setBackground(viewHolder.itemView, ContextCompat.getDrawable(ATHomeApplicationActivity.this, R.drawable.at_selector_f5f5f5_eeeeee));
        }
    };

    @Override
    public void requestResult(String result, String url) {
        try {
            org.json.JSONObject jsonResult = new org.json.JSONObject(result);
            if ("200".equals(jsonResult.getString("code"))) {
                switch (url) {
                    case ATConstants.Config.SERVER_URL_UPDATEAPPSORT:
                        mApplicationList.clear();
                        mApplicationList.addAll(mTempRoomList);
                        mHomeShortcutEditRVAdapter.setLists(mApplicationList);
                        recyclerView.setVisibility(View.VISIBLE);
                        swipeMenuRecyclerView.setVisibility(View.GONE);
                        titleBar.setSendText(getString(R.string.at_edit));
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