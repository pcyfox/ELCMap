package com.pcyfox.lib_elc.uitls;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;

public class Utils {
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static float dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (dpValue * scale + 0.5f);
    }

    /**
     * 检测坐标是否落在 View dest 所在区域内
     *
     * @param x    相对屏幕
     * @param y    相对屏幕
     * @param dest
     * @param slop  溢出的大小
     * @return
     */
    public static boolean isInView(float x, float y, View dest, float slop) {
        Rect rect = new Rect();
        dest.getGlobalVisibleRect(rect);
        return (x > rect.left - slop && x < rect.right + slop) && (y > rect.top - slop && y < rect.bottom + slop);
    }
}
