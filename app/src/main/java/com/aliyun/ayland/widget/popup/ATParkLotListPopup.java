package com.aliyun.ayland.widget.popup;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aliyun.ayland.interfaces.ATOnPopupItemClickListener;
import com.aliyun.ayland.widget.contrarywind.view.ATWheelView;
import com.aliyun.ayland.widget.pickerview.builder.ATOptionsPickerBuilder;
import com.aliyun.ayland.widget.pickerview.view.ATOptionsPickerView;
import com.aliyun.ayland.widget.popup.base.ATBasePopupWindow;
import com.anthouse.wyzhuoyue.R;

import java.util.List;

public class ATParkLotListPopup extends ATBasePopupWindow {
    private Activity context;
    private ATWheelView mWheelCompareType;
    private ATOptionsPickerView mPvOptions;

    public ATParkLotListPopup(Activity context) {
        super(context);
        this.context = context;
        setAdjustInputMethod(true);
        setBackPressEnable(false);
        setPopupWindowFullScreen(true);
        setDismissWhenTouchOuside(true);
        init();
    }

    private void init() {
        ((TextView) findViewById(R.id.tv_title)).setText("选择停车场");
        findViewById(R.id.tv_cancel).setOnClickListener(view -> dismiss());
        findViewById(R.id.tv_sure).setOnClickListener(view -> {
            mOnItemClickListener.onItemClick(mWheelCompareType.getCurrentItem(), 1);
            dismiss();
        });
        mPvOptions = new ATOptionsPickerBuilder(context, (options1, options2, options3, v) -> {
        }).setLayoutRes(R.layout.at_pickerview_gym_time, v -> {
            mWheelCompareType = v.findViewById(R.id.options1);
        })
                .isDialog(false)
                .setContentTextSize(20)
//                .setSelectOptions(compareTypeENList.indexOf(compareType))//默认选中项compareValue,compareType
                .setLineSpacingMultiplier(3.0f)
                .setDividerColor(0xFFEEEEEE)
                .setOutSideCancelable(false)
                .setTitleBgColor(Color.WHITE)
                .setTextColorCenter(0xFF333333)
                .setTextColorOut(0xFF999999)
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .setDecorView((LinearLayout) findViewById(R.id.popup_container))
                .setOptionsSelectChangeListener((options1, options2, options3) -> {
                })
                .setBgColor(Color.WHITE)
                .build();
    }

    public void setLists(List<String> list) {
        mPvOptions.setPicker(list);
        mPvOptions.show(false);
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
        return createPopupById(R.layout.at_bottom_time_popup);
    }

    @Override
    public View initAnimaView() {
        return findViewById(R.id.popup_anima);
    }

    private ATOnPopupItemClickListener mOnItemClickListener = null;

    public void setOnItemClickListener(ATOnPopupItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
}