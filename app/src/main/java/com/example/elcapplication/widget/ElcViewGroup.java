package com.example.elcapplication.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.elcapplication.R;
import com.example.elcapplication.TranslateOnTouchHandler;
import com.example.elcapplication.elcview.Electric;
import com.example.elcapplication.uitls.Utils;

import java.util.ArrayList;
import java.util.List;

public abstract class ElcViewGroup extends FrameLayout {
    public static final int STATE_NORMAL = 0;//正常模式，无编按可正常连线
    public static final int STATE_LAYOUT = 1;//可拖动、删除、可拖动
    public static final int STATE_EDITABLE = 2;//仅可删除以及切换至STATE_NORMAL 不可拖动
    public static final int STATE_BASE = 3;//什么也不能干
    private OnDeleteListener onDeleteListener;
    private static final String TAG = "ElcViewGroup";
    private TranslateOnTouchHandler translateOnTouchHandler;
    private List<Anchor> anchors = new ArrayList<>();
    private String name;
    private OnTranslateListener onTranslateListener;
    private ImageView deleteBtn, okBtn;
    private int state = STATE_BASE;
    private Long startDownTime = 0l;
    private boolean isLongClick = false;

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
            public void onTranslate(View view, float dx, float dy) {
                if (onTranslateListener != null) {
                    onTranslateListener.onTranslate(dx, dy);
                }
            }

            @Override
            public void onTranslateOver() {
                super.onTranslateOver();
                ViewGroup.LayoutParams groupLp = ElcViewGroup.this.getLayoutParams();
                if (groupLp instanceof MarginLayoutParams) {
                    MarginLayoutParams mlp = (MarginLayoutParams) groupLp;
                    mlp.leftMargin = getLeft();
                    mlp.topMargin = getTop();
                    setLayoutParams(mlp);
                    ((View) getParent()).invalidate();
                    invalidate();
                }
            }
        };

        deleteBtn = new ImageView(getContext());
        deleteBtn.setImageResource(R.drawable.elc_ic_delete_x);

        FrameLayout.LayoutParams dlp = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        int padding = Utils.dip2px(getContext(), 4f);
        dlp.gravity = Gravity.END | Gravity.TOP;
        deleteBtn.setLayoutParams(dlp);
        deleteBtn.setPadding(0, padding, padding, 0);
        deleteBtn.setVisibility(View.GONE);
        addView(deleteBtn);

        okBtn = new ImageView(getContext());
        FrameLayout.LayoutParams olp = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        olp.gravity = Gravity.START | Gravity.TOP;
        okBtn.setLayoutParams(olp);
        okBtn.setPadding(padding, padding, 0, 0);
        okBtn.setVisibility(View.GONE);
        okBtn.setImageResource(R.drawable.elc_ic_ok);
        addView(okBtn);

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onDeleteListener != null) {
                    onDeleteListener.onDelete(ElcViewGroup.this);
                }
                ViewGroup parent = (ViewGroup) getParent();
                parent.removeView(ElcViewGroup.this);
            }
        });
        invalidate();
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setState(STATE_NORMAL);
            }
        });
        setState(state);
    }

    private void setShowActionBtn(boolean isShow) {
        if (isShow) {
            deleteBtn.setVisibility(View.VISIBLE);
            okBtn.setVisibility(View.VISIBLE);
        } else {
            deleteBtn.setVisibility(View.GONE);
            okBtn.setVisibility(View.GONE);
        }
    }


    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
        switch (state) {
            case STATE_EDITABLE:
                setShowActionBtn(true);
                setBackgroundResource(R.drawable.elc_shape_evg_bg);
                break;
            case STATE_NORMAL:
                setBackgroundColor(Color.TRANSPARENT);
                setShowActionBtn(false);
                break;
            case STATE_LAYOUT:
                setBackgroundResource(R.drawable.elc_shape_evg_bg);
                setShowActionBtn(true);
                break;

            case STATE_BASE:
                setBackgroundResource(R.drawable.elc_shape_evg_bg);
                setShowActionBtn(false);
                break;
        }
        if (getParent() != null) {
            ((View) getParent()).invalidate();
        }
        invalidate();
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

    public OnDeleteListener getOnDeleteListener() {
        return onDeleteListener;
    }

    public void setOnDeleteListener(OnDeleteListener onDeleteListener) {
        this.onDeleteListener = onDeleteListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "onTouchEvent() called with: event = [" + event + "]");
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isLongClick = false;
                startDownTime = event.getEventTime();
                break;

            case MotionEvent.ACTION_MOVE:
                ansysAction(event);
                break;

            case MotionEvent.ACTION_UP:
                ansysAction(event);
                break;
        }

        if (state == STATE_LAYOUT) {
            if (Utils.isInDestArea(event.getRawX(), event.getRawY(), deleteBtn, 8)) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    deleteBtn.performClick();
                }
                return true;
            }

            if (Utils.isInDestArea(event.getRawX(), event.getRawY(), okBtn, 8)) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    okBtn.performClick();
                }
                return true;
            }
            return translateOnTouchHandler.translateOnTouch(this, event);
        }


        return true;
    }

    private void ansysAction(MotionEvent event) {
        if (startDownTime < 0) {
            isLongClick = false;
        } else if (event.getEventTime() - startDownTime > 1200) {
            isLongClick = true;
        }
        checkState(event);
    }

    private Boolean checkState(MotionEvent event) {
        switch (state) {
            case STATE_EDITABLE:
                if (Utils.isInDestArea(event.getRawX(), event.getRawY(), deleteBtn, 5)) {
                    deleteBtn.performClick();
                    return true;
                }
                break;
            case STATE_NORMAL:
                if (isLongClick) {
                    performLongClick(event);
                }
                break;
        }
        return true;
    }


    private void performLongClick(MotionEvent event) {
        setState(STATE_EDITABLE);
    }


    @Override
    public String toString() {
        return "ElcViewGroup{" +
                ", name='" + name + '\'' +
                "anchors=" + anchors +
                '}';
    }

    public interface OnTranslateListener {
        void onTranslate(float dx, float dy);
    }

    public interface OnDeleteListener {
        void onDelete(ElcViewGroup elcViewGroup);
    }

    public abstract ElcViewGroup create();

}
