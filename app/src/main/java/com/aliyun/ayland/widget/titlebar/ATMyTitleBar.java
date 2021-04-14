package com.aliyun.ayland.widget.titlebar;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aliyun.ayland.base.autolayout.util.ATAutoUtils;
import com.aliyun.ayland.global.ATConstants;
import com.aliyun.ayland.utils.ATDensityUtils;
import com.anthouse.wyzhuoyue.R;

import at.smarthome.ATHelp;

public class ATMyTitleBar extends RelativeLayout implements OnClickListener {
    private Context context;
    private ATOnTitleRightClickInter inter;
    private ATOnTitleRightToClickInter interTo;
    private ATOnTitlePublishClickInter interPublish;
    private ATTitleBarClickBackListener titleBackInter;
    private TextView tvBack, tvLeft, tvRight, tvTitle, tvRightText, tvSend, tvSend1;
    private String title, rightBtText;
    private RelativeLayout rightButton;
    private ImageView ibRightImg;
    private boolean showib = true, showBtImgFlag = false;
    private LinearLayout llTitleContent, llChoise, llRight, llRight1;
    private int titleColorBackground, titleBarBackground, titleColor;
    private ATCenterClick centerClick;

    public ATMyTitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.mytitlebar, defStyleAttr, 0);
        title = a.getString(R.styleable.mytitlebar_bartext);
        rightBtText = a.getString(R.styleable.mytitlebar_bttext);
        showBtImgFlag = a.getBoolean(R.styleable.mytitlebar_show_r_img, false);
        showib = a.getBoolean(R.styleable.mytitlebar_show_ib, true);
        titleColorBackground = a.getResourceId(R.styleable.mytitlebar_title_color_drawable, R.color.transparent);
        titleBarBackground = a.getResourceId(R.styleable.mytitlebar_titlebar_background, R.color.white);
        a.recycle();
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        View viewTitle = LayoutInflater.from(context).inflate(R.layout.at_title_bar_layout, null);
        llTitleContent = viewTitle.findViewById(R.id.ll_title_content);
        tvBack = viewTitle.findViewById(R.id.tv_back);
        tvBack.setOnClickListener(this);
        tvTitle = viewTitle.findViewById(R.id.tv_title_name);
        ImageView statusBar = viewTitle.findViewById(R.id.status_bar);
        rightButton = viewTitle.findViewById(R.id.bt_right);
        llRight = viewTitle.findViewById(R.id.ll_right);
        llRight1 = viewTitle.findViewById(R.id.ll_right1);
        tvRightText = viewTitle.findViewById(R.id.bt_text);
        tvSend = viewTitle.findViewById(R.id.tv_send);
        tvSend1 = viewTitle.findViewById(R.id.tv_send1);
        ibRightImg = viewTitle.findViewById(R.id.ib_img);
        rightButton.setOnClickListener(this);
        if (!TextUtils.isEmpty(tvRightText.getText().toString()))
            setRightButtonEnable(false);
        llRight.setOnClickListener(this);
        llRight1.setOnClickListener(this);
        ibRightImg.setOnClickListener(this);
        llChoise = viewTitle.findViewById(R.id.ll_choise);
        tvLeft = viewTitle.findViewById(R.id.tv_left);
        tvRight = viewTitle.findViewById(R.id.tv_right);

        Drawable mDrawableSelete = getResources().getDrawable(R.drawable.at_shape_5px_60px6px_1478c8);
        mDrawableSelete.setBounds(0, 0, mDrawableSelete.getMinimumWidth(), mDrawableSelete.getMinimumHeight());
        tvLeft.setOnClickListener(view -> {
            if (centerClick != null) {
                centerClick.buttonChoise(0);
            }
            tvLeft.setTextColor(getResources().getColor(R.color._1478C8));
            tvLeft.setCompoundDrawables(null, null, null, mDrawableSelete);
            tvLeft.setCompoundDrawablePadding(ATAutoUtils.getPercentWidthSize(3));
            tvRight.setTextColor(getResources().getColor(R.color._666666));
            tvRight.setCompoundDrawables(null, null, null, null);
        });
        tvRight.setOnClickListener(view -> {
            if (centerClick != null) {
                centerClick.buttonChoise(1);
            }
            tvLeft.setTextColor(getResources().getColor(R.color._666666));
            tvLeft.setCompoundDrawables(null, null, null, null);
            tvRight.setTextColor(getResources().getColor(R.color._1478C8));
            tvRight.setCompoundDrawables(null, null, null, mDrawableSelete);
            tvRight.setCompoundDrawablePadding(ATAutoUtils.getPercentWidthSize(3));
        });
        tvTitle.setText(title);
        tvRightText.setText(rightBtText);
        showRightButtonImg(showBtImgFlag);
        showBackIb(showib);
        // 当titlebar的颜色改变了。其他的颜色也要改变
//        setImageBackGroundRes(titleColorBackground);
        setTitleBarBackground(titleBarBackground);
//        setRightButtonBackground(titleColorBackground);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            // 只有系统版本19以上才支持状态栏。所以19以下的就把状态栏去掉。
            statusBar.setVisibility(View.GONE);
        } else {
            statusBar.setVisibility(View.GONE);
        }

        titleColor = context.getResources().getColor(R.color._009688);
        this.addView(viewTitle);
    }

    public ATMyTitleBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public void setCeneterClick(ATCenterClick click) {
        this.centerClick = click;
    }

    public ATMyTitleBar(Context context) {
        this(context, null);
    }

    public void clickBack() {
        if (context instanceof Activity) {
            ((Activity) context).finish();
        }
    }

    // 设置监听事件
    public void setRightClickListener(ATOnTitleRightClickInter inter) {
        this.inter = inter;
    }

    // 设置监听事件
    public void setRightToClickListener(ATOnTitleRightToClickInter interTo) {
        this.interTo = interTo;
    }

    public void setPublishClickListener(ATOnTitlePublishClickInter interPublish) {
        this.interPublish = interPublish;
    }

    // 是否隐藏右边的按钮
    public void showRightButton(boolean flag) {
        rightButton.setVisibility(flag ? View.VISIBLE : View.INVISIBLE);
    }

    // 是否隐藏右边的按钮的图片
    public void showRightButtonImg(boolean flag) {
        ibRightImg.setVisibility(flag ? View.VISIBLE : View.GONE);
    }

    // 是否显示中间选择器
    public void setTitleStrings(String left, String right) {
        llChoise.setVisibility(View.VISIBLE);
        tvLeft.setText(left);
        tvRight.setText(right);
    }

    public RelativeLayout getRightButton() {
        return rightButton;
    }

    // 右边的按钮是否可点
    public void setRightButtonEnable(boolean flag) {
        rightButton.setClickable(flag);
    }

    // 设置右边按钮的文字
    public void setRightButtonText(String str) {
        setRightButtonEnable(!TextUtils.isEmpty(str));
        tvRightText.setText(str);
    }

    public void setSendText(String str) {
        setRightButtonText(str);
//        llRight.setVisibility(TextUtils.isEmpty(str) ? View.INVISIBLE : View.VISIBLE);
//        tvSend.setText(str);
    }

    public void setSend1Text(String str) {
        llRight1.setVisibility(TextUtils.isEmpty(str) ? View.INVISIBLE : View.VISIBLE);
        tvSend1.setText(str);
    }

    public TextView getTvTitle() {
        return tvTitle;
    }

    public String getSendText() {
        return tvRightText.getText().toString();
    }

    public String getSend1Text() {
        return tvSend1.getText().toString();
    }

    public void setRightBtTextImage(int resId) {
        setRightButtonEnable(true);
        Drawable drawable = getResources().getDrawable(resId);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        tvRightText.setCompoundDrawables(drawable, null, null, null);
        tvRightText.setGravity(Gravity.CENTER);
        tvRightText.setCompoundDrawablePadding(ATDensityUtils.dip2px(getContext(), 3.33f));
    }

    public LinearLayout getLlTitleContent() {
        return llTitleContent;
    }

    public void setRightBtTextImage1(int resId) {
        setRightButtonEnable(true);
        Drawable drawable = getResources().getDrawable(resId);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        tvRightText.setCompoundDrawables(null, null, drawable, null);
        tvRightText.setGravity(Gravity.CENTER);
        tvRightText.setCompoundDrawablePadding(ATDensityUtils.dip2px(getContext(), 3.33f));
    }

    // 是否隐藏返回键
    public void showBackIb(boolean flag) {
        int show = flag ? View.VISIBLE : View.GONE;
        tvBack.setVisibility(show);
    }

    public void setTitleBarClickBackListener(ATTitleBarClickBackListener titleBackInter) {
        this.titleBackInter = titleBackInter;
    }

    public void setTitleBarBackground(int drawable) {
        llTitleContent.setBackgroundResource(drawable);
    }

    public void setTitle(String str) {
        if (!TextUtils.isEmpty(str))
            tvTitle.setText(str);
    }

    public void setTvBack(String str) {
        tvBack.setText(str);
    }

    @Override
    public void onClick(View v) {
        ATHelp.setClickDelay(v, ATConstants.clickDelay);
        if (R.id.tv_back == v.getId()) {
            if (titleBackInter != null) {
                titleBackInter.clickBack();
                return;
            }
            clickBack();
        } else if (R.id.bt_right == v.getId()) {
            if (inter != null) {
                inter.rightClick();
            }
        } else if (R.id.ib_img == v.getId()) {
            if (interTo != null) {
                interTo.rightToClick();
            }
        } else if (R.id.ll_right == v.getId()) {
            if (interPublish != null) {
                interPublish.publish();
            }
        } else if (R.id.ll_right1 == v.getId()) {
            if (interPublish != null) {
                interPublish.publish();
            }
        }
    }
}