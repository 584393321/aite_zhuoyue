package com.aliyun.ayland.widget.popup;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.animation.Animation;
import android.widget.LinearLayout;

import com.aliyun.ayland.listener.ATOnIntegerListener;
import com.aliyun.ayland.widget.contrarywind.view.ATWheelView;
import com.aliyun.ayland.widget.pickerview.builder.ATOptionsPickerBuilder;
import com.aliyun.ayland.widget.pickerview.listener.ATCustomListener;
import com.aliyun.ayland.widget.pickerview.view.ATOptionsPickerView;
import com.aliyun.ayland.widget.popup.base.ATBasePopupWindow;
import com.anthouse.wyzhuoyue.R;

import java.util.List;

public class ATGymDatePopup extends ATBasePopupWindow {
    private Activity context;
    private ATOptionsPickerView pvOptions;
    private ATWheelView mWheelCompareType;
    private ATOnIntegerListener atOnIntegerCallBack;

    public ATGymDatePopup(Activity context, ATOnIntegerListener atOnIntegerCallBack) {
        super(context);
        this.context = context;
        this.atOnIntegerCallBack = atOnIntegerCallBack;
        setAdjustInputMethod(true);
        setBackPressEnable(false);
        setPopupWindowFullScreen(true);
        setDismissWhenTouchOuside(true);
        init();
    }

    private void init() {
        findViewById(R.id.tv_cancel).setOnClickListener(view -> dismiss());
        findViewById(R.id.tv_sure).setOnClickListener(view -> {
            atOnIntegerCallBack.onItemClick(mWheelCompareType.getCurrentItem());
            dismiss();
        });
        pvOptions = new ATOptionsPickerBuilder(context, (options1, options2, options3, v) -> {
        }).setLayoutRes(R.layout.at_pickerview_gym_time, v -> mWheelCompareType = v.findViewById(R.id.options1))
                .isDialog(false)
                .setContentTextSize(24)
//                .setSelectOptions(compareTypeENList.indexOf(compareType))//默认选中项compareValue,compareType
                .setLineSpacingMultiplier(2.0f)
                .setDividerColor(0xFFF0F0F0)
                .setOutSideCancelable(false)
                .setTitleBgColor(Color.WHITE)
                .setTextColorCenter(0xFFFDA448)
                .setTextColorOut(0xFF333333)
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .setDecorView((LinearLayout) findViewById(R.id.popup_container))
                .setOptionsSelectChangeListener((options1, options2, options3) -> {

                })
                .setBgColor(Color.WHITE)
                .build();
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

    public void setList(List<String> list) {
        if (list != null) {
            pvOptions.setPicker(list);
            pvOptions.show(false);
        }
    }
}