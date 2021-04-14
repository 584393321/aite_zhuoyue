package com.aliyun.wuye.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.ayland.base.ATBaseActivity;
import com.aliyun.ayland.contract.ATMainContract;
import com.aliyun.ayland.data.ATFamilyMessageBean;
import com.aliyun.ayland.data.ATHouseBean;
import com.aliyun.ayland.data.ATParkNameBean;
import com.aliyun.ayland.data.ATParkNumberBean;
import com.aliyun.ayland.data.AllVillageDetailBean;
import com.aliyun.ayland.data.db.ATParkNumberDao;
import com.aliyun.ayland.global.ATConstants;
import com.aliyun.ayland.global.ATGlobalApplication;
import com.aliyun.ayland.interfaces.ATOnPopupItemClickListener;
import com.aliyun.ayland.presenter.ATMainPresenter;
import com.aliyun.ayland.ui.activity.ATVisitorAppointResultActivity;
import com.aliyun.ayland.widget.ATObservableScrollView;
import com.aliyun.ayland.widget.contrarywind.view.ATWheelView;
import com.aliyun.ayland.widget.pickerview.builder.ATOptionsPickerBuilder;
import com.aliyun.ayland.widget.pickerview.listener.ATOnOptionsSelectListener;
import com.aliyun.ayland.widget.pickerview.view.ATOptionsPickerView;
import com.aliyun.ayland.widget.popup.ATBottomPlatePopup;
import com.aliyun.ayland.widget.popup.ATOptionsPopup;
import com.aliyun.ayland.widget.popup.ATSlideFromBottomInputPopup;
import com.aliyun.ayland.widget.popup.ATWYSelectVillagePopup;
import com.anthouse.wyzhuoyue.R;
import com.google.gson.reflect.TypeToken;
import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.listener.OnDateSetListener;

import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ATVisiteAppointActivity extends ATBaseActivity implements ATMainContract.View, OnDateSetListener {
    private static final int PERMISS_CONTACT = 1001;
    private ATMainPresenter mPresenter;
    private ATOptionsPickerView pvOptions;
    private Calendar calendar = Calendar.getInstance();
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
    private ATParkNumberDao mATParkNumberDao;
    private List<ATParkNumberBean> mParkNumberList;
    private ATBottomPlatePopup mBottomPlatePopup;
    private ATSlideFromBottomInputPopup mSlideFromBottomInputPopup;
    private int park_name_position = 0, current_position = 0, current_rank = 0;
    private String visitorName, visitorTel, visite_time, leave_time, carNumber, visite_park, buildingCode, ownerPerson;
    private List<String> mParkNameList = new ArrayList<>();
    private List<ATParkNameBean> mATParkNameBeanList = new ArrayList<>();
    private ATHouseBean mHouseBean;
    private String[] permissList = {Manifest.permission.READ_CONTACTS, Manifest.permission.READ_PHONE_STATE};
    private TimePickerDialog mDialogAll;
    private boolean mTextFlag;
    private Button btnSubscribe;
    private RadioGroup rgMotors, rgParkFee;
    private ATObservableScrollView observableScrollView;
    private RelativeLayout rlPark, rlPlate, rlVisiteTime, rlLeaveTime, rlVisitorRoom, rlOwner;
    private RadioButton rbMotorsYes, rbMotorsNo, rbParkYes, rbParkNo;
    private TextView tvVisiteRoom, tvVisiteTime, tvLeaveTime, tvVisitePark, tvPlateNumbers, tvContact, tvOwner, tvRead;
    private EditText etVisitorName, etVisitorPhone;
    private ATWYSelectVillagePopup mATSelectVillagePopup;
    private ATOptionsPopup mATOptionsPopup;
    private List<ATFamilyMessageBean> mATFamilyMessageBeans;
    //    private List<AllVillageDetailBean1> mAllVillageBeanList;
//    private HashMap<Integer, Integer> hashMap = new HashMap<>();
    private String mAllVillageList;
    private HashMap<Integer, Integer> hashMap = new HashMap<>();

    @Override
    protected int getLayoutId() {
        return R.layout.at_activity_wy_visitor_appoint;
    }

    @Override
    protected void findView() {
        etVisitorName = findViewById(R.id.et_visitor_name);
        etVisitorPhone = findViewById(R.id.et_visitor_phone);
        tvVisiteRoom = findViewById(R.id.tv_visite_room);
        tvVisiteTime = findViewById(R.id.tv_visite_time);
        tvLeaveTime = findViewById(R.id.tv_leave_time);
        tvPlateNumbers = findViewById(R.id.tv_plate_numbers);
        tvVisitePark = findViewById(R.id.tv_visite_park);
        tvContact = findViewById(R.id.tv_contact);
        tvOwner = findViewById(R.id.tv_owner);
        tvRead = findViewById(R.id.tv_read);
        rbMotorsYes = findViewById(R.id.rb_motors_yes);
        rbMotorsNo = findViewById(R.id.rb_motors_no);
        rgMotors = findViewById(R.id.rg_motors);
        rgParkFee = findViewById(R.id.rg_park_fee);
        rbParkYes = findViewById(R.id.rb_park_yes);
        rbParkNo = findViewById(R.id.rb_park_no);
        btnSubscribe = findViewById(R.id.btn_subscribe);
        rlPark = findViewById(R.id.rl_park);
        rlPlate = findViewById(R.id.rl_plate);
        rlVisiteTime = findViewById(R.id.rl_visite_time);
        rlLeaveTime = findViewById(R.id.rl_leave_time);
        rlOwner = findViewById(R.id.rl_owner);
        rlVisitorRoom = findViewById(R.id.rl_visitor_room);
        observableScrollView = findViewById(R.id.observableScrollView);
        init();
    }

    @Override
    protected void initPresenter() {
        mPresenter = new ATMainPresenter(this);
        mPresenter.install(this);
        findParkingLotList();
        if (!TextUtils.isEmpty(ATGlobalApplication.getHouse())) {
            mHouseBean = gson.fromJson(ATGlobalApplication.getHouse(), ATHouseBean.class);
        }
        findTreeBuilding();
    }

    public void findPersonMessage() {
        if (mHouseBean == null)
            return;
        showBaseProgressDlg();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("villageCode", mHouseBean.getVillageId());
        jsonObject.put("buildingCode", buildingCode);
        mPresenter.request(ATConstants.Config.SERVER_URL_FINDPERSONMESSAGE, jsonObject);
    }

    public void findTreeBuilding() {
        if (mHouseBean == null)
            return;
        showBaseProgressDlg();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("villageCode", mHouseBean.getVillageId());
        mPresenter.request(ATConstants.Config.SERVER_URL_FINDTREEBUILDING, jsonObject);
    }

    public void findParkingLotList() {
        showBaseProgressDlg();
        JSONObject jsonObject = new JSONObject();
        mPresenter.request(ATConstants.Config.SERVER_URL_GETPARKLOTLIST, jsonObject);
    }

    private void addVisitorReservation() {
        visitorName = etVisitorName.getText().toString();
        if (TextUtils.isEmpty(visitorName)) {
            showToast(getString(R.string.at_input_visitors_name));
            return;
        }
        visitorTel = etVisitorPhone.getText().toString();
        if (TextUtils.isEmpty(visitorTel) || !isMobileNO(visitorTel)) {
            showToast(getString(R.string.at_text_phone_error));
            return;
        }
        String visitorRoom = tvVisiteRoom.getText().toString();
        if (getString(R.string.at_select_visit_room).equals(visitorRoom)) {
            showToast(getString(R.string.at_select_visit_room));
            return;
        }
        visite_time = tvVisiteTime.getText().toString();
        if (getString(R.string.at_select_visit_time).equals(visite_time)) {
            visite_time = "";
            showToast(getString(R.string.at_select_visit_time));
            return;
        }
        leave_time = tvLeaveTime.getText().toString();
        if (getString(R.string.at_select_leave_time).equals(leave_time)) {
            leave_time = "";
            showToast(getString(R.string.at_select_leave_time));
            return;
        }
        visite_park = tvVisitePark.getText().toString();
        if (rbMotorsYes.isChecked()) {
            carNumber = tvPlateNumbers.getText().toString();
            if (getString(R.string.at_input_plate_number).equals(carNumber)) {
                showToast(getString(R.string.at_input_correct_license_plate_number) + "\r\n（例：粤B-88888）");
                carNumber = "";
                return;
            }
            if (!TextUtils.isEmpty(carNumber) && carNumber.contains("-")) {
                String firstString = carNumber.substring(0, 1);
                if ("港".equals(firstString)) {
                    showToast(getString(R.string.at_input_correct_license_plate_number) + "\r\n（例：粤B-88888）");
                    return;
                }
                carNumber = carNumber.split("-")[0].substring(0, 1) + "-" + carNumber.split("-")[0].substring(1, 2) + carNumber.split("-")[1];
                //字母开头
                Pattern patternA = Pattern.compile("^[A-Z]{1}");
                //文字开头
                Pattern pattern1 = Pattern.compile("^[\u4e00-\u9fff]{1}[-][A-Z]{1}[A-Z0-9]{4}[A-Z0-9\u4e00-\u9fff]{1}$");
                Matcher mA = patternA.matcher(carNumber);
                Matcher m1 = pattern1.matcher(carNumber);
                if (m1.matches()) {
                    ATParkNumberBean parkNumberBean = mATParkNumberDao.getByParkNumber(firstString);
                    parkNumberBean.setCreate_time(new Date().getTime());
                    mATParkNumberDao.update(parkNumberBean);
                } else if (mA.matches()) {

                } else {
                    //车牌错误
                    showToast(getString(R.string.at_input_correct_license_plate_number) + "\r\n（例：粤B-88888）");
                    return;
                }
            } else {
                showToast(getString(R.string.at_input_correct_license_plate_number) + "\r\n（例：粤B-88888）");
                return;
            }
            if (getString(R.string.at_select_park).equals(visite_park))
                visite_park = "";
            if (TextUtils.isEmpty(visite_park)) {
                showToast(getString(R.string.at_select_park));
                return;
            }
        }
        showBaseProgressDlg();
        JSONObject jsonObject = new JSONObject();
//        if (rbMotorsYes.isChecked()) {
//            jsonObject.put("carNumber", carNumber.replace("-", ""));
//            if (mATParkNameBeanList.size() != 0)
//                jsonObject.put("carPark", mATParkNameBeanList.get(park_name_position).getId());
//        }
        jsonObject.put("hasCar", rbMotorsYes.isChecked() ? 1 : -1);
//        jsonObject.put("payPerson", rbParkYes.isChecked() ? 1 : 2);
//        jsonObject.put("intermediary", -1);
        jsonObject.put("carPark", "");
        jsonObject.put("carNumber", "");

        jsonObject.put("buildingCode", buildingCode);
        jsonObject.put("visitorHouse", visitorRoom);
        jsonObject.put("reservationStartTime", tvVisiteTime.getText());
        jsonObject.put("reservationEndTime", tvLeaveTime.getText());
        jsonObject.put("visitorName", visitorName);
        jsonObject.put("visitorTel", visitorTel);
        jsonObject.put("ownerName", tvOwner.getText().toString());
        jsonObject.put("createPerson", ATGlobalApplication.getATLoginBean().getPersonCode());
        jsonObject.put("ownerPerson", ownerPerson);
        jsonObject.put("villageCode", mHouseBean.getVillageId());
        mPresenter.request(ATConstants.Config.SERVER_URL_ADDPASSVISITORRESERVATIONBYPROPERTY, jsonObject);
    }

    private void init() {
        hashMap.put(0, 0);

        btnSubscribe.setOnClickListener(view -> addVisitorReservation());
        tvRead.setSelected(true);
        tvRead.setOnClickListener(v -> tvRead.setSelected(!tvRead.isSelected()));
        rlPark.setOnClickListener(view -> {
            if (mParkNameList.size() > 0) {
                pvOptions.setSelectOptions(park_name_position);
                pvOptions.show();
            } else {
                findParkingLotList();
            }
        });

        tvContact.setOnClickListener(view -> addPermissByPermissionList(this, permissList, PERMISS_CONTACT));
        btnSubscribe.setEnabled(false);
        etVisitorName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (TextUtils.isEmpty(charSequence)) {
                    btnSubscribe.setBackgroundResource(R.drawable.at_shape_66px_aaaaaa);
                    btnSubscribe.setEnabled(false);
                } else {
                    btnSubscribe.setBackgroundResource(R.drawable.at_selector_66px_1478c8_005395);
                    btnSubscribe.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        if (!TextUtils.isEmpty(ATGlobalApplication.getHouse())) {
            mHouseBean = gson.fromJson(ATGlobalApplication.getHouse(), ATHouseBean.class);
        }
        rgMotors.setOnCheckedChangeListener((radioGroup, i) -> {
            if (i == R.id.rb_motors_yes) {
                rlPlate.setVisibility(View.VISIBLE);
                rlPark.setVisibility(View.VISIBLE);
//                    rlParkFee.setVisibility(View.VISIBLE);
            } else {
                rlPlate.setVisibility(View.GONE);
                rlPark.setVisibility(View.GONE);
//                    rlParkFee.setVisibility(View.GONE);
            }
        });
        mATParkNumberDao = new ATParkNumberDao(this);
        mParkNumberList = mATParkNumberDao.getAll();
        if (mParkNumberList.size() == 0) {
            initLitepal();
        }
        mBottomPlatePopup = new ATBottomPlatePopup(this);
        mBottomPlatePopup.setOnItemClickListener((view, position) -> {
            String park_number = mParkNumberList.get(position).getPark_number();
            updateLitepal(park_number);
            tvPlateNumbers.setText(park_number);
            tvPlateNumbers.setTextColor(getResources().getColor(R.color._333333));
            observableScrollView.smoothScrollTo(0, 1500);
            mSlideFromBottomInputPopup.showPopupWindow();
        });
        mSlideFromBottomInputPopup = new ATSlideFromBottomInputPopup(this, "VisitorAppointActivity");
        mSlideFromBottomInputPopup.setOnItemClickListener((view, text) -> {
            String parkNumber = tvPlateNumbers.getText().toString();
            if (TextUtils.isEmpty(text)) {
                if (parkNumber.length() > 1) {
                    tvPlateNumbers.setText(parkNumber.substring(0, parkNumber.length() - 1));
                } else {
                    tvPlateNumbers.setText(getString(R.string.at_input_plate_number));
                    tvPlateNumbers.setTextColor(getResources().getColor(R.color._CCCCCC));
                    mSlideFromBottomInputPopup.dismiss();
                    observableScrollView.smoothScrollTo(0, 1000);
                    mBottomPlatePopup.showPopupWindow();
                }
            } else {
                if (parkNumber.length() == 2)
                    parkNumber += "-";
                tvPlateNumbers.setText(parkNumber + text);
            }
        });
//        tvPlateNumbers.setText(mParkNumberList.get(0).getPark_number());
//        tvPlateNumbers.setTextColor(getResources().getColor(R.color._333333));

        rlPlate.setOnClickListener(view -> {
            observableScrollView.smoothScrollTo(0, 1000);
            if (getString(R.string.at_input_plate_number).equals(tvPlateNumbers.getText().toString())) {
                mBottomPlatePopup.showPopupWindow();
            } else {
                mSlideFromBottomInputPopup.showPopupWindow();
            }
        });

        tvVisiteTime.setText(sdf.format(System.currentTimeMillis()));
        tvLeaveTime.setText(sdf.format(System.currentTimeMillis() + 86400000));
        rgMotors.check(R.id.rb_motors_no);
        rgParkFee.check(R.id.rb_park_no);

        rlVisiteTime.setOnClickListener(view -> {
            if (mDialogAll.isAdded())
                return;
            mTextFlag = false;
            mDialogAll.show(getSupportFragmentManager(), "visite_time");
        });
        rlLeaveTime.setOnClickListener(view -> {
            createLeaveDialog(tvVisiteTime.getText().toString());
        });
        rlVisitorRoom.setOnClickListener(view -> {
            if (TextUtils.isEmpty(mAllVillageList)) {
                findTreeBuilding();
            } else {
                mATSelectVillagePopup = new ATWYSelectVillagePopup(this, mAllVillageList, hashMap);
                mATSelectVillagePopup.setOnItemClickListener((HashMap<Integer, Integer> hashMap, int rank, int position) -> {
                    this.hashMap = hashMap;
                    tvVisiteRoom.setText(getVillageName(rank, position));
                    findPersonMessage();
                });
                mATSelectVillagePopup.showPopupWindow();
            }
        });

        rlOwner.setOnClickListener(view -> {
            if (TextUtils.isEmpty(buildingCode)) {
                showToast(getString(R.string.at_select_visit_room_first));
                if (TextUtils.isEmpty(mAllVillageList)) {
                    findTreeBuilding();
                } else {
                    mATSelectVillagePopup = new ATWYSelectVillagePopup(this, mAllVillageList, hashMap);
                    mATSelectVillagePopup.setOnItemClickListener((HashMap<Integer, Integer> hashMap, int rank, int position) -> {
                        this.hashMap = hashMap;
                        tvVisiteRoom.setText(getVillageName(rank, position));
                        findPersonMessage();
                    });
                    mATSelectVillagePopup.showPopupWindow();
                }
            } else {
                if (mATFamilyMessageBeans == null)
                    findPersonMessage();
                else {
                    mATOptionsPopup.showPopupWindow();
                }
            }
        });
        initDialog();
    }

    public void addPermissByPermissionList(Activity activity, String[] permissions, int request) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> mPermissionList = new ArrayList<>();
            for (int i = 0; i < permissions.length; i++) {
                if (ContextCompat.checkSelfPermission(activity, permissions[i])
                        != PackageManager.PERMISSION_GRANTED) {
                    mPermissionList.add(permissions[i]);
                }
            }
            if (mPermissionList.isEmpty()) {
                showContacts();
            } else {
                String[] permissionsNew = mPermissionList.toArray(new String[mPermissionList.size()]);
                ActivityCompat.requestPermissions(activity, permissionsNew, request);
            }
        }
    }

    private void showContacts() {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.PICK");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setType("vnd.android.cursor.dir/phone_v2");
        startActivityForResult(intent, 1);
    }

    private boolean isMobileNO(String mobiles) {
        return !TextUtils.isEmpty(mobiles) && mobiles.matches("[1][3456789]\\d{9}");
    }

    private void updateLitepal(String park_number) {
        ATParkNumberBean parkNumberBean = mATParkNumberDao.getByParkNumber(park_number);
        parkNumberBean.setCreate_time(new Date().getTime());
        mATParkNumberDao.update(parkNumberBean);
    }

    private void initLitepal() {
        List<String> s = new ArrayList<>();
        s.add("新");
        s.add("宁");
        s.add("青");
        s.add("甘");
        s.add("陕");
        s.add("藏");
        s.add("云");
        s.add("贵");
        s.add("川");
        s.add("渝");
        s.add("琼");
        s.add("桂");
        s.add("晋");
        s.add("蒙");
        s.add("鄂");
        s.add("豫");
        s.add("鲁");
        s.add("冀");
        s.add("闽");
        s.add("皖");
        s.add("浙");
        s.add("苏");
        s.add("沪");
        s.add("黑");
        s.add("吉");
        s.add("辽");
        s.add("津");
        s.add("京");
        s.add("湘");
        s.add("粤");
        s.add("赣");
        // 添加用户数据
        for (int i = 0; i < s.size(); i++) {
            ATParkNumberBean parkNumberBean = new ATParkNumberBean();
            parkNumberBean.setCreate_time(new Date().getTime() + i * 10);
            parkNumberBean.setPark_number(s.get(i));
            mATParkNumberDao.add(parkNumberBean);
        }
        mParkNumberList = mATParkNumberDao.getAll();
    }


    private void initDialog() {
        long tenYears = 10L * 365 * 1000 * 60 * 60 * 24L;
        mDialogAll = new TimePickerDialog.Builder()
                .setCallBack(this)
                .setCancelStringId(getString(R.string.at_cancel))
                .setSureStringId(getString(R.string.at_sure))
                .setTitleStringId("")
                .setYearText(getString(R.string.at_year))
                .setMonthText(getString(R.string.at_month))
                .setDayText(getString(R.string.at_day))
                .setHourText(getString(R.string.at_hour1))
                .setMinuteText(getString(R.string.at_minute))
                .setCyclic(true)
                .setMinMillseconds(System.currentTimeMillis())
                .setMaxMillseconds(System.currentTimeMillis() + tenYears)
                .setCurrentMillseconds(System.currentTimeMillis())
                .setThemeColor(getResources().getColor(R.color._1478C8))
                .setWheelItemTextNormalColor(getResources().getColor(R.color.timetimepicker_default_text_color))
                .setWheelItemTextSelectorColor(getResources().getColor(R.color.timepicker_toolbar_bg))
                .setWheelItemTextSize(12)
                .build();

        pvOptions = new ATOptionsPickerBuilder(this, new ATOnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
            }
        }).setLayoutRes(R.layout.at_pickerview_custom_options_yes_no, v -> {
            v.findViewById(R.id.tv_sure).setOnClickListener(view -> {
                park_name_position = ((ATWheelView) v.findViewById(R.id.options1)).getCurrentItem();
                tvVisitePark.setText(mParkNameList.get(park_name_position));
                tvVisitePark.setTextColor(getResources().getColor(R.color._333333));
                pvOptions.dismiss();
            });
            v.findViewById(R.id.tv_cancel).setOnClickListener(view -> pvOptions.dismiss());
        })
                .isDialog(true)
                .setDividerColor(0xFFEEEEEE)
                .setContentTextSize(20)
                .setOutSideCancelable(true)
                .setOptionsSelectChangeListener((options1, options2, options3) -> {
                })
                .build();
    }

    private void createLeaveDialog(String time) {
        mTextFlag = true;
        TimePickerDialog mDialogLeave = new TimePickerDialog.Builder()
                .setCallBack(this)
                .setCancelStringId(getString(R.string.at_cancel))
                .setSureStringId(getString(R.string.at_sure))
                .setTitleStringId("")
                .setYearText(getString(R.string.at_year))
                .setMonthText(getString(R.string.at_month))
                .setDayText(getString(R.string.at_day))
                .setHourText(getString(R.string.at_hour1))
                .setMinuteText(getString(R.string.at_minute))
                .setCyclic(false)
                .setMinMillseconds(getLongTime(time))
//                .setMaxMillseconds(getLongTime(time) + 1000 * 60 * 60 * 24L)
                .setCurrentMillseconds(System.currentTimeMillis())
                .setThemeColor(getResources().getColor(R.color._1478C8))
                .setWheelItemTextNormalColor(getResources().getColor(R.color.timetimepicker_default_text_color))
                .setWheelItemTextSelectorColor(getResources().getColor(R.color.timepicker_toolbar_bg))
                .setWheelItemTextSize(12)
                .build();
        mDialogLeave.show(getSupportFragmentManager(), "leave_time");
    }

    @Override
    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
        String time = getDateToString(millseconds);
        if (mTextFlag) {
            tvLeaveTime.setText(time);
        } else {
            tvVisiteTime.setText(time);
            tvLeaveTime.setText(getString(R.string.at_select_leave_time));
            createLeaveDialog(time);
        }
    }

    private Long getLongTime(String timeString) {
        long l = 0;
        Date d;
        try {
            d = sdf.parse(timeString);
            l = d.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return l;
    }

    public String getDateToString(long time) {
        Date d = new Date(time);
        return sdf.format(d);
    }

    private String getVillageName(Integer rank, Integer position) {
        String allVillageList = mAllVillageList;
        List<AllVillageDetailBean> list = new ArrayList<>();
        List<String> keyList = new ArrayList<>();
        List<String> valueList = new ArrayList<>();
        Iterator<String> it;
        String name = "";
        try {
            for (int i = 0; i < rank + 1; i++) {
                org.json.JSONArray jsonArray = new org.json.JSONArray(allVillageList);
                list.clear();
                list = new ArrayList<>();
                int j = -1;
                do {
                    j++;
                    for (int k = 0; k < jsonArray.length(); k++) {
                        keyList.clear();
                        valueList.clear();
                        org.json.JSONObject jsonObject = jsonArray.getJSONObject(k);
                        it = jsonObject.keys();
                        AllVillageDetailBean allVillageDetailBean = new AllVillageDetailBean();
                        while (it.hasNext()) {
                            keyList.add(it.next());
                        }
                        it = jsonObject.keys();
                        while (it.hasNext()) {
                            valueList.add(jsonObject.getString(it.next()));
                        }
                        for (int l = 0; l < keyList.size(); l++) {
                            if ("name".equals(keyList.get(l))) {
                                allVillageDetailBean.setName(valueList.get(l));
                            } else if ("buildingCode".equals(keyList.get(l))) {
                                allVillageDetailBean.setCode(valueList.get(l));
                            } else {
                                allVillageDetailBean.setString(valueList.get(l));
                            }
                        }
                        if (hashMap.get(i) == j && hashMap.get(i) == k) {
                            name += allVillageDetailBean.getName();
                        }
                        list.add(allVillageDetailBean);
                    }
                } while (!(position == -1 || j == hashMap.get(i).intValue() || (i == rank && j == position)));
                allVillageList = list.get(hashMap.get(i).intValue()).getString();
                if (TextUtils.isEmpty(allVillageList)){
                    buildingCode = list.get(hashMap.get(i).intValue()).getCode();
                    return name;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public void requestResult(String result, String url) {
        try {
            org.json.JSONObject jsonResult = new org.json.JSONObject(result);
            if ("200".equals(jsonResult.getString("code"))) {
                switch (url) {
                    case ATConstants.Config.SERVER_URL_FINDPERSONMESSAGE:
                        mATFamilyMessageBeans = gson.fromJson(jsonResult.getString("data"), new TypeToken<List<ATFamilyMessageBean>>() {
                        }.getType());
                        List<String> nameList = new ArrayList<>();
                        for (ATFamilyMessageBean familyMemberBean : mATFamilyMessageBeans) {
                            nameList.add(familyMemberBean.getPersonName());
                        }
                        runOnUiThread(() -> {
                            mATOptionsPopup = new ATOptionsPopup(this, "选择拜访业主", nameList);
                            mATOptionsPopup.setOnItemClickListener(new ATOnPopupItemClickListener() {
                                @Override
                                public void onItemClick(int i1, int i2) {
                                    tvOwner.setText(nameList.get(i1));
                                    tvOwner.setTextColor(getResources().getColor(R.color._333333));
                                    ownerPerson = mATFamilyMessageBeans.get(i1).getPersonCode();
                                }

                                @Override
                                public void onItemClick(String s1, String s2, String s3) {
                                }
                            });
                            mATOptionsPopup.showPopupWindow();
                        });
                        break;
                    case ATConstants.Config.SERVER_URL_FINDTREEBUILDING:
                        mAllVillageList = jsonResult.getString("list");
                        break;
                    case ATConstants.Config.SERVER_URL_ADDPASSVISITORRESERVATIONBYPROPERTY:
                        String id = jsonResult.getJSONObject("result").getString("id");
                        startActivity(new Intent(this, ATVisitorAppointResultActivity.class)
                                .putExtra("id", id));
                        finish();
                        break;
                    case ATConstants.Config.SERVER_URL_GETPARKLOTLIST:
                        mATParkNameBeanList = gson.fromJson(jsonResult.getString("data"), new TypeToken<List<ATParkNameBean>>() {
                        }.getType());
                        mParkNameList.clear();
                        if (mATParkNameBeanList.size() > 0) {
                            for (int i = 0; i < mATParkNameBeanList.size(); i++) {
                                mParkNameList.add(mATParkNameBeanList.get(i).getParkName());
                            }
                            pvOptions.setPicker(mParkNameList);
                            tvVisitePark.setText(mParkNameList.get(0));
                            tvVisitePark.setTextColor(getResources().getColor(R.color._333333));
                        }
                        break;
                }
            } else {
                showToast(jsonResult.getString("message"));
            }
            closeBaseProgressDlg();
        } catch (JSONException e) {
            e.printStackTrace();
            closeBaseProgressDlg();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean hasAllGranted = true;
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                hasAllGranted = false;
                break;
            }
        }
        if (hasAllGranted) {
            showContacts();
        } else {
            dealwithPermiss(this, permissions[0]);
        }

    }

    public void dealwithPermiss(final Activity context, String permission) {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(context, permission)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("操作提示")
                    .setMessage("注意：当前缺少必要权限！\n请点击“设置”-“权限”-打开所需权限\n最后点击两次后退按钮，即可返回")
                    .setPositiveButton("去授权", (dialog, which) -> {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", context.getApplicationContext().getPackageName(), null);
                        intent.setData(uri);
                        context.startActivity(intent);
                    })
                    .setNegativeButton("取消", (dialog, which) -> Toast.makeText(ATVisiteAppointActivity.this, "取消操作", Toast.LENGTH_SHORT).show()).show();

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                String phoneNum = null;
                String contactName = null;
                ContentResolver contentResolver = getContentResolver();
                Cursor cursor = null;
                if (uri != null) {
                    cursor = contentResolver.query(uri,
                            new String[]{"display_name", "data1"}, null, null, null);
                }
                assert cursor != null;
                while (cursor.moveToNext()) {
                    contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    phoneNum = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                }
                cursor.close();
                if (phoneNum != null) {
                    phoneNum = phoneNum.replaceAll("-", " ");
                    phoneNum = phoneNum.replaceAll(" ", "");
                }
                etVisitorName.setText(contactName);
                etVisitorPhone.setText(phoneNum);
            }
        }
    }
}