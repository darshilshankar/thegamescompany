package com.rjn.thegamescompany.Custom;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;

public class PVCornerView {

    private Activity activity;
    private View view;
    private float round = 1;

    public PVCornerView(Activity activity, View view, float round) {
        this.activity = activity;
        this.view = view;
        this.round = round;

        /*int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        canvas.drawARGB(0, 0, 0, 0);

        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.parseColor("#000000"));

        final Rect rect = new Rect(0, 0, width, height);
        final RectF rectF = new RectF(rect);

        canvas.drawRoundRect(rectF, round, round, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);*/

        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setColor(Color.LTGRAY);
        //shape.setStroke(3, Color.GRAY);
        shape.setCornerRadius(round);
        view.setBackgroundDrawable(shape);
    }
}
