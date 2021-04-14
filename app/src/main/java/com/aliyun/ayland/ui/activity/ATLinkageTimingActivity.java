package com.aliyun.ayland.ui.activity;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.ayland.base.ATBaseActivity;
import com.aliyun.ayland.interfaces.ATOnPopupItemClickListener;
import com.aliyun.ayland.widget.contrarywind.view.ATWheelView;
import com.aliyun.ayland.widget.pickerview.builder.ATTimePickerBuilder;
import com.aliyun.ayland.widget.pickerview.view.ATTimePickerView;
import com.aliyun.ayland.widget.popup.ATLinkageTimePopup;
import com.aliyun.ayland.widget.titlebar.ATMyTitleBar;
import com.anthouse.wyzhuoyue.R;

public class ATLinkageTimingActivity extends ATBaseActivity {
    private static final int REQUEST_CODE_CRON_WEEK = 0x1001;
    private ATLinkageTimingActivity mContext;
    private ATWheelView wheelMin, wheelHour;
    private String cron_week, week_text, uri;
    private StringBuilder repeat = new StringBuilder();
    private ATLinkageTimePopup mLinkageTimePopup;
    private boolean begin;
    private ATMyTitleBar titleBar;
    private LinearLayout llTiming, llCondition;
    private RelativeLayout rlTriggerTiming, rlBegin, rlEnd, rlRepeat;
    private TextView tvBeginTime, tvEndTime, tvWeek;

    @Override
    protected int getLayoutId() {
        return R.layout.at_activity_linkage_timing;
    }

    @Override
    protected void findView() {
        titleBar = findViewById(R.id.titlebar);
        tvBeginTime = findViewById(R.id.tv_begin_time);
        tvEndTime = findViewById(R.id.tv_end_time);
        tvWeek = findViewById(R.id.tv_week);
        llCondition = findViewById(R.id.ll_condition);
        llTiming = findViewById(R.id.ll_timing);
        rlTriggerTiming = findViewById(R.id.rl_trigger_timing);
        rlBegin = findViewById(R.id.rl_begin);
        rlEnd = findViewById(R.id.rl_end);
        rlRepeat = findViewById(R.id.rl_repeat);
        init();
    }

    @Override
    protected void initPresenter() {
    }

    private void init() {
        titleBar.setRightButtonText(getString(R.string.at_done));
        ATTimePickerView pvCustomTime = new ATTimePickerBuilder(this, (date, v) -> {//选中事件回调
//                btn_CustomTime.setText(getTime(date));
        }).setLayoutRes(R.layout.at_pickerview_custom_time, v -> {
            wheelMin = v.findViewById(R.id.min);
            wheelHour = v.findViewById(R.id.hour);
        })
                .isDialog(false)
                .isCyclic(true)
                .setContentTextSize(20)
                .setType(new boolean[]{false, false, false, true, true, false})
                .setLabel("", "", "", "", "", "")
                .setLineSpacingMultiplier(3.0f)
                .isCenterLabel(true)
                .setDividerColor(0xFF1478C8)
                .setDecorView(llTiming)
                .setOutSideCancelable(false)
                .build();
        pvCustomTime.setKeyBackCancelable(false);
        pvCustomTime.show(false);

        int flowType = getIntent().getIntExtra("flowType", 1);
        switch (flowType) {
            case 1:
                uri = "trigger/timer";
                rlTriggerTiming.setVisibility(View.VISIBLE);
                llCondition.setVisibility(View.GONE);
                if (getIntent().getStringExtra("params") != null) {
                    String[] cronArr = JSONObject.parseObject(getIntent().getStringExtra("params")).getString("cron").split(" ");
                    wheelHour.setCurrentItem(Integer.parseInt(cronArr[1]));
                    wheelMin.setCurrentItem(Integer.parseInt(cronArr[0]));
                    cron_week = cronArr[4];
                    week_text = cronArr[4].replace("0", "周日").replace("1", "周一").replace("2", "周二")
                            .replace("3", "周三").replace("4", "周四").replace("5", "周五")
                            .replace("6", "周六").replace("7", "周日").replaceAll(",", "、");
                    if ("*".equals(week_text) || week_text.length() == 20) {
                        week_text = "每天";
                        cron_week = "*";
                    }
                } else {
                    week_text = getString(R.string.at_every_day);
                    cron_week = "*";
                }
                break;
            case 2:
                uri = "condition/timeRange";
                if (getIntent().getStringExtra("params") != null) {
                    tvBeginTime.setText(JSONObject.parseObject(getIntent().getStringExtra("params")).getString("beginDate"));
                    tvEndTime.setText(JSONObject.parseObject(getIntent().getStringExtra("params")).getString("endDate"));
                    cron_week = JSONObject.parseObject(getIntent().getStringExtra("params")).getString("repeat");
                    week_text = cron_week.replace("0", "周日").replace("1", "周一").replace("2", "周二")
                            .replace("3", "周三").replace("4", "周四").replace("5", "周五")
                            .replace("6", "周六").replace("7", "周日").replaceAll(",", "、");
                    if ("*".equals(week_text) || week_text.length() == 20) {
                        week_text = "每天";
                        cron_week = "*";
                    }
                } else {
                    week_text = getString(R.string.at_every_day);
                    cron_week = "*";
                }
                rlTriggerTiming.setVisibility(View.GONE);
                llCondition.setVisibility(View.VISIBLE);
                break;
        }

        tvWeek.setText(week_text);
        mLinkageTimePopup = new ATLinkageTimePopup(this);
        mLinkageTimePopup.setOnItemClickListener(new ATOnPopupItemClickListener() {
            @Override
            public void onItemClick(int i1, int i2) {
                if (begin)
                    tvBeginTime.setText((i1 < 10 ? "0" + i1 : i1) + ":" + (i2 < 10 ? "0" + i2 : i2));
                else
                    tvEndTime.setText((i1 < 10 ? "0" + i1 : i1) + ":" + (i2 < 10 ? "0" + i2 : i2));
            }

            @Override
            public void onItemClick(String s1, String s2, String s3) {

            }
        });
        rlBegin.setOnClickListener(view -> {
            begin = true;
            mLinkageTimePopup.setCurrentTime(tvBeginTime.getText().toString(), begin);
            mLinkageTimePopup.showPopupWindow();
        });
        rlEnd.setOnClickListener(view -> {
            begin = false;
            mLinkageTimePopup.setCurrentTime(tvEndTime.getText().toString(), begin);
            mLinkageTimePopup.showPopupWindow();
        });
        rlRepeat.setOnClickListener(view -> startActivityForResult(new Intent(this, ATLinkageTimingRepeatActivity.class)
                .putExtra("cron_week", cron_week), REQUEST_CODE_CRON_WEEK));
        titleBar.setRightClickListener(() -> {
            JSONObject params = new JSONObject();
            String name, content;
            switch (flowType) {
                case 2:
                    uri = "condition/timeRange";
                    name = getString(R.string.at_time_limit);
                    content = tvBeginTime.getText().toString() + " " + tvEndTime.getText().toString() + " " + week_text;
                    params.put("format", "HH:mm");
                    params.put("beginDate", tvBeginTime.getText().toString());
                    params.put("endDate", tvEndTime.getText().toString());
                    params.put("timezoneID", "Asia/Shanghai");
                    params.put("repeat", "*".equals(cron_week) ? "1,2,3,4,5,6,7" : cron_week);
                    break;
                default:
                    uri = "trigger/timer";
                    name = getString(R.string.at_timing);
                    content = (wheelHour.getCurrentItem() < 10 ? "0" + wheelHour.getCurrentItem() : wheelHour.getCurrentItem()) + ":" + (wheelMin.getCurrentItem() < 10 ? "0" + wheelMin.getCurrentItem() : wheelMin.getCurrentItem()) + " " + week_text;
                    params.put("cron", wheelMin.getCurrentItem() + " " + wheelHour.getCurrentItem() + " * * " + cron_week);
                    params.put("cronType", "linux");
                    break;
            }
            setResult(RESULT_OK, getIntent().putExtra("name", name).putExtra("uri", uri)
                    .putExtra("content", content)
                    .putExtra("params", params.toJSONString()));
            finish();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_CRON_WEEK) {
            cron_week = data.getStringExtra("cron_week");
            if (cron_week.length() != 13 && cron_week.length() != 0) {
                week_text = cron_week.replace("0", "周日").replace("1", "周一").replace("2", "周二")
                        .replace("3", "周三").replace("4", "周四").replace("5", "周五")
                        .replace("6", "周六").replaceAll(",", "、");
            } else {
                week_text = getString(R.string.at_every_day);
                cron_week = "*";
            }
            tvWeek.setText(week_text);
        }
    }
}