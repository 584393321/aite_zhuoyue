package com.aliyun.ayland.widget.popup;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aliyun.ayland.data.ATEventInteger2;
import com.aliyun.ayland.widget.contrarywind.view.ATWheelView;
import com.aliyun.ayland.widget.pickerview.builder.ATOptionsPickerBuilder;
import com.aliyun.ayland.widget.pickerview.listener.ATCustomListener;
import com.aliyun.ayland.widget.pickerview.listener.ATOnOptionsSelectChangeListener;
import com.aliyun.ayland.widget.pickerview.listener.ATOnOptionsSelectListener;
import com.aliyun.ayland.widget.pickerview.view.ATOptionsPickerView;
import com.aliyun.ayland.widget.popup.base.ATBasePopupWindow;
import com.anthouse.wyzhuoyue.R;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class ATMorningTimePopup extends ATBasePopupWindow {
    private Activity context;
    private ATOptionsPickerView pvOptions;
    private ATWheelView mWheelHour, mWheelMinute;
    private List<Integer> mHourList;
    private List<List<Integer>> mMinList;

    public ATMorningTimePopup(Activity context) {
        super(context);
        this.context = context;
        setAdjustInputMethod(true);
        setBackPressEnable(false);
        setPopupWindowFullScreen(true);
        setDismissWhenTouchOuside(true);
        init();
    }

    private void init() {
        findViewById(R.id.img_close).setOnClickListener(view -> dismiss());
        ((TextView) findViewById(R.id.tv_title)).setText(context.getString(R.string.at_morning_start_time));
        findViewById(R.id.tv_sure).setOnClickListener(view -> {
            EventBus.getDefault().post(new ATEventInteger2("LinkageRecommendActivity", mWheelHour.getCurrentItem(), mWheelMinute.getCurrentItem()));
            dismiss();
        });
        pvOptions = new ATOptionsPickerBuilder(context, new ATOnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
            }
        }).setLayoutRes(R.layout.at_pickerview_morning_time, new ATCustomListener() {
            @Override
            public void customLayout(View v) {
                mWheelHour = v.findViewById(R.id.options1);
                mWheelMinute = v.findViewById(R.id.options2);
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
                .setLabels("时", "分", "秒")
                .setCyclic(true, true, true)
                .isCenterLabel(true)
                .setDecorView((LinearLayout) findViewById(R.id.popup_container))
                .setOptionsSelectChangeListener(new ATOnOptionsSelectChangeListener() {
                    @Override
                    public void onOptionsSelectChanged(int options1, int options2, int options3) {

                    }
                })
                .setBgColor(Color.WHITE)
                .build();


        mHourList = new ArrayList<>();
        for (int i = 0; i < 24; i++) {
            mHourList.add(i);
        }
        mMinList = new ArrayList<>();
        List<Integer> minList = new ArrayList<>();
        for (int j = 0; j < 60; j++) {
            minList.add(j);
        }
        for (int i = 0; i < 24; i++) {
            mMinList.add(minList);
        }
        pvOptions.setPicker(mHourList, mMinList);
        pvOptions.show(false);
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

    public void setCurrentTime(int hour, int min) {
        mWheelHour.setCurrentItem(hour);
        mWheelMinute.setCurrentItem(min);
    }
}