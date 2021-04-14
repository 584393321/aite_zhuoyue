package com.aliyun.ayland.ui.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.ayland.base.ATBaseActivity;
import com.aliyun.ayland.base.autolayout.util.ATAutoUtils;
import com.aliyun.ayland.contract.ATMainContract;
import com.aliyun.ayland.data.ATFamilyMemberBean;
import com.aliyun.ayland.global.ATGlobalApplication;
import com.aliyun.ayland.global.ATConstants;
import com.aliyun.ayland.presenter.ATMainPresenter;
import com.aliyun.ayland.utils.ATIDCardUtil;
import com.aliyun.ayland.widget.ATObservableScrollView;
import com.aliyun.ayland.widget.titlebar.ATMyTitleBar;
import com.anthouse.wyzhuoyue.R;
import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;

import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ATFamilyManageAdviceActivity extends ATBaseActivity implements ATMainContract.View, View.OnClickListener, OnDateSetListener {
    private static final int REQUEST_CODE_CHANGED = 0x1001;
    private ATMainPresenter mPresenter;
    private ATFamilyMemberBean mFamilyMemberBean;
    private String account;
    private boolean edit;
    private InnerLVAdapter mInnerLVAdapter;
    private TimePickerDialog mDialogAll;
    private TextView mCurrentTextView;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private ATMyTitleBar titleBar;
    private RadioGroup radioGroup;
    private RadioButton rbMember, rbRent, rbVisite;
    private Button btnSubscribe;
    private ATObservableScrollView observableScrollView;
    private EditText etName, etCardNumber, etPhone;
    private TextView tvBirthday, tvStartTime, tvRegistTip, tvXing2, tvXing4, tvXing5, tvXing6, tvXing7;

    @Override
    protected int getLayoutId() {
        return R.layout.at_activity_family_manage_advice;
    }

    @Override
    protected void findView() {
        titleBar = findViewById(R.id.titlebar);
        observableScrollView = findViewById(R.id.observableScrollView);
        tvBirthday = findViewById(R.id.tv_birthday);
        tvStartTime = findViewById(R.id.tv_start_time);
        tvRegistTip = findViewById(R.id.tv_regist_tip);
        tvXing2 = findViewById(R.id.tv_xing2);
        tvXing4 = findViewById(R.id.tv_xing4);
        tvXing5 = findViewById(R.id.tv_xing5);
        tvXing6 = findViewById(R.id.tv_xing6);
        tvXing7 = findViewById(R.id.tv_xing7);
        etName = findViewById(R.id.et_name);
        etCardNumber = findViewById(R.id.et_card_number);
        etPhone = findViewById(R.id.et_phone);
        radioGroup = findViewById(R.id.radioGroup);
        btnSubscribe = findViewById(R.id.btn_subscribe);
        rbMember = findViewById(R.id.rb_member);
        rbRent = findViewById(R.id.rb_rent);
        rbVisite = findViewById(R.id.rb_visite);
        init();
    }

    @Override
    protected void initPresenter() {
        mPresenter = new ATMainPresenter(this);
        mPresenter.install(this);
    }

    private void entryFamilyMember() {
        showBaseProgressDlg();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("personName", etName.getText().toString());
        jsonObject.put("inDate", tvStartTime.getText().toString());
        jsonObject.put("idNumber", etCardNumber.getText().toString());
        jsonObject.put("birthDate", tvBirthday.getText().toString());
        jsonObject.put("account", account);
        jsonObject.put("personCode", ATGlobalApplication.getATLoginBean().getPersonCode());
        jsonObject.put("householdType", radioGroup.getCheckedRadioButtonId() == R.id.rb_member ? 102 :
                (radioGroup.getCheckedRadioButtonId() == R.id.rb_visite ? 103 : 104));
        jsonObject.put("iotToken", ATGlobalApplication.getIoTToken());
        jsonObject.put("operatorCode", ATGlobalApplication.getATLoginBean().getPersonCode());
        mPresenter.request(ATConstants.Config.SERVER_URL_ENTRYFAMILYMEMBER, jsonObject);
    }

    private void updateFamilyMember() {
        showBaseProgressDlg();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("birthDate", tvBirthday.getText().toString());
        jsonObject.put("account", account);
        jsonObject.put("inDate", tvStartTime.getText().toString());
        jsonObject.put("personName", etName.getText().toString());
        jsonObject.put("idNumber", etCardNumber.getText().toString());
        jsonObject.put("personCode", mFamilyMemberBean.getPersonCode());
        jsonObject.put("operatorCode", ATGlobalApplication.getATLoginBean().getPersonCode());
        jsonObject.put("householdType", radioGroup.getCheckedRadioButtonId() == R.id.rb_member ? 102
                : (radioGroup.getCheckedRadioButtonId() == R.id.rb_visite ? 103 : 104));
        jsonObject.put("iotToken", ATGlobalApplication.getIoTToken());
        mPresenter.request(ATConstants.Config.SERVER_URL_UPDATEFAMILYMEMBER, jsonObject);
    }

    private void init() {
        edit = getIntent().getBooleanExtra("edit", false);
        boolean ifAdmin = getIntent().hasExtra("mOtherMemberList");

        long century = 100L * 365 * 1000 * 60 * 60 * 24L;
        mDialogAll = new TimePickerDialog.Builder()
                .setCallBack(this)
                .setCancelStringId(getString(R.string.at_cancel))
                .setSureStringId(getString(R.string.at_sure))
                .setTitleStringId("")
                .setYearText(getString(R.string.at_year))
                .setMonthText(getString(R.string.at_month))
                .setDayText(getString(R.string.at_day))
                .setType(Type.YEAR_MONTH_DAY)
                .setCyclic(true)
                .setMinMillseconds(System.currentTimeMillis() - century)
                .setMaxMillseconds(System.currentTimeMillis() + century)
                .setCurrentMillseconds(System.currentTimeMillis())
                .setThemeColor(getResources().getColor(R.color._1478C8))
                .setWheelItemTextNormalColor(getResources().getColor(R.color.timetimepicker_default_text_color))
                .setWheelItemTextSelectorColor(getResources().getColor(R.color.timepicker_toolbar_bg))
                .setWheelItemTextSize(12)
                .build();

        if (!edit) {
            mFamilyMemberBean = getIntent().getParcelableExtra("FamilyMemberBean");
            if (ifAdmin) {
                titleBar.setTitle("家庭成员编辑");
                titleBar.setRightButtonText("转交管理权");
                titleBar.setRightClickListener(() -> startActivityForResult(getIntent().setClass(this, ATFamilyManageTransferActivity.class), REQUEST_CODE_CHANGED));
                for (int i = 0; i < radioGroup.getChildCount(); i++) {
                    radioGroup.getChildAt(i).setEnabled(false);
                }
                rbVisite.setTextColor(getResources().getColor(R.color._999999));
                rbRent.setTextColor(getResources().getColor(R.color._999999));
                rbMember.setTextColor(getResources().getColor(R.color._999999));
            } else {
                titleBar.setTitle(getString(R.string.at_regist_family_member));
                titleBar.setRightButtonText("完成");
                titleBar.setRightClickListener(() -> {
                    account = etPhone.getText().toString();
                    updateFamilyMember();
                });
            }
            btnSubscribe.setText(R.string.at_managing_face_permissions);
            btnSubscribe.setOnClickListener(view -> {
                //跳人脸
                startActivity(new Intent(ATFamilyManageAdviceActivity.this, ATUserFaceActivity.class)
                        .putExtra("personCode", mFamilyMemberBean.getPersonCode()).putExtra("name", mFamilyMemberBean.getNickname()));
            });
            etName.setText(mFamilyMemberBean.getNickname());
            etCardNumber.setText(mFamilyMemberBean.getIdNumber());
            radioGroup.check("103".equals(mFamilyMemberBean.getHouseholdtype()) ? R.id.rb_visite : ("104".equals(mFamilyMemberBean.getHouseholdtype()) ? R.id.rb_rent : R.id.rb_member));
            etPhone.setText(mFamilyMemberBean.getPhone());
            tvBirthday.setText(mFamilyMemberBean.getBirthDate());
            tvStartTime.setText(mFamilyMemberBean.getInDate().split(" ")[0]);
            setEnable(edit);
        } else {
            radioGroup.check(R.id.rb_member);
            btnSubscribe.setText(R.string.at_save);
            btnSubscribe.setOnClickListener(view -> {
                if (TextUtils.isEmpty(etName.getText().toString())) {
                    showToast(getString(R.string.at_input_name));
                    return;
                }
                if (TextUtils.isEmpty(etCardNumber.getText().toString())) {
                    showToast(getString(R.string.at_input_id_number));
                    return;
                } else {
                    try {
                        if (!ATIDCardUtil.IDCardValidate(etCardNumber.getText().toString())) {
                            showToast("请输入正确的身份证号码");
                            return;
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                if (getString(R.string.at_select_birthday).equals(tvBirthday.getText().toString())) {
                    showToast(getString(R.string.at_select_birthday));
                    return;
                }
                account = etPhone.getText().toString();
                if (getString(R.string.at_please_select_start_time1).equals(tvStartTime.getText().toString())) {
                    showToast(getString(R.string.at_please_select_start_time1));
                    return;
                }
                entryFamilyMember();
            });
            setEnable(edit);
        }

        etCardNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 18) {
                    InputMethodManager inputManager = (InputMethodManager) etCardNumber.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.showSoftInput(etCardNumber, 0);
                    String s1 = etCardNumber.getText().toString();
                    tvBirthday.setText(s1.substring(6, 10) + "-" + s1.substring(10, 12) + "-" + s1.substring(12, 14));
                }
            }
        });
        tvBirthday.setOnClickListener(this);
        tvStartTime.setOnClickListener(this);
    }

    @SuppressLint("InflateParams")
    private void initDialog() {
    }

    private void setEnable(boolean editable) {
        edittextEnable(etName, editable);
        edittextEnable(etCardNumber, editable);
        edittextEnable(etPhone, editable);
        tvRegistTip.setVisibility(editable ? View.VISIBLE : View.GONE);
        tvXing2.setVisibility(editable ? View.VISIBLE : View.GONE);
        tvXing4.setVisibility(editable ? View.VISIBLE : View.GONE);
        tvXing5.setVisibility(editable ? View.VISIBLE : View.GONE);
        tvXing6.setVisibility(editable ? View.VISIBLE : View.GONE);
        tvXing7.setVisibility(editable ? View.VISIBLE : View.GONE);
        tvBirthday.setClickable(editable);
        tvStartTime.setClickable(editable);
        if (editable) {
            tvBirthday.setTextColor(getResources().getColor(R.color._666666));
            tvStartTime.setTextColor(getResources().getColor(R.color._666666));
        } else {
            tvBirthday.setTextColor(getResources().getColor(R.color._999999));
            tvStartTime.setTextColor(getResources().getColor(R.color._999999));
        }
    }

    private void edittextEnable(EditText editText, boolean enable) {
        if (enable) {
            editText.setTextColor(getResources().getColor(R.color._666666));
        } else {
            editText.setTextColor(getResources().getColor(R.color._999999));
        }
        editText.setFocusableInTouchMode(enable);
        editText.setFocusable(enable);
        editText.requestFocus();
        InputMethodManager inputManager = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (enable) {
            if (inputManager.isActive() && getCurrentFocus() != null) {
                if (getCurrentFocus().getWindowToken() != null) {
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        } else {
            inputManager.showSoftInput(editText, 0);
        }
    }

    @Override
    public void requestResult(String result, String url) {
        try {
            org.json.JSONObject jsonResult = new org.json.JSONObject(result);
            if ("200".equals(jsonResult.getString("code"))) {
                switch (url) {
                    case ATConstants.Config.SERVER_URL_ENTRYFAMILYMEMBER:
                        showToast(getString(R.string.at_begin_advice_success));
                        finish();
                        break;
                    case ATConstants.Config.SERVER_URL_UPDATEFAMILYMEMBER:
                        showToast(getString(R.string.at_save_success));
                        setResult(RESULT_OK);
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

    @Override
    public void onClick(View view) {
        if (R.id.tv_birthday == view.getId()) {
            if (edit) {
                if (mDialogAll.isAdded())
                    return;
                mDialogAll.show(getSupportFragmentManager(), "all");
                mCurrentTextView = tvBirthday;
            }
        } else if (R.id.tv_start_time == view.getId()) {
            if (edit) {
                if (mDialogAll.isAdded())
                    return;
                observableScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                mDialogAll.show(getSupportFragmentManager(), "all");
                mCurrentTextView = tvStartTime;
            }
        }
    }

    @Override
    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
        String time = getDateToString(millseconds);
        mCurrentTextView.setText(time);
        mCurrentTextView.setTextColor(getResources().getColor(R.color._666666));
    }

    public String getDateToString(long time) {
        Date d = new Date(time);
        return sdf.format(d);
    }

    private static class InnerLVAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private Context mContext;
        private List<String> list;
        private int mPosition;

        private InnerLVAdapter(Context context, List<String> list) {
            mContext = context;
            mInflater = LayoutInflater.from(context);
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public String getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            InnerLVAdapter.ViewHolder vh;
            if (convertView == null) {
                vh = new InnerLVAdapter.ViewHolder();
                convertView = mInflater.inflate(R.layout.at_item_inner_list, parent, false);
                vh.mTvParkName = convertView.findViewById(R.id.tv_park_name);
                vh.mImgCheck = convertView.findViewById(R.id.img_check);
                convertView.setTag(vh);
            } else {
                vh = (InnerLVAdapter.ViewHolder) convertView.getTag();
            }
            vh.mTvParkName.setText(list.get(position));
            if (mPosition == position) {
                vh.mImgCheck.setVisibility(View.VISIBLE);
                vh.mTvParkName.setTextColor(mContext.getResources().getColor(R.color._333333));
            } else {
                vh.mImgCheck.setVisibility(View.GONE);
                vh.mTvParkName.setTextColor(mContext.getResources().getColor(R.color._666666));
            }
            ATAutoUtils.autoSize(convertView);
            return convertView;
        }

        public void setPosition(int position) {
            mPosition = position;
        }

        class ViewHolder {
            private TextView mTvParkName;
            private ImageView mImgCheck;
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