package com.aliyun.ayland.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.ayland.base.ATBaseActivity;
import com.aliyun.ayland.base.autolayout.util.ATAutoUtils;
import com.aliyun.ayland.contract.ATMainContract;
import com.aliyun.ayland.global.ATGlobalApplication;
import com.aliyun.ayland.global.ATConstants;
import com.aliyun.ayland.presenter.ATMainPresenter;
import com.aliyun.ayland.ui.fragment.ATUserFaceCheckFragment;
import com.aliyun.ayland.ui.fragment.ATUserFaceRecordFragment;
import com.aliyun.ayland.utils.ATPermissionUtils;
import com.aliyun.ayland.widget.face.ATFaceLivenessExpActivity;
import com.anthouse.wyzhuoyue.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import static com.aliyun.ayland.utils.ATPermissionUtils.WRITE_PERMISSION_REQ_CODE;


public class ATUserFaceActivity extends ATBaseActivity implements ATMainContract.View {
    private ATMainPresenter mPresenter;
    private String[] mTitles;
    private List<Fragment> mFragments;
    private String imageUrl = "";
    private ATUserFaceCheckFragment mUserFaceCheckFragment;
    private ATUserFaceRecordFragment mUserFaceRecordFragment;
    public static String userId;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private AppBarLayout appBarLayout;
    private CoordinatorLayout mainContent;
    private ImageView imgFace;
    private TextView tvLogging, tvNameOrTip;
    private ViewPager viewpager;
    private MagicIndicator magicIndicator;
    private SmartRefreshLayout smartRefreshLayout;

    @Override
    protected int getLayoutId() {
        return R.layout.at_activity_user_face;
    }

    @Override
    protected void findView() {
        collapsingToolbarLayout = findViewById(R.id.collapsingToolbarLayout);
        appBarLayout = findViewById(R.id.appBarLayout);
        mainContent = findViewById(R.id.main_content);
        imgFace = findViewById(R.id.img_face);
        tvLogging = findViewById(R.id.tv_logging);
        viewpager = findViewById(R.id.viewPager);
        magicIndicator = findViewById(R.id.magicIndicator);
        smartRefreshLayout = findViewById(R.id.smartRefreshLayout);
        tvNameOrTip = findViewById(R.id.tv_name_or_tip);
        init();
    }

    @Override
    protected void initPresenter() {
        mPresenter = new ATMainPresenter(this);
        mPresenter.install(this);
    }

    private void faceVillageList() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("personCode", TextUtils.isEmpty(userId) ? ATGlobalApplication.getHid() : userId);
        mPresenter.request(ATConstants.Config.SERVER_URL_FACEVILLAGELIST, jsonObject);
    }

    private void getFace() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userType", "OPEN");
        jsonObject.put("userId", TextUtils.isEmpty(userId) ? ATGlobalApplication.getHid() : userId);
        jsonObject.put("imageFormat", "URL");
        mPresenter.request(ATConstants.Config.SERVER_URL_GETFACE, jsonObject);
    }

    private void init() {
        userId = getIntent().getStringExtra("personCode");
        mTitles = getResources().getStringArray(R.array.tab_face_manage);
        mFragments = new ArrayList<>();
        mUserFaceCheckFragment = new ATUserFaceCheckFragment();
        mUserFaceRecordFragment = new ATUserFaceRecordFragment();
        mFragments.add(mUserFaceCheckFragment);
        mFragments.add(mUserFaceRecordFragment);

        ATAutoUtils.auto(collapsingToolbarLayout);
        ATAutoUtils.auto(appBarLayout);
        ATAutoUtils.auto(mainContent);

        setUpViewPager();

        tvNameOrTip.setText(TextUtils.isEmpty(getIntent().getStringExtra("name"))
                ? getString(R.string.at_face_logging) : getIntent().getStringExtra("name"));
        imgFace.setOnClickListener(view ->
                startActivity(getIntent().setClass(this, ATFaceLivenessExpActivity.class)
                        .putExtra("imageUrl", imageUrl)));
        tvLogging.setOnClickListener(view -> startActivity(getIntent().setClass(this, ATFaceLivenessExpActivity.class)
                .putExtra("imageUrl", imageUrl)));
        tvLogging.setOnClickListener(view -> {
            if (ATPermissionUtils.justcheckCameraPermission(this))
                startActivity(getIntent().setClass(this, ATFaceLivenessExpActivity.class)
                        .putExtra("imageUrl", imageUrl));
        });
    }

    private void setUpViewPager() {
        viewpager.setOffscreenPageLimit(mFragments.size() - 1);
        viewpager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragments.get(position);
            }

            @Override
            public int getCount() {
                return 2;
            }
        });

        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdjustMode(true);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return mTitles == null ? 0 : mTitles.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                ColorTransitionPagerTitleView colorTransitionPagerTitleView = new ColorTransitionPagerTitleView(ATUserFaceActivity.this);
                colorTransitionPagerTitleView.setNormalColor(getResources().getColor(R.color._666666));
                colorTransitionPagerTitleView.setSelectedColor(getResources().getColor(R.color._1478C8));
                colorTransitionPagerTitleView.setText(mTitles[index]);
                colorTransitionPagerTitleView.setTextSize(18);
                colorTransitionPagerTitleView.setOnClickListener(view -> viewpager.setCurrentItem(index));
                ATAutoUtils.autoSize(colorTransitionPagerTitleView);
                return colorTransitionPagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                LinePagerIndicator indicator = new LinePagerIndicator(context);
                indicator.setMode(LinePagerIndicator.MODE_EXACTLY);
                //设置indicator的宽度
                indicator.setLineWidth(60);
                indicator.setColors(getResources().getColor(R.color._1478C8));
                return indicator;
            }
        });
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, viewpager);

        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                refreshLayout.finishRefresh(2000);
                getFace();
                faceVillageList();
                mUserFaceRecordFragment.queryVisitorRecord(0);
            }
        });
    }

    @Override
    public void requestResult(String result, String url) {
        try {
            org.json.JSONObject jsonResult = new org.json.JSONObject(result);
            if ("200".equals(jsonResult.getString("code"))) {
                switch (url) {
                    case ATConstants.Config.SERVER_URL_FACEVILLAGELIST:
                        mUserFaceCheckFragment.setJsonResult(jsonResult);
                        break;
                    case ATConstants.Config.SERVER_URL_GETFACE:
                        imageUrl = jsonResult.getJSONObject("data").getString("imageUrl");
                        tvLogging.setText(R.string.at_login_yet);
                        break;
                }
            } else if ("2008".equals(jsonResult.getString("code"))) {
                tvLogging.setText(R.string.at_no_login_yet);
                imageUrl = "";
            } else {
//                showToast(jsonResult.getString("message"));
            }
            smartRefreshLayout.finishRefresh();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        smartRefreshLayout.autoRefresh();
        super.onResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WRITE_PERMISSION_REQ_CODE) {
            //请求权限
            for (int i = 0; i < permissions.length; ++i) {
                if (permissions[i].equals(Manifest.permission.CAMERA)) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED)
                        startActivity(getIntent().setClass(this, ATFaceLivenessExpActivity.class)
                                .putExtra("imageUrl", imageUrl));
                    else
                        showToast(getString(R.string.at_camera_permission));
                    return;
                }
            }
        }
    }
}