package com.aliyun.ayland.widget.popup;

import android.app.Activity;
import android.view.View;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aliyun.ayland.interfaces.ATOnPopupItemClickListener;
import com.aliyun.ayland.widget.contrarywind.view.ATWheelView;
import com.aliyun.ayland.widget.pickerview.builder.ATTimePickerBuilder;
import com.aliyun.ayland.widget.pickerview.view.ATTimePickerView;
import com.aliyun.ayland.widget.popup.base.ATBasePopupWindow;
import com.anthouse.wyzhuoyue.R;

public class ATLinkageTimePopup extends ATBasePopupWindow {
    private Activity context;
    private ATWheelView mWheelHour, mWheelMinute;
    private TextView mTvTitle;

    public ATLinkageTimePopup(Activity context) {
        super(context);
        this.context = context;
        setAdjustInputMethod(true);
        setBackPressEnable(false);
        setPopupWindowFullScreen(true);
        setDismissWhenTouchOuside(true);
        init();
    }

    private void init() {
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        findViewById(R.id.tv_cancel).setOnClickListener(view -> dismiss());
        findViewById(R.id.tv_sure).setOnClickListener(view -> {
            mOnItemClickListener.onItemClick(mWheelHour.getCurrentItem(), mWheelMinute.getCurrentItem());
            dismiss();
        });
        ATTimePickerView pvCustomTime = new ATTimePickerBuilder(context, (date, v) -> {//选中事件回调

        }).setLayoutRes(R.layout.at_pickerview_custom_time, v -> {
            mWheelHour = v.findViewById(R.id.hour);
            mWheelMinute = v.findViewById(R.id.min);
        })
                .isDialog(false)
                .isCyclic(true)
                .setContentTextSize(20)
                .setType(new boolean[]{false, false, false, true, true, false})
                .setLabel("", "", "", "", "", "")
                .setLineSpacingMultiplier(3.0f)
                .isCenterLabel(true)
                .setDividerColor(0xFF1478C8)
                .setDecorView((LinearLayout) findViewById(R.id.ll_timing))
                .setOutSideCancelable(false)
                .build();
        pvCustomTime.setKeyBackCancelable(false);
        pvCustomTime.show(false);
    }

    @Override
    protected Animation initShowAnimation() {
        return getTranslateAnimation(250 * 2, 0, 300);
    }

    @Override
    protected Animation initExitAnimation() {
        return getTranslateAnimation(0, 250 * 2, 300);
    }

    @Override
    public View getClickToDismissView() {
        return findViewById(R.id.click_to_dismiss);
    }

    @Override
    public View onCreatePopupView() {
        return createPopupById(R.layout.at_popup_bottom_hour_min);
    }

    @Override
    public View initAnimaView() {
        return findViewById(R.id.popup_anima);
    }

    public void setCurrentTime(String time, boolean begin) {
        mTvTitle.setText(begin ? context.getString(R.string.at_begin) : context.getString(R.string.at_end));
        mWheelHour.setCurrentItem(Integer.parseInt(time.split(":")[0]));
        mWheelMinute.setCurrentItem(Integer.parseInt(time.split(":")[1]));
    }

    private ATOnPopupItemClickListener mOnItemClickListener = null;

    public void setOnItemClickListener(ATOnPopupItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
}