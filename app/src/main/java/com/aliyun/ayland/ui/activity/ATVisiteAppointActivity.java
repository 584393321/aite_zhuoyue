package com.aliyun.ayland.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.ayland.base.ATBaseActivity;
import com.aliyun.ayland.contract.ATMainContract;
import com.aliyun.ayland.data.ATAppointmentOpenBean;
import com.aliyun.ayland.data.ATHouseBean;
import com.aliyun.ayland.data.ATParkBean;
import com.aliyun.ayland.data.ATParkNameBean;
import com.aliyun.ayland.data.ATParkNumberBean;
import com.aliyun.ayland.data.db.ATParkNumberDao;
import com.aliyun.ayland.global.ATGlobalApplication;
import com.aliyun.ayland.global.ATConstants;
import com.aliyun.ayland.presenter.ATMainPresenter;
import com.aliyun.ayland.widget.ATObservableScrollView;
import com.aliyun.ayland.widget.contrarywind.view.ATWheelView;
import com.aliyun.ayland.widget.pickerview.builder.ATOptionsPickerBuilder;
import com.aliyun.ayland.widget.pickerview.listener.ATOnOptionsSelectChangeListener;
import com.aliyun.ayland.widget.pickerview.listener.ATOnOptionsSelectListener;
import com.aliyun.ayland.widget.pickerview.view.ATOptionsPickerView;
import com.aliyun.ayland.widget.popup.ATBottomPlatePopup;
import com.aliyun.ayland.widget.popup.ATSlideFromBottomInputPopup;
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
    private int park_name_position = 0;
    private String visitorName, visitorTel, visite_time, leave_time, carNumber, visite_park;
    private List<String> mParkNameList = new ArrayList<>();
    private List<ATParkBean> mATParkNameBeanList = new ArrayList<>();
    private ATHouseBean mHouseBean;
    private String[] permissList = {Manifest.permission.READ_CONTACTS, Manifest.permission.READ_PHONE_STATE};
    private TimePickerDialog mDialogAll;
    private boolean mTextFlag;
    private Button btnSubscribe;
    private RadioGroup rgMotors, rgParkFee;
    private ATObservableScrollView observableScrollView;
    private LinearLayout llShareAppoint;
    private RelativeLayout rlPark, rlPlate, rlVisiteTime, rlLeaveTime;
    private RadioButton rbMotorsYes, rbMotorsNo, rbParkYes, rbParkNo;
    private TextView tvVisitorRoom, tvVisiteTime, tvLeaveTime, tvShareAppoint, tvVisitePark, tvPlateNumbers, tvContact, tvRead;
    private EditText etVisitorName, etVisitorPhone;

    @Override
    protected int getLayoutId() {
        return R.layout.at_activity_visitor_appoint;
    }

    @Override
    protected void findView() {
        etVisitorName = findViewById(R.id.et_visitor_name);
        etVisitorPhone = findViewById(R.id.et_visitor_phone);
        tvVisitorRoom = findViewById(R.id.tv_visite_room);
        tvVisiteTime = findViewById(R.id.tv_visite_time);
        tvLeaveTime = findViewById(R.id.tv_leave_time);
        tvPlateNumbers = findViewById(R.id.tv_plate_numbers);
        tvVisitePark = findViewById(R.id.tv_visite_park);
        tvShareAppoint = findViewById(R.id.tv_share_appoint);
        tvContact = findViewById(R.id.tv_contact);
        tvRead = findViewById(R.id.tv_read);
        rbMotorsYes = findViewById(R.id.rb_motors_yes);
        rbMotorsNo = findViewById(R.id.rb_motors_no);
        rgMotors = findViewById(R.id.rg_motors);
        rgParkFee = findViewById(R.id.rg_park_fee);
        rbParkYes = findViewById(R.id.rb_park_yes);
        rbParkNo = findViewById(R.id.rb_park_no);
        btnSubscribe = findViewById(R.id.btn_subscribe);
        llShareAppoint = findViewById(R.id.ll_share_appoint);
        rlPark = findViewById(R.id.rl_park);
        rlPlate = findViewById(R.id.rl_plate);
        rlVisiteTime = findViewById(R.id.rl_visite_time);
        rlLeaveTime = findViewById(R.id.rl_leave_time);
        observableScrollView = findViewById(R.id.observableScrollView);
        init();
    }

    @Override
    protected void initPresenter() {
        mPresenter = new ATMainPresenter(this);
        mPresenter.install(this);
        parkingList();
        findAppointmentOpen();
    }

    public void findAppointmentOpen() {
        if (mHouseBean == null)
            return;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("villageCode", mHouseBean.getVillageId());
        mPresenter.request(ATConstants.Config.SERVER_URL_FINDAPPOINTMENTOPEN, jsonObject);
    }

    public void parkingList() {
        if (mHouseBean == null)
            return;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("villageCode", mHouseBean.getVillageId());
        mPresenter.request(ATConstants.Config.SERVER_URL_PARKINGLIST, jsonObject);
    }

    private void shareView(String url) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, url);
        intent.setType("text/*");
        //        intent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
//        intent.setType("image/*");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        PackageManager pm = getPackageManager();
        List<ResolveInfo> resInfo = pm.queryIntentActivities(intent, 0);
        if (resInfo.isEmpty())
            return;

        List<Intent> targetIntents = new ArrayList<>();
        for (ResolveInfo resolveInfo : resInfo) {
            ActivityInfo activityInfo = resolveInfo.activityInfo;
            if (activityInfo.packageName.contains("com.tencent.mm") || activityInfo.packageName.contains("com.tencent.mobileqq")
                    || activityInfo.packageName.contains("com.android.mms")) {
                //过滤掉qq收藏
                if (resolveInfo.loadLabel(pm).toString().contains("QQ收藏") || resolveInfo.loadLabel(pm).toString().contains("微信收藏")
                        || resolveInfo.loadLabel(pm).toString().contains("朋友圈") || resolveInfo.loadLabel(pm).toString().contains("我的电脑")
                        || resolveInfo.loadLabel(pm).toString().contains("面对面快传")) {
                    continue;
                }
                Intent target = new Intent();
                target.setAction(Intent.ACTION_SEND);
                target.setComponent(new ComponentName(activityInfo.packageName, activityInfo.name));

                target.putExtra(Intent.EXTRA_TEXT, url);
                target.setType("text/*");
//                target.putExtra(Intent.EXTRA_STREAM, bitmapUri);
//                target.setType("image/*");
                targetIntents.add(new LabeledIntent(target, activityInfo.packageName, resolveInfo.loadLabel(pm), resolveInfo.icon));
            }
        }
        if (targetIntents.size() <= 0)
            return;
        Intent chooser = Intent.createChooser(targetIntents.remove(targetIntents.size() - 1), "");
        if (chooser == null) return;
        LabeledIntent[] labeledIntents = targetIntents.toArray(new LabeledIntent[targetIntents.size()]);
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, labeledIntents);
        startActivity(chooser);
    }

    public void shareOperation() {
        showBaseProgressDlg();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("visitorHouse", mHouseBean.getName());
        jsonObject.put("ownerPerson", ATGlobalApplication.getATLoginBean().getPersonCode());
        jsonObject.put("buildingCode", mHouseBean.getBuildingCode());
        jsonObject.put("villageCode", mHouseBean.getVillageId());
        jsonObject.put("ownerName", ATGlobalApplication.getATLoginBean().getPersonName());
        jsonObject.put("visitorName", etVisitorName.getText().toString());
        jsonObject.put("visitorTel", etVisitorPhone.getText().toString());
        jsonObject.put("reservationStartTime", tvVisiteTime.getText());
        jsonObject.put("reservationEndTime", tvLeaveTime.getText());
        jsonObject.put("hasCar", rgMotors.getCheckedRadioButtonId() == R.id.rb_motors_yes ? 1 : 0);
        jsonObject.put("carNumber", getString(R.string.at_input_plate_number).contentEquals(tvPlateNumbers.getText()) ? "" : tvPlateNumbers.getText());
        jsonObject.put("carPark", getString(R.string.at_select_park).contentEquals(tvVisitePark.getText()) ? "" : mATParkNameBeanList.get(park_name_position).getId());
        mPresenter.request(ATConstants.Config.SERVER_URL_SHAREOPERATION, jsonObject);
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
        String visitorRoom = tvVisitorRoom.getText().toString();
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
        if (rbMotorsYes.isChecked()) {
            jsonObject.put("carNumber", carNumber.replace("-", ""));
            if (mATParkNameBeanList.size() != 0)
                jsonObject.put("carPark", mATParkNameBeanList.get(park_name_position).getId());
        }
        jsonObject.put("createPerson", ATGlobalApplication.getHid());
        jsonObject.put("hasCar", rbMotorsYes.isChecked() ? 1 : -1);
        jsonObject.put("payPerson", rbParkYes.isChecked() ? 1 : 2);
        jsonObject.put("intermediary", -1);
        jsonObject.put("reservationEndTime", tvLeaveTime.getText());
        jsonObject.put("reservationStartTime", tvVisiteTime.getText());
        jsonObject.put("visitorName", visitorName);
        jsonObject.put("visitorHouse", visitorRoom);
        jsonObject.put("visitorTel", visitorTel);
        jsonObject.put("buildingCode", mHouseBean.getBuildingCode());
        jsonObject.put("villageId", mHouseBean.getVillageId());
        mPresenter.request(ATConstants.Config.SERVER_URL_ADDVISITORRESERVATION, jsonObject);
    }

    private void init() {
        tvShareAppoint.setOnClickListener(view -> shareOperation());
        btnSubscribe.setOnClickListener(view -> addVisitorReservation());
        tvRead.setSelected(true);
        tvRead.setOnClickListener(v -> tvRead.setSelected(!tvRead.isSelected()));

        rlPark.setOnClickListener(view -> {
            if (mParkNameList.size() > 0) {
                pvOptions.setSelectOptions(park_name_position);
                pvOptions.show();
            } else {
                parkingList();
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
            tvVisitorRoom.setText(mHouseBean.getHouseAddress());
            tvVisitorRoom.setTextColor(getResources().getColor(R.color._333333));
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
                .setOptionsSelectChangeListener(new ATOnOptionsSelectChangeListener() {
                    @Override
                    public void onOptionsSelectChanged(int options1, int options2, int options3) {
                    }
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

    @Override
    public void requestResult(String result, String url) {
        try {
            org.json.JSONObject jsonResult = new org.json.JSONObject(result);
            if ("200".equals(jsonResult.getString("code"))) {
                switch (url) {
                    case ATConstants.Config.SERVER_URL_SHAREOPERATION:
                        shareView(jsonResult.getString("url"));
                        break;
                    case ATConstants.Config.SERVER_URL_ADDVISITORRESERVATION:
                        String id = jsonResult.getString("id");
                        startActivity(new Intent(this, ATVisitorAppointResultActivity.class)
                                .putExtra("id", id));
                        finish();
                        break;
                    case ATConstants.Config.SERVER_URL_FINDAPPOINTMENTOPEN:
                        ATAppointmentOpenBean appointmentOpenBean = gson.fromJson(jsonResult.getString("result"), ATAppointmentOpenBean.class);
                        llShareAppoint.setVisibility(appointmentOpenBean.isAppointment() ? View.VISIBLE : View.GONE);
                        break;
                    case ATConstants.Config.SERVER_URL_PARKINGLIST:
                        mATParkNameBeanList = gson.fromJson(jsonResult.getString("data"), new TypeToken<List<ATParkBean>>() {
                        }.getType());
                        mParkNameList.clear();
                        if (mATParkNameBeanList.size() > 0) {
                            for (ATParkBean atParkBean : mATParkNameBeanList) {
                                mParkNameList.add(atParkBean.getName());
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
                    .setPositiveButton("去授权", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", context.getApplicationContext().getPackageName(), null);
                            intent.setData(uri);
                            context.startActivity(intent);
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(ATVisiteAppointActivity.this, "取消操作", Toast.LENGTH_SHORT).show();
                        }
                    }).show();
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