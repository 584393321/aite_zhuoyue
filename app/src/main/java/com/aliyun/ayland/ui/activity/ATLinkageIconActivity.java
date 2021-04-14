package com.aliyun.ayland.ui.activity;

import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.ayland.base.ATBaseActivity;
import com.aliyun.ayland.contract.ATMainContract;
import com.aliyun.ayland.data.ATSceneManualTitle;
import com.aliyun.ayland.global.ATGlobalApplication;
import com.aliyun.ayland.global.ATConstants;
import com.aliyun.ayland.presenter.ATMainPresenter;
import com.aliyun.ayland.ui.adapter.ATLinkageIconRVAdapter;
import com.aliyun.ayland.widget.titlebar.ATMyTitleBar;
import com.anthouse.wyzhuoyue.R;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;

import java.util.List;

public class ATLinkageIconActivity extends ATBaseActivity implements ATMainContract.View {
    private ATMainPresenter mPresenter;
    private String sceneId;
    private ATSceneManualTitle mSceneManualTitle;
    private List<String> mSceneIconList;
    private ATLinkageIconRVAdapter mLinkageIconRVAdapter;
    private int select_position = -1;
    private boolean change;
    private ATMyTitleBar titleBar;
    private RecyclerView rvRoomPic;

    @Override
    protected int getLayoutId() {
        return R.layout.at_activity_room_pic;
    }

    @Override
    protected void findView() {
        titleBar = findViewById(R.id.titlebar);
        rvRoomPic = findViewById(R.id.rv_room_pic);
        init();
    }

    @Override
    protected void initPresenter() {
        mPresenter = new ATMainPresenter(this);
        mPresenter.install(this);

        if (TextUtils.isEmpty(ATGlobalApplication.getAllSceneIcon())) {
            linkageImageList();
        } else {
            mSceneIconList = gson.fromJson(ATGlobalApplication.getAllSceneIcon(), new TypeToken<List<String>>() {
            }.getType());
            for (int i = 0; i < mSceneIconList.size(); i++) {
                if (mSceneManualTitle.getScene_icon().equals(mSceneIconList.get(i))) {
                    select_position = i;
                    break;
                }
            }
            mLinkageIconRVAdapter.setList(mSceneIconList, select_position);
        }
    }

    public void linkageImageList() {
        showBaseProgressDlg();
        JSONObject jsonObject = new JSONObject();
        mPresenter.request(ATConstants.Config.SERVER_URL_LINKAGEIMAGELIST, jsonObject);
    }

    public void baseinfoUpdate() {
        showBaseProgressDlg();
        JSONObject operator = new JSONObject();
        operator.put("hid", ATGlobalApplication.getHid());
        operator.put("hidType", "OPEN");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("sceneId", mSceneManualTitle.getScene_id());
        jsonObject.put("name", mSceneManualTitle.getName());
        jsonObject.put("icon", mSceneIconList.get(select_position));
        jsonObject.put("description", "");
        jsonObject.put("operator", operator);
        jsonObject.put("iotToken", ATGlobalApplication.getIoTToken());
        mPresenter.request(ATConstants.Config.SERVER_URL_BASEINFOUPDATE, jsonObject);
    }

    private void init() {
        mSceneManualTitle = getIntent().getParcelableExtra("SceneManualTitle");
        titleBar.setRightButtonText(getString(R.string.at_done));
        titleBar.setTitle(getString(R.string.at_choose_room_icon));
        titleBar.setRightClickListener(() -> {
            if (change) {
                if (TextUtils.isEmpty(mSceneManualTitle.getScene_id())) {
                    setResult(RESULT_OK, new Intent().putExtra("scene_icon", mSceneIconList.get(select_position)));
                    finish();
                } else {
                    baseinfoUpdate();
                }
            } else {
                finish();
            }
        });
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        rvRoomPic.setLayoutManager(gridLayoutManager);
        mLinkageIconRVAdapter = new ATLinkageIconRVAdapter(this);
        mLinkageIconRVAdapter.setOnItemClickListener((view, position) -> {
            select_position = position;
            change = true;
        });
        rvRoomPic.setAdapter(mLinkageIconRVAdapter);
    }

    @Override
    public void requestResult(String result, String url) {
        try {
            org.json.JSONObject jsonResult = new org.json.JSONObject(result);
            if ("200".equals(jsonResult.getString("code"))) {
                switch (url) {
                    case ATConstants.Config.SERVER_URL_LINKAGEIMAGELIST:
                        ATGlobalApplication.setAllSceneIcon(jsonResult.getString("data"));
                        mSceneIconList = gson.fromJson(jsonResult.getString("data"), new TypeToken<List<String>>() {
                        }.getType());
                        for (int i = 0; i < mSceneIconList.size(); i++) {
                            if (mSceneManualTitle.getScene_icon().equals(mSceneIconList.get(i))) {
                                select_position = i;
                                break;
                            }
                        }
                        mLinkageIconRVAdapter.setList(mSceneIconList, select_position);
                        break;
                    case ATConstants.Config.SERVER_URL_BASEINFOUPDATE:
                        setResult(RESULT_OK, new Intent().putExtra("scene_icon", mSceneIconList.get(select_position)));
                        finish();
                        break;
                }
            } else {
                showToast(jsonResult.getString("message"));
                select_position = -1;
                mLinkageIconRVAdapter.setList(mSceneIconList, select_position);
            }
            closeBaseProgressDlg();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}