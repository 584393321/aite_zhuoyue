package com.aliyun.ayland.ui.activity;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.TextView;

import com.aliyun.ayland.base.ATBaseActivity;
import com.aliyun.ayland.base.autolayout.util.ATAutoUtils;
import com.aliyun.ayland.contract.ATMainContract;
import com.aliyun.ayland.global.ATConstants;
import com.aliyun.ayland.presenter.ATMainPresenter;
import com.aliyun.ayland.ui.fragment.ATSpaceSubsribeGymFragment;
import com.aliyun.ayland.utils.ATSystemStatusBarUtils;
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


public class ATSpaceSubscribeActivity extends ATBaseActivity implements ATMainContract.View {
    private ATMainPresenter mPresenter;
    private String[] mTitles;
    private List<Fragment> mFragments;
    private String imageUrl = "";
    private ATSpaceSubsribeGymFragment mSpaceSubsribeGymFragment, mSpaceSubsribeGymFragment1, mSpaceSubsribeGymFragment2;
    private ViewPager viewpager;
    private TextView tvBack;
    private MagicIndicator magicIndicator;
    private SmartRefreshLayout smartRefreshLayout;

    @Override
    protected int getLayoutId() {
        return R.layout.at_activity_space_subscribe;
    }

    @Override
    protected void findView() {
        tvBack = findViewById(R.id.tv_back);
        viewpager = findViewById(R.id.viewPager);
        magicIndicator = findViewById(R.id.magicIndicator);
        smartRefreshLayout = findViewById(R.id.smartRefreshLayout);
        init();
    }

    @Override
    protected void initPresenter() {
        mPresenter = new ATMainPresenter(this);
        mPresenter.install(this);
    }

    private void init() {
        tvBack.setOnClickListener(v -> finish());
        new android.os.Handler().postDelayed(() -> ATSystemStatusBarUtils.init(ATSpaceSubscribeActivity.this, false), 500);
        mTitles = getResources().getStringArray(R.array.tab_space_subscribe);
        mFragments = new ArrayList<>();
        mSpaceSubsribeGymFragment = new ATSpaceSubsribeGymFragment();
        mSpaceSubsribeGymFragment1 = new ATSpaceSubsribeGymFragment();
        mSpaceSubsribeGymFragment2 = new ATSpaceSubsribeGymFragment();
        mFragments.add(mSpaceSubsribeGymFragment);
        mFragments.add(mSpaceSubsribeGymFragment1);
        mFragments.add(mSpaceSubsribeGymFragment2);

        setUpViewPager();
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
                return 3;
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
                ColorTransitionPagerTitleView colorTransitionPagerTitleView = new ColorTransitionPagerTitleView(ATSpaceSubscribeActivity.this);
                colorTransitionPagerTitleView.setNormalColor(getResources().getColor(R.color.white));
                colorTransitionPagerTitleView.setSelectedColor(getResources().getColor(R.color._93CFFF));
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
                indicator.setColors(getResources().getColor(R.color._93CFFF));
                return indicator;
            }
        });
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, viewpager);

        smartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                refreshLayout.finishRefresh(2000);
            }
        });
        viewpager.setCurrentItem(1);
    }

    @Override
    public void requestResult(String result, String url) {
        try {
            org.json.JSONObject jsonResult = new org.json.JSONObject(result);
            if ("200".equals(jsonResult.getString("code"))) {
                switch (url) {
                    case ATConstants.Config.SERVER_URL_FACEVILLAGELIST:
                        break;
                    case ATConstants.Config.SERVER_URL_GETFACE:
                        break;
                }
            } else {
                showToast(jsonResult.getString("message"));
            }
            smartRefreshLayout.finishRefresh();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}