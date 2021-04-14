package com.aliyun.ayland.ui.activity;

import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;

import com.aliyun.ayland.base.ATBaseActivity;
import com.aliyun.ayland.widget.titlebar.ATMyTitleBar;
import com.anthouse.wyzhuoyue.R;

public class ATLinkageTimingRepeatActivity extends ATBaseActivity implements View.OnClickListener {
    private StringBuilder cron = new StringBuilder();
    private ATMyTitleBar titleBar;
    private CheckBox cbSun, cbMon, cbTue, cbWed, cbThu, cbFri, cbSat;
    private RelativeLayout rlSun, rlMon, rlTue, rlWed, rlThu, rlFri, rlSat;

    @Override
    protected int getLayoutId() {
        return R.layout.at_activity_linkage_timing_repeat;
    }

    @Override
    protected void findView() {
        titleBar = findViewById(R.id.titlebar);
        cbSun = findViewById(R.id.cb_sun);
        cbMon = findViewById(R.id.cb_mon);
        cbTue = findViewById(R.id.cb_tue);
        cbWed = findViewById(R.id.cb_wed);
        cbThu = findViewById(R.id.cb_thu);
        cbFri = findViewById(R.id.cb_fri);
        cbSat = findViewById(R.id.cb_sat);
        rlSun = findViewById(R.id.rl_sun);
        rlMon = findViewById(R.id.rl_mon);
        rlTue = findViewById(R.id.rl_tue);
        rlWed = findViewById(R.id.rl_wed);
        rlThu = findViewById(R.id.rl_thu);
        rlFri = findViewById(R.id.rl_fri);
        rlSat = findViewById(R.id.rl_sat);
        init();
    }

    @Override
    protected void initPresenter() {
    }

    private void init() {
        String cron_week = getIntent().getStringExtra("cron_week");
        if ("*".equals(cron_week) || "每天".equals(cron_week)) {
            cbSun.setChecked(true);
            cbMon.setChecked(true);
            cbTue.setChecked(true);
            cbWed.setChecked(true);
            cbThu.setChecked(true);
            cbFri.setChecked(true);
            cbSat.setChecked(true);
        } else {
            cbSun.setChecked(cron_week.contains("7"));
            cbMon.setChecked(cron_week.contains("1"));
            cbTue.setChecked(cron_week.contains("2"));
            cbWed.setChecked(cron_week.contains("3"));
            cbThu.setChecked(cron_week.contains("4"));
            cbFri.setChecked(cron_week.contains("5"));
            cbSat.setChecked(cron_week.contains("6"));
        }
        rlSun.setOnClickListener(this);
        rlMon.setOnClickListener(this);
        rlTue.setOnClickListener(this);
        rlWed.setOnClickListener(this);
        rlThu.setOnClickListener(this);
        rlFri.setOnClickListener(this);
        rlSat.setOnClickListener(this);
        titleBar.setTitleBarClickBackListener(() -> {
            if (cbMon.isChecked()) {
                cron.append("1,");
            }
            if (cbTue.isChecked()) {
                cron.append("2,");
            }
            if (cbWed.isChecked()) {
                cron.append("3,");
            }
            if (cbThu.isChecked()) {
                cron.append("4,");
            }
            if (cbFri.isChecked()) {
                cron.append("5,");
            }
            if (cbSat.isChecked()) {
                cron.append("6,");
            }
            if (cbSun.isChecked()) {
                cron.append("7,");
            }
            if (cron.length() != 0)
                cron.deleteCharAt(cron.length() - 1);
            else {
                showToast(getString(R.string.at_lease_one_day));
                return;
            }
            setResult(RESULT_OK, new Intent().putExtra("cron_week", cron.toString()));
            finish();
        });
    }

    @Override
    public void onClick(View view) {
        if (R.id.rl_sun == view.getId())
            cbSun.setChecked(!cbSun.isChecked());
        else if (R.id.rl_mon == view.getId())
            cbMon.setChecked(!cbMon.isChecked());
        else if (R.id.rl_tue == view.getId())
            cbTue.setChecked(!cbTue.isChecked());
        else if (R.id.rl_wed == view.getId())
            cbWed.setChecked(!cbWed.isChecked());
        else if (R.id.rl_thu == view.getId())
            cbThu.setChecked(!cbThu.isChecked());
        else if (R.id.rl_fri == view.getId())
            cbFri.setChecked(!cbFri.isChecked());
        else if (R.id.rl_sat == view.getId())
            cbSat.setChecked(!cbSat.isChecked());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (cbMon.isChecked()) {
                cron.append("1,");
            }
            if (cbTue.isChecked()) {
                cron.append("2,");
            }
            if (cbWed.isChecked()) {
                cron.append("3,");
            }
            if (cbThu.isChecked()) {
                cron.append("4,");
            }
            if (cbFri.isChecked()) {
                cron.append("5,");
            }
            if (cbSat.isChecked()) {
                cron.append("6,");
            }
            if (cbSun.isChecked()) {
                cron.append("7,");
            }
            if (cron.length() != 0)
                cron.deleteCharAt(cron.length() - 1);
            else {
                showToast(getString(R.string.at_lease_one_day));
                return false;
            }
            setResult(RESULT_OK, new Intent().putExtra("cron_week", cron.toString()));
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
