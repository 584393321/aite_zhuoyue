package com.aliyun.ayland.ui.activity;

import android.text.TextUtils;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.ayland.base.ATBaseActivity;
import com.aliyun.ayland.base.autolayout.util.ATAutoUtils;
import com.aliyun.ayland.contract.ATMainContract;
import com.aliyun.ayland.data.ATParkSpaceBean;
import com.aliyun.ayland.data.ATParkingBean;
import com.aliyun.ayland.global.ATConstants;
import com.aliyun.ayland.presenter.ATMainPresenter;
import com.aliyun.ayland.widget.ATSemicircleProgressView;
import com.anthouse.wyzhuoyue.R;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import org.json.JSONException;

public class ATVehicleCheckDetailActivity extends ATBaseActivity implements ATMainContract.View {
    private ATMainPresenter mPresenter;
    private ATParkingBean mParkingBean;
    private SmartRefreshLayout smartRefreshLayout;
    private ATSemicircleProgressView semicircleProgressView;
    private TextView tvAll, tvEmptyVehicle, tvCurrent, tvParkName, tvParkCode, tvBuyOutSpace, tvPublicSpace, tvTitleSpace;

    @Override
    protected int getLayoutId() {
        return R.layout.at_activity_vehicle_check;
    }

    @Override
    protected void findView() {
        tvEmptyVehicle = findViewById(R.id.tv_empty_vehicle);
        tvCurrent = findViewById(R.id.tv_current);
        tvAll = findViewById(R.id.tv_all);
        tvParkName = findViewById(R.id.tv_park_name);
        tvParkCode = findViewById(R.id.tv_park_code);
        tvBuyOutSpace = findViewById(R.id.tv_buy_out_space);
        tvPublicSpace = findViewById(R.id.tv_public_space);
        tvTitleSpace = findViewById(R.id.tv_title_space);
        semicircleProgressView = findViewById(R.id.semicircleProgressView);
        smartRefreshLayout = findViewById(R.id.smartRefreshLayout);
        init();
    }

    @Override
    protected void initPresenter() {
        mPresenter = new ATMainPresenter(this);
        mPresenter.install(this);

        mParkingBean = getIntent().getParcelableExtra("parkingBean");
        if (!TextUtils.isEmpty(mParkingBean.getParkcode())) {
            tvAll.setText("/" + mParkingBean.getTotalspace());
            tvParkName.setText(mParkingBean.getParkname());
            tvParkCode.setText(String.format(getString(R.string.at_number_), mParkingBean.getParkcode()));
            tvBuyOutSpace.setText(mParkingBean.getBuyoutspace());
            tvPublicSpace.setText(mParkingBean.getPublicspace());
            tvTitleSpace.setText(mParkingBean.getTotalspace());
            findSpace();
        }
    }

    private void findSpace() {
        showBaseProgressDlg();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("parkCode", mParkingBean.getParkcode());
        mPresenter.request(ATConstants.Config.SERVER_URL_FINDSPACE, jsonObject);
    }

    private void init() {
        ATAutoUtils.autoSize(semicircleProgressView);

        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            refreshLayout.finishRefresh(2000);
            findSpace();
        });
//        arcSeekBar.setOnProgressChangeListener(new ArcSeekBar.OnProgressChangeListener() {
//            @Override
//            public void onProgressChanged(ArcSeekBar seekBar, int progress, boolean isUser) {}
//
//            @Override
//            public void onStartTrackingTouch(ArcSeekBar seekBar) {}
//
//            @Override
//            public void onStopTrackingTouch(ArcSeekBar seekBar) {}
//        });
    }

    @Override
    public void requestResult(String result, String url) {
        try {
            org.json.JSONObject jsonResult = new org.json.JSONObject(result);
            if ("200".equals(jsonResult.getString("code"))) {
                switch (url) {
                    case ATConstants.Config.SERVER_URL_FINDSPACE:
                        ATParkSpaceBean parkSpaceBean = gson.fromJson(jsonResult.getString("data"), new TypeToken<ATParkSpaceBean>() {
                        }.getType());
                        semicircleProgressView.setSesameValues(Integer.parseInt(parkSpaceBean.getCurrent_space()), Integer.parseInt(mParkingBean.getTotalspace()));
                        tvCurrent.setText(parkSpaceBean.getCurrent_space());
                        tvEmptyVehicle.setText(parkSpaceBean.getCurrent_space());
                        break;
                }
            } else {
                showToast(jsonResult.getString("message"));
            }
            closeBaseProgressDlg();
            smartRefreshLayout.finishRefresh();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
