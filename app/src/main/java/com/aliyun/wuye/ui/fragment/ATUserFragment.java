package com.aliyun.wuye.ui.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.aliyun.ayland.base.ATBaseFragment;
import com.aliyun.ayland.global.ATGlobalApplication;
import com.aliyun.ayland.ui.activity.ATSettingActivity;
import com.aliyun.ayland.ui.activity.ATUserCenterActivity;
import com.anthouse.wyzhuoyue.R;


public class ATUserFragment extends ATBaseFragment {
    private String TAG = "TestFragment";

    @Override
    protected int getLayoutId() {
        return R.layout.at_fragment_user;
    }

    @Override
    protected void findView(View view) {
        TextView tvName = view.findViewById(R.id.tv_name);
        view.findViewById(R.id.rl_user).setOnClickListener(v -> startActivity(new Intent(getActivity(), ATUserCenterActivity.class)));
        view.findViewById(R.id.rl_setting).setOnClickListener(v -> startActivity(new Intent(getActivity(), ATSettingActivity.class)));
        tvName.setText(ATGlobalApplication.getATLoginBean().getNickName());
        init();
    }

    private void init() {
    }

    @Override
    protected void initPresenter() {

    }
}
