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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.elcapplication.R;
import com.example.elcapplication.TranslateOnTouchHandler;
import com.example.elcapplication.uitls.Utils;

import java.util.ArrayList;
import java.util.List;

public class ElcViewGroup extends FrameLayout {
    public static final int STATE_NORMAL = 0;//长按进入删除模式（STATE_DELETE）
    public static final int STATE_LAYOUT = 1;//可拖动、删除以及固定位置
    public static final int STATE_EDITABLE = 2;//仅可删除以及切换至STATE_NORMAL

    private static final String TAG = "ElcViewGroup";
    private TranslateOnTouchHandler translateOnTouchHandler;
    private List<Anchor> anchors = new ArrayList<>();
    private String name;
    private OnTranslateListener onTranslateListener;
    private ImageView deleteBtn, okBtn;
    private int state = STATE_LAYOUT;
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
            public void onTranslate(float dx, float dy) {
                super.onTranslate(dx, dy);
                if (onTranslateListener != null) {
                    onTranslateListener.onTranslate(dx, dy);
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
        okBtn.setPadding(0, padding, padding, 0);
        okBtn.setVisibility(View.GONE);
        okBtn.setImageResource(R.drawable.elc_ic_ok);
        addView(okBtn);


        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, " deleteBtn onClick() called with: v = [" + v + "]");
                switch (state) {
                    case STATE_EDITABLE:
                        ViewGroup parent = (ViewGroup) getParent();
                        parent.removeView(ElcViewGroup.this);
                        break;
                    case STATE_LAYOUT:
                        setState(STATE_NORMAL);
                        break;
                }

            }
        });


        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, " deleteBtn onClick() called with: v = [" + v + "]");
                setState(STATE_NORMAL);
            }
        });

        setState(state);

    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
        switch (state) {
            case STATE_EDITABLE:
                deleteBtn.setVisibility(View.VISIBLE);
                okBtn.setVisibility(View.VISIBLE);
                setBackgroundResource(R.drawable.elc_shape_evg_bg);
                break;
            case STATE_NORMAL:
                setBackgroundColor(Color.TRANSPARENT);
                deleteBtn.setVisibility(View.GONE);
                okBtn.setVisibility(View.GONE);
                break;
            case STATE_LAYOUT:
                setBackgroundResource(R.drawable.elc_shape_evg_bg);
                deleteBtn.setVisibility(View.VISIBLE);
                okBtn.setVisibility(View.VISIBLE);
                break;

        }
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
        Log.d(TAG, "onTouchEvent() called with: event = [" + event + "]");
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startDownTime = event.getEventTime();
                break;
            case MotionEvent.ACTION_MOVE:
                break;

            case MotionEvent.ACTION_UP:
                if (startDownTime < 0) {
                    isLongClick = false;
                } else if (event.getEventTime() - startDownTime > 1200) {
                    isLongClick = true;
                    Log.d(TAG, "onTouchEvent() called with:---------> isLongClick = [" + isLongClick + "]");
                }
                checkState(event);
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
