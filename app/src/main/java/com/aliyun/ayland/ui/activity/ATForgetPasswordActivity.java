package com.aliyun.ayland.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.ayland.base.ATBaseActivity;
import com.aliyun.ayland.contract.ATMainContract;
import com.aliyun.ayland.global.ATConstants;
import com.aliyun.ayland.presenter.ATMainPresenter;
import com.aliyun.ayland.widget.ATChoiseContryDialog;
import com.anthouse.wyzhuoyue.R;

import org.json.JSONException;

import java.util.Arrays;

public class ATForgetPasswordActivity extends ATBaseActivity implements ATMainContract.View {
    private static final int MSG_TIME_CLICK = 0x1001;
    private ATMainPresenter mPresenter;
    private String[] areaCodes;
    private String areaCode = "86";
    private ATChoiseContryDialog areaDialog;
    private String phone = "", code = "", password;
    private int time;
    private RelativeLayout rlSelectCountry;
    private Button btnSubmit;
    private TextView tvGetCode, tvCountry;
    private EditText etCode, etPhone, etPassword;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_TIME_CLICK:
                    --time;
                    if (time == 0) {
                        tvGetCode.setClickable(true);
                        tvGetCode.setText(getResources().getString(R.string.at_get_code_again));
                        tvGetCode.setTextColor(getResources().getColor(R.color._775422));
                        tvGetCode.setBackground(getResources().getDrawable(R.drawable.at_shape_45px_fce5cc_to_fef2d5));
                    } else {
                        tvGetCode.setText(String.format(getString(R.string.at_get_code_after), time));
                        mHandler.sendEmptyMessageDelayed(MSG_TIME_CLICK, 1000);
                        tvGetCode.setTextColor(getResources().getColor(R.color._96775422));
                        tvGetCode.setBackground(getResources().getDrawable(R.drawable.at_shape_45px_f0f0f0));
                    }
                    break;
            }
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.at_activity_forget_password;
    }

    @Override
    protected void findView() {
        rlSelectCountry = findViewById(R.id.rl_select_country);
        tvCountry = findViewById(R.id.tv_country);
        btnSubmit = findViewById(R.id.btn_submit);
        tvGetCode = findViewById(R.id.tv_get_code);
        etCode = findViewById(R.id.et_code);
        etPhone = findViewById(R.id.et_phone);
        etPassword = findViewById(R.id.et_password);
        init();
    }

    @Override
    protected void initPresenter() {
        mPresenter = new ATMainPresenter(this);
        mPresenter.install(this);
    }

    private void init() {
        String[] areaArray = getResources().getStringArray(R.array.area_array);
        areaCodes = getResources().getStringArray(R.array.area_code);
        areaCode = areaCodes[0];
        areaDialog = new ATChoiseContryDialog(this, Arrays.asList(areaArray), (view, s, position) -> {
            tvCountry.setText(s);
            tvCountry.setTextColor(getResources().getColor(R.color._505050));
            areaCode = areaCodes[position];
            areaDialog.dismiss();
        });
        rlSelectCountry.setOnClickListener(view -> areaDialog.show());
        tvGetCode.setOnClickListener(view -> {
                    phone = etPhone.getText().toString();
                    if (!isMobileNO(phone)) {
                        showToast(getString(R.string.at_input_correct_phone));
                        return;
                    }
                    showBaseProgressDlg();
                    getMpasswdcode();
                }
        );
        btnSubmit.setOnClickListener(view -> {
                    phone = etPhone.getText().toString();
                    if (!isMobileNO(phone)) {
                        showToast(getString(R.string.at_input_correct_phone));
                        return;
                    }
                    code = etCode.getText().toString();
                    if (TextUtils.isEmpty(code)) {
                        showToast(getString(R.string.at_empty_code));
                        return;
                    }
                    password = etPassword.getText().toString();
                    if (TextUtils.isEmpty(password)) {
                        showToast(getString(R.string.at_empty_password));
                        return;
                    }
                    showBaseProgressDlg();
                    resetPasswd();
                }
        );
    }

    private void resetPasswd() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("passwd", password);
        jsonObject.put("phoneCode", code);
        jsonObject.put("phone", phone);
        mPresenter.request(ATConstants.Config.SERVER_URL_RESETPASSWD, jsonObject);
    }

    private void getMpasswdcode() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("phone", phone);
        jsonObject.put("areacode", areaCode);
        jsonObject.put("codetype", "atte");
        mPresenter.request(ATConstants.Config.SERVER_URL_GETMPASSWDCODE, jsonObject);
    }

    /**
     * 验证手机格式
     */
    private boolean isMobileNO(String mobiles) {
        String telRegex = "[1][3456789]\\d{9}";
        return !TextUtils.isEmpty(mobiles) && mobiles.matches(telRegex);
    }

    private void setCodeText() {
        time = 60;
        tvGetCode.setClickable(false);
        tvGetCode.setText(String.format(getString(R.string.at_get_code_after), time));
        tvGetCode.setTextColor(getResources().getColor(R.color._96775422));
        tvGetCode.setBackground(getResources().getDrawable(R.drawable.at_shape_45px_f0f0f0));
        mHandler.sendEmptyMessageDelayed(MSG_TIME_CLICK, 1000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void requestResult(String result, String url) {
        try {
            org.json.JSONObject jsonResult = new org.json.JSONObject(result);
            if ("200".equals(jsonResult.getString("code"))) {
                switch (url) {
                    case ATConstants.Config.SERVER_URL_RESETPASSWD:
                        closeBaseProgressDlg();
                        showToast(getString(R.string.at_change_password_success));
                        setResult(RESULT_OK, new Intent().putExtra("phone", phone));
                        finish();
                        break;
                    case ATConstants.Config.SERVER_URL_GETMPASSWDCODE:
                        showToast(getString(R.string.at_get_verification_code_success));
                        closeBaseProgressDlg();
                        setCodeText();
                        break;
                }
            } else {
                closeBaseProgressDlg();
                showToast(jsonResult.getString("message"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}