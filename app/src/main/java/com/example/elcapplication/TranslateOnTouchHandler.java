package com.example.elcapplication;

import android.view.MotionEvent;
import android.view.View;

public class TranslateOnTouchHandler {
    private float rawX;
    private float rawY;

    public boolean translateOnTouch(View view, MotionEvent event) {
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
                //移动的时候原控件的坐标
                int l = view.getLeft();
                int t = view.getTop();
                //新控件的坐标
                l += dX;
                t += dY;
                int r = l + view.getWidth();
                int b = t + view.getHeight();
                view.layout(l, t, r, b);
                //更新坐标
                rawX = newX;
                rawY = newY;
                break;
        }

        return true;
    }
}
