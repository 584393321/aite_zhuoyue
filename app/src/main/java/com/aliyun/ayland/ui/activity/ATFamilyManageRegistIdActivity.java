package com.aliyun.ayland.ui.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.widget.EditText;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.ayland.base.ATBaseActivity;
import com.aliyun.ayland.contract.ATMainContract;
import com.aliyun.ayland.data.ATFamilyMemberBean;
import com.aliyun.ayland.data.ATHouseBean;
import com.aliyun.ayland.global.ATGlobalApplication;
import com.aliyun.ayland.global.ATConstants;
import com.aliyun.ayland.presenter.ATMainPresenter;
import com.aliyun.ayland.utils.ATIDCardUtil;
import com.anthouse.wyzhuoyue.R;

import org.json.JSONException;

import java.text.ParseException;

public class ATFamilyManageRegistIdActivity extends ATBaseActivity implements ATMainContract.View {
    private static final int REQUEST_CODE_CHANGED = 0x1001;
    private ATMainPresenter mPresenter;
    private EditText etId;
    private String id_number;
    private ATHouseBean houseBean;

    @Override
    protected int getLayoutId() {
        return R.layout.at_activity_family_manage_id;
    }

    @Override
    protected void findView() {
        etId = findViewById(R.id.et_id);
        findViewById(R.id.button).setOnClickListener(v -> {
            id_number = etId.getText().toString();
            if (TextUtils.isEmpty(id_number)) {
                showToast(getString(R.string.at_input_id_number));
                return;
            } else {
                try {
                    if (!ATIDCardUtil.IDCardValidate(id_number)) {
                        showToast("请输入正确的身份证号码");
                        return;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            findPerson();
        });
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
        jsonObject.put("idNumber", id_number);
        mPresenter.request(ATConstants.Config.SERVER_URL_FINDPERSON, jsonObject);
    }

    private void init() {
        houseBean = gson.fromJson(ATGlobalApplication.getHouse(), ATHouseBean.class);
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
                                    .putExtra("type", "id_number")
                                    .putExtra("id_number", id_number), REQUEST_CODE_CHANGED);
                            break;
                        case "10119":
                            //人员记录不存在
                            showToast(jsonResult.getString("message"));
                            break;
                        case "10125":
                            //家庭成员已登记
                            startActivityForResult(new Intent(this, ATFamilyManageRegistActivity.class)
                                    .putExtra("type", "id_number")
                                    .putExtra("id_number", id_number), REQUEST_CODE_CHANGED);
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
            setResult(RESULT_OK);
            finish();
        }
    }
}