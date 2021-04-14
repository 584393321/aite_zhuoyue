package com.aliyun.ayland.ui.fragment;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.aliyun.ayland.base.ATBaseFragment;
import com.aliyun.ayland.data.ATEventClazz;
import com.aliyun.ayland.global.ATGlobalApplication;
import com.aliyun.ayland.ui.activity.ATLinkageAddActivity;
import com.aliyun.ayland.widget.titlebar.ATMyTitleBar;
import com.anthouse.wyzhuoyue.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;


/**
 * Created by fr on 2017/12/19.
 */

public class ATLinkageFragment extends ATBaseFragment {
    public static final int REQUEST_CODE_EDIT_LINKAGE = 0x1002;
    private ATLinkageFamilyFragment mLinkageFamilyFragment;
    private ATTestFragment mTestFragment;
    private List<Fragment> mFragments;
    private Fragment mCurFragment, toFragment;
    private Handler handler = new Handler();
    private RecyclerView rvLinkageScene;
    private SmartRefreshLayout smartRefreshLayout;
    private ATMyTitleBar titleBar;

    @Override
    protected int getLayoutId() {
        return R.layout.at_activity_linkage;
    }

    @Override
    protected void findView(View view) {
        titleBar = view.findViewById(R.id.titlebar);
        rvLinkageScene = view.findViewById(R.id.rv_linkage_scene);
        smartRefreshLayout = view.findViewById(R.id.smartRefreshLayout);
        init();
    }

    @Override
    protected void initPresenter() {

    }

    private void init() {
        titleBar.setTitle("");
        titleBar.setTitleStrings(getString(R.string.at_recommend), getString(R.string.at_mine));
        titleBar.setRightBtTextImage(R.drawable.gengduo_a);
        titleBar.setCeneterClick(who -> {
            toFragment = mFragments.get(who);
            showFragment(mCurFragment, toFragment);
            mCurFragment = toFragment;
        });

        titleBar.setTitleBarClickBackListener(() -> {
            EventBus.getDefault().post(new ATEventClazz("HomeFragment"));
        });

        titleBar.setRightClickListener(() -> {
            if (TextUtils.isEmpty(ATGlobalApplication.getHouse())) {
                showToast(getString(R.string.at_can_not_create_scene));
                return;
            }
            startActivityForResult(new Intent(getActivity(), ATLinkageAddActivity.class), REQUEST_CODE_EDIT_LINKAGE);
        });
//        titleBar.settitleBarClickBackListener(() -> {
////            startActivity(new Intent(getActivity(), LinkageAddConditionActivity.class));
//            Router.getInstance().toUrl(getContext(), "link://router/devicenotices");
//        });
        mLinkageFamilyFragment = new ATLinkageFamilyFragment();
        mTestFragment = new ATTestFragment();

        mFragments = new ArrayList<>();
        if (mFragments.size() == 0) {
            mFragments.add(mLinkageFamilyFragment);
            mFragments.add(mTestFragment);
        }
        mCurFragment = mFragments.get(0);
        replaceFragment(mCurFragment);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager supportFragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.framelayout, fragment).commit();
    }

    private void showFragment(Fragment from, Fragment to) {
        FragmentManager supportFragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = supportFragmentManager.beginTransaction();
        if (!to.isAdded()) {
            transaction.hide(from).add(R.id.framelayout, to).commitAllowingStateLoss();
        } else {
            transaction.hide(from).show(to).commitAllowingStateLoss();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_EDIT_LINKAGE) {
            mLinkageFamilyFragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}