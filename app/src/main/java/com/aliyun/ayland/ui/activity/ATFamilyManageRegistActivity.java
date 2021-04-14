package com.aliyun.ayland.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.ayland.base.ATBaseActivity;
import com.aliyun.ayland.base.autolayout.util.ATAutoUtils;
import com.aliyun.ayland.contract.ATMainContract;
import com.aliyun.ayland.data.ATFamilyMemberBean;
import com.aliyun.ayland.global.ATConstants;
import com.aliyun.ayland.global.ATGlobalApplication;
import com.aliyun.ayland.presenter.ATMainPresenter;
import com.aliyun.ayland.widget.ATObservableScrollView;
import com.anthouse.wyzhuoyue.R;
import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ATFamilyManageRegistActivity extends ATBaseActivity implements ATMainContract.View, View.OnClickListener, OnDateSetListener {
    private static final int REQUEST_CODE_CHANGED = 0x1001;
    private ATMainPresenter mPresenter;
    private List<String> mGenderList = new ArrayList<>();
    private String account, idNumber, birthDate, nickName;
    private TimePickerDialog mDialogAll;
    private TextView mCurrentTextView;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private RadioGroup radioGroup;
    private Button btnSubscribe;
    private ATObservableScrollView observableScrollView;
    private LinearLayout llNone, llHas;
    private EditText etName;
    private TextView tvPhoneId, tvPhoneId1, tvPhoneIdNumber, tvPhoneIdNumber1, tvBirthday, tvStartTime,
            tvName1, tvBirthday1;
    private boolean mHasMember;

    @Override
    protected int getLayoutId() {
        return R.layout.at_activity_family_manage_regist;
    }

    @Override
    protected void findView() {
        observableScrollView = findViewById(R.id.observableScrollView);
        llNone = findViewById(R.id.ll_none);
        llHas = findViewById(R.id.ll_has);
        tvPhoneId = findViewById(R.id.tv_phone_id);
        tvPhoneId1 = findViewById(R.id.tv_phone_id1);
        tvPhoneIdNumber = findViewById(R.id.tv_phone_id_number);
        tvPhoneIdNumber1 = findViewById(R.id.tv_phone_id_number1);
        etName = findViewById(R.id.et_name);
        tvBirthday = findViewById(R.id.tv_birthday);
        tvName1 = findViewById(R.id.tv_name1);
        tvBirthday1 = findViewById(R.id.tv_birthday1);
        radioGroup = findViewById(R.id.radioGroup);
        tvStartTime = findViewById(R.id.tv_start_time);
        btnSubscribe = findViewById(R.id.btn_subscribe);
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
        jsonObject.put("birthDate", birthDate);
        if (!TextUtils.isEmpty(account))
            jsonObject.put("account", account);
        if (!TextUtils.isEmpty(idNumber))
            jsonObject.put("idNumber", idNumber);
        jsonObject.put("inDate", tvStartTime.getText().toString());
        jsonObject.put("personName", nickName);
        jsonObject.put("personCode", ATGlobalApplication.getATLoginBean().getPersonCode());
        jsonObject.put("householdType", radioGroup.getCheckedRadioButtonId() == R.id.rb_member ? 102 : (radioGroup.getCheckedRadioButtonId() == R.id.rb_visite ? 103 : 104));
        jsonObject.put("iotToken", ATGlobalApplication.getIoTToken());
        mPresenter.request(ATConstants.Config.SERVER_URL_ENTRYFAMILYMEMBER, jsonObject);
    }

    private void init() {
        mHasMember = getIntent().hasExtra("FamilyMemberBean");
        if (mHasMember) {
            ATFamilyMemberBean familyMemberBean = getIntent().getParcelableExtra("FamilyMemberBean");
            llHas.setVisibility(View.VISIBLE);
            llNone.setVisibility(View.GONE);
            birthDate = familyMemberBean.getBirthDate();
            nickName = familyMemberBean.getNickname();
            tvName1.setText(nickName);
            tvBirthday1.setText(birthDate);
        } else {
            llHas.setVisibility(View.GONE);
            llNone.setVisibility(View.VISIBLE);
        }
        boolean ifPhone = "phone".equals(getIntent().getStringExtra("type"));
        if (ifPhone) {
            account = getIntent().getStringExtra("phone");
            tvPhoneId.setText(getString(R.string.at_phone1_));
            tvPhoneIdNumber.setText(account);
            tvPhoneId1.setText(getString(R.string.at_phone1));
            tvPhoneIdNumber1.setText(account);
        } else {
            idNumber = getIntent().getStringExtra("id_number");
            tvPhoneId.setText(getString(R.string.at_id_number1_));
            tvPhoneIdNumber.setText(idNumber);
            tvPhoneId1.setText(getString(R.string.at_id_number1));
            tvPhoneIdNumber1.setText(idNumber);
        }

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

        radioGroup.check(R.id.rb_member);
        btnSubscribe.setText(R.string.at_save);
        btnSubscribe.setOnClickListener(view -> {
            if (!mHasMember) {
                nickName = etName.getText().toString();
                if (TextUtils.isEmpty(nickName)) {
                    showToast(getString(R.string.at_input_name));
                    return;
                }
                birthDate = tvBirthday.getText().toString();
                if (getString(R.string.at_select_birthday).equals(birthDate)) {
                    showToast(getString(R.string.at_select_birthday));
                    return;
                }
            }
            entryFamilyMember();
        });

        tvBirthday.setOnClickListener(this);
        tvStartTime.setOnClickListener(this);
    }

    @Override
    public void requestResult(String result, String url) {
        try {
            org.json.JSONObject jsonResult = new org.json.JSONObject(result);
            if ("200".equals(jsonResult.getString("code"))) {
                switch (url) {
                    case ATConstants.Config.SERVER_URL_ENTRYFAMILYMEMBER:
                        showToast(getString(R.string.at_begin_advice_success));
                        setResult(RESULT_OK);
                        finish();
                        break;
                    case ATConstants.Config.SERVER_URL_UPDATEFAMILYMEMBER:
                        showToast(getString(R.string.at_save_success));
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
            if (mDialogAll.isAdded())
                return;
            mDialogAll.show(getSupportFragmentManager(), "all");
            mCurrentTextView = tvBirthday;
        } else if (R.id.tv_start_time == view.getId()) {
            if (mDialogAll.isAdded())
                return;
            observableScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            mDialogAll.show(getSupportFragmentManager(), "all");
            mCurrentTextView = tvStartTime;
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