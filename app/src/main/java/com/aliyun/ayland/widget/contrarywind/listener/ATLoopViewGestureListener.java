package com.aliyun.ayland.widget.contrarywind.listener;

import android.view.MotionEvent;

import com.aliyun.ayland.widget.contrarywind.view.ATWheelView;


/**
 * 手势监听
 */
public final class ATLoopViewGestureListener extends android.view.GestureDetector.SimpleOnGestureListener {

    private final ATWheelView wheelView;


    public ATLoopViewGestureListener(ATWheelView wheelView) {
        this.wheelView = wheelView;
    }

    @Override
    public final boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        wheelView.scrollBy(velocityY);
        return true;
    }
}
