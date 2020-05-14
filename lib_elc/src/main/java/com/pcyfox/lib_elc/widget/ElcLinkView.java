package com.pcyfox.lib_elc.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.pcyfox.lib_elc.markview.DrawMarkView;
import com.pcyfox.lib_elc.markview.MarkLine;
import com.pcyfox.lib_elc.uitls.Utils;


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
    private ElcViewGroup currentElcViewGroup;

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
                Log.e(TAG, "onDelete() called with: line = [" + line + "]");
                //有线段被删除，清除对应的锚点
                for (ElcViewGroup group : elcViewGroups) {
                    for (Anchor anchor : group.getAnchors()) {
                        if (Utils.isInDestArea(line.getStartX(), line.getStartY(), anchor, 2)) {
                            Anchor deleteAnchor = null;
                            for (Anchor nAnchor : anchor.getNextAnchors()) {
                                if (Utils.isInDestArea(line.getEndX(), line.getEndY(), nAnchor, 2)) {
                                    deleteAnchor = nAnchor;
                                }
                            }
                            if (deleteAnchor != null) {
                                anchor.getNextAnchors().remove(deleteAnchor);
                            }
                        } else if (Utils.isInDestArea(line.getEndX(), line.getEndY(), anchor, 2)) {
                            Anchor deleteAnchor = null;
                            for (Anchor nAnchor : anchor.getNextAnchors()) {
                                if (Utils.isInDestArea(line.getStartX(), line.getStartY(), nAnchor, 2)) {
                                    deleteAnchor = nAnchor;
                                }
                            }
                            if (deleteAnchor != null) {
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
                        markView.deleteLineByPoint(anchor.getCentreX(), anchor.getCentreY());
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

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                headAnchor = findAnchor(event.getRawX(), event.getRawY());
                if (headAnchor != null) {
                    markView.setCanStartToDraw(true);
                    event.setLocation(headAnchor.getCentreX(), headAnchor.getCentreY());
                    markView.dispatchTouchEvent(event);
                    headAnchor.dispatchTouchEvent(event);
                } else {
                    markView.setCanStartToDraw(false);
                    if (isHasSelectedLine) {
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
                    markView.setCanStartToDraw(false);
                } else {
                    Anchor nextAnchor = findAnchor(event.getRawX(), event.getRawY());
                    headAnchor.dispatchTouchEvent(event);

                    if (nextAnchor != null && !checkAnchor(headAnchor, nextAnchor)) {
                        nextAnchor = null;
                    }

                    if (nextAnchor == null) {
                        markView.setCanStartToDraw(false);
                        markView.dispatchTouchEvent(event);
                    } else {
                        headAnchor.addNextAnchor(nextAnchor);
                        markView.setCanStartToDraw(true);
                        event.setLocation(nextAnchor.getCentreX(), nextAnchor.getCentreY());
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


    private Anchor findAnchor(float x, float y) {
        currentElcViewGroup = findElcViewGroup(x, y);
        if (currentElcViewGroup != null) {
            if (currentElcViewGroup.getState() != ElcViewGroup.STATE_NORMAL) {
                return null;
            }
            List<Anchor> anchors = currentElcViewGroup.getAnchors();
            for (Anchor anchor : anchors) {
                if (anchor.getTouchRadius() == 0) {
                    anchor.invalidate();
                }
                if (Utils.isInDestArea(x, y, anchor, anchor.getTouchRadius() * 3)) {
                    return anchor;
                }
            }
        }
        return null;
    }

    private ElcViewGroup findElcViewGroup(float x, float y) {
        for (ElcViewGroup evg : elcViewGroups) {
            if (Utils.isInDestArea(x, y, evg, 30)) {
                return evg;
            }
        }
        return null;
    }


    @Override
    public boolean intercept(float x, float y) {
        return findAnchor(x, y) == null;
    }
}
