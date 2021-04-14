package com.aliyun.ayland.ui.fragment;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.ayland.base.ATBaseFragment;
import com.aliyun.ayland.contract.ATMainContract;
import com.aliyun.ayland.data.ATEnvironmentBean;
import com.aliyun.ayland.data.ATEnvironmentOutsideBottom;
import com.aliyun.ayland.data.ATEnvironmentTopBean;
import com.aliyun.ayland.data.ATEventInteger;
import com.aliyun.ayland.data.ATHouseBean;
import com.aliyun.ayland.global.ATGlobalApplication;
import com.aliyun.ayland.global.ATConstants;
import com.aliyun.ayland.presenter.ATMainPresenter;
import com.aliyun.ayland.ui.adapter.ATEnvironmentRVAdapter;
import com.aliyun.ayland.utils.ATResourceUtils;
import com.aliyun.ayland.widget.ATIndexHorizontalScrollView;
import com.aliyun.ayland.widget.ATSmoothnessLayoutManage;
import com.aliyun.ayland.widget.ATToday24HourView;
import com.aliyun.ayland.widget.popup.ATEnvironmentTimePopup;
import com.anthouse.wyzhuoyue.R;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;


public class ATEnvironmentOutsideFragment extends ATBaseFragment implements ATMainContract.View {
    private ATMainPresenter mPresenter;
    private ATEnvironmentRVAdapter mEnvironmentRVAdapter;
    private ATHouseBean houseBean;
    private int category = 1, timeInterval = 1;
    private double max = 0, min = 0;
    private ATEnvironmentTimePopup mEnvironmentTimePopup;
    private RecyclerView recyclerView;
    private ATToday24HourView today24HourView;
    private ATIndexHorizontalScrollView indexHorizontalScrollView;
    private ImageView imgBegin, imgTo, imgEnd;
    private TextView tvTime, tvTemp, tvWet, tvPm, tvWeather, tvCity, tvWind, tvNoData, tvAirQuality, tvNoise, tvTips;
    private List<String> mTime = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.at_fragment_environment_outside;
    }

    @Override
    protected void findView(View view) {
        tvTime = view.findViewById(R.id.tv_time);
        tvTemp = view.findViewById(R.id.tv_temp);
        tvWet = view.findViewById(R.id.tv_wet);
        tvPm = view.findViewById(R.id.tv_pm);
        tvWeather = view.findViewById(R.id.tv_weather);
        tvCity = view.findViewById(R.id.tv_city);
        tvWind = view.findViewById(R.id.tv_wind);
        tvNoData = view.findViewById(R.id.tv_no_data);
        tvAirQuality = view.findViewById(R.id.tv_air_quality);
        tvNoise = view.findViewById(R.id.tv_noise);
        tvTips = view.findViewById(R.id.tv_tips);
        imgBegin = view.findViewById(R.id.img_begin);
        imgTo = view.findViewById(R.id.img_to);
        imgEnd = view.findViewById(R.id.img_end);
        today24HourView = view.findViewById(R.id.today24HourView);
        indexHorizontalScrollView = view.findViewById(R.id.indexHorizontalScrollView);
        recyclerView = view.findViewById(R.id.recyclerView);
        EventBus.getDefault().register(this);
        init();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ATEventInteger eventInteger) {
        if ("EnvironmentOutsideFragment".equals(eventInteger.getClazz())) {
            tvTime.setText(mTime.get(eventInteger.getPosition()));
            timeInterval = eventInteger.getPosition() == 0 ? 1 : (eventInteger.getPosition() == 1 ? 3 : 4);
            getOutdoorEnvironmentHistoryData();
        }
    }

    @Override
    protected void initPresenter() {
        mPresenter = new ATMainPresenter(this);
        mPresenter.install(getActivity());
        request();
    }

    public void request() {
        getOutdoorEnvironmentData();
        getOutdoorEnvironmentHistoryData();
    }

    private void getOutdoorEnvironmentHistoryData() {
        showBaseProgressDlg();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("villageId", houseBean.getVillageId());
        jsonObject.put("category", category);
        jsonObject.put("timeInterval", timeInterval);
        mPresenter.request(ATConstants.Config.SERVER_URL_GETOUTDOORENVIRONMENTHISTORYDATA, jsonObject);
    }

    private void getOutdoorEnvironmentData() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("villageId", houseBean.getVillageId());
        mPresenter.request(ATConstants.Config.SERVER_URL_GETOUTDOORENVIRONMENTDATA, jsonObject);
    }

    private void init() {
        houseBean = gson.fromJson(ATGlobalApplication.getHouse(), ATHouseBean.class);

        recyclerView.setLayoutManager(new ATSmoothnessLayoutManage(getActivity(), LinearLayout.HORIZONTAL, false));
        mEnvironmentRVAdapter = new ATEnvironmentRVAdapter(getActivity());
        recyclerView.setAdapter(mEnvironmentRVAdapter);
        List<ATEnvironmentBean> list = new ArrayList<>();
        ATEnvironmentBean environmentBean = new ATEnvironmentBean();
        environmentBean.setTemp("温度");
        environmentBean.setUnit("单位：℃");
        ATEnvironmentBean environmentBean1 = new ATEnvironmentBean();
        environmentBean1.setTemp("湿度");
        environmentBean1.setUnit("单位：%");
        ATEnvironmentBean environmentBean3 = new ATEnvironmentBean();
        environmentBean3.setTemp("PM2.5");
        environmentBean3.setUnit("单位：ug/m3");
        ATEnvironmentBean environmentBean4 = new ATEnvironmentBean();
        environmentBean4.setTemp("风力");
        environmentBean4.setUnit("单位：级");
        ATEnvironmentBean environmentBean5 = new ATEnvironmentBean();
        environmentBean5.setTemp("噪声");
        environmentBean5.setUnit("单位：dB");
        list.add(environmentBean);
        list.add(environmentBean1);
        list.add(environmentBean3);
        list.add(environmentBean4);
        list.add(environmentBean5);
        mEnvironmentRVAdapter.setLists(list);
        mEnvironmentRVAdapter.setOnItemClickListener((view, o, position) -> {
            category = ++position;
            showBaseProgressDlg();
            getOutdoorEnvironmentHistoryData();
        });
        mEnvironmentTimePopup = new ATEnvironmentTimePopup(getActivity());
        mTime.clear();
        mTime.add("24小时");
        mTime.add("30天");
        mTime.add("一年");
        mEnvironmentTimePopup.setList(mTime);
        tvTime.setOnClickListener(view -> mEnvironmentTimePopup.showPopupWindow());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void requestResult(String result, String url) {
        try {
            org.json.JSONObject jsonResult = new org.json.JSONObject(result);
            if ("200".equals(jsonResult.getString("code"))) {
                switch (url) {
                    case ATConstants.Config.SERVER_URL_GETOUTDOORENVIRONMENTHISTORYDATA:
                        List<ATEnvironmentOutsideBottom> environmentOutsideBottomList = gson.fromJson(jsonResult.getString("data"), new TypeToken<List<ATEnvironmentOutsideBottom>>() {
                        }.getType());
                        for (ATEnvironmentOutsideBottom environmentOutsideBottom : environmentOutsideBottomList) {
                            max = max > environmentOutsideBottom.getNum() ? max : environmentOutsideBottom.getNum();
                            min = min < environmentOutsideBottom.getNum() ? min : environmentOutsideBottom.getNum();
                        }
                        today24HourView.initEnvironmentOutsideBottoms(environmentOutsideBottomList, max, min);
                        indexHorizontalScrollView.fullScroll(View.FOCUS_RIGHT);
                        if (environmentOutsideBottomList.size() == 0) {
                            tvNoData.setVisibility(View.VISIBLE);
                        } else {
                            tvNoData.setVisibility(View.GONE);
                        }
                        break;
                    case ATConstants.Config.SERVER_URL_GETOUTDOORENVIRONMENTDATA:
                        ATEnvironmentTopBean environmentTopBean = gson.fromJson(jsonResult.getJSONObject("data").getString("environmentData"), ATEnvironmentTopBean.class);
                        tvTemp.setText(environmentTopBean.getTem());
                        tvWeather.setText(environmentTopBean.getWea());
                        tvCity.setText(environmentTopBean.getCity());
                        tvWet.setText(String.format(getString(R.string.at_wet_), environmentTopBean.getHumidity()));
                        tvPm.setText(String.format(getString(R.string.at_pm_), environmentTopBean.getAir_pm25()));
                        tvWind.setText(environmentTopBean.getWin_speed());
                        tvAirQuality.setText(environmentTopBean.getAir_level());
                        tvNoise.setText(String.format(getString(R.string.at_noise_),environmentTopBean.getSoundDecibelValue()));

//                        tvWet.setText(environmentTopBean.getHumidity());
//                        tvPm.setText(String.format(getString(R.string.pm_unit_),environmentTopBean.getAir_pm25()));
//                        tvWind.setText(environmentTopBean.getWin_speed());
//                        tvAirQuality.setText(environmentTopBean.getAir_level());
//                        tvNoise.setText(String.format(getString(R.string.noise_unit_),environmentTopBean.getSoundDecibelValue()));

                        tvTips.setText(environmentTopBean.getAir_tips());
                        imgBegin.setVisibility(View.VISIBLE);
                        imgTo.setVisibility(View.VISIBLE);
                        switch (environmentTopBean.getWeatherCode()) {
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
                                imgEnd.setImageResource(ATResourceUtils.getResIdByName(String.format(getString(R.string.at_weather_), environmentTopBean.getWeatherCode()), ATResourceUtils.ResourceType.DRAWABLE));
                                break;
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
}