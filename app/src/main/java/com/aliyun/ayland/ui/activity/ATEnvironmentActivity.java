package com.aliyun.ayland.ui.activity;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.aliyun.ayland.base.ATBaseActivity;
import com.aliyun.ayland.base.autolayout.util.ATAutoUtils;
import com.aliyun.ayland.contract.ATMainContract;
import com.aliyun.ayland.global.ATConstants;
import com.aliyun.ayland.presenter.ATMainPresenter;
import com.aliyun.ayland.ui.adapter.ATLinkageSceneAutoRVAdapter;
import com.aliyun.ayland.ui.fragment.ATEnvironmentInsideFragment;
import com.aliyun.ayland.ui.fragment.ATEnvironmentOutsideFragment;
import com.aliyun.ayland.widget.titlebar.ATMyTitleBar;
import com.anthouse.wyzhuoyue.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

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


/**
 * Created by fr on 2017/12/19.
 */

public class ATEnvironmentActivity extends ATBaseActivity implements ATMainContract.View {
    private ATMainPresenter mPresenter;
    private ATLinkageSceneAutoRVAdapter mLinkageSceneRVAdapter;
    private int sceneType = 1, current_position = 0;
    private List<Fragment> mFragments;
    private ATEnvironmentOutsideFragment mEnvironmentOutsideFragment;
    private ATEnvironmentInsideFragment mEnvironmentInsideFragment;
    private ATMyTitleBar titleBar;
    private SmartRefreshLayout smartRefreshLayout;
    private ViewPager viewPager;
    private MagicIndicator magicIndicator;
    private String[] mTitles;

    @Override
    protected int getLayoutId() {
        return R.layout.at_activity_environment;
    }

    @Override
    protected void findView() {
        titleBar = findViewById(R.id.titlebar);
        smartRefreshLayout = findViewById(R.id.smartRefreshLayout);
        viewPager = findViewById(R.id.viewPager);
        magicIndicator = findViewById(R.id.magicIndicator);
        init();
    }

    @Override
    protected void initPresenter() {
        mPresenter = new ATMainPresenter(this);
        mPresenter.install(this);
    }

    private void init() {
        mTitles = getResources().getStringArray(R.array.environmental_data);
        mFragments = new ArrayList<>();
        if (mFragments.size() == 0) {
            mEnvironmentOutsideFragment = new ATEnvironmentOutsideFragment();
            mEnvironmentInsideFragment = new ATEnvironmentInsideFragment();
            mFragments.add(mEnvironmentOutsideFragment);
            mFragments.add(mEnvironmentInsideFragment);
        }

        setUpViewPager();

        smartRefreshLayout.setOnRefreshListener(refreshLayout -> {
            refreshLayout.finishRefresh(2000);
                mEnvironmentOutsideFragment.request();
        });
    }

    private void setUpViewPager() {
        viewPager.setOffscreenPageLimit(mFragments.size() - 1);
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
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
                ColorTransitionPagerTitleView colorTransitionPagerTitleView = new ColorTransitionPagerTitleView(ATEnvironmentActivity.this);
                colorTransitionPagerTitleView.setNormalColor(getResources().getColor(R.color._666666));
                colorTransitionPagerTitleView.setSelectedColor(getResources().getColor(R.color._1478C8));
                colorTransitionPagerTitleView.setText(mTitles[index]);
                colorTransitionPagerTitleView.setTextSize(18);
                colorTransitionPagerTitleView.setOnClickListener(view -> viewPager.setCurrentItem(index));
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
        ViewPagerHelper.bind(magicIndicator, viewPager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        smartRefreshLayout.autoRefresh();
    }

    @Override
    public void requestResult(String result, String url) {
        try {
            org.json.JSONObject jsonResult = new org.json.JSONObject(result);
            if ("200".equals(jsonResult.getString("code"))) {
                switch (url) {
                    case ATConstants.Config.SERVER_URL_GETENVIRONMENTRECOMMENDSCENELIST:
                        break;
                }
            } else {
                showToast(jsonResult.getString("message"));
            }
            smartRefreshLayout.finishRefresh();
            closeBaseProgressDlg();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}