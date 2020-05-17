package com.pcyfox.lib_elc.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.pcyfox.lib_elc.markview.DrawMarkView;
import com.pcyfox.lib_elc.markview.MarkLine;
import com.pcyfox.lib_elc.uitls.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 电路连线
 */
public class ElcLinkView extends FrameLayout implements DrawMarkView.DragEventInterceptor {
    private static final String TAG = "ElcLinkView";
    private DrawMarkView markView;
    private List<ElcViewGroup> elcViewGroups;
    private Anchor headAnchor = null;
    private ElcViewGroup currentElcViewGroup;
    private Rect rect = new Rect();

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
        markView.setOnDeleteLineListener(new DrawMarkView.OnDeleteLineListener() {
            @Override
            public boolean onDelete(MarkLine line) {
                //有线段被删除，清除对应的锚点
                for (ElcViewGroup group : elcViewGroups) {
                    for (Anchor anchor : group.getAnchors()) {
                        //从线段头部找Anchor
                        if (Utils.isInDestArea(line.getStartX() + rect.left, line.getStartY() + rect.top, anchor, anchor.getTouchRadius())) {
                            Anchor deleteAnchor = null;
                            //检测线段尾部是否在nextAnchors中
                            for (Anchor nAnchor : anchor.getNextAnchors()) {
                                if (Utils.isInDestArea(line.getEndX() + rect.left, line.getEndY() + rect.top, nAnchor, anchor.getTouchRadius())) {
                                    deleteAnchor = nAnchor;
                                }
                            }
                            if (deleteAnchor != null) {
                                Log.e(TAG, "onDelete() called with: headAnchor = [" + anchor + "]");
                                Log.e(TAG, "onDelete() called with: deleteAnchor = [" + deleteAnchor + "]");
                                anchor.getNextAnchors().remove(deleteAnchor);
                            }
                        } else if (Utils.isInDestArea(line.getEndX() + rect.left, line.getEndY() + rect.top, anchor, anchor.getTouchRadius())) {
                            Anchor deleteAnchor = null;
                            for (Anchor nAnchor : anchor.getNextAnchors()) {
                                if (Utils.isInDestArea(line.getStartX() + rect.left, line.getStartY() + rect.top, nAnchor, anchor.getTouchRadius())) {
                                    deleteAnchor = nAnchor;
                                }
                            }
                            if (deleteAnchor != null) {
                                Log.e(TAG, "onDelete() called with: headAnchor = [" + anchor + "]");
                                Log.e(TAG, "onDelete() called with: deleteAnchor = [" + deleteAnchor + "]");
                                anchor.getNextAnchors().remove(deleteAnchor);
                            }
                        }
                    }
                }
                return false;
            }
        });
        setClickable(true);
        setClickable(true);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        getGlobalVisibleRect(rect);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        addView(markView, 0);
        markView.getLayoutParams().width = LayoutParams.MATCH_PARENT;
        markView.getLayoutParams().height = LayoutParams.MATCH_PARENT;
        //  markView.setBackgroundColor(Color.WHITE);
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
            addElcView(child);
        }
    }

    public List<ElcViewGroup> getElcViewGroups() {
        return elcViewGroups;
    }

    private void addElcView(View child) {
        if (child instanceof ElcViewGroup) {
            ElcViewGroup elcViewGroup = (ElcViewGroup) child;
            elcViewGroups.add(elcViewGroup);
            elcViewGroup.setOnDeleteListener(new ElcViewGroup.OnDeleteListener() {
                @Override
                public void onDelete(ElcViewGroup elcViewGroup) {
                    Log.e(TAG, "onDelete() called with: elcViewGroup = [" + elcViewGroup + "]");
                    List<Anchor> anchors = elcViewGroup.getAnchors();
                    for (Anchor anchor : anchors) {
                        markView.deleteLineByPoint(anchor.getCentreX() - rect.left, anchor.getCentreY() - rect.top);
                    }
                }
            });
            invalidate();
            child.invalidate();
        }
    }

    private boolean isIntercept = true;


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return isIntercept;
    }


    @Override
    public void onViewAdded(View child) {
        super.onViewAdded(child);
        addElcView(child);
    }

    @Override
    public void onViewRemoved(View child) {
        if (child instanceof ElcViewGroup) {
            elcViewGroups.remove(child);
        }
        super.onViewRemoved(child);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int action = event.getAction();
        boolean isHasSelectedLine = markView.getSelectedLine() != null;
        float rawX = event.getRawX();
        float rawY = event.getRawY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:

                currentElcViewGroup = findElcViewGroup(rawX, rawY);
                if (currentElcViewGroup != null) {
                    headAnchor = findAnchor(rawX, rawY, currentElcViewGroup);
                } else {
                    headAnchor = null;
                }

                if (headAnchor != null) {
                    float anchorCentreX = headAnchor.getCentreX() - rect.left;
                    float anchorCentreY = headAnchor.getCentreY() - rect.top;
                    event.setLocation(anchorCentreX, anchorCentreY);
                    markView.setCanStartToDraw(true);
                    markView.setStartXY(anchorCentreX, anchorCentreY);
                    markView.dispatchTouchEvent(event);
                    headAnchor.dispatchTouchEvent(event);
                } else {
                    markView.setCanStartToDraw(false);
                    if (isHasSelectedLine) {
                        markView.setCanStartToDraw(true);
                        markView.dispatchTouchEvent(event);
                    }
                    if (currentElcViewGroup != null) {
                        currentElcViewGroup.dispatchTouchEvent(event);
//                        List<Anchor> anchorList = currentElcViewGroup.getAnchors();
//                        for (Anchor anchor : anchorList) {
//                            markView.addDragLine(anchor.getCentreX(), anchor.getCentreY());
//                        }
//                        currentElcViewGroup.setOnTranslateListener(new ElcViewGroup.OnTranslateListener() {
//                            @Override
//                            public void onTranslate(float dx, float dy) {
//                                markView.dragLines(dx, dy);
//                            }
//                        });
                    }

                }
                isIntercept = headAnchor != null;
                break;

            case MotionEvent.ACTION_MOVE:
                isIntercept = headAnchor != null;
                if (headAnchor != null) {
                    headAnchor.dispatchTouchEvent(event);
                    markView.dispatchTouchEvent(event);
                }
                if (isHasSelectedLine) {
                    markView.dispatchTouchEvent(event);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (isHasSelectedLine) {
                    markView.dispatchTouchEvent(event);
                }
                if (currentElcViewGroup != null && headAnchor == null) {
                    isIntercept = false;
                    currentElcViewGroup.dispatchTouchEvent(event);
                }
                if (headAnchor == null) {
                    isIntercept = false;
                } else {
                    Anchor nextAnchor = findAnchor(rawX, rawY, findElcViewGroup(rawX, rawY, currentElcViewGroup));
                    headAnchor.dispatchTouchEvent(event);
                    if (nextAnchor != null && !checkAnchor(headAnchor, nextAnchor)) {
                        nextAnchor = null;
                    }

                    if (nextAnchor == null) {
                        markView.setCanStartToDraw(false);
                    } else {
                        headAnchor.addNextAnchor(nextAnchor);
                        markView.setCanStartToDraw(true);
                        markView.setEndXY(nextAnchor.getCentreX() - rect.left, nextAnchor.getCentreY() - rect.top);
                        markView.dispatchTouchEvent(event);
                    }
                }
                isIntercept = headAnchor != null;
                headAnchor = null;
                break;

        }
        return super.dispatchTouchEvent(event);
    }

    /**
     * 检查连接节点合法性
     *
     * @param headAnchor
     * @param nextAnchor
     * @return
     */
    private boolean checkAnchor(Anchor headAnchor, Anchor nextAnchor) {
        //同一个元件的中的锚点不鞥相互连接
        if (nextAnchor.getParentId() == headAnchor.getParentId()) {
            return false;
        }
        List<Anchor> nextAnchors = headAnchor.getNextAnchors();
        for (Anchor anchor : nextAnchors) {
            if (anchor == nextAnchor) {
                return false;
            }
            //元器件的的一个锚点不能同时连接另一个元器件的多个端点
            if (anchor.getParentId() == nextAnchor.getParentId()) {
                return false;
            }
        }
        return true;
    }


    private Anchor findAnchor(float x, float y, ElcViewGroup form) {
        Log.d(TAG, "findAnchor() called with: x = [" + x + "], y = [" + y + "], form = [" + form + "]");
        if (form != null && form.getState() == ElcViewGroup.STATE_NORMAL) {
            List<Anchor> anchors = form.getAnchors();
            for (Anchor anchor : anchors) {
                if (anchor.getTouchRadius() == 0) {
                    anchor.invalidate();
                }
                if (Utils.isInDestArea(x, y, anchor, anchor.getTouchRadius())) {
                    Log.d(TAG, "findAnchor() called with: anchor = [" + anchor + "]");
                    return anchor;
                }
            }
        }
        return null;
    }

    private Anchor findAnchor(float x, float y) {
        ElcViewGroup elcViewGroup = findElcViewGroup(x, y);
        return findAnchor(x, y, elcViewGroup);
    }


    private ElcViewGroup findElcViewGroup(float x, float y) {
        return findElcViewGroup(x, y, null);
    }

    private ElcViewGroup findElcViewGroup(float x, float y, ElcViewGroup except) {
        ElcViewGroup findView = null;
        for (ElcViewGroup evg : elcViewGroups) {
            if (evg == except) {
                continue;
            }
            if (Utils.isInDestArea(x, y, evg, Utils.dip2px(getContext(), 40f))) {
                findView = evg;
                if (evg.getState() == ElcViewGroup.STATE_LAYOUT) {//有多个View叠加时，优先使用处于STATE_LAYOUT状态的
                    break;
                }
            }
        }
        return findView;
    }


    @Override
    public boolean intercept(float x, float y) {
        return findAnchor(x, y, currentElcViewGroup) == null;
    }
}
