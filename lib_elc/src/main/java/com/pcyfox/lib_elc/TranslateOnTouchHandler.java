package com.pcyfox.lib_elc;

import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.pcyfox.lib_elc.uitls.Utils;

public class TranslateOnTouchHandler {
    private float rawX;
    private float rawY;
    private int margin = 0;
    private Rect startRect;
    private static final String TAG = "TranslateOnTouchHandler";
    public boolean translateOnTouch(View view, MotionEvent event) {
        if (view == null || view.getParent() == null) {
            return false;
        }
        int w = view.getWidth();
        int h = view.getHeight();
        ViewGroup parent = ((ViewGroup) view.getParent());
        int pw = parent.getWidth();
        int ph = parent.getHeight();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startRect = new Rect(view.getLeft(), view.getTop(), view.getRight(), view.getBottom());
                rawX = event.getRawX();
                rawY = event.getRawY();
                onTranslateStart(view, rawX, rawY);
                break;
            case MotionEvent.ACTION_MOVE:

                float newX = event.getRawX();
                float newY = event.getRawY();
                float dX = newX - rawX;
                float dY = newY - rawY;
                onTranslate(view, dX, dY);

                int l = view.getLeft();
                int t = view.getTop();
                //新控件的坐标
                l += dX;
                t += dY;

                int r = l + view.getWidth();
                int b = t + view.getHeight();

                if (l + w <= pw - margin && b <= ph - margin && l > margin && t > margin) {
                    if (Math.abs(t - b) >= view.getHeight() && Math.abs(r - l) >= view.getWidth()) {
                        view.layout(l, t, r, b);
                    }
                }

                //更新坐标
                rawX = newX;
                rawY = newY;
                break;
            case MotionEvent.ACTION_UP:
              //  Log.d(TAG, "translateOnTouch() called with: view = [" + view + "], event = [" + event + "]");
                onTranslateOver(view, startRect);
                break;
        }
        return true;
    }



    public void onTranslateStart(View view, float dx, float dy) {
    }

    public void onTranslate(View view, float dx, float dy) {
    }

    public void onTranslateOver(View view, Rect startRect) {
    }

    public void setMargin(int margin) {
        this.margin = margin;
    }
}
