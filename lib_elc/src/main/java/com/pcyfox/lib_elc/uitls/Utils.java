package com.pcyfox.lib_elc.uitls;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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

    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bd = (BitmapDrawable) drawable;
            return bd.getBitmap();
        }
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }


    public static boolean isLineIntersectRectangle(double linePointX1,
                                                   double linePointY1,
                                                   double linePointX2,
                                                   double linePointY2,
                                                   double rectangleLeftTopX,
                                                   double rectangleLeftTopY,
                                                   double rectangleRightBottomX,
                                                   double rectangleRightBottomY) {
        double lineHeight = linePointY1 - linePointY2;
        double lineWidth = linePointX2 - linePointX1;
        double t1 = lineHeight * rectangleLeftTopX + lineWidth * rectangleLeftTopY;
        double t2 = lineHeight * rectangleRightBottomX + lineWidth * rectangleRightBottomY;
        double t3 = lineHeight * rectangleLeftTopX + lineWidth * rectangleRightBottomY;
        double t4 = lineHeight * rectangleRightBottomX + lineWidth * rectangleLeftTopY;
        double c = linePointX1 * linePointY2 - linePointX2 * linePointY1;
        if ((t1 + c >= 0 && t2 + c <= 0)
                || (t1 + c <= 0 && t2 + c >= 0)
                || (t3 + c >= 0 && t4 + c <= 0)
                || (t3 + c <= 0 && t4 + c >= 0)) {
            if (rectangleLeftTopX > rectangleRightBottomX) {
                double temp = rectangleLeftTopX;
                rectangleLeftTopX = rectangleRightBottomX;
                rectangleRightBottomX = temp;
            }

            if (rectangleLeftTopY < rectangleRightBottomY) {
                double temp1 = rectangleLeftTopY;
                rectangleLeftTopY = rectangleRightBottomY;
                rectangleRightBottomY = temp1;
            }

            if ((linePointX1 < rectangleLeftTopX && linePointX2 < rectangleLeftTopX)
                    || (linePointX1 > rectangleRightBottomX && linePointX2 > rectangleRightBottomX)
                    || (linePointY1 > rectangleLeftTopY && linePointY2 > rectangleLeftTopY)
                    || (linePointY1 < rectangleRightBottomY && linePointY2 < rectangleRightBottomY)) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    //直线
    public static boolean isStraightLineIntersectRectangle(Point pointStart,
                                                          Point pointEnd,
                                                          Rect rect) {
        return (pointStart.x > rect.left && pointStart.x < rect.right) && (pointStart.y > rect.bottom && pointStart.y < rect.top) &&
                (pointEnd.x > rect.left && pointEnd.x < rect.right) && (pointEnd.y > rect.bottom && pointEnd.y < rect.top);
    }


    public enum RelativeRelationship {
        LT, LB, RT, RB, CENTER
    }

}
