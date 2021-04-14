package com.aliyun.ayland.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.ayland.base.ATBaseActivity;
import com.aliyun.ayland.base.autolayout.util.ATAutoUtils;
import com.aliyun.ayland.contract.ATMainContract;
import com.aliyun.ayland.data.ATVisitorReservateBean;
import com.aliyun.ayland.global.ATGlobalApplication;
import com.aliyun.ayland.global.ATConstants;
import com.aliyun.ayland.presenter.ATMainPresenter;
import com.aliyun.ayland.ui.adapter.ATVisitorAppointRecordRVAdapter;
import com.aliyun.ayland.ui.fragment.ATAdvanceAppointRecordFragment;
import com.aliyun.ayland.ui.fragment.ATAppointRecordFragment;
import com.aliyun.ayland.utils.ATSystemStatusBarUtils;
import com.aliyun.wuye.ui.activity.ATShareSpaceActivity;
import com.aliyun.wuye.ui.fragment.ATAppointRecordHandleFragment;
import com.aliyun.wuye.ui.fragment.ATAppointRecordUnhandleFragment;
import com.anthouse.wyzhuoyue.R;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

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

public class ATVisitorRecordActivity extends ATBaseActivity{
    private ATMainPresenter mPresenter;
    private ATVisitorAppointRecordRVAdapter mVisitorAppointRecordRVAdapter;
    private int pageNum;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private AppBarLayout appBarLayout;
    private CoordinatorLayout mainContent;
    private TextView tvBack, tvTitle, tvVisitorAppoint;
    private RelativeLayout iv;
    private LinearLayout llContent;
    private ViewPager viewpager;
    private MagicIndicator magicIndicator;
    private String[] mTitles;
    private List<Fragment> mFragments;

    @Override
    protected int getLayoutId() {
        return R.layout.at_activity_visitor_record;
    }

    @Override
    protected void findView() {
        collapsingToolbarLayout = findViewById(R.id.collapsingToolbarLayout);
        appBarLayout = findViewById(R.id.appBarLayout);
        mainContent = findViewById(R.id.main_content);
        viewpager = findViewById(R.id.viewPager);
        magicIndicator = findViewById(R.id.magicIndicator);
        tvBack = findViewById(R.id.tv_back);
        tvTitle = findViewById(R.id.tv_title);
        tvVisitorAppoint = findViewById(R.id.tv_visitor_appoint);
        iv = findViewById(R.id.iv);
        llContent = findViewById(R.id.ll_content);
        init();
    }

    @Override
    protected void initPresenter() {
    }

    private void init() {
        ATSystemStatusBarUtils.init(ATVisitorRecordActivity.this, false);
        ATAutoUtils.auto(collapsingToolbarLayout);
        ATAutoUtils.auto(appBarLayout);
        ATAutoUtils.auto(mainContent);
        tvBack.setOnClickListener(view -> finish());
        appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            iv.getBackground().setAlpha(255 * verticalOffset / (-appBarLayout.getTotalScrollRange()));
            if (iv.getBackground().getAlpha() == 0) {
                ATSystemStatusBarUtils.init(ATVisitorRecordActivity.this, false);
                tvBack.setSelected(false);
                tvTitle.setTextColor(getResources().getColor(R.color.white));
            } else if (iv.getBackground().getAlpha() == 255) {
                ATSystemStatusBarUtils.init(ATVisitorRecordActivity.this, true);
                tvBack.setSelected(true);
                tvTitle.setTextColor(getResources().getColor(R.color._333333));
            }
        });
        tvVisitorAppoint.setOnClickListener(view -> startActivity(new Intent(this, ATVisiteAppointActivity.class)));

        mTitles = getResources().getStringArray(R.array.visite_appoint_record);

        mFragments = new ArrayList<>();
        mFragments.add(new ATAdvanceAppointRecordFragment());
        mFragments.add(new ATAppointRecordFragment());
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
                ColorTransitionPagerTitleView colorTransitionPagerTitleView = new ColorTransitionPagerTitleView(ATVisitorRecordActivity.this);
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
    }
}