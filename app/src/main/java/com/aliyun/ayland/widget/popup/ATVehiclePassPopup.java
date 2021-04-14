package com.aliyun.ayland.widget.popup;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;

import com.aliyun.ayland.ui.activity.ATCarApplyActivity;
import com.aliyun.ayland.ui.activity.ATMyCarAddActivity;
import com.aliyun.ayland.widget.popup.base.ATBasePopupWindow;
import com.anthouse.wyzhuoyue.R;


public class ATVehiclePassPopup extends ATBasePopupWindow {
    public ATVehiclePassPopup(Activity context) {
        super(context, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        findViewById(R.id.tv_vehicle_login).setOnClickListener(view -> {
            //车辆录入
            context.startActivity(new Intent(context, ATMyCarAddActivity.class));
            dismiss();
        });
        findViewById(R.id.tv_application_record).setOnClickListener(view -> {
            //申请记录
            context.startActivity(new Intent(context, ATCarApplyActivity.class));
            dismiss();
        });
    }

    @Override
    protected Animation initShowAnimation() {
        AnimationSet set = new AnimationSet(true);
        set.setInterpolator(new DecelerateInterpolator());
        set.addAnimation(getScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF, 0));
        set.addAnimation(getDefaultAlphaAnimation());
        return set;
    }

    @Override
    public View getClickToDismissView() {
        return null;
    }

    @Override
    public View onCreatePopupView() {
        return createPopupById(R.layout.at_popup_vehicle_pass);
    }

    @Override
    public View initAnimaView() {
        return getPopupWindowView().findViewById(R.id.popup_container);
    }
}