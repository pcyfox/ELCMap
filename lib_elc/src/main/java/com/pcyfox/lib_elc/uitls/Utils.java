package com.pcyfox.lib_elc.uitls;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;

public class Utils {
    public static final int ALIGN_TYPE_TOP = 0;
    public static final int ALIGN_TYPE_BOTTOM = 1;
    public static final int ALIGN_TYPE_LEFT = 2;
    public static final int ALIGN_TYPE_RIGHT = 3;
    public static final int ALIGN_TYPE_CENTER_HORIZON = 4;

    private static final String TAG = "Utils";
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static float dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (dpValue * scale + 0.5f);
    }

    public static float dip2px(float dpValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (dpValue * scale + 0.5f);
    }

    /**
     * 检测坐标是否落在 View dest 所在区域内
     *
     * @param x    相对屏幕
     * @param y    相对屏幕
     * @param dest
     * @param slop 溢出的大小
     * @return
     */
    public static boolean isInView(float x, float y, View dest, float slop) {
        Rect rect = new Rect();
        dest.getGlobalVisibleRect(rect);
        return (x > rect.left - slop && x < rect.right + slop) && (y > rect.top - slop && y < rect.bottom + slop);
    }

    public static Point getViewCenterPoint(View view) {
        Rect rect = new Rect();
        view.getGlobalVisibleRect(rect);
        return new Point(rect.left + view.getWidth() / 2, rect.top + view.getHeight() / 2);
    }

    public static RelativeRelationship getPointsRelativeRelationship(Point point, Point destPoint, float slop) {
        Log.d(TAG, "getPointsRelativeRelationship() called with: point = [" + point + "], destPoint = [" + destPoint + "], slop = [" + slop + "]");
        if (Math.abs(point.x - destPoint.x) <= slop && Math.abs(point.y - destPoint.y) <= slop) {
            return RelativeRelationship.CENTER;
        }


        if (point.x < destPoint.x) {
            if (point.y < destPoint.y) {
                return RelativeRelationship.LT;
            } else {
                return RelativeRelationship.LB;
            }
        } else {
            if (point.y < destPoint.y) {
                return RelativeRelationship.RT;
            } else {
                return RelativeRelationship.RB;
            }
        }
    }


    public static void align(View view, View destView, int type) {
        int h = view.getHeight();
        int w = view.getWidth();
        switch (type) {
            case ALIGN_TYPE_TOP:
                view.layout(view.getLeft(), destView.getTop(), view.getRight(), destView.getTop() + h);
                break;
            case ALIGN_TYPE_BOTTOM:
                view.layout(view.getLeft(), destView.getBottom() - h, view.getRight(), destView.getBottom());
                break;
            case ALIGN_TYPE_LEFT:
                view.layout(destView.getLeft(), view.getBottom() - h, destView.getLeft() + w, view.getBottom());
                break;
            case ALIGN_TYPE_RIGHT:
                view.layout(destView.getRight() - w, view.getBottom() - h, destView.getRight(), view.getBottom());
                break;
            case ALIGN_TYPE_CENTER_HORIZON:
                int top = destView.getHeight() / 2 + destView.getTop() - h / 2;
                view.layout(view.getLeft(), top, view.getRight(), top + h);
                break;

        }
    }


    public enum RelativeRelationship {
        LT, LB, RT, RB, CENTER
    }
}
