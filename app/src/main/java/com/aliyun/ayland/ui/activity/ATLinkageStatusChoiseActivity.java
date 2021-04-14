package com.aliyun.ayland.ui.activity;

import android.app.Dialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.ayland.base.ATBaseActivity;
import com.aliyun.ayland.data.ATDeviceTslDataType;
import com.aliyun.ayland.ui.adapter.ATLinkageStatusChooseRVAdapter;
import com.aliyun.ayland.widget.contrarywind.view.ATWheelView;
import com.aliyun.ayland.widget.pickerview.builder.ATOptionsPickerBuilder;
import com.aliyun.ayland.widget.pickerview.builder.ATTimePickerBuilder;
import com.aliyun.ayland.widget.pickerview.listener.ATCustomListener;
import com.aliyun.ayland.widget.pickerview.listener.ATOnOptionsSelectChangeListener;
import com.aliyun.ayland.widget.pickerview.listener.ATOnOptionsSelectListener;
import com.aliyun.ayland.widget.pickerview.listener.ATOnTimeSelectListener;
import com.aliyun.ayland.widget.pickerview.view.ATOptionsPickerView;
import com.aliyun.ayland.widget.pickerview.view.ATTimePickerView;
import com.aliyun.ayland.widget.titlebar.ATMyTitleBar;
import com.anthouse.wyzhuoyue.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ATLinkageStatusChoiseActivity extends ATBaseActivity {
    private HashMap<String, Integer> statusMap = new HashMap<>();
    private String valueType, valueName, content, compareType, unit = "", time;
    private ATOptionsPickerView pvOptions;
    private int compareValue = 0, delayedExecutionSeconds = 0;
    private ATWheelView mWheelCompareType, mWheelCompareValue, mWheel;
    private List<String> compareTypeCNList = new ArrayList<>(), compareTypeENList = new ArrayList<>(), statusList = new ArrayList<>();
    private List<Integer> compareBiggerList = new ArrayList<>(), compareEqualList = new ArrayList<>(), compareLessList = new ArrayList<>();
    private List<List<Integer>> compareValueList = new ArrayList<>();
    private ATTimePickerView mPvCustomTime;
    private String mDeviceTslDataTypeName;
    private ATMyTitleBar titleBar;
    private RecyclerView rvTca;
    private TextView tvUnit, tvDelay, tvDelayPerform;
    private RelativeLayout rlTiming, rlTriggerTiming, rlDelayPopup1, rlDelay;

    @Override
    protected int getLayoutId() {
        return R.layout.at_activity_linkage_status_choise;
    }

    @Override
    protected void findView() {
        titleBar = findViewById(R.id.titlebar);
        tvUnit = findViewById(R.id.tv_unit);
        tvDelay = findViewById(R.id.tv_delay);
        tvDelayPerform = findViewById(R.id.tv_delay_perform);
        rlTiming = findViewById(R.id.rl_timing);
        rlDelay = findViewById(R.id.rl_delay);
        rlTriggerTiming = findViewById(R.id.rl_trigger_timing);
        rlDelayPopup1 = findViewById(R.id.rl_delay_popup1);
        rvTca = findViewById(R.id.rv_tca);
        init();
    }

    @Override
    protected void initPresenter() {

    }

    private void initOptionPicker() {
        pvOptions = new ATOptionsPickerBuilder(this, new ATOnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
            }
        }).setLayoutRes(R.layout.at_pickerview_custom_options, new ATCustomListener() {
            @Override
            public void customLayout(View v) {
                mWheelCompareType = v.findViewById(R.id.options1);
                mWheelCompareValue = v.findViewById(R.id.options2);
            }
        })
                .isDialog(false)
                .setContentTextSize(20)
                .setSelectOptions(compareTypeENList.indexOf(compareType), compareValueList.get(compareTypeENList.indexOf(compareType)).indexOf(compareValue))//默认选中项compareValue,compareType
                .setLineSpacingMultiplier(3.0f)
                .setDividerColor(0xFFEEEEEE)
                .setOutSideCancelable(false)
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .setDecorView(rlTiming)
                .setOptionsSelectChangeListener(new ATOnOptionsSelectChangeListener() {
                    @Override
                    public void onOptionsSelectChanged(int options1, int options2, int options3) {
//                        Log.e("onOptionsSelectChanged: ", );
                    }
                })
                .build();
        pvOptions.setPicker(compareTypeCNList, compareValueList);//二级选择器
        pvOptions.show(false);//二级选择器
    }

    private void init() {
        content = getIntent().getStringExtra("content");
        if (content.contains(" ")) {
//            titleBar.setTitle(content.split(" ")[0].split("~")[1]);
//        } else {
            titleBar.setTitle(content.split(" ")[0]);
        }
        titleBar.setRightButtonText(getString(R.string.at_done));
//        if (content.contains(" ")) {
//        } else {
//            if(content.contains("_")) {
//                titleBar.setTitle(content.split("_")[1]);
//            }else {
//                titleBar.setTitle(content);
//            }
//        }

        JSONObject params = JSONObject.parseObject(getIntent().getStringExtra("params"));
        if (params.getJSONObject("propertyItems") != null) {
            for (String s : params.getJSONObject("propertyItems").keySet()) {
                compareValue = params.getJSONObject("propertyItems").getInteger(s);
                compareType = "==";
            }
        } else if (params.getString("compareType") != null) {
            compareValue = params.getInteger("compareValue");
            compareType = params.getString("compareType");
        }
        int position = 0;
        ATDeviceTslDataType deviceTslDataType = gson.fromJson(getIntent().getStringExtra("dataType"), ATDeviceTslDataType.class);
        valueType = deviceTslDataType.getType();
        switch (valueType) {
            case "bool":
            case "enum":
                rvTca.setVisibility(View.VISIBLE);
                rlTriggerTiming.setVisibility(View.GONE);
                for (Map.Entry<String, Object> entry : deviceTslDataType.getSpecs().entrySet()) {
                    statusMap.put(entry.getValue().toString(), Integer.parseInt(entry.getKey()));
                    statusList.add(entry.getValue().toString());
                    if (compareValue == Integer.parseInt(entry.getKey())) {
                        position = statusList.size() - 1;
                    }
                }
                ATLinkageStatusChooseRVAdapter linkageStatusChooseRVAdapter = new ATLinkageStatusChooseRVAdapter(this, statusList, position);
                rvTca.setLayoutManager(new LinearLayoutManager(this));
                rvTca.setAdapter(linkageStatusChooseRVAdapter);
                linkageStatusChooseRVAdapter.setOnItemClickListener((view, position1) -> {
                    compareValue = statusMap.get(statusList.get(position1));
                    valueName = statusList.get(position1);
                });
                compareTypeCNList.add("等于");
                compareTypeENList.add("==");
                compareType = "==";
                valueName = statusList.get(position);
                break;
            case "int":
            case "double":
            case "float":
                rvTca.setVisibility(View.GONE);
                unit = deviceTslDataType.getSpecs().getString("unit");
                tvUnit.setText(unit);
                rlTriggerTiming.setVisibility(View.VISIBLE);
                switch (getIntent().getStringExtra("uri")) {
                    case "trigger/device/property":
                    case "trigger/device/event":
                    case "condition/device/property":
                        compareTypeCNList.add("大于");
                        compareTypeCNList.add("等于");
                        compareTypeCNList.add("小于");
                        compareTypeENList.add(">");
                        compareTypeENList.add("==");
                        compareTypeENList.add("<");
                        for (int i = Integer.parseInt(deviceTslDataType.getSpecs().getString("min"))
                             ; i <= Integer.parseInt(deviceTslDataType.getSpecs().getString("max"))
                                ; i += Integer.parseInt(deviceTslDataType.getSpecs().getString("step"))) {
                            if (i == Integer.parseInt(deviceTslDataType.getSpecs().getString("min"))) {
                                compareBiggerList.add(i);
                            } else if (i == Integer.parseInt(deviceTslDataType.getSpecs().getString("max"))) {
                                compareLessList.add(i);
                            } else {
                                compareBiggerList.add(i);
                                compareLessList.add(i);
                            }
                            compareEqualList.add(i);
                        }
                        compareValueList.add(compareBiggerList);
                        compareValueList.add(compareEqualList);
                        compareValueList.add(compareLessList);
                        break;
                    case "action/device/setProperty":
                        compareTypeCNList.add("等于");
                        compareTypeENList.add("==");
                        for (int i = Integer.parseInt(deviceTslDataType.getSpecs().getString("min"))
                             ; i <= Integer.parseInt(deviceTslDataType.getSpecs().getString("max"))
                                ; i += Integer.parseInt(deviceTslDataType.getSpecs().getString("step"))) {
                            compareEqualList.add(i);
                        }
                        compareValueList.add(compareEqualList);
                        break;
                }
                if (TextUtils.isEmpty(compareType)) {
                    compareType = "==";
                    compareValue = compareValueList.get(compareTypeENList.indexOf(compareType)).get(0);
                }
                valueName = compareValue + unit;
                initOptionPicker();
            default:
                break;
        }
        mPvCustomTime = new ATTimePickerBuilder(this, (date, v) -> {//选中事件回调
//                ((TextView) v).setText(sdf.format(date));
            Log.e("onTimeSelect: ", date.getHours() + "----" + date.getMinutes() + "---" + date.getSeconds());
            time = "";
            delayedExecutionSeconds = 0;
            if (date.getHours() != 0) {
                time += date.getHours() + "时";
                delayedExecutionSeconds += date.getHours() * 60 * 60;
            }
            if (date.getMinutes() != 0) {
                time += date.getMinutes() + "分";
                delayedExecutionSeconds += date.getMinutes() * 60;
            }
            if (date.getSeconds() != 0) {
                time += date.getSeconds() + "秒";
                delayedExecutionSeconds += date.getSeconds();
            }
            tvDelayPerform.setText(TextUtils.isEmpty(time) ? getString(R.string.at_perform_now) : String.format(getString(R.string.at_delay_after), time));
        })
                .setType(new boolean[]{false, false, false, true, true, true})
                .setLabel("", "", "", "时", "分", "秒")
                .setDividerColor(0xFFEEEEEE)
                .isCyclic(true)
                .isDialog(true)
                .setLineSpacingMultiplier(2.0f)
                .isCenterLabel(true)
                .build();
        Dialog mDialog = mPvCustomTime.getDialog();
        if (mDialog != null) {
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.BOTTOM);
            layoutParams.leftMargin = 0;
            layoutParams.rightMargin = 0;
            mPvCustomTime.getDialogContainerLayout().setLayoutParams(layoutParams);
            Window dialogWindow = mDialog.getWindow();
            if (dialogWindow != null) {
                dialogWindow.setWindowAnimations(R.style.picker_view_slide_anim);//修改动画样式
                dialogWindow.setGravity(Gravity.BOTTOM);//改成Bottom,底部显示
            }
        }

        ATTimePickerView pvDelay = new ATTimePickerBuilder(this, new ATOnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
//                btn_CustomTime.setText(getTime(date));
            }
        }).setLayoutRes(R.layout.at_pickerview_custom_time, new ATCustomListener() {
            @Override
            public void customLayout(View v) {
                mWheel = v.findViewById(R.id.second);
                mWheel.setOnItemSelectedListener(index -> {
//                        tvDelayPerform.setText(index == 0 ? getString(R.string.perform_now) : String.format(getString(R.string.delay_after), index));
//                        delayedExecutionSeconds = index;
                });
            }
        })
                .isDialog(false)
                .isCyclic(true)
                .setContentTextSize(20)
                .setType(new boolean[]{false, false, false, true, true, true})
                .setLabel("", "", "", "", "", "")
                .setLineSpacingMultiplier(3.0f)
                .isCenterLabel(true)
                .setDividerColor(0xFFEEEEEE)
                .setDecorView(rlDelayPopup1)
                .setOutSideCancelable(false)
                .build();
        pvDelay.setKeyBackCancelable(false);
        pvDelay.show(false);

        if ("action/device/setProperty".equals(getIntent().getStringExtra("uri"))) {
            rlDelay.setVisibility(View.VISIBLE);
            tvDelay.setVisibility(View.VISIBLE);
        }
        if (params.containsKey("delayedExecutionSeconds") && params.getInteger("delayedExecutionSeconds") != 0) {
            delayedExecutionSeconds = params.getInteger("delayedExecutionSeconds");
            tvDelayPerform.setText(String.format(getString(R.string.at_delay_after), getTime(delayedExecutionSeconds)));
//            rlDelayPopup.setVisibility(View.VISIBLE);
        }
        Calendar cd = Calendar.getInstance();

        cd.setTime(new Date(57600000 + delayedExecutionSeconds * 1000));
        mPvCustomTime.setDate(cd);

        rlDelay.setOnClickListener(view -> {
            mPvCustomTime.show(tvDelayPerform);
        });

        titleBar.setRightClickListener(() -> {
            if ("int".equals(valueType) || "double".equals(valueType) || "float".equals(valueType)) {
                compareType = compareTypeENList.get(mWheelCompareType.getCurrentItem());
                compareValue = compareValueList.get(mWheelCompareType.getCurrentItem()).get(mWheelCompareValue.getCurrentItem());
                valueName = String.valueOf(compareValueList.get(mWheelCompareType.getCurrentItem()).get(mWheelCompareValue.getCurrentItem())) + unit;
            }
            switch (getIntent().getStringExtra("uri")) {
                case "trigger/device/property":
                case "trigger/device/event":
                case "condition/device/property":
                    params.put("compareType", compareType);
                    params.put("compareValue", compareValue);
                    break;
                case "action/device/setProperty":
                    if (delayedExecutionSeconds != 0)
                        params.put("delayedExecutionSeconds", delayedExecutionSeconds);
                    JSONObject propertyItems = new JSONObject();
                    JSONObject identifier = new JSONObject();
                    identifier.put("valueName", valueName);
                    identifier.put("valueType", valueType);
                    JSONObject propertyNamesItems;
                    String propertyName = "";
                    if (params.containsKey("propertyNamesItems")) {
                        propertyNamesItems = params.getJSONObject("propertyNamesItems");
                        for (String property : propertyNamesItems.keySet()) {
                            propertyName = property;
                            break;
                        }
                    } else {
                        propertyNamesItems = new JSONObject();
                        propertyName = params.getString("propertyName");
                        if (TextUtils.isEmpty(propertyName))
                            for (String property : params.getJSONObject("propertyItems").keySet()) {
                                propertyName = property;
                                break;
                            }
                    }
                    propertyNamesItems.put(propertyName, identifier);
                    propertyItems.put(propertyName, compareValue);
                    params.put("propertyItems", propertyItems);
                    params.put("propertyNamesItems", propertyNamesItems);
                    break;
            }
            if ("int".equals(valueType)) {
                content = content.split(" ")[0] + " " + compareTypeCNList.get(compareTypeENList.indexOf(compareType)) + " " + valueName;
            } else {
                content = content.split(" ")[0] + " " + valueName;
            }
            if (delayedExecutionSeconds != 0) {
                content += " " + tvDelayPerform.getText().toString();
            }
            setResult(RESULT_OK, getIntent()
                    .putExtra("content", content)
                    .putExtra("params", params.toJSONString()));
            finish();
        });
    }

    private String getTime(long time) {
        String hour = time / (60 * 60) + "";
        String minute = time % (60 * 60) / 60 + "";
        String seconds = time % (60 * 60) % 60 + "";
        String all = "";
        if (!"0".equals(hour))
            all += hour + "时";
        if (!"0".equals(minute))
            all += minute + "分";
        if (!"0".equals(seconds))
            all += seconds + "秒";
        return all;
    }
}