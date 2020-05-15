package com.pcyfox.lib_elc;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public class TranslateOnTouchHandler {
    private float rawX;
    private float rawY;

    public boolean translateOnTouch(View view, MotionEvent event) {
        if (view == null || view.getParent() == null) {
            return false;
        }
        int w = view.getWidth();
        int h = view.getHeight();

        int pw = ((ViewGroup) view.getParent()).getWidth();
        int ph = ((ViewGroup) view.getParent()).getHeight();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //按下的
                rawX = event.getRawX();
                rawY = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                //移动的事件
                float newX = event.getRawX();
                float newY = event.getRawY();
                float dX = newX - rawX;
                float dY = newY - rawY;
                onTranslate(view, dX, dY);
                //移动的时候原控件的坐标
                int l = view.getLeft();
                int t = view.getTop();
                //新控件的坐标
                l += dX;
                t += dY;

                int r = l + view.getWidth();
                int b = t + view.getHeight();

                if (l + w <= pw && b <= ph && l > 0 && t > 0) {
                    if (Math.abs(t - b) >= view.getHeight() && Math.abs(r - l) >= view.getWidth()) {
                        view.layout(l, t, r, b);
                    }
                }

                //更新坐标
                rawX = newX;
                rawY = newY;
                break;
            case MotionEvent.ACTION_UP:
                onTranslateOver();
                break;
        }
        return true;
    }

    public void onTranslate(View view, float dx, float dy) {
    }

    public void onTranslateOver() {
    }
}
