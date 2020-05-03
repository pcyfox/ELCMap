package com.example.elcapplication.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.elcapplication.markview.DrawMarkView;
import com.example.elcapplication.markview.MarkLine;

import java.util.ArrayList;
import java.util.List;

/**
 * 电路连线
 */
public class ElcLinkView extends RelativeLayout implements DrawMarkView.DragEventInterceptor {

    private static final String TAG = "ElcLinkView";
    private DrawMarkView markView;
    private List<ElcViewGroup> elcViewGroups;
    private Anchor headAnchor = null;
    private Anchor nextAnchor = null;
    //private ElcViewGroup currentElcViewGroup;

    public ElcLinkView(Context context) {
        super(context);
    }

    public ElcLinkView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public ElcLinkView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    {
        markView = new DrawMarkView(getContext());
        markView.setDragEventInterceptor(this);
        setClickable(true);
        setClickable(true);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        addView(markView);
        markView.getLayoutParams().width = LayoutParams.MATCH_PARENT;
        markView.getLayoutParams().height = LayoutParams.MATCH_PARENT;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        int count = getChildCount();
        if (elcViewGroups == null) {
            elcViewGroups = new ArrayList<>(count - 1);
        }

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child instanceof ElcViewGroup) {
                elcViewGroups.add((ElcViewGroup) child);
            }
        }

        Log.d(TAG, "onAttachedToWindow() called anchors:" + elcViewGroups.size());
    }

    private boolean isIntercept = true;


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return isIntercept;
    }

    private MarkLine touchedLine;

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchedLine = markView.findLine(event.getRawX(), event.getRawY());
                if (touchedLine != null) {
                    isIntercept = true;
                    headAnchor = findAnchor(touchedLine.getStartX(), touchedLine.getStartY());
                    markView.dispatchTouchEvent(event);
                    return true;
                } else {
                    headAnchor = findAnchor(event.getRawX(), event.getRawY());
                }

                if (headAnchor != null) {
                    Log.d(TAG, "onTouchEvent() called with: headAnchor = -----------------[" + headAnchor + "]");
                    event.setLocation(headAnchor.getCentreX(), headAnchor.getCentreY());
                    markView.dispatchTouchEvent(event);
                } else {

                    markView.setCanStartToDraw(false);
                }

                isIntercept = headAnchor != null;
                break;

            case MotionEvent.ACTION_MOVE:
                if (touchedLine != null || headAnchor != null) {
                    markView.dispatchTouchEvent(event);
                }
                break;
            case MotionEvent.ACTION_UP:

                if (headAnchor == null) {
                    markView.setCanStartToDraw(false);
                    isIntercept = false;
                    return true;
                }

                nextAnchor = findAnchor(event.getRawX(), event.getRawY());
                //同一个元件的中的锚点不鞥相互连接
                if (nextAnchor != null && nextAnchor.getParentId() == headAnchor.getParentId()) {
                    nextAnchor = null;
                }

                if (nextAnchor == null) {
                    markView.setCanStartToDraw(false);
                    markView.dispatchTouchEvent(event);
                } else {
                    headAnchor.setNext(nextAnchor);
                    markView.setCanStartToDraw(true);
                    Log.d(TAG, "onTouchEvent() nextAnchor = [" + nextAnchor + "]");
                    event.setLocation(nextAnchor.getCentreX(), nextAnchor.getCentreY());
                    markView.dispatchTouchEvent(event);
                }

                isIntercept = false;
                break;

        }


        return super.dispatchTouchEvent(event);
    }


    private Anchor findAnchor(float x, float y) {
        ElcViewGroup evg = findElcViewGroup(x, y);
        if (evg != null) {
            List<Anchor> anchors = evg.getAnchors();
            for (Anchor anchor : anchors) {
                if (isInDestArea(x, y, anchor, anchor.getTouchRadius())) {
                    return anchor;
                }
            }
        }
        return null;
    }

    private ElcViewGroup findElcViewGroup(float x, float y) {
        for (ElcViewGroup evg : elcViewGroups) {
            if (isInDestArea(x, y, evg, 0)) {
                return evg;
            }
        }
        return null;
    }


    private boolean isInDestArea(float x, float y, View dest, float offset) {
        Rect rect = new Rect();
        dest.getGlobalVisibleRect(rect);
        boolean isFound=(x > rect.left - offset && x < rect.right + offset) && (y > rect.top - offset && y < Math.abs(rect.bottom + offset));
        if(isFound){
            Log.d(TAG, "isInDestArea() called with: x = [" + x + "], y = [" + y + "]");
            Log.d(TAG, "isInDestArea() called with: rect: = " + rect.toString());
        }
        return isFound;
    }

    @Override
    public boolean intercept(float x, float y) {
        return findAnchor(x, y) == null;
    }
}
