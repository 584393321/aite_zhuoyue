package com.aliyun.ayland.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.WindowManager;

import com.anthouse.wyzhuoyue.R;

public class ATPingfangTextView extends android.support.v7.widget.AppCompatTextView {

    private int baseScreenHeight;

    public ATPingfangTextView(Context context) {
        super(context);
    }

    public ATPingfangTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray type = context.obtainStyledAttributes(attrs, R.styleable.ATPingfangTextView);//获得属性值
        int i = type.getInteger(R.styleable.ATPingfangTextView_textSizePx, 25);
        baseScreenHeight = type.getInteger(R.styleable.ATPingfangTextView_baseScreenHeight, 1920);
        setTextSize(TypedValue.COMPLEX_UNIT_PX, getFontSize(i));
        setTypeface(Typeface.createFromAsset(getContext().getAssets(), "PingFang.ttc"));
    }

    /**
     * 获取当前手机屏幕分辨率，然后根据和设计图的比例对照换算实际字体大小
     * @param textSize
     * @return
     */
    private int getFontSize(int textSize) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        int screenHeight = dm.heightPixels;
        Log.d("LOGCAT","screenHeight"+ screenHeight+"baseScreenHeight"+ baseScreenHeight);
        int rate = (int) (textSize * (float) screenHeight / baseScreenHeight);
        return rate;
    }
}