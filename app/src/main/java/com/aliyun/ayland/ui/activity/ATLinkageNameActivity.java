package com.aliyun.ayland.ui.activity;

import android.content.Intent;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.ayland.base.ATBaseActivity;
import com.aliyun.ayland.contract.ATMainContract;
import com.aliyun.ayland.data.ATSceneManualTitle;
import com.aliyun.ayland.global.ATGlobalApplication;
import com.aliyun.ayland.global.ATConstants;
import com.aliyun.ayland.presenter.ATMainPresenter;
import com.aliyun.ayland.widget.ATClearEditText;
import com.aliyun.ayland.widget.titlebar.ATMyTitleBar;
import com.anthouse.wyzhuoyue.R;

import org.json.JSONException;

public class ATLinkageNameActivity extends ATBaseActivity implements ATMainContract.View {
    private ATMainPresenter mPresenter;
    private String sceneId;
    private ATSceneManualTitle mSceneManualTitle;
    private ATMyTitleBar titleBar;
    private ATClearEditText clearEditText;

    @Override
    protected int getLayoutId() {
        return R.layout.at_activity_linkage_name;
    }

    @Override
    protected void findView() {
        titleBar = findViewById(R.id.titlebar);
        clearEditText = findViewById(R.id.clearEditText);
        init();
    }

    @Override
    protected void initPresenter() {
        mPresenter = new ATMainPresenter(this);
        mPresenter.install(this);
    }

    public void baseinfoUpdate() {
        showBaseProgressDlg();

        JSONObject operator = new JSONObject();
        operator.put("hid", ATGlobalApplication.getHid());
        operator.put("hidType", "OPEN");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("sceneId", sceneId);
        jsonObject.put("name", clearEditText.getText());
        jsonObject.put("icon", TextUtils.isEmpty(mSceneManualTitle.getScene_icon()) ? "https://g.aliplus.com/scene_icons/default.png" : mSceneManualTitle.getScene_icon());
        jsonObject.put("description", "");
        jsonObject.put("operator", operator);
        jsonObject.put("iotToken", ATGlobalApplication.getIoTToken());
        mPresenter.request(ATConstants.Config.SERVER_URL_BASEINFOUPDATE, jsonObject);
    }

    private void init() {
        titleBar.setRightButtonText(getString(R.string.at_sure1));
        mSceneManualTitle = getIntent().getParcelableExtra("SceneManualTitle");
        sceneId = mSceneManualTitle.getScene_id();
        if (!TextUtils.isEmpty(mSceneManualTitle.getName())) {
            clearEditText.setText(mSceneManualTitle.getName());
            clearEditText.setSelection(mSceneManualTitle.getName().length());
        }

        titleBar.setRightClickListener(() -> {
            if (TextUtils.isEmpty(clearEditText.getText())) {
                showToast(getString(R.string.at_input_scene_name));
                return;
            }
            if (TextUtils.isEmpty(sceneId)) {
                setResult(RESULT_OK, new Intent().putExtra("scene_name", clearEditText.getText().toString()));
                finish();
            } else {
                baseinfoUpdate();
            }
        });
    }

    @Override
    public void requestResult(String result, String url) {
        try {
            org.json.JSONObject jsonResult = new org.json.JSONObject(result);
            if ("200".equals(jsonResult.getString("code"))) {
                switch (url) {
                    case ATConstants.Config.SERVER_URL_BASEINFOUPDATE:
                        setResult(RESULT_OK, new Intent().putExtra("scene_name", clearEditText.getText().toString()));
                        showToast(getString(R.string.at_edit_linkage_name_success));
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
}