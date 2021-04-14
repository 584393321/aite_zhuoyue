package com.aliyun.ayland.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.ayland.base.ATBaseActivity;
import com.aliyun.ayland.contract.ATMainContract;
import com.aliyun.ayland.data.ATAuthCodeBean;
import com.aliyun.ayland.data.ATLoginBean;
import com.aliyun.ayland.global.ATConstants;
import com.aliyun.ayland.global.ATGlobalApplication;
import com.aliyun.ayland.global.ATWebSockIO;
import com.aliyun.ayland.presenter.ATLoginPresenter;
import com.aliyun.ayland.utils.ATCallbackUtil;
import com.aliyun.ayland.utils.ATNetWorkUtils;
import com.aliyun.ayland.utils.ATPreferencesUtils;
import com.aliyun.ayland.utils.ATSystemStatusBarUtils;
import com.anthouse.wyzhuoyue.R;

import org.json.JSONException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ATLoginActivity extends ATBaseActivity implements ATMainContract.View {
    private static final int REQUEST_CODE_SUCCESS = 0x1000;
    private static final int MSG_TIME_CLICK = 0x1001;
    private ATLoginPresenter mPresenter;
    private boolean mPasswordVisible = false, accountEmpty = true, passwordEmpty = true, accountEmpty1 = true, passwordEmpty1 = true;
    private String phone, code, password, phone_regist, password_regist;
    private int time;
    private Button btnLogin;
    private RadioGroup radioGroup;
    private RadioButton rbLogin, rbRegist;
    private LinearLayout llLogin, llRegist;
    private ImageView imgLinePhone, imgLinePhone1, imgLineCode, imgLinePassword, imgLinePassword1, imgPasswordVisible;
    private TextView tvAgreement, tvGetCode, tvForgetPassword;
    private EditText etNewPassword, etCode, etNumberPhone1, etPassword, etNumberPhone, etPassword1;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_TIME_CLICK:
                    --time;
                    if (time == 0) {
                        tvGetCode.setClickable(true);
                        tvGetCode.setText(getResources().getString(R.string.at_get_code_again));
                        tvGetCode.setTextColor(getResources().getColor(R.color._B98C67));
                        tvGetCode.setBackground(getResources().getDrawable(R.drawable.at_shape_45px_fce5cc_to_fef2d5));
                    } else {
                        tvGetCode.setText(String.format(getString(R.string.at_get_code_after), time));
                        tvGetCode.setTextColor(getResources().getColor(R.color._999999));
                        tvGetCode.setBackground(getResources().getDrawable(R.drawable.at_shape_45px_f0f0f0));
                        mHandler.sendEmptyMessageDelayed(MSG_TIME_CLICK, 1000);
                    }
                    break;
            }
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.at_activity_login;
    }

    @Override
    protected void findView() {
        llLogin = findViewById(R.id.ll_login);
        radioGroup = findViewById(R.id.radioGroup);
        llRegist = findViewById(R.id.ll_regist);
        rbRegist = findViewById(R.id.rb_regist);
        rbLogin = findViewById(R.id.rb_login);
        tvForgetPassword = findViewById(R.id.tv_forget_password);
        btnLogin = findViewById(R.id.btn_login);
        tvGetCode = findViewById(R.id.tv_get_code);
        tvAgreement = findViewById(R.id.tv_agreement);
        etCode = findViewById(R.id.et_code);
        etNumberPhone = findViewById(R.id.et_number_phone);
        etNumberPhone1 = findViewById(R.id.et_number_phone1);
        etPassword = findViewById(R.id.et_password);
        etPassword1 = findViewById(R.id.et_password1);
        imgLinePhone = findViewById(R.id.img_line_phone);
        imgLinePhone1 = findViewById(R.id.img_line_phone1);
        imgLineCode = findViewById(R.id.img_line_code);
        imgLinePassword = findViewById(R.id.img_line_password);
        imgLinePassword1 = findViewById(R.id.img_line_password1);
        imgPasswordVisible = findViewById(R.id.img_password_visible);
        TextView textView = findViewById(R.id.textView);

        if (ATGlobalApplication.isIsWuye()) {
            textView.setText("物业");
        } else {
            textView.setText("业主");
        }

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ATGlobalApplication.isIsWuye()) {
                    ATGlobalApplication.setIsWuye(false);
                    textView.setText("业主");
                } else {
                    ATGlobalApplication.setIsWuye(true);
                    textView.setText("物业");
                }
            }
        });
        ATSystemStatusBarUtils.init(this, true);
        init();
    }

    @Override
    protected void initPresenter() {
        mPresenter = new ATLoginPresenter(this);
        mPresenter.install(this);
    }

    private void initRegistView() {
        tvGetCode.setOnClickListener(view -> {
                    phone_regist = etNumberPhone1.getText().toString();
                    if (!isMobileNO(phone_regist)) {
                        showToast(getString(R.string.at_input_correct_phone));
                        return;
                    }
                    showBaseProgressDlg();
                    codeToRegister();
                }
        );

        etNumberPhone1.setOnFocusChangeListener((view, b) -> imgLinePhone1.setBackgroundColor(b ? getResources().getColor(R.color._333333) : getResources().getColor(R.color._CCCCCC)));
        etNumberPhone1.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    accountEmpty1 = true;
                    btnLogin.setBackgroundResource(R.drawable.at_shape_66px_aaaaaa);
                    btnLogin.setEnabled(false);
                } else {
                    accountEmpty1 = false;
                    if (!passwordEmpty1) {
                        btnLogin.setBackgroundResource(R.drawable.at_selector_66px_1478c8_005395);
                        btnLogin.setEnabled(true);
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {

            }
        });
        etCode.setOnFocusChangeListener((view, b) -> imgLineCode.setBackgroundColor(b ? getResources().getColor(R.color._333333) : getResources().getColor(R.color._CCCCCC)));
        etPassword1.setOnFocusChangeListener((view, b) -> imgLinePassword1.setBackgroundColor(b ? getResources().getColor(R.color._333333) : getResources().getColor(R.color._CCCCCC)));
        etPassword1.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    passwordEmpty1 = true;
                    btnLogin.setBackgroundResource(R.drawable.at_shape_66px_aaaaaa);
                    btnLogin.setEnabled(false);
                } else {
                    passwordEmpty1 = false;
                    if (!accountEmpty1) {
                        btnLogin.setBackgroundResource(R.drawable.at_selector_66px_1478c8_005395);
                        btnLogin.setEnabled(true);
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {

            }
        });
        etPassword1.setTransformationMethod(PasswordTransformationMethod.getInstance());
    }

    private void initLoginView() {
        etNumberPhone.setOnFocusChangeListener((view, b) -> imgLinePhone.setBackgroundColor(b ? getResources().getColor(R.color._333333) : getResources().getColor(R.color._CCCCCC)));
        etNumberPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    accountEmpty = true;
                    btnLogin.setBackgroundResource(R.drawable.at_shape_66px_aaaaaa);
                    btnLogin.setEnabled(false);
                } else {
                    accountEmpty = false;
                    if (!passwordEmpty) {
                        btnLogin.setBackgroundResource(R.drawable.at_selector_66px_1478c8_005395);
                        btnLogin.setEnabled(true);
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {

            }
        });
        etPassword.setOnFocusChangeListener((view, b) -> imgLinePassword.setBackgroundColor(b ? getResources().getColor(R.color._333333) : getResources().getColor(R.color._CCCCCC)));
        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    passwordEmpty = true;
                    btnLogin.setBackgroundResource(R.drawable.at_shape_66px_aaaaaa);
                    btnLogin.setEnabled(false);
                } else {
                    passwordEmpty = false;
                    if (!accountEmpty) {
                        btnLogin.setBackgroundResource(R.drawable.at_selector_66px_1478c8_005395);
                        btnLogin.setEnabled(true);
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {

            }
        });

        imgPasswordVisible.setOnClickListener(v -> {
            mPasswordVisible = !mPasswordVisible;
            if (mPasswordVisible) {
                etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                imgPasswordVisible.setImageResource(R.drawable.login_hide_n);
            } else {
                etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                imgPasswordVisible.setImageResource(R.drawable.login_hide_p);
            }
            etPassword.setSelection(etPassword.getText().toString().length());
        });

        tvForgetPassword.setOnClickListener(view ->
                startActivityForResult(new Intent(this, ATForgetPasswordActivity.class)
                        .putExtra("regist", false), REQUEST_CODE_SUCCESS));
    }

    private void init() {
        phone = ATPreferencesUtils.getString(this, "tempAccount", "");
        password = ATGlobalApplication.getPassword();
        rbLogin.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                btnLogin.setText(getString(R.string.at_login));
                llLogin.setVisibility(View.VISIBLE);
                llRegist.setVisibility(View.GONE);
                if (!TextUtils.isEmpty(phone)) {
                    etNumberPhone.setText(phone);
                    etNumberPhone.setSelection(phone.length());
                    accountEmpty = false;
                }
                if (!TextUtils.isEmpty(password)) {
                    etPassword.setText(password);
                    passwordEmpty = false;
                    btnLogin.setBackgroundResource(R.drawable.at_selector_66px_1478c8_005395);
                    btnLogin.setEnabled(true);
                }
            }
        });
        rbRegist.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                llLogin.setVisibility(View.GONE);
                llRegist.setVisibility(View.VISIBLE);
                btnLogin.setText(getString(R.string.at_regist));
                if (!TextUtils.isEmpty(phone_regist)) {
                    etNumberPhone1.setText(phone_regist);
                    etNumberPhone1.setSelection(phone_regist.length());
                    accountEmpty1 = false;
                }
                if (!TextUtils.isEmpty(password_regist)) {
                    etPassword1.setText(password_regist);
                    passwordEmpty1 = false;
                    btnLogin.setBackgroundResource(R.drawable.at_selector_66px_1478c8_005395);
                    btnLogin.setEnabled(true);
                }
            }
        });
        radioGroup.check(R.id.rb_login);
        btnLogin.setOnClickListener(v -> {
            if (getString(R.string.at_login).equals(btnLogin.getText())) {
                if (!ATNetWorkUtils.isNetworkAvailable(ATLoginActivity.this)) {
                    showToast(getString(R.string.at_net_work_wrong));
                    return;
                }
                phone = etNumberPhone.getText().toString();
                if (!isMobileNO(phone)) {
                    showToast(getString(R.string.at_input_correct_phone));
                    return;
                }
                password = etPassword.getText().toString();
                if (TextUtils.isEmpty(password)) {
                    showToast(getResources().getString(R.string.at_empty_password));
                    return;
                } else {
                    if (password.length() > 16 || password.length() < 6) {
                        showToast(getResources().getString(R.string.at_text_hint_import_password));
                        return;
                    } else {
                        if (!isPassword(password)) {
                            showToast(getResources().getString(R.string.at_text_hint_password_not_china));
                            return;
                        }
                    }
                }
                showBaseProgressDlg();
                login();
            } else {
                phone_regist = etNumberPhone1.getText().toString();
                if (!isMobileNO(phone_regist)) {
                    showToast(getString(R.string.at_input_correct_phone));
                    return;
                }
                code = etCode.getText().toString();
                if (TextUtils.isEmpty(code)) {
                    showToast(getString(R.string.at_empty_code));
                    return;
                }
                password_regist = etPassword1.getText().toString();
                if (TextUtils.isEmpty(password_regist)) {
                    showToast(getResources().getString(R.string.at_empty_password));
                    return;
                } else {
                    if (password_regist.length() > 16 || password_regist.length() < 6) {
                        showToast(getResources().getString(R.string.at_text_hint_import_password));
                        return;
                    } else {
                        if (!isPassword(password_regist)) {
                            showToast(getResources().getString(R.string.at_text_hint_password_not_china));
                            return;
                        }
                    }
                }
                showBaseProgressDlg();
                registerClient();
            }
        });
        SpannableString s = new SpannableString(getString(R.string.at_login_agreement));
        s.setSpan(new ForegroundColorSpan(Color.parseColor("#FF999999")), 0, 10, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        s.setSpan(new ForegroundColorSpan(Color.parseColor("#FFCCA56A")), 10, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvAgreement.setText(s);
        initLoginView();
        initRegistView();
        if (!TextUtils.isEmpty(phone)) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }
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
        tvGetCode.setBackground(getResources().getDrawable(R.drawable.at_shape_45px_f0f0f0));
        tvGetCode.setTextColor(getResources().getColor(R.color._999999));
        mHandler.sendEmptyMessageDelayed(MSG_TIME_CLICK, 1000);
    }

    private void codeToRegister() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("phone", phone_regist);
        jsonObject.put("areacode", "86");
        mPresenter.request(ATConstants.Config.SERVER_URL_CODETOREGISTER, jsonObject);
    }

    private void registerClient() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("phone", phone_regist);
        jsonObject.put("username", phone_regist);
        jsonObject.put("password", password_regist);
        jsonObject.put("userType", "1");
        jsonObject.put("nickName", "");
        jsonObject.put("phoneCode", code);
        mPresenter.request(ATConstants.Config.SERVER_URL_REGISTERCLIENT, jsonObject);
    }

    private void login() {
        ATPreferencesUtils.putString(this, "tempAccount", phone);
        ATGlobalApplication.setPassword(password);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", phone);
        jsonObject.put("password", password);
        mPresenter.request(ATConstants.Config.SERVER_URL_LOGIN, jsonObject);
    }

    private void getAuthCode() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", phone);
        jsonObject.put("password", password);
        mPresenter.request(ATConstants.Config.SERVER_URL_GETAUTHCODE, jsonObject);
    }

    private boolean isPassword(String mpassword) {
        Pattern p = Pattern.compile("[^\u4e00-\u9fa5]+");
        Matcher m = p.matcher(mpassword);
        return m.matches();
    }

    @Override
    public void requestResult(String result, String url) {
        try {
            if (TextUtils.isEmpty(result))
                return;
            org.json.JSONObject jsonResult = new org.json.JSONObject(result);
            if ("200".equals(jsonResult.getString("code"))) {
                switch (url) {
                    case ATConstants.Config.SERVER_URL_GETAUTHCODE:
                        ATAuthCodeBean aTLogin = gson.fromJson(jsonResult.toString(), ATAuthCodeBean.class);
                        ATCallbackUtil.doCallBackMethod(aTLogin.getAuthCode());
                        ATGlobalApplication.setAuthCode(aTLogin.getAuthCode());
                        ATWebSockIO.getInstance().closeSock();
                        ATWebSockIO.getInstance().setUpConnect();

                        if (aTLogin.getHouse() != null)
                            ATGlobalApplication.setHouse(aTLogin.getHouse().toString());

                        closeBaseProgressDlg();
                        showToast(getString(R.string.at_login_success));
                        if (ATGlobalApplication.isIsWuye()) {
                            startActivity(new Intent(ATLoginActivity.this, com.aliyun.wuye.ui.activity.ATMainActivity.class));
                        } else {
                            startActivity(new Intent(ATLoginActivity.this, com.aliyun.ayland.ui.activity.ATMainActivity.class));
                        }
                        mPresenter.request(ATConstants.Config.SERVER_URL_GETACCESSTOKEN, null);
                        finish();
                        break;
                    case ATConstants.Config.SERVER_URL_LOGIN:
                        ATLoginBean mATLoginBean = gson.fromJson(jsonResult.getString("result"), ATLoginBean.class);
                        ATGlobalApplication.setAccount(etNumberPhone.getText().toString().replaceAll(" ", ""));
                        ATGlobalApplication.setLoginBeanStr(jsonResult.getString("result"));

                        if (mATLoginBean.isHasHouse() && mATLoginBean.getHouse() != null) {
                            getAuthCode();
                        } else {
                            //游客
                            startActivity(new Intent(ATLoginActivity.this, ATEmptyActivity.class));
                            closeBaseProgressDlg();
                        }
                        break;
                    case ATConstants.Config.SERVER_URL_CODETOREGISTER:
                        showToast(getString(R.string.at_get_verification_code_success));
                        closeBaseProgressDlg();
                        setCodeText();
                        break;
                    case ATConstants.Config.SERVER_URL_REGISTERCLIENT:
                        closeBaseProgressDlg();
                        showToast(getString(R.string.at_regist_success));
                        showBaseProgressDlg(getString(R.string.at_logining));
                        phone = phone_regist;
                        password = password_regist;
                        login();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            String phone = data.getStringExtra("phone");
            etNumberPhone.setText(phone);
            etPassword.requestFocus();
        }
    }
}