package com.aliyun.wuye.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aliyun.ayland.base.ATBaseActivity;
import com.aliyun.ayland.base.autolayout.ATAutoAppBarLayout;
import com.aliyun.ayland.base.autolayout.ATAutoCollapsingToolbarLayout;
import com.aliyun.ayland.base.autolayout.util.ATAutoUtils;
import com.aliyun.ayland.data.ATHouseBean;
import com.aliyun.ayland.global.ATGlobalApplication;
import com.aliyun.ayland.utils.ATSystemStatusBarUtils;
import com.aliyun.wuye.ui.fragment.ATAppointRecordUnhandleFragment;
import com.aliyun.wuye.ui.fragment.ATAppointRecordHandleFragment;
import com.anthouse.wyzhuoyue.R;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;

import java.util.ArrayList;
import java.util.List;

public class ATShareSpaceActivity extends ATBaseActivity{
    private ATAutoCollapsingToolbarLayout collapsingToolbarLayout;
    private ATAutoAppBarLayout appBarLayout;
    private CoordinatorLayout mainContent;
    private TextView tvTitle, tvAppoint;
    private ImageView imgBack;
    private RelativeLayout rlContent;
    private ViewPager viewpager;
    private MagicIndicator magicIndicator;
    private String[] mTitles;
    private List<Fragment> mFragments;

    @Override
    protected int getLayoutId() {
        return R.layout.at_activity_wy_share_space;
    }

    @Override
    protected void findView() {
        collapsingToolbarLayout = findViewById(R.id.collapsingToolbarLayout);
        appBarLayout = findViewById(R.id.appBarLayout);
        viewpager = findViewById(R.id.viewPager);
        magicIndicator = findViewById(R.id.magicIndicator);
        mainContent = findViewById(R.id.main_content);
        tvTitle = findViewById(R.id.tv_title);
        imgBack = findViewById(R.id.img_back);
        rlContent = findViewById(R.id.rl_content);
        tvAppoint = findViewById(R.id.tv_appoint);
        init();
    }

    @Override
    protected void initPresenter() {
    }

    private void init() {
        ATSystemStatusBarUtils.init(ATShareSpaceActivity.this, false);

        ATAutoUtils.auto(collapsingToolbarLayout);
        ATAutoUtils.auto(appBarLayout);
        ATAutoUtils.auto(mainContent);

        tvAppoint.setOnClickListener(view -> startActivity(new Intent(this, ATShareGardenActivity.class)));

        imgBack.setOnClickListener(view -> finish());
        imgBack.setSelected(true);
        appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            rlContent.getBackground().setAlpha(255 * verticalOffset / (-appBarLayout.getTotalScrollRange()));
            if (rlContent.getBackground().getAlpha() == 0) {
                ATSystemStatusBarUtils.init(ATShareSpaceActivity.this, false);
                imgBack.setSelected(false);
                tvTitle.setTextColor(getResources().getColor(R.color.white));
            } else if (rlContent.getBackground().getAlpha() == 255) {
                ATSystemStatusBarUtils.init(ATShareSpaceActivity.this, true);
                imgBack.setSelected(true);
                tvTitle.setTextColor(getResources().getColor(R.color._333333));
            }
        });

        mTitles = getResources().getStringArray(R.array.wy_public_space_record);
        mFragments = new ArrayList<>();
        mFragments.add(new ATAppointRecordUnhandleFragment());
        mFragments.add(new ATAppointRecordHandleFragment());
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
                ColorTransitionPagerTitleView colorTransitionPagerTitleView = new ColorTransitionPagerTitleView(ATShareSpaceActivity.this);
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