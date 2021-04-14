package com.aliyun.ayland.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.aliyun.ayland.base.ATBaseActivity;
import com.aliyun.ayland.ui.fragment.ATMonitorRecordFragment;
import com.aliyun.ayland.ui.fragment.ATMonitorScreenFragment;
import com.aliyun.ayland.ui.fragment.ATSaveRecordFragment;
import com.aliyun.ayland.widget.titlebar.ATMyTitleBar;
import com.aliyun.iot.aep.component.router.Router;
import com.anthouse.wyzhuoyue.BuildConfig;
import com.anthouse.wyzhuoyue.R;
import com.facebook.react.bridge.AssertionException;

import java.util.ArrayList;
import java.util.List;

import static com.aliyun.ayland.ui.fragment.ATMonitorScreenFragment.REQUEST_CODE_RECORD;

public class ATIntelligentMonitorActivity extends ATBaseActivity {
    private ATMonitorScreenFragment mMonitorScreenFragment;
    private ATMyTitleBar titleBar;
    private ViewPager viewpager;
    private TabLayout tabs;

    @Override
    protected int getLayoutId() {
        return R.layout.at_activity_intelligent_monitor;
    }

    @Override
    protected void findView() {
        viewpager = findViewById(R.id.viewPager);
        titleBar = findViewById(R.id.titlebar);
        tabs = findViewById(R.id.tabs);
        init();
    }

    @Override
    protected void initPresenter() {
    }

    private void init() {
        setUpViewPager();
    }

    public final static int PAGE_COUNT = 3;

    private void setUpViewPager() {
//        titleBar.setRightButtonText(getString(R.string.at_setting));
//        titleBar.setRightClickListener(() -> {
//            Bundle bundle = new Bundle();
//            bundle.putString("iotId", getIntent().getStringExtra("iotId"));
//            Router.getInstance().toUrl(this, "link://router/" + getIntent().getStringExtra("productKey"), bundle);
//        });

        List<Fragment> fragments = initFragments();
        if (BuildConfig.DEBUG && PAGE_COUNT != fragments.size()) {
            throw new AssertionException("Fragments size must be equal to: " + PAGE_COUNT);
        }
        viewpager.setOffscreenPageLimit(fragments.size() - 1);
        viewpager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return PAGE_COUNT;
            }
        });

        tabs.setupWithViewPager(viewpager);
        initTabs();
    }

    private void initTabs() {
        final String[] titles = getResources().getStringArray(R.array.monitor_titles);
        for (int i = 0; i < PAGE_COUNT; i++) {
            View view = View.inflate(this, R.layout.at_tab_intelligent_monitor, null);
            ((TextView) view.findViewById(R.id.text)).setText(titles[i]);
            tabs.getTabAt(i).setCustomView(view);
        }
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
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
    }

    private void onSelectedStateChanged(TabLayout.Tab tab, boolean selected) {
        if (tab.getCustomView() != null) {
            TextView text = tab.getCustomView().findViewById(R.id.text);
            if (text != null) {
                text.setSelected(selected);
            }
        }
    }

    private List<Fragment> initFragments() {
        List<Fragment> mFragments = new ArrayList<>();
        mMonitorScreenFragment = new ATMonitorScreenFragment();
        mFragments.add(mMonitorScreenFragment);
        mFragments.add(new ATMonitorRecordFragment());
        mFragments.add(new ATSaveRecordFragment());
        return mFragments;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_RECORD)
            mMonitorScreenFragment.onActivityResult(requestCode, resultCode, data);
    }
}