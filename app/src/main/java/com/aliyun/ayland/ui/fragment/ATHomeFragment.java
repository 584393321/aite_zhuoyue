package com.aliyun.ayland.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.ayland.base.ATBaseFragment;
import com.aliyun.ayland.contract.ATSceneContract;
import com.aliyun.ayland.data.ATApplicationBean;
import com.aliyun.ayland.data.ATEventClazz;
import com.aliyun.ayland.data.ATEventInteger;
import com.aliyun.ayland.data.ATHouseBean;
import com.aliyun.ayland.data.ATLoginBean;
import com.aliyun.ayland.data.ATSceneManualAutoBean;
import com.aliyun.ayland.data.ATShortcutBean;
import com.aliyun.ayland.data.ATWeatherBean1;
import com.aliyun.ayland.global.ATGlobalApplication;
import com.aliyun.ayland.global.ATConstants;
import com.aliyun.ayland.presenter.ATScenePresenter;
import com.aliyun.ayland.ui.activity.ATHomeShortcutActivity;
import com.aliyun.ayland.ui.activity.ATIntelligentMonitorActivity;
import com.aliyun.ayland.ui.adapter.ATHomeBottomRVAdapter;
import com.aliyun.ayland.ui.adapter.ATHomeShortcutRVAdapter;
import com.aliyun.ayland.utils.ATPreferencesUtils;
import com.aliyun.ayland.utils.ATResourceUtils;
import com.aliyun.ayland.widget.ATSmoothnessLayoutManage;
import com.aliyun.iot.aep.component.router.Router;
import com.anthouse.wyzhuoyue.R;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

/**
 * Created by fr on 2017/12/19.
 */
public class ATHomeFragment extends ATBaseFragment implements ATSceneContract.View {
    public static final int REQUEST_CODE_HOME_CHANGE = 0x100;
    private static final int MSG_GET_WEATHER = 0x1001;
    private ATHomeShortcutRVAdapter mHomeShortcutRVAdapter;
    private ATHomeBottomRVAdapter mHomeAppRVAdapter;
    private ATScenePresenter mPresenter;
    private ArrayList<ATShortcutBean> mShortcutList = new ArrayList<>();
    private Handler handler;
    private ATWeatherBean1 mWeatherBean;
    private int shortCutPosition = 0;
    private ATHouseBean houseBean;
    private RecyclerView rvShortcut, rvApplication;
    private SmartRefreshLayout smartRefreshLayout;
    private LinearLayout llInside, llWeather, llHas, llNone;
    private ImageView imgBegin, imgTo, imgEnd, imgClose;
    private TextView tvWisdom, tvAddShortcut, tvNoShortcut, tvOutsideTemp, tvWind, tvWeather, tvPm, tvInsideTemp, tvWet, tvPmInside, tvTvoc, tvAddBox;
    private RequestOptions options = new RequestOptions()
            .placeholder(R.drawable.ico_touxiang_mr)
            .diskCacheStrategy(DiskCacheStrategy.ALL);

    @Override
    protected int getLayoutId() {
        return R.layout.at_fragment_home;
    }

    @Override
    protected void findView(View view) {
        tvWisdom = view.findViewById(R.id.tv_wisdom);
        rvShortcut = view.findViewById(R.id.rv_shortcut);
        rvApplication = view.findViewById(R.id.rv_application);
        smartRefreshLayout = view.findViewById(R.id.smartRefreshLayout);
        tvAddShortcut = view.findViewById(R.id.tv_add_shortcut);
        tvNoShortcut = view.findViewById(R.id.tv_no_shortcut);
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
        llHas = view.findViewById(R.id.ll_has);
        llNone = view.findViewById(R.id.ll_none);
        imgBegin = view.findViewById(R.id.img_begin);
        imgTo = view.findViewById(R.id.img_to);
        imgEnd = view.findViewById(R.id.img_end);
        imgClose = view.findViewById(R.id.img_close);
        init();
        handler = new Handler(Objects.requireNonNull(getActivity()).getMainLooper()) {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == MSG_GET_WEATHER) {
                    getWeather();
                    handler.sendEmptyMessageDelayed(MSG_GET_WEATHER, 600000);
                }
            }
        };
    }

    private void init() {
        houseBean = gson.fromJson(ATGlobalApplication.getHouse(), ATHouseBean.class);
        imgClose.setOnClickListener(view -> Objects.requireNonNull(getActivity()).finish());
//        imgClose.setOnClickListener(v -> {
//            LoginBusiness.logout(new ILogoutCallback() {
//                @Override
//                public void onLogoutSuccess() {
//                    Objects.requireNonNull(getActivity()).finish();
//                    ATApplication.setAccessToken("");
//                    ATApplication.setNull();
//                    ATApplication.getLoginBean().setPersonCode("");
//                    getActivity().stopService(new Intent(getActivity(), SocketServer.class));
//                    LoginBusiness.login(new ILoginCallback() {
//                        @Override
//                        public void onLoginSuccess() {
//                        }
//
//                        @Override
//                        public void onLoginFailed(int code, String error) {
//                        }
//                    });
//                }
//
//                @Override
//                public void onLogoutFailed(int code, String error) {
//                    Log.e("onLogoutFailed: ", code + "----" + error);
//                }
//            });
//        });

//        llWeather.setOnClickListener(view -> startActivity(new Intent(getActivity(), EnvironmentActivity.class)));

        tvAddShortcut.setOnClickListener((v) -> startActivityForResult(new Intent(getActivity(), ATHomeShortcutActivity.class)
                .putParcelableArrayListExtra("mShortcutList", mShortcutList), REQUEST_CODE_HOME_CHANGE));

        rvShortcut.setLayoutManager(new ATSmoothnessLayoutManage(getActivity(), LinearLayout.HORIZONTAL, false));
        mHomeShortcutRVAdapter = new ATHomeShortcutRVAdapter(getActivity());
        mHomeShortcutRVAdapter.setOnItemClickListener((view, position) -> {
            shortCutPosition = position;
            if (2 == mShortcutList.get(position).getShortcutType()) {
                sceneInstanceRun();
            } else {
                if (mShortcutList.get(position).getOperateType() == 1) {
                    startActivity(new Intent(getActivity(), ATIntelligentMonitorActivity.class)
                            .putExtra("productKey", mShortcutList.get(position).getProductKey())
                            .putExtra("iotId", mShortcutList.get(position).getItemId()));
                } else {
                    if (ATConstants.ProductKey.CAMERA_HAIKANG.equals(mShortcutList.get(position).getProductKey())
                            || ATConstants.ProductKey.CAMERA_AITE.equals(mShortcutList.get(position).getProductKey())) {
                        startActivity(new Intent(getContext(), ATIntelligentMonitorActivity.class)
                                .putExtra("productKey", mShortcutList.get(position).getProductKey())
                                .putExtra("iotId", mShortcutList.get(position).getItemId()));
                    } else {
                        Bundle bundle = new Bundle();
                        bundle.putString("iotId", mShortcutList.get(position).getItemId());
                        Router.getInstance().toUrl(getContext(), "link://router/" + mShortcutList.get(position).getProductKey(), bundle);
                    }
//                    if (mShortcutList.get(position).getStatus() != 1) {
//                        showToast(getString(R.string.device_outoff_line));
//                    } else if (mShortcutList.get(position).getAttributes().size() == 0) {
//                        showToast(getString(R.string.device_control_failed));
//                    } else {
//                        control();
//                    }
                }
            }
        });
        rvShortcut.setAdapter(mHomeShortcutRVAdapter);
        ATLoginBean mATLoginBean = gson.fromJson(ATGlobalApplication.getLoginBeanStr(), ATLoginBean.class);
        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            refreshLayout.finishRefresh(2000);
            houseBean = gson.fromJson(ATGlobalApplication.getHouse(), ATHouseBean.class);

            if (mATLoginBean.isHasHouse() && mATLoginBean.getHouse() != null) {
                llHas.setVisibility(View.VISIBLE);
                tvWisdom.setOnClickListener(view -> EventBus.getDefault().post(new ATEventClazz("MainActivity")));
                EventBus.getDefault().post(new ATEventInteger("MainActivity", 2));
                llNone.setVisibility(View.GONE);
                request();
            } else {
                llHas.setVisibility(View.GONE);
                tvWisdom.setOnClickListener(v -> {

                });
                EventBus.getDefault().post(new ATEventInteger("MainActivity", 1));
                llNone.setVisibility(View.VISIBLE);
                smartRefreshLayout.finishRefresh();
            }
        });

        rvApplication.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvApplication.setNestedScrollingEnabled(false);
        mHomeAppRVAdapter = new ATHomeBottomRVAdapter(getActivity());
        rvApplication.setAdapter(mHomeAppRVAdapter);

        if (!TextUtils.isEmpty(ATPreferencesUtils.getString(ATGlobalApplication.getContext(), "weather", ""))) {
            mWeatherBean = gson.fromJson(ATPreferencesUtils.getString(ATGlobalApplication.getContext(), "weather", ""), ATWeatherBean1.class);
            if (mWeatherBean != null && getActivity() != null) {
                tvOutsideTemp.setText(String.format(getString(R.string.at_temp_), mWeatherBean.getOutdoorsWeather().getTem()));
                tvWind.setText(String.format(getString(R.string.at_wind_), mWeatherBean.getOutdoorsWeather().getWindLevel() == null ? "" : mWeatherBean.getOutdoorsWeather().getWindLevel().getCnName()));
                tvPm.setText(String.format(getString(R.string.at_pm_), mWeatherBean.getOutdoorsWeather().get_$PM25153()));
            }
        }else {
            tvOutsideTemp.setText(String.format(getString(R.string.at_temp_), "28"));
            tvWind.setText(String.format(getString(R.string.at_wind_), "3级"));
            tvPm.setText(String.format(getString(R.string.at_pm_), "95"));
        }
    }

    @Override
    protected void initPresenter() {
        mPresenter = new ATScenePresenter(this);
        mPresenter.install(getActivity());
    }

    public void setHouseBean(ATHouseBean houseBean) {
        this.houseBean = houseBean;
        smartRefreshLayout.autoRefresh();
    }

    private void control() {
        showBaseProgressDlg();
        JSONObject operator = new JSONObject();
        operator.put("hid", ATGlobalApplication.getHid());
        operator.put("hidType", "OPEN");
        JSONArray commands = new JSONArray();
        JSONObject command = new JSONObject();
        JSONObject data = new JSONObject();
        data.put(mShortcutList.get(shortCutPosition).getAttributes().get(0).getAttribute(),
                mShortcutList.get(shortCutPosition).getAttributes().get(0).getValue().equals("1") ? 0 : 1);
        command.put("data", data);
        command.put("type", "SET_PROPERTIES");
        commands.add(command);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("targetId", mShortcutList.get(shortCutPosition).getItemId());
        jsonObject.put("operator", operator);
        jsonObject.put("commands", commands);
        jsonObject.put("iotToken", ATGlobalApplication.getIoTToken());
        mPresenter.request(ATConstants.Config.SERVER_URL_CONTROL, jsonObject);
    }

    private void sceneInstanceRun() {
        JSONObject operator = new JSONObject();
        operator.put("hid", ATGlobalApplication.getHid());
        operator.put("hidType", "OPEN");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("operator", operator);
        jsonObject.put("sceneId", mShortcutList.get(shortCutPosition).getItemId());
        jsonObject.put("iotToken", ATGlobalApplication.getIoTToken());
        mPresenter.request(ATConstants.Config.SERVER_URL_SCENEINSTANCERUN, jsonObject, shortCutPosition);
    }

    private void request() {
        shortcutList();
        getWeather();
        findAllApplication();
    }

    private void getWeather() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("villageId", houseBean.getVillageId());
        jsonObject.put("personCode", ATGlobalApplication.getATLoginBean().getPersonCode());
        mPresenter.request(ATConstants.Config.SERVER_URL_GETWEATHER, jsonObject);
    }

    private void findAllApplication() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("openId", ATGlobalApplication.getHid());
        jsonObject.put("villageCode", houseBean.getVillageId());
        mPresenter.request(ATConstants.Config.SERVER_URL_FINDALLAPPLICATION, jsonObject);
    }

    private void shortcutList() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("personCode", ATGlobalApplication.getATLoginBean().getPersonCode());
        jsonObject.put("villageId", houseBean.getVillageId());
        jsonObject.put("buildingCode", houseBean.getBuildingCode());
        mPresenter.request(ATConstants.Config.SERVER_URL_SHORTCUTLIST, jsonObject);
    }

    @Override
    public void onResume() {
        super.onResume();
        smartRefreshLayout.autoRefresh();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeMessages(MSG_GET_WEATHER);
    }

    @Override
    public void requestResult(String result, String url, Object o) {
        try {
            org.json.JSONObject jsonResult = new org.json.JSONObject(result);
            if (ATConstants.Config.SERVER_URL_SCENEINSTANCERUN.equals(url)) {
                if ("200".equals(jsonResult.getString("code"))) {
                    mHomeShortcutRVAdapter.setShowing((int) o, ATSceneManualAutoBean.NOTSHOW);
                    showToast(getString(R.string.at_perform_scene_success));
                } else {
                    showToast(jsonResult.getString("message"));
                }
            }
            if ("200".equals(jsonResult.getString("code"))) {
                switch (url) {
                    case ATConstants.Config.SERVER_URL_GETWEATHER:
                        ATPreferencesUtils.putString(ATGlobalApplication.getContext(), "weather", jsonResult.getString("data"));
                        mWeatherBean = gson.fromJson(jsonResult.getString("data"), ATWeatherBean1.class);
                        requestComplete();
                        break;
                    case ATConstants.Config.SERVER_URL_SHORTCUTLIST:
                        mShortcutList = gson.fromJson(jsonResult.getString("result"), new TypeToken<List<ATShortcutBean>>() {
                        }.getType());
                        if (mShortcutList.size() == 0) {
                            tvNoShortcut.setVisibility(View.VISIBLE);
                        } else {
                            tvNoShortcut.setVisibility(View.GONE);
                        }
                        mHomeShortcutRVAdapter.setLists(mShortcutList);
                        break;
                    case ATConstants.Config.SERVER_URL_FINDALLAPPLICATION:
                        List<ATApplicationBean> applicationBeanList = gson.fromJson(jsonResult.getString("data"), new TypeToken<List<ATApplicationBean>>() {
                        }.getType());
                        mHomeAppRVAdapter.setLists(applicationBeanList);
                        break;
                    case ATConstants.Config.SERVER_URL_CONTROL:
                        if (result.contains("success")) {
                            showToast(getString(R.string.at_operate_success));
                            mShortcutList.get(shortCutPosition).getAttributes().get(0).setValue(mShortcutList.get(shortCutPosition)
                                    .getAttributes().get(0).getValue().equals("1") ? "0" : "1");
                            mHomeShortcutRVAdapter.setLists(mShortcutList);
                        }
                        break;
                }
            } else {
//                showToast(jsonResult.getString("message"));
            }
            closeBaseProgressDlg();
            smartRefreshLayout.finishRefresh();
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_HOME_CHANGE:
                    smartRefreshLayout.autoRefresh();
                    break;
            }
        }
    }
}