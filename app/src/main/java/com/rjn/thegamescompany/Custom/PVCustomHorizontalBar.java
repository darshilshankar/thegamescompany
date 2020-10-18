package com.rjn.thegamescompany.Custom;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import androidx.annotation.RequiresApi;
import android.util.AttributeSet;

import com.rjn.thegamescompany.R;

public class PVCustomHorizontalBar extends androidx.appcompat.widget.AppCompatSeekBar {

    public PVCustomHorizontalBar(Context context) {
        super(context);
        init();
    }

    public PVCustomHorizontalBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PVCustomHorizontalBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        this.setMax(100);
        this.setThumbOffset(dip2px(getContext(), 20));
        this.setBackgroundResource(R.drawable.sbg);
        int padding = dip2px(getContext(), (float) 20);
        this.setPadding(padding, padding, padding, padding);
        this.setProgressDrawable(getResources().getDrawable(R.drawable.pvbar_define_style));
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        AnimationDrawable drawable = (AnimationDrawable) this.getThumb();
        drawable.start();
    }

    public int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
