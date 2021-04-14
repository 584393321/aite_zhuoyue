package com.aliyun.ayland.ui.activity;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;

import com.aliyun.ayland.base.ATBaseActivity;
import com.aliyun.ayland.base.autolayout.util.ATAutoUtils;
import com.aliyun.ayland.contract.ATMainContract;
import com.aliyun.ayland.interfaces.ATOnPopupItemClickListener;
import com.aliyun.ayland.presenter.ATMainPresenter;
import com.aliyun.ayland.ui.fragment.ATPersonAccessFragment;
import com.aliyun.ayland.ui.fragment.ATVehicleAccessFragment;
import com.aliyun.ayland.widget.ATCustomViewPager;
import com.aliyun.ayland.widget.popup.ATAccessRecordPopup;
import com.aliyun.ayland.widget.titlebar.ATMyTitleBar;
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

public class ATAccessRecordActivity extends ATBaseActivity implements ATMainContract.View {
    private ATMainPresenter mPresenter;
    private ATAccessRecordPopup mAccessRecordPopup;
    private String[] mTitles;
    private List<Fragment> mFragments;
    private ATPersonAccessFragment mPersonAccessFragment;
    private ATVehicleAccessFragment mVehicleAccessFragment;
    private ATMyTitleBar titlebar;
    private ATCustomViewPager viewPager;
    private MagicIndicator magicIndicator;

    @Override
    protected int getLayoutId() {
        return R.layout.at_activity_access_record;
    }

    @Override
    protected void findView() {
        titlebar = findViewById(R.id.titlebar);
        viewPager = findViewById(R.id.viewPager);
        magicIndicator = findViewById(R.id.magicIndicator);
        init();
    }

    @Override
    protected void initPresenter() {
        mPresenter = new ATMainPresenter(this);
        mPresenter.install(this);
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
                ColorTransitionPagerTitleView colorTransitionPagerTitleView = new ColorTransitionPagerTitleView(ATAccessRecordActivity.this);
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


    private void init() {
        mTitles = getResources().getStringArray(R.array.tab_access_record);
        mFragments = new ArrayList<>();
        mPersonAccessFragment = new ATPersonAccessFragment();
        mVehicleAccessFragment = new ATVehicleAccessFragment();
        mFragments.add(mPersonAccessFragment);
        mFragments.add(mVehicleAccessFragment);
        setUpViewPager();

        //下拉弹框
        mAccessRecordPopup = new ATAccessRecordPopup(this, new ATOnPopupItemClickListener() {
            @Override
            public void onItemClick(int i1, int i2) {

            }

            @Override
            public void onItemClick(String s1, String s2, String s3) {
                mPersonAccessFragment.findHumnRecord(s1, s2, s3);
                mVehicleAccessFragment.findCarParkRecord(s1, s2, s3);
            }
        });
        titlebar.setSendText(getString(R.string.at_select));
        titlebar.setRightClickListener(() -> mAccessRecordPopup.showPopupWindow(titlebar));
    }

    @Override
    public void requestResult(String result, String url) {

    }
}