package com.example.elcapplication.markview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import java.util.ArrayList;
import java.util.List;

public class DrawMarkView extends View {
    private List<MarkLine> markLines = new ArrayList<>();
    private Paint mLinePaint, textPaint, dashPaint/*专门用来绘制虚线*/;
    private Path mPath;
    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;
    private float startX, startY;
    private Canvas mCanvas;
    private MarkLine selectedLine;
    private OnMarkInfoCallback callback;
    private Runnable runnable;
    private boolean isCanStartToDraw = false;
    private static final String TAG = "DrawMarkView";
    private DragEventInterceptor dragEventInterceptor;
    private List<Pair<MarkLine, Integer>> dragLines;

    //用于记录与恢复
    private float selectedLineEndX;
    private float selectedLineEndY;

    public DrawMarkView(Context context) {
        this(context, null);
    }

    public DrawMarkView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);//此处第二个参数不能为空，否则java中无法根据id获得实例的引用
    }

    public DrawMarkView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    {
        runnable = new Runnable() {
            @Override
            public void run() {
                //执行长按点击事件的逻辑代码
                if (callback != null && selectedLine != null) {
                    callback.onEditText(selectedLine.getText());
                }
            }
        };


        //线的Paint
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setAntiAlias(true);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeWidth(5);
        mLinePaint.setColor(Color.GREEN);

        dashPaint = new Paint(mLinePaint);
        dashPaint.setColor(Color.RED);
        dashPaint.setStrokeWidth(3);
        dashPaint.setPathEffect(new DashPathEffect(new float[]{25, 5}, 0));


        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setAntiAlias(true);
        textPaint.setStyle(Paint.Style.STROKE);
        textPaint.setStrokeWidth(5);
        textPaint.setColor(Color.BLUE);
        textPaint.setTextSize(50);
        textPaint.setStyle(Paint.Style.FILL);
        //该方法即为设置基线上那个点到底是left,center,还是right  这里我设置为center
        textPaint.setTextAlign(Paint.Align.CENTER);
        //路径
        mPath = new Path();
        dragLines = new ArrayList<>();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // boolean isConsume = false;
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchDown(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(x, y);
                break;
            case MotionEvent.ACTION_UP:
                touchUp(x, y);
                break;
        }

        invalidate();
        return false;
    }


    private void addDragLine(Pair<MarkLine, Integer> line) {
        if (markLines.contains(line)) {
            dragLines.add(line);
        }
    }

    private void addDragLine(float x, float y) {

    }


    public void dragLines(float dx, float dy) {
        for (Pair<MarkLine, Integer> markLinePair : dragLines) {
            MarkLine line = markLinePair.first;
            if (markLinePair.second == 0) {//拖动头部
                line.setStartX(line.getStartY() + dx);
                line.setStartX(line.getStartY() + dy);
            } else {//拖动尾部
                line.setEndX(line.getEndX() + dx);
                line.setEndY(line.getEndY() + dy);
            }
        }
        invalidate();
    }

    /**
     * 手指按下时
     *
     * @param x
     * @param y
     */
    private void touchDown(float x, float y) {
        selectedLine = findLineByEndPoint(x, y);
        if (selectedLine == null) {
            mPath.reset();
            mPath.moveTo(x, y);
            mX = x;
            mY = y;
            startX = x;
            startY = y;
        } else {
            selectedLineEndX = selectedLine.getEndX();
            selectedLineEndY = selectedLine.getEndY();
            postDelayed(runnable, ViewConfiguration.getLongPressTimeout());
        }
    }

    public MarkLine findLineByEndPoint(float x, float y) {
        for (MarkLine line : markLines) {
            int offset = 100;
            if (Math.abs(line.getEndX() - x) < offset && Math.abs(line.getEndY() - y) < offset) {
                return line;
            }
        }
        return null;
    }


    public List<Pair<MarkLine, Integer>> findLine(float x, float y) {
        List<Pair<MarkLine, Integer>> pairList = new ArrayList<>();
        for (MarkLine line : markLines) {
            int offset = 100;
            if (Math.abs(line.getStartX() - x) < offset && Math.abs(line.getStartY() - y) < offset) {
                pairList.add(new Pair<>(line, 0));
            }
            if (Math.abs(line.getEndX() - x) < offset && Math.abs(line.getEndY() - y) < offset) {
                postDelayed(runnable, ViewConfiguration.getLongPressTimeout());
                pairList.add(new Pair<>(line, 1));
            }
        }
        return pairList;
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
        } else {
//            removeCallbacks(runnable);
//            selectedLine.setEndX(x);
//            selectedLine.setEndY(y);
        }

    }

    /**
     * 手指抬起时
     */
    private void touchUp(float endX, float endY) {
        Log.d(TAG, "touchUp() called with: endX = [" + endX + "], endY = [" + endY + "]  isCanStartToDraw:" + isCanStartToDraw);
        if (selectedLine == null) {
            mPath.lineTo(mX, mY);
            if (Math.abs(startX - endX) > 100 || Math.abs(startY - endY) > 100) {
                if (isCanStartToDraw) {
                    MarkLine line = new MarkLine(startX, startY, endX, endY, "");
                    //去重
                    if (markLines.size() == 0) {
                        markLines.add(line);
                    } else {
                        selectedLine = markLines.get(markLines.size() - 1);
                        selectedLineEndX = selectedLine.getEndX();
                        selectedLineEndY = selectedLine.getEndY();
                        if (!line.equals(selectedLine)) {
                            selectedLine = line;
                            markLines.add(line);
                        }
                    }

                }
            }
            mPath.reset();
            return;
        }

        if (dragEventInterceptor != null && dragEventInterceptor.intercept(endX, endY)) {
            removeCallbacks(runnable);
            selectedLine.setEndX(selectedLineEndX);
            selectedLine.setEndY(selectedLineEndY);
        } else {
            removeCallbacks(runnable);
            selectedLine.setEndX(endX);
            selectedLine.setEndY(endY);
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(mPath, dashPaint);
        for (MarkLine line : markLines) {
            float startX = line.getStartX();
            float startY = line.getStartY();
            float endX = line.getEndX();
            float endY = line.getEndY();
            canvas.drawLine(startX, startY, endX, endY, mLinePaint);
            canvas.drawCircle(startX, startY, 10.0f, mLinePaint);
            canvas.drawCircle(endX, endY, 10.0f, mLinePaint);
        }
        if (mCanvas == null) {
            mCanvas = canvas;
        }
    }


    public void addText(float startX, float startY, float endX, float endY, String text, Canvas canvas) {
        if (!TextUtils.isEmpty(text)) {
            Rect rect = new Rect();
            textPaint.getTextBounds(text, 0, text.length(), rect);
            int w = rect.width() + 30;
            int h = rect.height() + 30;
            float tempX = 0;
            float tempY = 0;
            if (Math.abs(startX - endX) > Math.abs(startY - endY)) {
                if (startX > endX) {
                    tempX = -w >> 1;
                } else {
                    tempX = w >> 1;
                }
            } else {
                if (startY > endY) {
                    tempY = -30;
                } else {
                    tempY = h;
                }
            }
            float centerX = endX + tempX;
            float centerY = endY + tempY;
            canvas.drawText(text, centerX, centerY, textPaint);
        }
    }


    /**
     * 撤销绘图步骤，移除上一个节点
     */
    public void cancelStep() {
        post(new Runnable() {
            @Override
            public void run() {
                if (markLines.size() > 0) {
                    markLines.remove(markLines.size() - 1);
                    postInvalidate();
                }
            }
        });
    }


    public void updateMarkView(String text) {
        if (selectedLine != null) {
            selectedLine.setText(text);
            postInvalidate();
        }
    }

    public void rollback() {
        cancelStep();
    }

    public void clear() {
        if (markLines != null) {
            if (markLines.size() > 0) {
                markLines.clear();
            }
            postInvalidate();
        }
    }


    public interface OnMarkInfoCallback {
        void onEditText(String preText);

        void onSaveFinish(String path);
    }

    public boolean isCanStartToDraw() {
        return isCanStartToDraw;
    }

    public void setCanStartToDraw(boolean canStartToDraw) {
        isCanStartToDraw = canStartToDraw;
    }

    public DragEventInterceptor getDragEventInterceptor() {
        return dragEventInterceptor;
    }

    public void setDragEventInterceptor(DragEventInterceptor dragEventInterceptor) {
        this.dragEventInterceptor = dragEventInterceptor;
    }

    public interface DragEventInterceptor {
        boolean intercept(float x, float y);
    }
}


