package com.aliyun.ayland.widget.popup;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.animation.Animation;
import android.widget.LinearLayout;

import com.aliyun.ayland.data.ATEventInteger;
import com.aliyun.ayland.widget.contrarywind.view.ATWheelView;
import com.aliyun.ayland.widget.pickerview.builder.ATOptionsPickerBuilder;
import com.aliyun.ayland.widget.pickerview.listener.ATCustomListener;
import com.aliyun.ayland.widget.pickerview.listener.ATOnOptionsSelectChangeListener;
import com.aliyun.ayland.widget.pickerview.listener.ATOnOptionsSelectListener;
import com.aliyun.ayland.widget.pickerview.view.ATOptionsPickerView;
import com.aliyun.ayland.widget.popup.base.ATBasePopupWindow;
import com.anthouse.wyzhuoyue.R;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class ATEnvironmentTimePopup extends ATBasePopupWindow {
    private Activity context;
    private ATOptionsPickerView pvOptions;
    private ATWheelView mWheelCompareType;

    public ATEnvironmentTimePopup(Activity context) {
        super(context);
        this.context = context;
        setAdjustInputMethod(true);
        setBackPressEnable(false);
        setPopupWindowFullScreen(true);
        setDismissWhenTouchOuside(true);
        init();
    }

    private void init() {
        findViewById(R.id.tv_cancel).setOnClickListener(view -> dismiss());
        findViewById(R.id.tv_sure).setOnClickListener(view -> {
            EventBus.getDefault().post(new ATEventInteger("EnvironmentOutsideFragment", mWheelCompareType.getCurrentItem()));
            dismiss();
        });
        pvOptions = new ATOptionsPickerBuilder(context, new ATOnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
            }
        }).setLayoutRes(R.layout.at_pickerview_gym_time, new ATCustomListener() {
            @Override
            public void customLayout(View v) {
                mWheelCompareType = v.findViewById(R.id.options1);
            }
        })
                .isDialog(false)
                .setContentTextSize(20)
//                .setSelectOptions(compareTypeENList.indexOf(compareType))//默认选中项compareValue,compareType
                .setLineSpacingMultiplier(0f)
                .setDividerColor(0xFFEEEEEE)
                .setOutSideCancelable(false)
                .setTitleBgColor(Color.WHITE)
                .setTextColorCenter(0xFF333333)
                .setTextColorOut(0xFF999999)
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .setDecorView((LinearLayout) findViewById(R.id.popup_container))
                .setOptionsSelectChangeListener(new ATOnOptionsSelectChangeListener() {
                    @Override
                    public void onOptionsSelectChanged(int options1, int options2, int options3) {

                    }
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