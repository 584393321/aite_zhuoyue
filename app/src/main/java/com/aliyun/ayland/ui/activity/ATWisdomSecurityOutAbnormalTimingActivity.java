package com.aliyun.ayland.ui.activity;

import android.content.Intent;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aliyun.ayland.base.ATBaseActivity;
import com.aliyun.ayland.interfaces.ATOnPopupItemClickListener;
import com.aliyun.ayland.utils.ATTimeFormatUtils;
import com.aliyun.ayland.widget.contrarywind.view.ATWheelView;
import com.aliyun.ayland.widget.popup.ATLinkageTimePopup;
import com.aliyun.ayland.widget.titlebar.ATMyTitleBar;
import com.anthouse.wyzhuoyue.R;

public class ATWisdomSecurityOutAbnormalTimingActivity extends ATBaseActivity {
    private static final int REQUEST_CODE_CRON_WEEK = 0x1001;
    private ATWisdomSecurityOutAbnormalTimingActivity mContext;
    private ATWheelView wheelMin, wheelHour;
    private String cron_week = "7", week_text, uri;
    private StringBuilder repeat = new StringBuilder();
    private ATLinkageTimePopup mLinkageTimePopup;
    private boolean begin;
    private int week_day, beginTime, endTime;
    private ATMyTitleBar titleBar;
    private RelativeLayout rlBegin, rlEnd, rlRepeat;
    private TextView tvBeginTime, tvEndTime, tvWeek;

    @Override
    protected int getLayoutId() {
        return R.layout.at_activity_wisdom_security_out_abnormal_timing;
    }

    @Override
    protected void findView() {
        titleBar = findViewById(R.id.titlebar);
        rlRepeat = findViewById(R.id.rl_repeat);
        tvWeek = findViewById(R.id.tv_week);
        rlBegin = findViewById(R.id.rl_begin);
        tvBeginTime = findViewById(R.id.tv_begin_time);
        rlEnd = findViewById(R.id.rl_end);
        tvEndTime = findViewById(R.id.tv_end_time);
        init();
    }

    @Override
    protected void initPresenter() {
    }

    private void init() {
        week_day = getIntent().getIntExtra("week_day", 7);
        beginTime = getIntent().getIntExtra("beginTime", 0);
        endTime = getIntent().getIntExtra("endTime", 1439);
        week_text = getResources().getStringArray(R.array.every_weekday)[week_day];

        titleBar.setRightButtonText(getString(R.string.at_done));
        tvWeek.setText(week_text);

        tvBeginTime.setText(ATTimeFormatUtils.minuteToTime(beginTime));
        tvEndTime.setText(ATTimeFormatUtils.minuteToTime(endTime));
        mLinkageTimePopup = new ATLinkageTimePopup(this);
        mLinkageTimePopup.setOnItemClickListener(new ATOnPopupItemClickListener() {
            @Override
            public void onItemClick(int i1, int i2) {
                if (begin) {
                    beginTime = i1 * 60 + i2;
                    tvBeginTime.setText(ATTimeFormatUtils.minuteToTime(beginTime));
                } else {
                    endTime = i1 * 60 + i2;
                    tvEndTime.setText(ATTimeFormatUtils.minuteToTime(endTime));
                }
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

        if (week_day == 7) {
            rlRepeat.setClickable(true);
            rlRepeat.setOnClickListener(view -> startActivityForResult(new Intent(this, ATWisdomSecurityOutAbnormalTimingRepeatActivity.class)
                    .putExtra("cron_week", cron_week), REQUEST_CODE_CRON_WEEK));
        } else
            rlRepeat.setClickable(false);

        titleBar.setRightClickListener(() -> {
            if(beginTime > endTime){
                showToast(getString(R.string.at_the_end_time_is_earlier_than_the_start_time));
                return;
            }
            setResult(RESULT_OK, getIntent().putExtra("beginTime", beginTime).putExtra("endTime", endTime)
                    .putExtra("cron_week", cron_week));
            finish();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_CRON_WEEK) {
            cron_week = data.getStringExtra("cron_week");
            if (cron_week.length() != 13 && cron_week.length() != 0) {
                week_text = cron_week.replace("0", "每周日").replace("1", "每周一").replace("2", "每周二")
                        .replace("3", "每周三").replace("4", "每周四").replace("5", "每周五")
                        .replace("6", "每周六").replaceAll(",", "、");
            } else {
                week_text = getString(R.string.at_every_day);
                cron_week = "7";
            }
            tvWeek.setText(week_text);
        }
    }
}