package com.aliyun.ayland.ui.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.ayland.base.ATBaseActivity;
import com.aliyun.ayland.contract.ATMainContract;
import com.aliyun.ayland.data.ATSceneBean;
import com.aliyun.ayland.global.ATGlobalApplication;
import com.aliyun.ayland.global.ATConstants;
import com.aliyun.ayland.presenter.ATMainPresenter;
import com.aliyun.ayland.ui.adapter.ATLinkagePerformSceneRVAdapter;
import com.anthouse.wyzhuoyue.R;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class ATLinkagePerformSceneActivity extends ATBaseActivity implements ATMainContract.View {
    private ATMainPresenter mPresenter;
    private int pageNo = 1;
    private ATLinkagePerformSceneRVAdapter mLinkagePerformSceneRVAdapter;
    private List<ATSceneBean> mSceneBeanList = new ArrayList<>();
    private SmartRefreshLayout smartRefreshLayout;
    private RecyclerView rvPerformScene;

    @Override
    protected int getLayoutId() {
        return R.layout.at_activity_linkage_perform_scene;
    }

    @Override
    protected void findView() {
        smartRefreshLayout = findViewById(R.id.smartRefreshLayout);
        rvPerformScene = findViewById(R.id.rv_perform_scene);
        init();
    }

    @Override
    protected void initPresenter() {
        mPresenter = new ATMainPresenter(this);
        mPresenter.install(this);

        showBaseProgressDlg();
        userSceneList();
    }

    private void userSceneList() {
        JSONObject operator = new JSONObject();
        operator.put("hid", ATGlobalApplication.getHid());
        operator.put("hidType", "OPEN");
        JSONObject target = new JSONObject();
        target.put("hid", ATGlobalApplication.getHid());
        target.put("hidType", "OPEN");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("operator", operator);
        jsonObject.put("target", target);
        jsonObject.put("pageNo", pageNo);
        jsonObject.put("pageSize", 10);
        jsonObject.put("iotToken", ATGlobalApplication.getIoTToken());
        mPresenter.request(ATConstants.Config.SERVER_URL_USERSCENELIST, jsonObject);
    }

    private void init() {

        mLinkagePerformSceneRVAdapter = new ATLinkagePerformSceneRVAdapter(this);
        rvPerformScene.setLayoutManager(new LinearLayoutManager(this));
        rvPerformScene.setAdapter(mLinkagePerformSceneRVAdapter);

        mLinkagePerformSceneRVAdapter.setOnRVClickListener((view, position) -> {
            JSONObject params = new JSONObject();
            params.put("sceneId", mSceneBeanList.get(position).getSceneId());
            setResult(RESULT_OK, getIntent()
                    .putExtra("content", mSceneBeanList.get(position).getName())
                    .putExtra("uri", "action/scene/trigger")
                    .putExtra("name", getString(R.string.at_perform_scene))
                    .putExtra("params", params.toJSONString()));
            finish();
        });
        smartRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                refreshLayout.finishLoadMore(2000);
                pageNo++;
                userSceneList();
            }

            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                refreshLayout.finishRefresh(2000);
                pageNo = 1;
                userSceneList();
                smartRefreshLayout.setNoMoreData(false);
            }
        });
    }

    @Override
    public void requestResult(String result, String url) {
        try {
            org.json.JSONObject jsonResult = new org.json.JSONObject(result);
            if ("200".equals(jsonResult.getString("code"))) {
                switch (url) {
                    case ATConstants.Config.SERVER_URL_USERSCENELIST:
                        List<ATSceneBean> sceneBeanList = gson.fromJson(jsonResult.getJSONObject("data").getString("data"), new TypeToken<List<ATSceneBean>>() {
                        }.getType());
                        if (pageNo != 1 && sceneBeanList.size() == 0) {
                            pageNo--;
                            smartRefreshLayout.setNoMoreData(true);
                            return;
                        }
                        mSceneBeanList.addAll(sceneBeanList);
                        mLinkagePerformSceneRVAdapter.setList(mSceneBeanList);
                        break;
                }
            } else {
                showToast(jsonResult.getString("message"));
            }
            smartRefreshLayout.finishRefresh();
            smartRefreshLayout.finishLoadMore();
            closeBaseProgressDlg();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}