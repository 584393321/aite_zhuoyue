package com.aliyun.ayland.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.AppCompatImageView;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.ayland.base.ATBaseActivity;
import com.aliyun.ayland.contract.ATMainContract;
import com.aliyun.ayland.data.ATEventClazz;
import com.aliyun.ayland.data.ATEventInteger;
import com.aliyun.ayland.data.ATHouseBean;
import com.aliyun.ayland.data.ATSipListBean;
import com.aliyun.ayland.global.ATConstants;
import com.aliyun.ayland.global.ATGlobalApplication;
import com.aliyun.ayland.presenter.ATMainPresenter;
import com.aliyun.ayland.service.ATSocketServer;
import com.aliyun.ayland.ui.fragment.ATHomeFragment;
import com.aliyun.ayland.utils.ATSystemStatusBarUtils;
import com.aliyun.ayland.widget.ATKeyBoardUI;
import com.aliyun.ayland.widget.voip.VoipManager;
import com.aliyun.iot.aep.sdk.credential.IotCredentialManager.IoTCredentialListener;
import com.aliyun.iot.aep.sdk.credential.IotCredentialManager.IoTCredentialManageError;
import com.aliyun.iot.aep.sdk.credential.IotCredentialManager.IoTCredentialManageImpl;
import com.aliyun.iot.aep.sdk.credential.data.IoTCredentialData;
import com.aliyun.iot.push.PushManager;
import com.anthouse.wyzhuoyue.BuildConfig;
import com.anthouse.wyzhuoyue.R;
import com.evideo.voip.sdk.EVVoipException;
import com.facebook.react.bridge.AssertionException;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import static com.aliyun.ayland.ui.fragment.ATLinkageFragment.REQUEST_CODE_EDIT_LINKAGE;

public class ATMainActivity extends ATBaseActivity implements ATMainContract.View {
    public static final int REQUEST_CODE_PUBLISH = 0x1001;
    private ATMainActivity mContext;
    private ATMainPresenter mPresenter;
    private ATHomeFragment mHomeFragment;
    private DrawerLayout drawerLayout;
    private LinearLayout llContent;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private RelativeLayout rlDeviceManageShare, rlSetting, rlRoomManage, rlMessageCenter, rlFamilyManage, rlChangeHouse, rlSoundAuthorization;
    private long clickTime = 0;
    private ATHouseBean mATHouseBean;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            // 避免在子线程中改变了adapter中的数据
            switch (msg.what) {
                case 1:
                    IoTCredentialManageImpl.getInstance(ATGlobalApplication.getInstance()).asyncRefreshIoTCredential(new IoTCredentialListener() {
                        @Override
                        public void onRefreshIoTCredentialSuccess(IoTCredentialData ioTCredentialData) {
                            runOnUiThread(() -> startService(new Intent(ATMainActivity.this, ATSocketServer.class)));
                        }

                        @Override
                        public void onRefreshIoTCredentialFailed(IoTCredentialManageError ioTCredentialManageError) {
                            mHandler.sendEmptyMessageDelayed(1, 3000);
                        }
                    });
                    break;
            }
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.at_activity_main;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ATEventClazz eventClazz) {
        if ("MainActivity".equals(eventClazz.getClazz())) {
            if (!drawerLayout.isDrawerOpen(Gravity.START)) {
                drawerLayout.openDrawer(Gravity.START);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ATEventInteger eventInteger) {
        if ("MainActivity".equals(eventInteger.getClazz())) {
            if (1 == eventInteger.getPosition()) {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            } else {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            }
        }
    }

    @Override
    protected void findView() {
        drawerLayout = findViewById(R.id.drawer_layout);
        llContent = findViewById(R.id.ll_menu);
        rlDeviceManageShare = findViewById(R.id.rl_device_manage_share);
        rlSetting = findViewById(R.id.rl_setting);
        rlRoomManage = findViewById(R.id.rl_room_manage);
        rlMessageCenter = findViewById(R.id.rl_message_center);
        rlFamilyManage = findViewById(R.id.rl_family_manage);
        rlChangeHouse = findViewById(R.id.rl_change_house);
        rlSoundAuthorization = findViewById(R.id.rl_sound_authorization);
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        EventBus.getDefault().register(this);
        ATSystemStatusBarUtils.init(this, false);
        ATKeyBoardUI.buildKeyBoardUI(this);

        mHandler.postDelayed(() -> IoTCredentialManageImpl.getInstance(ATGlobalApplication.getInstance())
                .asyncRefreshIoTCredential(new IoTCredentialListener() {
                    @Override
                    public void onRefreshIoTCredentialSuccess(IoTCredentialData ioTCredentialData) {
                        runOnUiThread(() -> startService(new Intent(ATMainActivity.this, ATSocketServer.class)));
                    }

                    @Override
                    public void onRefreshIoTCredentialFailed(IoTCredentialManageError ioTCredentialManageError) {
                        mHandler.sendEmptyMessageDelayed(1, 3000);
                    }
                }), 1000);
        init();
    }

    @Override
    protected void initPresenter() {
        mPresenter = new ATMainPresenter(this);
        mPresenter.install(this);

    }

    private void list() {
        if (mATHouseBean == null)
            return;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", 0);
        jsonObject.put("buildingCode", mATHouseBean.getBuildingCode());
        jsonObject.put("villageCode", mATHouseBean.getVillageId());
        jsonObject.put("targetId", ATGlobalApplication.getATLoginBean().getPersonCode());
        mPresenter.request(ATConstants.Config.SERVER_URL_LIST, jsonObject);
    }

    private void findPresent() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("username", ATGlobalApplication.getAccount());
        jsonObject.put("iotToken", ATGlobalApplication.getIoTToken());
        mPresenter.request(ATConstants.Config.SERVER_URL_FINDPRESENT, jsonObject);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void init() {
        mATHouseBean = gson.fromJson(ATGlobalApplication.getHouse(), ATHouseBean.class);
        PushManager.getInstance().bindUser();
        setUpViewPager();
        llContent.setOnClickListener(view -> {
        });
        rlDeviceManageShare.setOnClickListener(view -> startActivity(new Intent(this, ATDeviceManageActivity.class)));
        rlSetting.setOnClickListener(view -> startActivity(new Intent(this, ATSettingActivity.class)));
        rlRoomManage.setOnClickListener(view -> startActivity(new Intent(this, ATManageRoomActivity.class)));
        rlMessageCenter.setOnClickListener(view -> {
            startActivity(new Intent(this, ATMessageCenterActivity.class));
        });
        rlFamilyManage.setOnClickListener(view -> startActivity(new Intent(this, ATFamilyManageActivity.class)));
        rlChangeHouse.setOnClickListener(view -> startActivity(new Intent(this, ATChangeHouseActivity.class)));
        rlSoundAuthorization.setOnClickListener(view -> startActivity(new Intent(this, ATSoundAuthorizationActivity.class)));
//        startService(new Intent(this, MyMessageReceiver.class));
    }

    public final static int PAGE_COUNT = 1;

    private void setUpViewPager() {
        List<Fragment> fragments = initFragments();
        if (BuildConfig.DEBUG && PAGE_COUNT != fragments.size()) {
            throw new AssertionException("Fragments size must be equal to: " + PAGE_COUNT);
        }
        viewPager.setOffscreenPageLimit(fragments.size() - 1);
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return PAGE_COUNT;
            }
        });
        tabLayout.setupWithViewPager(viewPager);
        initTabs();
    }

    private List<Fragment> initFragments() {
        List<Fragment> fragments = new ArrayList<>();
        mHomeFragment = new ATHomeFragment();
//        fragments.add(new TestFragment());
//        fragments.add(new TestFragment());
        fragments.add(mHomeFragment);
//        fragments.add(new SpeedyLifeFragment());
//        fragments.add(mConvenientLifeFragment);
//        fragments.add(new TestFragment());
//        fragments.add(new TestFragment());
        return fragments;
    }

    private static final int[] TAB_ICONS = {
//            R.drawable.selector_home_tab,
//            R.drawable.selector_home_tab1,
            R.drawable.at_selector_home_tab2,
//            R.drawable.selector_home_tab3,
//            R.drawable.selector_home_tab4,
    };

    private void initTabs() {
        final String[] titles = getResources().getStringArray(R.array.tab_titles);
        for (int i = 0; i < PAGE_COUNT; i++) {
            View view = View.inflate(this, R.layout.at_tab_main, null);
            ((TextView) view.findViewById(R.id.text)).setText(titles[i]);
            ((AppCompatImageView) view.findViewById(R.id.icon)).setImageResource(TAB_ICONS[i]);
            //noinspection ConstantConditions
            tabLayout.getTabAt(i).setCustomView(view);
        }
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                onSelectedStateChanged(tab, true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                onSelectedStateChanged(tab, false);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
//        tabLayout.getTabAt(2).select();
    }

    private void onSelectedStateChanged(TabLayout.Tab tab, boolean selected) {
        if (tab.getCustomView() == null) {
            return;
        }
        TextView text = tab.getCustomView().findViewById(R.id.text);
        if (text != null) {
            text.setSelected(selected);
        }
        ImageView icon = tab.getCustomView().findViewById(R.id.icon);
        if (icon != null) {
            icon.setSelected(selected);
        }
    }

    @Override
    public void requestResult(String result, String url) {
        try {
            org.json.JSONObject jsonResult = new org.json.JSONObject(result);
            switch (url) {
                case ATConstants.Config.SERVER_URL_FINDPRESENT:
                    try {
                        if ("200".equals(jsonResult.getString("code"))) {
                            if ("{}".equals(jsonResult.getString("data"))) {
                                startActivity(new Intent(this, ATChangeHouseActivity.class));
                            } else {
                                mATHouseBean = gson.fromJson(jsonResult.getString("data"), ATHouseBean.class);
                                ATGlobalApplication.setHouse(mATHouseBean.toString());
                                list();
                            }
                            ATGlobalApplication.setHouseState(jsonResult.getString("houseState"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case ATConstants.Config.SERVER_URL_LIST:
                    try {
                        List<ATSipListBean> atSipListBean = gson.fromJson(jsonResult.getString("data"), new TypeToken<List<ATSipListBean>>() {
                        }.getType());
                        if (atSipListBean.size() > 0) {
                            ATSipListBean sipListBean = atSipListBean.get(0);
                            if (sipListBean.getStatus() == 1)
                                try {
                                    VoipManager.getInstance().login(sipListBean.getSipNumber(), sipListBean.getSipPassword(), sipListBean.getSipHost()
                                            , sipListBean.getSipPort());
                                } catch (EVVoipException e) {
                                    e.printStackTrace();
                                }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - clickTime) > 2000) {
                showToast(getString(R.string.at_click_again_to_exit));
                clickTime = System.currentTimeMillis();
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        findPresent();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_EDIT_LINKAGE:
                    break;
                case REQUEST_CODE_PUBLISH:
                    break;
            }
        }
    }
}