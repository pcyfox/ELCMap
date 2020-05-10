package com.example.elcapplication.uitls;

import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;

public class Utils {
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static boolean isInDestArea(float x, float y, View dest, float offset) {
        Rect rect = new Rect();
        dest.getGlobalVisibleRect(rect);
        boolean isFound = (x > rect.left - offset && x < rect.right + offset) && (y > rect.top - offset && y < Math.abs(rect.bottom + offset));
        if (isFound) {
            Log.d("Utils", "isInDestArea() called with: rect: = " + rect.toString());
        }
        return isFound;
    }
}
