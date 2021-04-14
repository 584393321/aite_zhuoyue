package com.aliyun.ayland.widget.pickerview.builder;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.ColorInt;
import android.view.View;
import android.view.ViewGroup;

import com.aliyun.ayland.widget.contrarywind.view.ATWheelView;
import com.aliyun.ayland.widget.pickerview.configure.ATPickerOptions;
import com.aliyun.ayland.widget.pickerview.listener.ATCustomListener;
import com.aliyun.ayland.widget.pickerview.listener.ATOnOptionsSelectChangeListener;
import com.aliyun.ayland.widget.pickerview.listener.ATOnOptionsSelectListener;
import com.aliyun.ayland.widget.pickerview.view.ATOptionsPickerView;

/**
 * Created by xiaosongzeem on 2018/3/20.
 */

public class ATOptionsPickerBuilder {

    //配置类
    private ATPickerOptions mPickerOptions;


    //Required
    public ATOptionsPickerBuilder(Context context, ATOnOptionsSelectListener listener) {
        mPickerOptions = new ATPickerOptions(ATPickerOptions.TYPE_PICKER_OPTIONS);
        mPickerOptions.context = context;
        mPickerOptions.optionsSelectListener = listener;
    }

    //Option
    public ATOptionsPickerBuilder setSubmitText(String textContentConfirm) {
        mPickerOptions.textContentConfirm = textContentConfirm;
        return this;
    }

    public ATOptionsPickerBuilder setCancelText(String textContentCancel) {
        mPickerOptions.textContentCancel = textContentCancel;
        return this;
    }

    public ATOptionsPickerBuilder setTitleText(String textContentTitle) {
        mPickerOptions.textContentTitle = textContentTitle;
        return this;
    }

    public ATOptionsPickerBuilder isDialog(boolean isDialog) {
        mPickerOptions.isDialog = isDialog;
        return this;
    }

    public ATOptionsPickerBuilder setOnCancelClickListener(View.OnClickListener cancelListener) {
        mPickerOptions.cancelListener = cancelListener;
        return this;
    }


    public ATOptionsPickerBuilder setSubmitColor(int textColorConfirm) {
        mPickerOptions.textColorConfirm = textColorConfirm;
        return this;
    }

    public ATOptionsPickerBuilder setCancelColor(int textColorCancel) {
        mPickerOptions.textColorCancel = textColorCancel;
        return this;
    }


    /**
     * {@link #setOutSideColor} instead.
     *
     * @param backgroundId color resId.
     */
    @Deprecated
    public ATOptionsPickerBuilder setBackgroundId(int backgroundId) {
        mPickerOptions.outSideColor = backgroundId;
        return this;
    }

    /**
     * 显示时的外部背景色颜色,默认是灰色
     *
     * @param outSideColor color resId.
     * @return
     */
    public ATOptionsPickerBuilder setOutSideColor(int outSideColor) {
        mPickerOptions.outSideColor = outSideColor;
        return this;
    }

    /**
     * ViewGroup 类型
     * 设置PickerView的显示容器
     *
     * @param decorView Parent View.
     * @return
     */
    public ATOptionsPickerBuilder setDecorView(ViewGroup decorView) {
        mPickerOptions.decorView = decorView;
        return this;
    }

    public ATOptionsPickerBuilder setLayoutRes(int res, ATCustomListener listener) {
        mPickerOptions.layoutRes = res;
        mPickerOptions.customListener = listener;
        return this;
    }

    public ATOptionsPickerBuilder setBgColor(int bgColorWheel) {
        mPickerOptions.bgColorWheel = bgColorWheel;
        return this;
    }

    public ATOptionsPickerBuilder setTitleBgColor(int bgColorTitle) {
        mPickerOptions.bgColorTitle = bgColorTitle;
        return this;
    }

    public ATOptionsPickerBuilder setTitleColor(int textColorTitle) {
        mPickerOptions.textColorTitle = textColorTitle;
        return this;
    }

    public ATOptionsPickerBuilder setSubCalSize(int textSizeSubmitCancel) {
        mPickerOptions.textSizeSubmitCancel = textSizeSubmitCancel;
        return this;
    }

    public ATOptionsPickerBuilder setTitleSize(int textSizeTitle) {
        mPickerOptions.textSizeTitle = textSizeTitle;
        return this;
    }

    public ATOptionsPickerBuilder setContentTextSize(int textSizeContent) {
        mPickerOptions.textSizeContent = textSizeContent;
        return this;
    }

    public ATOptionsPickerBuilder setOutSideCancelable(boolean cancelable) {
        mPickerOptions.cancelable = cancelable;
        return this;
    }


    public ATOptionsPickerBuilder setLabels(String label1, String label2, String label3) {
        mPickerOptions.label1 = label1;
        mPickerOptions.label2 = label2;
        mPickerOptions.label3 = label3;
        return this;
    }

    /**
     * 设置Item 的间距倍数，用于控制 Item 高度间隔
     *
     * @param lineSpacingMultiplier 浮点型，1.0-4.0f 之间有效,超过则取极值。
     */
    public ATOptionsPickerBuilder setLineSpacingMultiplier(float lineSpacingMultiplier) {
        mPickerOptions.lineSpacingMultiplier = lineSpacingMultiplier;
        return this;
    }

    /**
     * Set item divider line type color.
     *
     * @param dividerColor color resId.
     */
    public ATOptionsPickerBuilder setDividerColor(@ColorInt int dividerColor) {
        mPickerOptions.dividerColor = dividerColor;
        return this;
    }

    /**
     * Set item divider line type.
     *
     * @param dividerType enum Type {@link ATWheelView.DividerType}
     */
    public ATOptionsPickerBuilder setDividerType(ATWheelView.DividerType dividerType) {
        mPickerOptions.dividerType = dividerType;
        return this;
    }

    /**
     * Set the textColor of selected item.
     *
     * @param textColorCenter color res.
     */
    public ATOptionsPickerBuilder setTextColorCenter(int textColorCenter) {
        mPickerOptions.textColorCenter = textColorCenter;
        return this;
    }

    /**
     * Set the textColor of outside item.
     *
     * @param textColorOut color resId.
     */
    public ATOptionsPickerBuilder setTextColorOut(@ColorInt int textColorOut) {
        mPickerOptions.textColorOut = textColorOut;
        return this;
    }

    public ATOptionsPickerBuilder setTypeface(Typeface font) {
        mPickerOptions.font = font;
        return this;
    }

    public ATOptionsPickerBuilder setCyclic(boolean cyclic1, boolean cyclic2, boolean cyclic3) {
        mPickerOptions.cyclic1 = cyclic1;
        mPickerOptions.cyclic2 = cyclic2;
        mPickerOptions.cyclic3 = cyclic3;
        return this;
    }

    public ATOptionsPickerBuilder setSelectOptions(int option1) {
        mPickerOptions.option1 = option1;
        return this;
    }

    public ATOptionsPickerBuilder setSelectOptions(int option1, int option2) {
        mPickerOptions.option1 = option1;
        mPickerOptions.option2 = option2;
        return this;
    }

    public ATOptionsPickerBuilder setSelectOptions(int option1, int option2, int option3) {
        mPickerOptions.option1 = option1;
        mPickerOptions.option2 = option2;
        mPickerOptions.option3 = option3;
        return this;
    }

    public ATOptionsPickerBuilder setTextXOffset(int xoffset_one, int xoffset_two, int xoffset_three) {
        mPickerOptions.x_offset_one = xoffset_one;
        mPickerOptions.x_offset_two = xoffset_two;
        mPickerOptions.x_offset_three = xoffset_three;
        return this;
    }

    public ATOptionsPickerBuilder isCenterLabel(boolean isCenterLabel) {
        mPickerOptions.isCenterLabel = isCenterLabel;
        return this;
    }

    /**
     * 切换选项时，是否还原第一项
     *
     * @param isRestoreItem true：还原； false: 保持上一个选项
     * @return TimePickerBuilder
     */
    public ATOptionsPickerBuilder isRestoreItem(boolean isRestoreItem) {
        mPickerOptions.isRestoreItem = isRestoreItem;
        return this;
    }

    /**
     * @param listener 切换item项滚动停止时，实时回调监听。
     * @return
     */
    public ATOptionsPickerBuilder setOptionsSelectChangeListener(ATOnOptionsSelectChangeListener listener) {
        mPickerOptions.optionsSelectChangeListener = listener;
        return this;
    }


    public <T> ATOptionsPickerView<T> build() {
        return new ATOptionsPickerView<>(mPickerOptions);
    }
}
