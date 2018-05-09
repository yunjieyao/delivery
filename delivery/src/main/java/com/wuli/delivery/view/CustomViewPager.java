package com.wuli.delivery.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 自定义的ViewPager，可禁用滑动
 *
 * @author ziv
 */

public class CustomViewPager extends ViewPager {

    private boolean mPagingEnabled = true;

    public CustomViewPager(Context context) {
        super(context);
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mPagingEnabled && super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return mPagingEnabled && super.onInterceptTouchEvent(event);
    }

    /**
     * 是否允许页面滑动
     */
    public void setPagingEnabled(boolean b) {
        mPagingEnabled = b;
    }

    @Override
    public void setCurrentItem(int item) {
        // 不展示滑动动画
        setCurrentItem(item, mPagingEnabled);
    }
}
