package com.aliyun.ayland.ui.fragment;


import android.support.v7.widget.RecyclerView;
import android.view.View;
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
import com.aliyun.ayland.widget.ATIndexHorizontalScrollView;
import com.aliyun.ayland.widget.ATSmoothnessLayoutManage;
import com.aliyun.ayland.widget.ATToday24HourView;
import com.aliyun.ayland.widget.popup.ATGymTimePopup;
import com.anthouse.wyzhuoyue.R;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;


public class ATEnvironmentInsideFragment extends ATBaseFragment implements ATMainContract.View {
    private ATMainPresenter mPresenter;
    private ATEnvironmentRVAdapter mEnvironmentRVAdapter;
    private ATHouseBean houseBean;
    private String category = "1";
    private double max = 0, min = 0;
    private int timeInterval = 1;
    private ATGymTimePopup mGymTimePopup;
    private RecyclerView recyclerView;
    private ATToday24HourView today24HourView;
    private ATIndexHorizontalScrollView indexHorizontalScrollView;
    private View vHcho, vCo2, vTvoc;
    private TextView tvTime, tvTemp, tvWet, tvPm, tvHcho, tvCo2, tvTvoc, tvNoData;
    private List<String> mTime = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.at_fragment_environment_inside;
    }

    @Override
    protected void findView(View view) {
        tvTime = view.findViewById(R.id.tv_time);
        tvTemp = view.findViewById(R.id.tv_temp);
        tvWet = view.findViewById(R.id.tv_wet);
        tvPm = view.findViewById(R.id.tv_pm);
        tvHcho = view.findViewById(R.id.tv_hcho);
        tvCo2 = view.findViewById(R.id.tv_co2);
        tvTvoc = view.findViewById(R.id.tv_tvoc);
        tvNoData = view.findViewById(R.id.tv_no_data);
        vHcho = view.findViewById(R.id.v_hcho);
        vCo2 = view.findViewById(R.id.v_co2);
        vTvoc = view.findViewById(R.id.v_tvoc);
        today24HourView = view.findViewById(R.id.today24HourView);
        indexHorizontalScrollView = view.findViewById(R.id.indexHorizontalScrollView);
        recyclerView = view.findViewById(R.id.recyclerView);
        EventBus.getDefault().register(this);
        init();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ATEventInteger eventInteger) {
        if ("ConvenientShareGYMActivity2".equals(eventInteger.getClazz())) {
            tvTime.setText(mTime.get(eventInteger.getPosition()));
            timeInterval = eventInteger.getPosition() == 0 ? 1 : (eventInteger.getPosition() == 1 ? 3 : 4);
            getIndoorEnvironmentHistoryData();
        }
    }

    @Override
    protected void initPresenter() {
        mPresenter = new ATMainPresenter(this);
        mPresenter.install(getActivity());
        request();
    }

    public void request() {
        getIndoorEnvironmentData();
        getIndoorEnvironmentHistoryData();
    }

    private void getIndoorEnvironmentHistoryData() {
        showBaseProgressDlg();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("villageId", houseBean.getVillageId());
        jsonObject.put("category", category);
        jsonObject.put("timeInterval", timeInterval);
        jsonObject.put("buildingCode", houseBean.getBuildingCode());
        mPresenter.request(ATConstants.Config.SERVER_URL_GETINDOORENVIRONMENTHISTORYDATA, jsonObject);
    }

    private void getIndoorEnvironmentData() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("villageId", houseBean.getVillageId());
        jsonObject.put("buildingCode", houseBean.getBuildingCode());
        mPresenter.request(ATConstants.Config.SERVER_URL_GETINDOORENVIRONMENTDATA, jsonObject);
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
        environmentBean.setCategory("1");
        ATEnvironmentBean environmentBean1 = new ATEnvironmentBean();
        environmentBean1.setTemp("湿度");
        environmentBean1.setUnit("单位：%");
        environmentBean1.setCategory("2");
        ATEnvironmentBean environmentBean3 = new ATEnvironmentBean();
        environmentBean3.setTemp("PM2.5");
        environmentBean3.setUnit("单位：ug/m3");
        environmentBean3.setCategory("4");
        ATEnvironmentBean environmentBean4 = new ATEnvironmentBean();
        environmentBean4.setTemp("HCHO");
        environmentBean4.setUnit("单位：mg/m3");
        environmentBean4.setCategory("6");
        ATEnvironmentBean environmentBean5 = new ATEnvironmentBean();
        environmentBean5.setTemp("CO2");
        environmentBean5.setUnit("单位：mg/m3");
        environmentBean5.setCategory("7");
        ATEnvironmentBean environmentBean6 = new ATEnvironmentBean();
        environmentBean6.setTemp("TVOC");
        environmentBean6.setUnit("单位：mg/m3");
        environmentBean6.setCategory("8");
        list.add(environmentBean);
        list.add(environmentBean1);
        list.add(environmentBean3);
        list.add(environmentBean4);
        list.add(environmentBean5);
        list.add(environmentBean6);
        mEnvironmentRVAdapter.setLists(list);
        mEnvironmentRVAdapter.setOnItemClickListener((view, o, position) -> {
            category = list.get(position).getCategory();
            showBaseProgressDlg();
            getIndoorEnvironmentHistoryData();
        });
        mGymTimePopup = new ATGymTimePopup(getActivity());
        mTime.clear();
        mTime.add("24小时");
        mTime.add("30天");
        mTime.add("一年");
        mGymTimePopup.setList(mTime);
        tvTime.setOnClickListener(view -> mGymTimePopup.showPopupWindow());
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
                    case ATConstants.Config.SERVER_URL_GETINDOORENVIRONMENTDATA:
                        ATEnvironmentTopBean environmentTopBean = gson.fromJson(jsonResult.getJSONObject("data").getString("environmentData"), ATEnvironmentTopBean.class);
                        tvTemp.setText(environmentTopBean.getTem());
                        tvWet.setText(environmentTopBean.getHumidity());
                        tvPm.setText(environmentTopBean.getAir_pm25());
                        tvHcho.setText(String.valueOf(environmentTopBean.getHCHO()));
                        tvCo2.setText(String.valueOf(environmentTopBean.getCO2()));
                        tvTvoc.setText(String.valueOf(environmentTopBean.getTVOC()));
                        int maxValue, minValue;
                        if (environmentTopBean.getCO2() > environmentTopBean.getHCHO()) {
                            if (environmentTopBean.getTVOC() > environmentTopBean.getCO2()) {
                                maxValue = environmentTopBean.getTVOC();
                                minValue = environmentTopBean.getHCHO();
                            } else {
                                maxValue = environmentTopBean.getCO2();
                                if (environmentTopBean.getTVOC() < environmentTopBean.getHCHO()) {
                                    minValue = environmentTopBean.getTVOC();
                                } else {
                                    minValue = environmentTopBean.getHCHO();
                                }
                            }
                        } else {
                            if (environmentTopBean.getTVOC() < environmentTopBean.getCO2()) {
                                minValue = environmentTopBean.getTVOC();
                                maxValue = environmentTopBean.getHCHO();
                            } else {
                                minValue = environmentTopBean.getCO2();
                                if (environmentTopBean.getTVOC() < environmentTopBean.getHCHO()) {
                                    maxValue = environmentTopBean.getHCHO();
                                } else {
                                    maxValue = environmentTopBean.getTVOC();
                                }
                            }
                        }
                        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) vHcho.getLayoutParams();
                        linearParams.width = 600 * environmentTopBean.getHCHO() / maxValue;
                        vHcho.setLayoutParams(linearParams);

                        LinearLayout.LayoutParams linearParams1 = (LinearLayout.LayoutParams) vCo2.getLayoutParams();
                        linearParams1.width = 600 * environmentTopBean.getCO2() / maxValue;
                        vCo2.setLayoutParams(linearParams1);

                        LinearLayout.LayoutParams linearParams2 = (LinearLayout.LayoutParams) vTvoc.getLayoutParams();
                        linearParams2.width = 600 * environmentTopBean.getTVOC() / maxValue;
                        vTvoc.setLayoutParams(linearParams2);
                        break;
                    case ATConstants.Config.SERVER_URL_GETINDOORENVIRONMENTHISTORYDATA:
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