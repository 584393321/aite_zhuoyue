package com.aliyun.ayland.base.autolayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.aliyun.ayland.base.autolayout.util.ATAutoLayoutHelper;

/**
 * Created by zhy on 15/6/30.
 */
public class ATAutoLinearLayout extends LinearLayout {
    private ATAutoLayoutHelper mHelper = new ATAutoLayoutHelper(this);

    public ATAutoLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!isInEditMode())
            mHelper.adjustChildren();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new ATAutoLinearLayout.LayoutParams(getContext(), attrs);
    }

    public static class LayoutParams extends LinearLayout.LayoutParams implements ATAutoLayoutHelper.AutoLayoutParams {
        private ATAutoLayoutInfo mATAutoLayoutInfo;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            mATAutoLayoutInfo = ATAutoLayoutHelper.getAutoLayoutInfo(c, attrs);
        }

        @Override
        public ATAutoLayoutInfo getATAutoLayoutInfo() {
            return mATAutoLayoutInfo;
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }
    }
}