package com.pcyfox.lib_elc.markview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.pcyfox.lib_elc.R;
import com.pcyfox.lib_elc.uitls.Utils;

import java.util.ArrayList;
import java.util.List;


public class DrawLineView extends View {
    private List<Line> lines = new ArrayList<>();
    private Paint dashPaint/*专门用来绘制虚线*/;
    private Path mPath;
    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;
    private float startX, startY;
    private float endX, endY;
    private Line selectedLine;
    private boolean isCanStartToDraw = false;
    private static final String TAG = "DrawLineView";
    private OnDeleteLineListener onDeleteLineListener;
    private Rect rect = new Rect();
    private float strokeWidth = Utils.dip2px(2);
    private Bitmap deleteLineBtn;
    private Rect startPointRect;
    private Rect endPointRect;

    public DrawLineView(Context context) {
        this(context, null);
    }

    public DrawLineView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);//此处第二个参数不能为空，否则java中无法根据id获得实例的引用
    }

    public DrawLineView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    {
        dashPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dashPaint.setColor(Color.RED);
        dashPaint.setStyle(Paint.Style.STROKE);
        dashPaint.setStrokeWidth(strokeWidth);
        dashPaint.setPathEffect(new DashPathEffect(new float[]{25, 5}, 0));
        //路径
        mPath = new Path();
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        getGlobalVisibleRect(rect);
        if (deleteLineBtn == null) {
            deleteLineBtn = Utils.drawableToBitmap(ContextCompat.getDrawable(getContext(), R.drawable.elc_ic_delete_line_btn));
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        float x = event.getRawX() - rect.left;
        float y = event.getRawY() - rect.top;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchDown(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(x, y);
                break;
            case MotionEvent.ACTION_UP:
                touchUp(x, y);
                startX = startY = endY = endX = 0;
                startPointRect = null;
                break;
        }
        invalidate();
        return false;
    }


    public void deleteLineByPoint(float x, float y) {
        List<Line> findLines = findLineByPoint(x, y, 6);
        lines.removeAll(findLines);
        invalidate();
    }

    public List<Line> findLineByPoint(float x, float y, int offset) {
        List<Line> findLines = new ArrayList<>();
        for (Line line : lines) {
            Point head = line.getHeadPoint();
            Point trail = line.getTrailPoint();

            if (Math.abs(head.x - x) < offset && Math.abs(head.y - y) < offset) {
                findLines.add(line);
            }
            if (Math.abs(trail.x - x) < offset && Math.abs(trail.y - y) < offset) {
                findLines.add(line);
            }
        }
        return findLines;

    }


    /**
     * 手指按下时
     *
     * @param x
     * @param y
     */
    private void touchDown(float x, float y) {
        if (selectedLine != null) {
            tryDeleteTouchLine(x, y);
            invalidate();
        }
        clearSelectedLine();
        selectedLine = findTouchedLine(x, y);
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    /**
     * 手指移动时
     *
     * @param x
     * @param y
     */
    private void touchMove(float x, float y) {
        if (selectedLine == null) {
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);
            //两点之间的距离大于等于4时，生成贝塞尔绘制曲线
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                //设置贝塞尔曲线的操作点为起点和终点的一半
                mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
                mX = x;
                mY = y;
            }
        }

    }

    /**
     * 手指抬起时
     */
    private void touchUp(float x, float y) {
        if (selectedLine == null) {
            mPath.lineTo(mX, mY);
            if (Math.abs(startX - x) > 100 || Math.abs(startY - y) > 100) {
                if (isCanStartToDraw) {
                    if (startX > 0 && startY > 0 && endX > 0 && endY > 0) {
                        addLine(createLine());
                    }
                }
            }
            mPath.reset();
        }
    }

    private Line createLine() {
        Line line = new Line(new Point(startX, startY), new Point(endX, endY));
        line.setDeleteBtn(deleteLineBtn);
        //如果起点与终点的的X或Y坐标不是近似在条线上
        if (Math.abs(startX - endX) > 4 || Math.abs(startY - endY) > 4) {
            Point midPoint = new Point();
            if (startPointRect != null) {
                //TODO 当两个控件在同一条线上时 ------------》
                if (isPassRect(startX, startY, endX, startY, startPointRect)) {
                    Log.d(TAG, "createLine() called   isPassRect:" + true);
                    midPoint.set(startX, endY);
                } else {
                    Log.d(TAG, "createLine() called   isPassRect:" + false);
                    midPoint.set(endX, startY);
                }
            }
            line.addMidPoint(midPoint);
        }
        return line;
    }

    private boolean isPassRect(float startX, float startY, float endX, float endY, Rect rect) {
        Log.d(TAG, "isPassRect() called with: startX = [" + startX + "], startY = [" + startY + "], endX = [" + endX + "], endY = [" + endY + "], rect = [" + rect + "]");
        return Utils.isLineIntersectRectangle(startX, startY, endX, endY, rect.left, rect.top, rect.right, rect.bottom);
    }

    private void addLine(Line line) {
        Log.d(TAG, "addLine() called with: line = [" + line + "]");
        //去重
        if (lines.size() == 0) {
            lines.add(line);
        } else {
            Line lastLine = lines.get(lines.size() - 1);
            if (!line.equals(lastLine)) {
                lines.add(line);
            }
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(mPath, dashPaint);
        for (Line line : lines) {
            line.draw(canvas);
        }
    }


    private Boolean deleteLine(Line line) {
        return lines.remove(line);
    }


    public void setStartXY(float startX, float startY) {
        Log.d(TAG, "setStartXY() called with: startX = [" + startX + "], startY = [" + startY + "]");
        this.startX = startX;
        this.startY = startY;
    }

    public void setEndXY(float endX, float endY) {
        Log.d(TAG, "setEndXY() called with: endX = [" + endX + "], endY = [" + endY + "]");
        this.endX = endX;
        this.endY = endY;
    }

    /**
     * 起点所在View的Rect，用于设计避障规则
     *
     * @param rect
     */
    public void setStartPointInRect(Rect rect) {
        startPointRect = rect;
    }

    public void setEndPointInRect(Rect rect) {
        endPointRect = rect;
    }

    public Line getSelectedLine() {
        return selectedLine;
    }


    /**
     * @param x
     * @param y
     */
    private void tryDeleteTouchLine(float x, float y) {
        if (selectedLine.isTouchedDeleteBtn(x, y)) {
            Log.e(TAG, "tryDeleteTouchLine() delete   success        --");
            if (onDeleteLineListener != null) {
                onDeleteLineListener.onDelete(selectedLine);
            }
            if (deleteLine(selectedLine)) {
                selectedLine = null;
                invalidate();
            }
        }
    }


    /**
     * 撤销绘图步骤，移除上一个节点
     */
    public void cancelStep() {

    }


    public void rollback() {
        cancelStep();
    }

    public void clear() {
        if (lines != null) {
            lines.clear();
            postInvalidate();
        }
    }

    /**
     * 查找触摸区域内是否存在连线
     *
     * @param x
     * @param y
     * @return
     */
    public Line findTouchedLine(float x, float y) {
        for (Line line : lines) {
            boolean selected = line.selectedLine(x, y, 12);
            Log.d(TAG, "findTouchedLine() called with: x = [" + x + "], y = [" + y + "]  isInRect:" + selected);
            if (selected) {
                return line;
            }
        }
        return null;
    }

    private void clearSelectedLine() {
        for (Line line : lines) {
            line.setLineIsSelected(false);
        }
    }


    public boolean isCanStartToDraw() {
        return isCanStartToDraw;
    }

    public void setCanStartToDraw(boolean canStartToDraw) {
        mPath.reset();
        invalidate();
        isCanStartToDraw = canStartToDraw;
    }


    public void setOnDeleteLineListener(OnDeleteLineListener onDeleteLineListener) {
        this.onDeleteLineListener = onDeleteLineListener;
    }


    public interface OnDeleteLineListener {
        boolean onDelete(Line line);
    }
}


