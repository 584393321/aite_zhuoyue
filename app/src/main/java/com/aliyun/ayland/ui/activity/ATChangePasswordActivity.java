package com.aliyun.ayland.ui.activity;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.ayland.base.ATBaseActivity;
import com.aliyun.ayland.contract.ATMainContract;
import com.aliyun.ayland.global.ATGlobalApplication;
import com.aliyun.ayland.global.ATConstants;
import com.aliyun.ayland.presenter.ATMainPresenter;
import com.aliyun.ayland.utils.ATPreferencesUtils;
import com.aliyun.ayland.widget.titlebar.ATMyTitleBar;
import com.anthouse.wyzhuoyue.R;

import org.json.JSONException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ATChangePasswordActivity extends ATBaseActivity implements ATMainContract.View {
    private ATMainPresenter mPresenter;
    private int time;
    private String password;
    private ATMyTitleBar titleBar;
    private TextView tvGetCode, tvPhone;
    private EditText etNewPassword, etCode, etOldPassword, etConfirmPassword;
    @SuppressLint("HandlerLeak")
    private Handler handlertime = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    --time;
                    if (time == 0) {
                        tvGetCode.setClickable(true);
                        tvGetCode.setText(getResources().getString(R.string.at_get_code_again));
                        tvGetCode.setTextColor(getResources().getColor(R.color._775422));
                        tvGetCode.setBackground(getResources().getDrawable(R.drawable.at_shape_45px_fce5cc_to_fef2d5));
                    } else {
                        tvGetCode.setText(String.format(getString(R.string.at_get_code_after), time));
                        handlertime.sendEmptyMessageDelayed(1, 1000);
                        tvGetCode.setTextColor(getResources().getColor(R.color._96775422));
                        tvGetCode.setBackground(getResources().getDrawable(R.drawable.at_shape_45px_f0f0f0));
                    }
                    break;
            }
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.at_activity_change_password;
    }

    @Override
    protected void findView() {
        titleBar = findViewById(R.id.titlebar);
        tvPhone = findViewById(R.id.tv_phone);
        tvGetCode = findViewById(R.id.tv_get_code);
        etCode = findViewById(R.id.et_code);
        etOldPassword = findViewById(R.id.et_old_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        etNewPassword = findViewById(R.id.et_new_password);
        init();
    }

    @Override
    protected void initPresenter() {
        mPresenter = new ATMainPresenter(this);
        mPresenter.install(this);
    }

    private void resetPasswd() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("passwd", etNewPassword.getText().toString().trim());
        jsonObject.put("phoneCode", etCode.getText().toString());
        jsonObject.put("phone", tvPhone.getText().toString());
        mPresenter.request(ATConstants.Config.SERVER_URL_RESETPASSWD, jsonObject);
    }

    private void getMpasswdcode() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("phone", tvPhone.getText().toString());
        jsonObject.put("areacode", "86");
        jsonObject.put("codetype", "atte");
        mPresenter.request(ATConstants.Config.SERVER_URL_GETMPASSWDCODE, jsonObject);
    }

    private void init() {
        password = ATPreferencesUtils.getString(this, "tempPass", "");
        tvPhone.setText(ATGlobalApplication.getAccount());
        titleBar.setSendText(getString(R.string.at_sure1));
        titleBar.setPublishClickListener(() -> {
            if (TextUtils.isEmpty(etCode.getText().toString())) {
                showToast(getString(R.string.at_the_code_cannot_be_empty));
                return;
            }
            if (!password.equals(etOldPassword.getText().toString())) {
                showToast(getString(R.string.at_error_old_password));
                return;
            }
            if (etOldPassword.getText().toString().equals(etNewPassword.getText().toString())) {
                showToast(getString(R.string.at_same_password));
                return;
            }
            if (!etNewPassword.getText().toString().equals(etConfirmPassword.getText().toString())) {
                showToast(getString(R.string.at_entered_passwords_differ));
                return;
            }
            String password = etNewPassword.getText().toString().trim();
            if (TextUtils.isEmpty(password) || password.length() > 16 || password.length() < 6) {
                showToast(getResources().getString(R.string.at_text_hint_import_password));
                return;
            }
            if (!isPassword(password)) {
                showToast(getResources().getString(R.string.at_text_hint_password_not_china));
                return;
            }
            resetPasswd();
        });

        tvGetCode.setOnClickListener(view -> {
                    if (isMobileNO(tvPhone.getText().toString())) {
                        showToast(getString(R.string.at_input_correct_phone));
                        return;
                    }
                    showBaseProgressDlg();
                    getMpasswdcode();
                }
        );
    }

    private boolean isMobileNO(String mobiles) {
        String telRegex = "[1][3456789]\\d{9}";
        return TextUtils.isEmpty(mobiles) || !mobiles.matches(telRegex);
    }

    private boolean isPassword(String passWord) {
        boolean flag;
        try {
            Pattern p = Pattern.compile("[^\u4e00-\u9fa5]+");
            Matcher m = p.matcher(passWord);
            flag = m.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    private void setCodeText() {
        time = 60;
        tvGetCode.setClickable(false);
        tvGetCode.setText(String.format(getString(R.string.at_get_code_after), time));
        tvGetCode.setTextColor(getResources().getColor(R.color._96775422));
        tvGetCode.setBackground(getResources().getDrawable(R.drawable.at_shape_45px_f0f0f0));
        handlertime.sendEmptyMessageDelayed(1, 1000);
    }

    @Override
    public void requestResult(String result, String url) {
        try {
            org.json.JSONObject jsonResult = new org.json.JSONObject(result);
            if ("200".equals(jsonResult.getString("code"))) {
                switch (url) {
                    case ATConstants.Config.SERVER_URL_RESETPASSWD:
                        showToast(getString(R.string.at_change_password_success));
                        ATPreferencesUtils.putString(this, "tempPass", etNewPassword.getText().toString().trim());
                        finish();
                        break;
                    case ATConstants.Config.SERVER_URL_GETMPASSWDCODE:
                        showToast(getString(R.string.at_get_verification_code_success));
                        closeBaseProgressDlg();
                        setCodeText();
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