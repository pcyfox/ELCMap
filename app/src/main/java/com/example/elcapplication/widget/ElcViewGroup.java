package com.example.elcapplication.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.elcapplication.TranslateOnTouchHandler;

import java.util.ArrayList;
import java.util.List;

public class ElcViewGroup extends FrameLayout {
    private static final String TAG = "ElcViewGroup";
    private TranslateOnTouchHandler translateOnTouchHandler;
    private List<Anchor> anchors = new ArrayList<>();
    private String name;
    private OnTranslateListener onTranslateListener;

    public List<Anchor> getAnchors() {
        return anchors;
    }

    public ElcViewGroup(@NonNull Context context) {
        super(context);
    }

    public ElcViewGroup(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ElcViewGroup(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    {

        translateOnTouchHandler = new TranslateOnTouchHandler() {
            @Override
            public void onTranslate(float dx, float dy) {
                super.onTranslate(dx, dy);
                if (onTranslateListener != null) {
                    onTranslateListener.onTranslate(dx, dy);
                }
            }
        };
    }

    public void setOnTranslateListener(OnTranslateListener onTranslateListener) {
        this.onTranslateListener = onTranslateListener;
    }

    public void setAnchors(List<Anchor> anchors) {
        this.anchors = anchors;
    }

    protected void addAnchor(Anchor... anchors) {
        for (Anchor anchor : anchors) {
            anchor.setParentId(hashCode());
            anchor.setParentName(name);
            this.anchors.add(anchor);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return translateOnTouchHandler.translateOnTouch(this, event);
    }

    private String string = "ElcViewGroup{" +
            ", name='" + name + '\'' +
            "anchors=" + anchors +
            '}';

    @Override
    public String toString() {
        return string;
    }

    public interface OnTranslateListener {
        void onTranslate(float dx, float dy);
    }
}
