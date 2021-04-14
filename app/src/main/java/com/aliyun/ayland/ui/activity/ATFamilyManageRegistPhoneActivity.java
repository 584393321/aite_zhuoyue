package com.aliyun.ayland.ui.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.ayland.base.ATBaseActivity;
import com.aliyun.ayland.contract.ATMainContract;
import com.aliyun.ayland.data.ATFamilyMemberBean;
import com.aliyun.ayland.data.ATHouseBean;
import com.aliyun.ayland.global.ATGlobalApplication;
import com.aliyun.ayland.global.ATConstants;
import com.aliyun.ayland.presenter.ATMainPresenter;
import com.aliyun.ayland.widget.titlebar.ATMyTitleBar;
import com.anthouse.wyzhuoyue.R;

import org.json.JSONException;

public class ATFamilyManageRegistPhoneActivity extends ATBaseActivity implements View.OnClickListener, ATMainContract.View {
    private static final int REQUEST_CODE_CHANGED = 0x1001;
    private ATMainPresenter mPresenter;
    private Button button;
    private ATMyTitleBar titleBar;
    private EditText etPhone;
    private RelativeLayout rlNoPhone;
    private String phone;
    private ATHouseBean houseBean;

    @Override
    protected int getLayoutId() {
        return R.layout.at_activity_family_manage_phone;
    }

    @Override
    protected void findView() {
        titleBar = findViewById(R.id.titlebar);
        etPhone = findViewById(R.id.et_phone);
        rlNoPhone = findViewById(R.id.rl_no_phone);
        button = findViewById(R.id.button);
        rlNoPhone.setOnClickListener(this);
        button.setOnClickListener(this);
        init();
    }

    @Override
    protected void initPresenter() {
        mPresenter = new ATMainPresenter(this);
        mPresenter.install(this);
    }

    private void findPerson() {
        showBaseProgressDlg();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("buildingCode", houseBean.getBuildingCode());
        jsonObject.put("villageId", houseBean.getVillageId());
        jsonObject.put("phone", phone);
        mPresenter.request(ATConstants.Config.SERVER_URL_FINDPERSON, jsonObject);
    }

    private void init() {
        houseBean = gson.fromJson(ATGlobalApplication.getHouse(), ATHouseBean.class);
    }

    /**
     * 验证手机格式
     */
    private boolean isMobileNO(String mobiles) {
        String telRegex = "[1][3456789]\\d{9}";
        return !TextUtils.isEmpty(mobiles) && mobiles.matches(telRegex);
    }

    @Override
    public void onClick(View view) {
        if (R.id.rl_no_phone == view.getId()) {
            startActivityForResult(new Intent(this, ATFamilyManageRegistIdActivity.class), REQUEST_CODE_CHANGED);
        } else if (R.id.button == view.getId()) {
            phone = etPhone.getText().toString();
            if (!TextUtils.isEmpty(phone) && !isMobileNO(phone)) {
                showToast(getString(R.string.at_input_correct_phone));
            } else {
                findPerson();
            }
        }
    }

    @Override
    public void requestResult(String result, String url) {
        try {
            org.json.JSONObject jsonResult = new org.json.JSONObject(result);
            switch (url) {
                case ATConstants.Config.SERVER_URL_FINDPERSON:
                    switch (jsonResult.getString("code")) {
                        case "200":
                            ATFamilyMemberBean familyMenber = gson.fromJson(jsonResult.getString("data"), ATFamilyMemberBean.class);
                            startActivityForResult(new Intent(this, ATFamilyManageRegistActivity.class)
                                    .putExtra("FamilyMemberBean", familyMenber)
                                    .putExtra("type", "phone")
                                    .putExtra("phone", phone), REQUEST_CODE_CHANGED);
                            break;
                        case "10124":
                            showToast(jsonResult.getString("message"));
                            //家庭成员已登记
                            break;
                        case "10119":
                            showToast(jsonResult.getString("message"));
                            //家庭成员已登记
                            break;
                        case "10125":
                            //人员记录不存在
                            startActivityForResult(new Intent(this, ATFamilyManageRegistActivity.class)
                                    .putExtra("type", "phone")
                                    .putExtra("phone", phone), REQUEST_CODE_CHANGED);
                            break;
                    }
                    closeBaseProgressDlg();
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_CHANGED) {
            finish();
        }
    }
}