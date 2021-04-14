package com.aliyun.ayland.ui.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.ayland.base.ATBaseFragment;
import com.aliyun.ayland.contract.ATMainContract;
import com.aliyun.ayland.data.ATEventInteger;
import com.aliyun.ayland.data.ATHouseBean;
import com.aliyun.ayland.data.ATUserFaceCheckBean;
import com.aliyun.ayland.data.ATUserFaceCheckListBean;
import com.aliyun.ayland.global.ATGlobalApplication;
import com.aliyun.ayland.global.ATConstants;
import com.aliyun.ayland.presenter.ATMainPresenter;
import com.aliyun.ayland.ui.activity.ATUserFaceActivity;
import com.aliyun.ayland.ui.adapter.ATUserFaceCheckRvAdapter;
import com.anthouse.wyzhuoyue.R;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;


public class ATUserFaceCheckFragment extends ATBaseFragment implements ATMainContract.View {
    private ATMainPresenter mPresenter;
    private ATUserFaceCheckRvAdapter mUserFaceCheckRvAdapter;
    private List<ATUserFaceCheckListBean> allUserFaceCheckList = new ArrayList<>();
    private ATHouseBean houseBean;
    private RecyclerView recyclerView;

    @Override
    protected int getLayoutId() {
        return R.layout.at_fragment_user_face_check;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ATEventInteger eventInteger) {
        if ("UserFaceCheckFragment".equals(eventInteger.getClazz())) {
            showBaseProgressDlg();
            addUserFaceVillage((ATUserFaceCheckBean) mUserFaceCheckRvAdapter.getData().get(eventInteger.getPosition()));
        }
    }

    @Override
    protected void findView(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        EventBus.getDefault().register(this);
        init();
    }

    @Override
    protected void initPresenter() {
        mPresenter = new ATMainPresenter(this);
        mPresenter.install(getActivity());
    }

    private void faceVillageList() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("personCode", ATGlobalApplication.getATLoginBean().getPersonCode());
        mPresenter.request(ATConstants.Config.SERVER_URL_FACEVILLAGELIST, jsonObject);
    }

    private void addUserFaceVillage(ATUserFaceCheckBean userFaceCheckBean) {
        JSONObject jsonObject = new JSONObject();
        JSONArray scopeIdList = new JSONArray();
        scopeIdList.add(userFaceCheckBean.getDeviceId());
        JSONArray userIdList = new JSONArray();
        userIdList.add(ATGlobalApplication.getHid());
        jsonObject.put("scopeIdList", scopeIdList);
        jsonObject.put("userIdList", userIdList);
        jsonObject.put("scopeType", "IOT_ID");
        jsonObject.put("userType", "OPEN");
        jsonObject.put("villageId", houseBean.getVillageId());
        jsonObject.put("deviceType", TextUtils.isEmpty(userFaceCheckBean.getDeviceType()) ? 1 : 2);
        mPresenter.request(ATConstants.Config.SERVER_URL_ADDUSERFACEVILLAGE, jsonObject);
    }

    private void init() {
        houseBean = gson.fromJson(ATGlobalApplication.getHouse(), ATHouseBean.class);
        mUserFaceCheckRvAdapter = new ATUserFaceCheckRvAdapter();
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager1);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mUserFaceCheckRvAdapter);
    }

    public void setJsonResult(org.json.JSONObject jsonResult) {
        try {
            allUserFaceCheckList = gson.fromJson(jsonResult.getString("data"), new TypeToken<List<ATUserFaceCheckListBean>>() {
            }.getType());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mUserFaceCheckRvAdapter.setList(allUserFaceCheckList, !TextUtils.isEmpty(ATUserFaceActivity.userId));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void requestResult(String result, String url) {
        try {
            org.json.JSONObject jsonResult = new org.json.JSONObject(result);
            if ("200".equals(jsonResult.getString("code"))) {
                switch (url) {
                    case ATConstants.Config.SERVER_URL_ADDUSERFACEVILLAGE:
                        faceVillageList();
                        break;
                    case ATConstants.Config.SERVER_URL_FACEVILLAGELIST:
                        setJsonResult(jsonResult);
                        break;
                }
            } else {
//                showToast(jsonResult.getString("message"));
            }
            closeBaseProgressDlg();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}