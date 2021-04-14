package com.aliyun.ayland.ui.fragment;

import android.view.View;

import com.aliyun.ayland.base.ATBaseFragment;
import com.anthouse.wyzhuoyue.R;


public class ATEmptyFragment extends ATBaseFragment {
    private String TAG = "TestFragment";

    @Override
    protected int getLayoutId() {
        return R.layout.at_fragment_empty;
    }

    @Override
    protected void findView(View view) {
        init();
    }

    private void init() {
    }

    @Override
    protected void initPresenter() {

    }
}
