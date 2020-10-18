package com.rjn.thegamescompany.Custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.ScrollView;

public class PVRecyclerScrollview extends ScrollView {

    private int downX;
    private int downY;
    private int mTouchSlop;

    public PVRecyclerScrollview(Context context) {
        super(context);
        this.mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public PVRecyclerScrollview(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public PVRecyclerScrollview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public boolean onInterceptTouchEvent(MotionEvent e) {
        int action = e.getAction();
        if (action == 0) {
            this.downX = (int) e.getRawX();
            this.downY = (int) e.getRawY();
        } else if (action == 2 && Math.abs(((int) e.getRawY()) - this.downY) > this.mTouchSlop) {
            return true;
        }
        return super.onInterceptTouchEvent(e);
    }
}
