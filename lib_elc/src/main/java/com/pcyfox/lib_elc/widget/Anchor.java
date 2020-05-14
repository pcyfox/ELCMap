package com.pcyfox.lib_elc.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;

import androidx.annotation.Nullable;


import com.pcyfox.lib_elc.R;

import java.util.ArrayList;
import java.util.List;

public class Anchor extends androidx.appcompat.widget.AppCompatImageView {
    private static final String TAG = "Anchor";
    private float centreX;
    private float centreY;
    private int touchRadius;
    private String name;
    private List<Anchor> nextAnchors;
    private int parentId = 0;
    private String parentName;

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
        nextAnchors = new ArrayList<>();
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                init();;
            }
        });
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        init();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        postDelayed(new Runnable() {
            @Override
            public void run() {
                init();
            }
        }, 60);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                setImageResource(R.drawable.elc_shape_red_point);
                break;

            case MotionEvent.ACTION_UP:
                setImageResource(R.drawable.elc_shape_black_point);
                break;
        }
        return false;
    }

    private void init() {
        touchRadius = getWidth() / 2;
        Rect rect = new Rect();
        getGlobalVisibleRect(rect);
        float width = getWidth();
        float height = getHeight();
        centreX = rect.left + width / 2;
        centreY = rect.bottom - height / 2;
    }

    public float getCentreX() {
        return centreX;
    }

    public float getCentreY() {
        return centreY;
    }

    public int getTouchRadius() {
        return touchRadius;
    }

    public List<Anchor> getNextAnchors() {
        return nextAnchors;
    }

    public void setNextAnchors(List<Anchor> nextAnchors) {
        this.nextAnchors = nextAnchors;
    }

    public void addNextAnchor(Anchor anchor) {
        nextAnchors.add(anchor);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    @Override
    public String toString() {
        return "Anchor{" +
                "centreX=" + centreX +
                ", centreY=" + centreY +
                ", touchRadius=" + touchRadius +
                ", name='" + name + '\'' +
                ", nextAnchors=" + nextAnchors +
                ", parentId=" + parentId +
                ", parentName='" + parentName + '\'' +
                '}';
    }
}
