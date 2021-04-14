package com.aliyun.ayland.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.ayland.base.ATBaseFragment;
import com.aliyun.ayland.contract.ATMainContract;
import com.aliyun.ayland.data.ATEventString;
import com.aliyun.ayland.data.ATSceneBean;
import com.aliyun.ayland.global.ATGlobalApplication;
import com.aliyun.ayland.global.ATConstants;
import com.aliyun.ayland.presenter.ATMainPresenter;
import com.aliyun.ayland.ui.adapter.ATLinkageSceneRvAdapter;
import com.anthouse.wyzhuoyue.R;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;

import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.aliyun.ayland.ui.fragment.ATLinkageFragment.REQUEST_CODE_EDIT_LINKAGE;


/**
 * Created by fr on 2017/12/19.
 */

public class ATLinkageFamilyFragment extends ATBaseFragment implements ATMainContract.View {
    private String TAG = "LinkageFamilyFragment";
    private int pageNo = 1;
    private ATMainContract.Presenter mPresenter;
    private ATLinkageSceneRvAdapter mLinkageSceneRVAdapter;
    private RecyclerView rvLinkageScene;
    private SmartRefreshLayout smartRefreshLayout;

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ATEventString eventString) {
        if ("LinkageFamilyFragment".equals(eventString.getClazz())) {
            sceneInstanceRun(eventString.getValue());
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.at_fragment_linkage_family;
    }

    @Override
    protected void findView(View view) {
        rvLinkageScene = view.findViewById(R.id.rv_linkage_scene);
        smartRefreshLayout = view.findViewById(R.id.smartRefreshLayout);
        init();
    }

    @Override
    protected void initPresenter() {
        mPresenter = new ATMainPresenter(this);
        mPresenter.install(getActivity());

        showBaseProgressDlg();
        userSceneList();
    }

    private void sceneInstanceRun(String sceneId) {
        showBaseProgressDlg();
        JSONObject operator = new JSONObject();
        operator.put("hid", ATGlobalApplication.getHid());
        operator.put("hidType", "OPEN");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("operator", operator);
        jsonObject.put("sceneId", sceneId);
        jsonObject.put("iotToken", ATGlobalApplication.getIoTToken());
        mPresenter.request(ATConstants.Config.SERVER_URL_SCENEINSTANCERUN, jsonObject);
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
        rvLinkageScene.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        mLinkageSceneRVAdapter = new ATLinkageSceneRvAdapter();
        rvLinkageScene.setAdapter(mLinkageSceneRVAdapter);
        rvLinkageScene.setNestedScrollingEnabled(false);

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
                smartRefreshLayout.setNoMoreData(false);
                smartRefreshLayout.setEnableAutoLoadMore(true);
                pageNo = 1;
                userSceneList();
            }
        });
    }

    @Override
    public void requestResult(String result, String url) {
        try {
            org.json.JSONObject jsonResult = new org.json.JSONObject(result);
            if ("200".equals(jsonResult.getString("code"))) {
                switch (url) {
                    case ATConstants.Config.SERVER_URL_SCENEINSTANCERUN:
                        showToast(getString(R.string.at_perform_scene_success));
                        break;
                    case ATConstants.Config.SERVER_URL_USERSCENELIST:
                        List<ATSceneBean> sceneBeanList = gson.fromJson(jsonResult.getJSONObject("data").getString("data"), new TypeToken<List<ATSceneBean>>() {
                        }.getType());
                        if (pageNo != 1 && sceneBeanList.size() == 0) {
                            pageNo--;
                            smartRefreshLayout.setNoMoreData(true);
                            smartRefreshLayout.setEnableAutoLoadMore(false);
                            return;
                        }
                        mLinkageSceneRVAdapter.setLists(sceneBeanList, pageNo);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_EDIT_LINKAGE) {
            smartRefreshLayout.setEnableAutoLoadMore(true);
            smartRefreshLayout.setNoMoreData(false);
            pageNo = 1;
            userSceneList();
        }
    }
}