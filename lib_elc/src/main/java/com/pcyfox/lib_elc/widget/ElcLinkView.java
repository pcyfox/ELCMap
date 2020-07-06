package com.pcyfox.lib_elc.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.pcyfox.lib_elc.R;
import com.pcyfox.lib_elc.markview.DrawLineView;
import com.pcyfox.lib_elc.markview.DrawMarkView;
import com.pcyfox.lib_elc.markview.Line;
import com.pcyfox.lib_elc.markview.Point;
import com.pcyfox.lib_elc.uitls.Utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.pcyfox.lib_elc.uitls.Utils.ALIGN_TYPE_CENTER_HORIZON;
import static com.pcyfox.lib_elc.uitls.Utils.ALIGN_TYPE_LEFT;

/**
 * 电路连线
 */
public class ElcLinkView extends FrameLayout implements DrawMarkView.DragEventInterceptor {
    private static final String TAG = "ElcLinkView";
    private DrawLineView markView;
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
        markView = new DrawLineView(getContext());

        markView.setOnDeleteLineListener(new DrawLineView.OnDeleteLineListener() {
            @Override
            public boolean onDelete(Line line) {
                //有线段被删除，清除对应的锚点
                Anchor deleteAnchor = null;
                boolean foundAnchor = false;
                for (ElcViewGroup group : elcViewGroups) {
                    for (Anchor anchor : group.getAnchors()) {
                        //从线段头部找Anchor
                        Point head = line.getHeadPoint();
                        Point end = line.getTrailPoint();
                        if (Utils.isInView(head.x + rect.left, head.y + rect.top, anchor, anchor.getTouchRadius())) {
                            //检测线段尾部是否在nextAnchors中
                            for (Anchor nAnchor : anchor.getNextAnchors()) {
                                if (Utils.isInView(end.x + rect.left, end.y + rect.top, nAnchor, anchor.getTouchRadius())) {
                                    deleteAnchor = nAnchor;
                                }
                            }
                            if (deleteAnchor != null) {
                                foundAnchor = true;
                                boolean ret = anchor.getNextAnchors().remove(deleteAnchor);
                                if (ret) {
                                    Log.e(TAG, "onDelete() from line head Anchor called with: delete headAnchor = [" + anchor + "]" + " deleteAnchor = [" + deleteAnchor + "]");
                                }
                            }
                        }
                        //从线段尾部找Anchor
                        if (Utils.isInView(end.x + rect.left, end.y + rect.top, anchor, anchor.getTouchRadius())) {
                            for (Anchor nAnchor : anchor.getNextAnchors()) {
                                if (Utils.isInView(head.x + rect.left, head.y + rect.top, nAnchor, anchor.getTouchRadius())) {
                                    deleteAnchor = nAnchor;
                                }
                            }
                            if (deleteAnchor != null) {
                                foundAnchor = true;
                                boolean ret = anchor.getNextAnchors().remove(deleteAnchor);
                                if (ret) {
                                    Log.e(TAG, "onDelete() form line trail Anchor called with: delete headAnchor = [" + anchor + "]" + " delete deleteAnchor = [" + deleteAnchor + "]");
                                }
                            }
                        }
                    }

                }
                if (!foundAnchor) {
                    //throw new IllegalStateException("on delete line:" + line + "  but not found any Anchor");
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
            final ElcViewGroup elcViewGroup = (ElcViewGroup) child;

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

            elcViewGroup.setOnTranslateListener(new ElcViewGroup.OnTranslateListener() {
                @Override
                public void onTranslate(View view, float dx, float dy) {
                    boolean ret = checkOverlap(view);
                    if (ret) {
                        elcViewGroup.setBackgroundResource(R.drawable.elc_shape_evg_bg_red);
                    } else {
                        elcViewGroup.setBackgroundResource(R.drawable.elc_shape_evg_bg);
                    }
                }

                @Override
                public void onTranslateOver(ElcViewGroup view, Rect startRect) {
                    elcViewGroup.setBackgroundResource(R.drawable.elc_shape_evg_bg);
                    boolean ret = checkOverlap(view);
                    Log.d(TAG, "checkOverlap() called with: foundOverlappedView = [" + ret + "]");
                    if (ret) {
                        //放回原处
                        view.layout(startRect.left, startRect.top, startRect.right, startRect.bottom);
                        return;
                    }
                    align(view);
                }
            });

            elcViewGroups.add(elcViewGroup);
            invalidate();
            child.invalidate();
        }
    }

    private boolean checkOverlap(View view) {
        //lt、lb、rt、rb、center、lc、rc
        Point[] pointArray = {new Point(), new Point(), new Point(), new Point(), new Point(), new Point(), new Point()};
        Rect rect = new Rect();
        view.getGlobalVisibleRect(rect);
        //目前设计一条线最多由留个点构成
        pointArray[0].set(rect.left, rect.top);
        pointArray[1].set(rect.left, rect.bottom);
        pointArray[2].set(rect.right, rect.top);
        pointArray[3].set(rect.right, rect.bottom);
        pointArray[4].set(rect.left + view.getWidth() / 2, rect.top + view.getHeight() / 2);
        pointArray[5].set(rect.left, rect.top + view.getHeight() / 2);
        pointArray[6].set(rect.right, rect.top + view.getHeight() / 2);

        for (View destView : elcViewGroups) {
            if (destView == view) {
                continue;
            }
            for (Point point : pointArray) {
                if (Utils.isInView(point.x, point.y, destView, Utils.dip2px(36))) {
                    return true;
                }
            }
        }
        return false;
    }

    private void align(View view) {
        View horizontalAlignView = null;
        View verticalAlignView = null;
        for (View destView : elcViewGroups) {
            if (view != destView) {
                //找到水平方向上与View位置接近且处于最高处的View
                if (Math.abs(view.getTop() - destView.getTop()) < view.getHeight() * 0.7) {
                    if (horizontalAlignView == null) {
                        horizontalAlignView = destView;
                    } else {
                        if (destView.getTop() < horizontalAlignView.getTop()) {
                            horizontalAlignView = destView;
                        }
                    }
                }

                if (Math.abs(view.getLeft() - destView.getLeft()) < view.getHeight() * 0.7) {
                    if (verticalAlignView == null) {
                        verticalAlignView = destView;
                    } else {
                        if (destView.getLeft() < verticalAlignView.getLeft()) {
                            verticalAlignView = destView;
                        }
                    }
                }
            }
        }

        if (horizontalAlignView != null) {
            Utils.align(view, horizontalAlignView, ALIGN_TYPE_CENTER_HORIZON);
        }

        if (verticalAlignView != null) {
            Utils.align(view, verticalAlignView, ALIGN_TYPE_LEFT);
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

    public void clearAllElcViewGroup() {
        Iterator<ElcViewGroup> iterator = elcViewGroups.iterator();
        while (iterator.hasNext()) {
            View view = iterator.next();
            iterator.remove();
            removeView(view);
        }
        markView.clear();
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
                    markView.setStartPointInRect(getElcViewGroupInnerRect(currentElcViewGroup));

                    markView.setStartXY(anchorCentreX, anchorCentreY,""+headAnchor.getTag());

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
                    markView.setEndPointInRect(getElcViewGroupInnerRect(currentElcViewGroup));
                    headAnchor.dispatchTouchEvent(event);
                    if (nextAnchor != null && !checkAnchor(headAnchor, nextAnchor)) {
                        nextAnchor = null;
                    }
                    if (nextAnchor == null) {
                        markView.setCanStartToDraw(false);
                    } else {
                        headAnchor.addNextAnchor(nextAnchor);
                        markView.setCanStartToDraw(true);
                        markView.setEndXY(nextAnchor.getCentreX() - rect.left, nextAnchor.getCentreY() - rect.top,""+nextAnchor.getTag());
                        markView.dispatchTouchEvent(event);
                    }
                }
                isIntercept = headAnchor != null;
                headAnchor = null;
                break;

        }
        return super.dispatchTouchEvent(event);
    }

    //从currentElcViewGroup中抠出一块区域，用于判断起点与拐点的连线是否穿过该区域
    private Rect getElcViewGroupInnerRect(ElcViewGroup view) {
        Rect elcViewGroupInnerRect = new Rect();
        view.getGlobalVisibleRect(elcViewGroupInnerRect);

        float startPadding = (rect.left + view.getWidth() * 0.15f);
        float topPadding = (rect.top + view.getHeight() * 0.15f);

        elcViewGroupInnerRect.left -= startPadding;
        elcViewGroupInnerRect.right -= startPadding;
        elcViewGroupInnerRect.top -= topPadding;
        elcViewGroupInnerRect.bottom -= topPadding;
        return elcViewGroupInnerRect;
    }


    /**
     * 检查连接节点合法性
     *
     * @param headAnchor
     * @param nextAnchor
     * @return
     */
    private boolean checkAnchor(Anchor headAnchor, Anchor nextAnchor) {
        Log.d(TAG, "checkAnchor() called with: headAnchor = [" + headAnchor + "], nextAnchor = [" + nextAnchor + "]");
        //同一个元件的中的锚点不鞥相互连接
        if (nextAnchor.getParentId() == headAnchor.getParentId()) {
            Log.e(TAG, "checkAnchor()   同一个元件的中的锚点不鞥相互连接!");
            return false;
        }

        List<Anchor> nextAnchors = headAnchor.getNextAnchors();

        for (Anchor anchor : nextAnchors) {
            if (anchor == nextAnchor) {
                Log.e(TAG, "checkAnchor()   nextAnchor不能再次连接headAnchor!");
                //nextAnchor不能再次连接headAnchor
                return false;
            }
            //元器件的的一个锚点不能同时连接另一个元器件的多个端点
            if (anchor.getParentId() == nextAnchor.getParentId()) {
                Log.e(TAG, "checkAnchor()   元器件的的一个锚点不能同时连接另一个元器件的多个端点!");
                return false;
            }

        }


        nextAnchors = nextAnchor.getNextAnchors();
        for (Anchor anchor : nextAnchors) {
            if (anchor == headAnchor) {
                Log.e(TAG, "checkAnchor() -2  nextAnchor不能再次连接headAnchor!");
                //nextAnchor不能再次连接headAnchor
                return false;
            }
            //元器件的的一个锚点不能同时连接另一个元器件的多个端点
            if (anchor.getParentId() == headAnchor.getParentId()) {
                Log.e(TAG, "checkAnchor() -2  元器件的的一个锚点不能同时连接另一个元器件的多个端点!");
                return false;
            }
        }

        //TODO：还需要阻止一个元器件的的两个Anchor同时去连接一个元器件的同一个Anchor
        return true;
    }


    private Anchor findAnchor(float x, float y, ElcViewGroup form) {
        //Log.d(TAG, "findAnchor() called with: x = [" + x + "], y = [" + y + "], form = [" + form + "]");
        if (form != null && form.getState() == ElcViewGroup.STATE_NORMAL) {
            List<Anchor> anchors = form.getAnchors();
            for (Anchor anchor : anchors) {
                if (anchor.getTouchRadius() == 0) {
                    anchor.invalidate();
                }
                if (Utils.isInView(x, y, anchor, anchor.getTouchRadius())) {
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
            if (Utils.isInView(x, y, evg, Utils.dip2px(getContext(), 40f))) {
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
