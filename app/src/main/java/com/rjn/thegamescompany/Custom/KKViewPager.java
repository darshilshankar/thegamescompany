package com.rjn.thegamescompany.Custom;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import androidx.core.view.MotionEventCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import com.rjn.thegamescompany.Adapter.InfinitePagerAdapter;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

public class KKViewPager extends ViewPager implements ViewPager.PageTransformer {
    public static final String TAG = "PVViewPager";
    private float MAX_SCALE = 0.05f;
    private int mPageMargin;
    private boolean animationEnabled = true;
    private boolean fadeEnabled = false;
    private float fadeFactor = 0.5f;

    // 默认滚动间隔时间
    private static final int DEFAULT_SLIDE_INTERVAL = 5000;
    // 自动滚动间隔时间
    private int slideInterval = DEFAULT_SLIDE_INTERVAL;

    private static final int DEFAULT_SLIDE_DURATION = 800;
    // 滑动时间
    private int slideDuration = DEFAULT_SLIDE_DURATION;

    // 滑动方向向右
    public static final int DIRECTION_RIGHT = 1;
    // 向左滑动
    public static final int DIRECTION_LEFT = 0;
    // 滑动方向
    private int direction = DIRECTION_RIGHT;
    // 触摸时停止滚动
    private boolean stopWhenTouch = true;

    // 滚动消息
    private static final int MSG_SCROLL = 1;

    // 是否循环（只针对普通ViewPager）
    private boolean cycle;

    // 是否是自动滚动
    private boolean isAutoScroll;
    // 是否已经触摸停止滚动
    private boolean isStopedWhenTouch;

    private MyHandler mHandler;
    private CustomDurationScroller mScroller;

    public KKViewPager(Context context) {
        this(context, null);
        init();
    }

    public KKViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        // clipping should be off on the pager for its children so that they can scale out of bounds.
        setClipChildren(true);
        setClipToPadding(false);
        // to avoid fade effect at the end of the page
        setOverScrollMode(2);
        setPageTransformer(false, this);
        setOffscreenPageLimit(3);
        mPageMargin = dp2px(context.getResources(), 15);
        setPadding(mPageMargin, mPageMargin, mPageMargin, mPageMargin);

        init();
    }

    public int dp2px(Resources resource, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resource.getDisplayMetrics());
    }

    public void setAnimationEnabled(boolean enable) {
        this.animationEnabled = enable;
    }

    public void setFadeEnabled(boolean fadeEnabled) {
        this.fadeEnabled = fadeEnabled;
    }

    public void setFadeFactor(float fadeFactor) {
        this.fadeFactor = fadeFactor;
    }

    @Override
    public void setPageMargin(int marginPixels) {
        mPageMargin = marginPixels;
        setPadding(mPageMargin, mPageMargin, mPageMargin, mPageMargin);
    }

    @Override
    public void transformPage(View page, float position) {
        if (mPageMargin <= 0 || !animationEnabled)
            return;
        page.setPadding(mPageMargin / 3, mPageMargin / 3, mPageMargin / 3, mPageMargin / 3);

        if (MAX_SCALE == 0.0f && position > 0.0f && position < 1.0f) {
            MAX_SCALE = position;
        }
        position = position - MAX_SCALE;
        float absolutePosition = Math.abs(position);
        if (position <= -1.0f || position >= 1.0f) {
            if (fadeEnabled)
                page.setAlpha(fadeFactor);
            // Page is not visible -- stop any running animations

        } else if (position == 0.0f) {

            // Page is selected -- reset any views if necessary
            page.setScaleX((1 + MAX_SCALE));
            page.setScaleY((1 + MAX_SCALE));
            page.setAlpha(1);
        } else {
            page.setScaleX(1 + MAX_SCALE * (1 - absolutePosition));
            page.setScaleY(1 + MAX_SCALE * (1 - absolutePosition));
            if (fadeEnabled)
                page.setAlpha(Math.max(fadeFactor, 1 - absolutePosition));
        }
    }

    private static class MyHandler extends Handler {
        private WeakReference<KKViewPager> hostWeakReference;

        public MyHandler(KKViewPager host) {
            hostWeakReference = new WeakReference<KKViewPager>(host);
        }

        @Override
        public void handleMessage(Message msg) {
            KKViewPager host = hostWeakReference.get();
            if (host != null) {
                host.scroll();
                host.sendScrollMsg(host.slideInterval);
            }
        }
    }

    private SimpleOnPageChangeListener mOnPageChangeListener = new SimpleOnPageChangeListener() {
        @Override
        public void onPageScrollStateChanged(int state) {
            super.onPageScrollStateChanged(state);
            if (state == SCROLL_STATE_IDLE) {
                if (getAdapter() != null && getAdapter() instanceof InfinitePagerAdapter) {
                    int cur = getCurrentItem();
                    // 获取最后一个真实的item
                    int lastReal = getAdapter().getCount() - 2;
                    if (cur == 0) {
                        setCurrentItem(lastReal, false);
                    } else if (cur > lastReal) {
                        setCurrentItem(1, false);
                    }
                }
            }
        }
    };

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = MotionEventCompat.getActionMasked(ev);
        if (stopWhenTouch) {
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    if (isAutoScroll) {
                        isStopedWhenTouch = true;
                        stopAutoScroll();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_OUTSIDE:
                    if (isStopedWhenTouch) {
                        startAutoScroll();
                    }
                    break;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeOnPageChangeListener(mOnPageChangeListener);

    }

    private void init() {
        mHandler = new MyHandler(this);
        setScroller();

        slideInterval = DEFAULT_SLIDE_INTERVAL;
        direction = DIRECTION_RIGHT;
        stopWhenTouch = true;
        cycle = false;
        slideDuration = DEFAULT_SLIDE_DURATION;

    }

   /* @Override
    public void setAdapter(PagerAdapter adapter) {
        super.setAdapter(adapter);
        if (adapter != null && adapter instanceof InfinitePagerAdapter && adapter.getCount() > 1) {
            // 设置ViewPager初始位置在List的中间位置
            int midPos = ((adapter.getCount() - 2) / 2) - (adapter.getCount() - 2) / 2 % (
                    (InfinitePagerAdapter) adapter).getItemCount() + 1;
            setCurrentItem(midPos);
        }
    }*/

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        addOnPageChangeListener(mOnPageChangeListener);
    }

    /**
     * ViewPager自动滚动方法
     */
    private void scroll() {
        PagerAdapter adapter = getAdapter();
        int currentItem = getCurrentItem();
        int totalCount;
        if (adapter == null || (totalCount = adapter.getCount()) <= 1) {
            // 如果adapter为空或者item数量小于等于1则不做任何操作
            return;
        }
        int nextItem = (direction == DIRECTION_RIGHT) ? ++currentItem : --currentItem;
        if (!(getAdapter() instanceof InfinitePagerAdapter) && cycle) {
            // 如果是普通adapter并且是循环滚动则在第一个和最后一个特殊处理
            if (nextItem < 0) {
                setCurrentItem(totalCount - 1, true);
            } else if (nextItem == totalCount) {
                setCurrentItem(0, true);
            } else {
                setCurrentItem(nextItem, true);
            }
        } else {
            setCurrentItem(nextItem, true);
        }
    }

    /**
     * 设置自定义scroller控制ViewPager速度
     */
    private void setScroller() {
        try {
            Field scrollerField = ViewPager.class.getDeclaredField("mScroller");
            scrollerField.setAccessible(true);
            Field interpolatorField = ViewPager.class.getDeclaredField("sInterpolator");
            interpolatorField.setAccessible(true);
            mScroller = new CustomDurationScroller(getContext(), (Interpolator) interpolatorField
                    .get(null));
            mScroller.setDuration(slideDuration);
            scrollerField.set(this, mScroller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 开始自动滚动
     */
    public void startAutoScroll() {
        if (getAdapter().getCount() <= 1) {
            return;
        }
        isAutoScroll = true;
        sendScrollMsg(slideInterval);
    }

    /**
     * 开启自动滚动
     *
     * @param delayTime 滚动间隔时间，会覆盖之前设置的间隔时间。
     */
    public void startAutoScroll(int delayTime) {
        isAutoScroll = true;
        slideInterval = delayTime;
        setScroller();
        sendScrollMsg(delayTime);
    }

    /**
     * 停止自动滚动
     */
    public void stopAutoScroll() {
        isAutoScroll = false;
        mHandler.removeMessages(MSG_SCROLL);
    }

    /**
     * 发送滚动消息
     */
    private void sendScrollMsg(long delayTime) {
        mHandler.removeMessages(MSG_SCROLL);
        mHandler.sendEmptyMessageDelayed(MSG_SCROLL, delayTime);
    }


    public int getSlideInterval() {
        return slideInterval;
    }

    /**
     * 设置滑动间隔时间
     *
     * @param slideInterval
     */
    public void setSlideInterval(int slideInterval) {
        this.slideInterval = slideInterval;
        setScroller();
    }

    public int getDirection() {
        return direction;
    }

    /**
     * 设置滑动方向
     *
     * @param direction
     */
    public void setDirection(int direction) {
        this.direction = direction;
    }

    public boolean isStopWhenTouch() {
        return stopWhenTouch;
    }

    /**
     * 触摸时停止滑动
     *
     * @param stopWhenTouch
     */
    public void setStopWhenTouch(boolean stopWhenTouch) {
        this.stopWhenTouch = stopWhenTouch;
    }

    public boolean isCycle() {
        return cycle;
    }

    /**
     * 设置是否循环滚动（只对普通adapter设置）
     *
     * @param cycle
     */
    public void setCycle(boolean cycle) {
        this.cycle = cycle;
    }

    /**
     * 设置滑动时间
     *
     * @param slideDuration
     */
    public void setSlideDuration(int slideDuration) {
        this.slideDuration = slideDuration;
    }

    public class CustomDurationScroller extends Scroller {

        private int mDuration;

        public CustomDurationScroller(Context context) {
            super(context);
        }

        public CustomDurationScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            startScroll(startX, startY, dx, dy, mDuration);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            super.startScroll(startX, startY, dx, dy, mDuration);
        }

        /**
         * 获取滚动间隔时间
         *
         * @return
         */
        public int getMduration() {
            return mDuration;
        }

        /**
         * 设置滑动时间间隔
         *
         * @param duration
         */
        public void setDuration(int duration) {
            mDuration = duration;
        }
    }
}
