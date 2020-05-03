package com.example.elcapplication.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

public class Anchor extends FrameLayout {
    private static final String TAG = "Anchor";
    private float centreX;
    private float centreY;
    private int touchRadius;
    private String name;
    private Anchor next;
    private int parentId = 0;
    private String parentName;
    private View realAnchorView;

    public Anchor(Context context) {
        super(context);
    }

    public Anchor(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public Anchor(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    {
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Log.d(TAG, "onGlobalLayout() called");
            }
        });
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        init();
    }


    private void init() {
        if (realAnchorView == null) {
            if (getChildCount() == 0) {
                return;
            }
            realAnchorView = getChildAt(0);
        }
        if (realAnchorView == null) {
            return;
        }
        touchRadius = getWidth() / 2;
        Rect rect = new Rect();
        getGlobalVisibleRect(rect);
        float width = realAnchorView.getWidth();
        float height = realAnchorView.getHeight();
        centreX = rect.left + width / 2;
        centreY = rect.bottom - height / 2;
    }

    public float getCentreX() {
        init();
        return centreX;
    }

    public float getCentreY() {
        init();
        return centreY;
    }


    public int getTouchRadius() {
        return touchRadius;
    }

    public Anchor getNext() {
        return next;
    }

    public void setNext(Anchor next) {
        this.next = next;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    private String string = "Anchor{" +
            "centreX=" + centreX +
            ", centreY=" + centreY +
            ", touchRadius=" + touchRadius +
            ", name='" + name + '\'' +
            ", next=" + next +
            ", parentId=" + parentId +
            ", parentName='" + parentName + '\'' +
            '}';

    @Override
    public String toString() {
        return string;
    }
}
