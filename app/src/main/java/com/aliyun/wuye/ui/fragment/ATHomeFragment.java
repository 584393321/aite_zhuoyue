package com.aliyun.wuye.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.ayland.base.ATBaseFragment;
import com.aliyun.ayland.base.autolayout.util.ATAutoUtils;
import com.aliyun.ayland.contract.ATMainContract;
import com.aliyun.ayland.contract.ATSceneContract;
import com.aliyun.ayland.data.ATHouseBean;
import com.aliyun.ayland.data.ATLoginBean;
import com.aliyun.ayland.data.ATWeatherBean1;
import com.aliyun.ayland.global.ATConstants;
import com.aliyun.ayland.global.ATGlobalApplication;
import com.aliyun.ayland.presenter.ATMainPresenter;
import com.aliyun.wuye.ui.activity.ATShareSpaceActivity;
import com.aliyun.wuye.ui.activity.ATVisitorRecordActivity;
import com.aliyun.ayland.ui.activity.ATUserFaceActivity;
import com.aliyun.ayland.utils.ATPreferencesUtils;
import com.aliyun.ayland.utils.ATQRCodeUtil;
import com.aliyun.ayland.utils.ATResourceUtils;
import com.anthouse.wyzhuoyue.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import org.json.JSONException;

import java.io.File;
import java.util.Objects;

/**
 * Created by fr on 2017/12/19.
 */
public class ATHomeFragment extends ATBaseFragment implements ATMainContract.View {
    private static final int MSG_REFRESH_QRCODE = 0x1001;
    private static final int MSG_GET_WEATHER = 0x1002;
    private ATMainPresenter mPresenter;
    private ATWeatherBean1 mWeatherBean;
    private ATHouseBean houseBean;
    private SmartRefreshLayout smartRefreshLayout;
    private LinearLayout llInside, llWeather;
    private ImageView mImgQR, imgBegin, imgTo, imgEnd;
    private Dialog mDialogQRCode;
    private String baseString;
    private TextView tvOutsideTemp, tvWind, tvWeather, tvPm, tvInsideTemp, tvWet, tvPmInside, tvTvoc, tvAddBox;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_REFRESH_QRCODE:
                    createQrcode();
                    handler.sendEmptyMessageDelayed(MSG_REFRESH_QRCODE, 180000);
                    break;
                case MSG_GET_WEATHER:
                    getWeather();
                    handler.sendEmptyMessageDelayed(MSG_GET_WEATHER, 600000);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.at_fragment_wyhome;
    }

    @Override
    protected void findView(View view) {
        smartRefreshLayout = view.findViewById(R.id.smartRefreshLayout);
        tvOutsideTemp = view.findViewById(R.id.tv_outside_temp);
        tvWind = view.findViewById(R.id.tv_wind);
        tvWeather = view.findViewById(R.id.tv_weather);
        tvPm = view.findViewById(R.id.tv_pm);
        tvInsideTemp = view.findViewById(R.id.tv_inside_temp);
        tvWet = view.findViewById(R.id.tv_wet);
        tvPmInside = view.findViewById(R.id.tv_pm_inside);
        tvTvoc = view.findViewById(R.id.tv_tvoc);
        tvAddBox = view.findViewById(R.id.tv_add_box);
        llWeather = view.findViewById(R.id.ll_weather);
        llInside = view.findViewById(R.id.ll_inside);
        view.findViewById(R.id.ll_access).setOnClickListener(v -> {
            //人脸通行
            startActivity(new Intent(getActivity(), ATUserFaceActivity.class));
//            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("xuhuishenghuo://com.anthouse.xuhui/transit?type=1")));
        });
        view.findViewById(R.id.ll_qrcode).setOnClickListener(v -> {
//            二维码通行
            showBaseProgressDlg();
            handler.removeMessages(MSG_REFRESH_QRCODE);
            handler.sendEmptyMessage(MSG_REFRESH_QRCODE);
        });
        view.findViewById(R.id.ll_visite).setOnClickListener(v -> {
            //访客登记
            startActivity(new Intent(getActivity(), ATVisitorRecordActivity.class));
        });
        view.findViewById(R.id.ll_space).setOnClickListener(v -> {
            //空间预约
            startActivity(new Intent(getActivity(), ATShareSpaceActivity.class));
        });
        imgBegin = view.findViewById(R.id.img_begin);
        imgTo = view.findViewById(R.id.img_to);
        imgEnd = view.findViewById(R.id.img_end);
        init();
    }

    private void init() {
        houseBean = gson.fromJson(ATGlobalApplication.getHouse(), ATHouseBean.class);
        baseString = "mnt/sdcard/" + Objects.requireNonNull(getActivity()).getApplicationInfo().packageName + "/";

        ATLoginBean mATLoginBean = gson.fromJson(ATGlobalApplication.getLoginBeanStr(), ATLoginBean.class);
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            refreshLayout.finishRefresh(2000);
            if (mATLoginBean.isHasHouse() && mATLoginBean.getHouse() != null) {
                getWeather();
            } else {
                smartRefreshLayout.finishRefresh();
            }
        });

        if (!TextUtils.isEmpty(ATPreferencesUtils.getString(ATGlobalApplication.getContext(), "weather", ""))) {
            mWeatherBean = gson.fromJson(ATPreferencesUtils.getString(ATGlobalApplication.getContext(), "weather", ""), ATWeatherBean1.class);
            if (mWeatherBean != null && getActivity() != null) {
                tvOutsideTemp.setText(String.format(getString(R.string.at_temp_), mWeatherBean.getOutdoorsWeather().getTem()));
                tvWind.setText(String.format(getString(R.string.at_wind_), mWeatherBean.getOutdoorsWeather().getWindLevel() == null ? "" : mWeatherBean.getOutdoorsWeather().getWindLevel().getCnName()));
                tvPm.setText(String.format(getString(R.string.at_pm_), mWeatherBean.getOutdoorsWeather().get_$PM25153()));
            }
        } else {
            tvOutsideTemp.setText(String.format(getString(R.string.at_temp_), "28"));
            tvWind.setText(String.format(getString(R.string.at_wind_), "3级"));
            tvPm.setText(String.format(getString(R.string.at_pm_), "95"));
        }
        initQRCodeDialog();
    }

    @SuppressLint("InflateParams")
    private void initQRCodeDialog() {
        mDialogQRCode = new Dialog(Objects.requireNonNull(getActivity()), R.style.nameDialog);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.at_dialog_qrcode, null, false);
        mImgQR = view.findViewById(R.id.img);
        view.findViewById(R.id.tv_refresh).setOnClickListener(v -> {
            handler.removeMessages(MSG_REFRESH_QRCODE);
            handler.sendEmptyMessage(MSG_REFRESH_QRCODE);
        });
        view.findViewById(R.id.img_close).setOnClickListener(v -> mDialogQRCode.dismiss());
        mDialogQRCode.setContentView(view);
        mDialogQRCode.setOnDismissListener(dialogInterface -> handler.removeMessages(MSG_REFRESH_QRCODE));
    }

    @Override
    protected void initPresenter() {
        mPresenter = new ATMainPresenter(this);
        mPresenter.install(getActivity());
    }

    private void createQrcode() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("personCode", ATGlobalApplication.getATLoginBean().getPersonCode());
        mPresenter.request(ATConstants.Config.SERVER_URL_CREATEQRCODE, jsonObject);
    }

    public void setHouseBean(ATHouseBean houseBean) {
        this.houseBean = houseBean;
        getWeather();
    }

    private void getWeather() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("villageId", houseBean.getVillageId());
        jsonObject.put("openId", ATGlobalApplication.getATLoginBean().getPersonCode());
        jsonObject.put("buildingCode", houseBean.getBuildingCode());
        mPresenter.request(ATConstants.Config.SERVER_URL_GETWEATHER, jsonObject);
    }

    private void requestComplete() {
        if (mWeatherBean != null && getActivity() != null) {
            tvOutsideTemp.setText(String.format(getString(R.string.at_temp_), mWeatherBean.getOutdoorsWeather().getTem()));
            tvWind.setText(String.format(getString(R.string.at_wind_), mWeatherBean.getOutdoorsWeather().getWindLevel() == null ? "" : mWeatherBean.getOutdoorsWeather().getWindLevel().getCnName()));
            tvWeather.setText(mWeatherBean.getOutdoorsWeather().getOutdoorsWeather() == null ? "" : mWeatherBean.getOutdoorsWeather().getOutdoorsWeather().getCnName());
            tvPm.setText(String.format(getString(R.string.at_pm_), mWeatherBean.getOutdoorsWeather().get_$PM25153()));
            switch (mWeatherBean.getRoomWeather().getDeviceStatus()) {
                case 1:
                    llInside.setVisibility(View.VISIBLE);
                    tvInsideTemp.setText(String.format(getString(R.string.at_temp_), mWeatherBean.getRoomWeather().getTemperature()));
                    tvWet.setText(String.format(getString(R.string.at_wet_), mWeatherBean.getRoomWeather().getHumidity()));
                    tvPmInside.setText(String.format(getString(R.string.at_pm_), mWeatherBean.getRoomWeather().get_$PM2577()));
                    tvTvoc.setText(String.format(getString(R.string.at_tvoc_), mWeatherBean.getRoomWeather().getTVOC()));
                    tvAddBox.setText("");
                    break;
                case 0:
                    llInside.setVisibility(View.GONE);
                    tvAddBox.setVisibility(View.VISIBLE);
                    tvAddBox.setText(getString(R.string.at_add_box));
                    break;
                case -1:
                    llInside.setVisibility(View.GONE);
                    tvAddBox.setVisibility(View.VISIBLE);
                    tvAddBox.setText(getString(R.string.at_box_off_line));
                    break;
            }
            imgBegin.setVisibility(View.VISIBLE);
            imgTo.setVisibility(View.VISIBLE);
            switch (mWeatherBean.getOutdoorsWeather().getOutdoorsWeather() == null ? "" : mWeatherBean.getOutdoorsWeather().getOutdoorsWeather().getCode()) {
                case "21":
                    imgBegin.setImageResource(R.drawable.at_weather_07);
                    imgEnd.setImageResource(R.drawable.at_weather_08);
                    break;
                case "22":
                    imgBegin.setImageResource(R.drawable.at_weather_08);
                    imgEnd.setImageResource(R.drawable.at_weather_09);
                    break;
                case "23":
                    imgBegin.setImageResource(R.drawable.at_weather_09);
                    imgEnd.setImageResource(R.drawable.at_weather_10);
                    break;
                case "24":
                    imgBegin.setImageResource(R.drawable.at_weather_10);
                    imgEnd.setImageResource(R.drawable.at_weather_11);
                    break;
                case "25":
                    imgBegin.setImageResource(R.drawable.at_weather_11);
                    imgEnd.setImageResource(R.drawable.at_weather_12);
                    break;
                case "26":
                    imgBegin.setImageResource(R.drawable.at_weather_14);
                    imgEnd.setImageResource(R.drawable.at_weather_15);
                    break;
                case "27":
                    imgBegin.setImageResource(R.drawable.at_weather_15);
                    imgEnd.setImageResource(R.drawable.at_weather_16);
                    break;
                case "28":
                    imgBegin.setImageResource(R.drawable.at_weather_16);
                    imgEnd.setImageResource(R.drawable.at_weather_17);
                    break;
                default:
                    imgBegin.setVisibility(View.GONE);
                    imgTo.setVisibility(View.GONE);
                    imgEnd.setImageResource(ATResourceUtils.getResIdByName(String.format(getString(R.string.at_weather_), mWeatherBean.getOutdoorsWeather().getOutdoorsWeather() == null ? "" : mWeatherBean.getOutdoorsWeather().getOutdoorsWeather().getCode()), ATResourceUtils.ResourceType.DRAWABLE));
                    break;
            }
        }
    }

    private String getPathString() {
        String nowFilePath = baseString + System.currentTimeMillis() + ".jpg";
        File file = new File(baseString);
        if (!file.exists()) {
            file.mkdir();
        }
        return nowFilePath;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeMessages(MSG_REFRESH_QRCODE);
        handler.removeMessages(MSG_GET_WEATHER);
    }

    @Override
    public void requestResult(String result, String url) {
        try {
            org.json.JSONObject jsonResult = new org.json.JSONObject(result);
            if ("200".equals(jsonResult.getString("code"))) {
                switch (url) {
                    case ATConstants.Config.SERVER_URL_GETWEATHER:
                        ATPreferencesUtils.putString(ATGlobalApplication.getContext(), "weather", jsonResult.getString("data"));
                        mWeatherBean = gson.fromJson(jsonResult.getString("data"), ATWeatherBean1.class);
                        requestComplete();
                        break;
                    case ATConstants.Config.SERVER_URL_CREATEQRCODE:
                        String qrcode = jsonResult.has("qrcode") ? jsonResult.getString("qrcode") : "";
                        if (!TextUtils.isEmpty(qrcode)) {
                            mImgQR.setImageBitmap(ATQRCodeUtil.createQRImage(qrcode, ATAutoUtils.getPercentWidthSize(701)
                                    , ATAutoUtils.getPercentHeightSize(701), getPathString(), false, null));
                            if (!mDialogQRCode.isShowing())
                                mDialogQRCode.show();
                        }
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