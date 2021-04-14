package com.aliyun.ayland.ui.activity;

import android.graphics.Color;
import android.graphics.Paint;
import android.view.animation.AccelerateInterpolator;

import com.aliyun.ayland.base.ATBaseActivity;
import com.aliyun.ayland.contract.ATMainContract;
import com.aliyun.ayland.global.ATConstants;
import com.aliyun.ayland.presenter.ATMainPresenter;
import com.aliyun.ayland.widget.ATWaveView1;
import com.anthouse.wyzhuoyue.R;

import org.json.JSONException;

public class ATWarningNoticeActivity extends ATBaseActivity implements ATMainContract.View {
    private ATMainPresenter mPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.at_activity_warning_notice;
    }

    @Override
    protected void findView() {
        init();
    }

    @Override
    protected void initPresenter() {
        mPresenter = new ATMainPresenter(this);
        mPresenter.install(this);
    }

    private void init() {
        ATWaveView1 mWaveView = findViewById(R.id.wave_view);
        mWaveView.setDuration(5000);
        mWaveView.setStyle(Paint.Style.STROKE);
        mWaveView.setSpeed(500);
        mWaveView.setInitialRadius(300);
        mWaveView.setMaxRadius(450);
        mWaveView.setColor(Color.parseColor("#ff0000"));
        mWaveView.setInterpolator(new AccelerateInterpolator(1.2f));
        mWaveView.start();
    }

    @Override
    public void requestResult(String result, String url) {
        try {
            org.json.JSONObject jsonResult = new org.json.JSONObject(result);
            if ("200".equals(jsonResult.getString("code"))) {
                switch (url) {
                    case ATConstants.Config.SERVER_URL_HOUSEDEVICE:

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
}
