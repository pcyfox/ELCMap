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

import com.example.elcapplication.uitls.Utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class DrawMarkView extends View {
    private List<MarkLine> markLines = new ArrayList<>();
    private Paint mLinePaint, textPaint, dashPaint/*专门用来绘制虚线*/;
    private Path mPath;
    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;
    private float startX, startY;
    private MarkLine selectedLine;
    private boolean isCanStartToDraw = false;
    private static final String TAG = "DrawMarkView";
    private DragEventInterceptor dragEventInterceptor;
    private Set<Pair<MarkLine, Integer>> dragLines;
    private OnDeleteLineListener onDeleteLineListener;


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
        textPaint.setColor(Color.RED);
        textPaint.setTextSize(Utils.dip2px(getContext(), 14));
        textPaint.setStyle(Paint.Style.FILL);
        //该方法即为设置基线上那个点到底是left,center,还是right  这里我设置为center
        textPaint.setTextAlign(Paint.Align.CENTER);
        //路径
        mPath = new Path();
        dragLines = new HashSet<>();
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
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
        dragLines.add(line);
    }

    public void addDragLine(float x, float y) {
        Set<Pair<MarkLine, Integer>> lines = findDragLine(x, y);
        for (Pair<MarkLine, Integer> lineIntegerPair : lines) {
            addDragLine(lineIntegerPair);
        }
    }

    public void clearDragLines() {
        dragLines.clear();
    }

    public void dragLines(float dx, float dy) {
        Log.d(TAG, "dragLines() called with: dx = [" + dx + "], dy = [" + dy + "]");
        for (Pair<MarkLine, Integer> markLinePair : dragLines) {
            MarkLine line = markLinePair.first;
            if (markLinePair.second == 0) {//拖动头部
                line.setStartX(line.getStartX() + dx);
                line.setStartY(line.getStartY() + dy);
            } else {//拖动尾部
                line.setEndX(line.getEndX() + dx);
                line.setEndY(line.getEndY() + dy);
            }
        }
        invalidate();
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


    public Set<Pair<MarkLine, Integer>> findDragLine(float x, float y) {
        Set<Pair<MarkLine, Integer>> pairList = new HashSet<>();
        for (MarkLine line : markLines) {
            int offset = 60;
            if (Math.abs(line.getStartX() - x) < offset && Math.abs(line.getStartY() - y) < offset) {
                pairList.add(new Pair<>(line, 0));
            }
            if (Math.abs(line.getEndX() - x) < offset && Math.abs(line.getEndY() - y) < offset) {
                pairList.add(new Pair<>(line, 1));
            }
        }
        return pairList;
    }

    public void deleteLineByPoint(float x, float y) {
        List<MarkLine> findLines = findLineByPoint(x, y, 6);
        for (MarkLine line : findLines) {
            markLines.remove(line);
            invalidate();
        }
    }

    public List<MarkLine> findLineByPoint(float x, float y, int offset) {
        List<MarkLine> findLines = new ArrayList<>();
        for (MarkLine line : markLines) {
            if (Math.abs(line.getStartX() - x) < offset && Math.abs(line.getStartY() - y) < offset) {
                findLines.add(line);
            }
            if (Math.abs(line.getEndX() - x) < offset && Math.abs(line.getEndY() - y) < offset) {
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
        startX = x;
        startY = y;
        if (selectedLine != null) {
            tryDeleteTouchLine(x, y);
        }
        selectedLine = findTouchedLine(x, y);
        if (selectedLine == null) {
            mPath.reset();
            mPath.moveTo(x, y);
            mX = x;
            mY = y;
            startX = x;
            startY = y;
        }
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
                    MarkLine line = new MarkLine(startX, startY, x, y, "");
                    line.setText("X");
                    //去重
                    if (markLines.size() == 0) {
                        markLines.add(line);
                    } else {
                        selectedLine = markLines.get(markLines.size() - 1);
                        if (!line.equals(selectedLine)) {
                            selectedLine = line;
                            markLines.add(line);
                        }
                    }

                }
            }
            mPath.reset();
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(mPath, dashPaint);
        if (dragLines != null && dragLines.size() > 0) {
            for (Pair<MarkLine, Integer> pair : dragLines) {
                MarkLine line = pair.first;
                drawLine(canvas, line);
            }
        } else {
            for (MarkLine line : markLines) {
                drawLine(canvas, line);
            }
        }

        if (selectedLine != null) {
            drawLineText(selectedLine, canvas);
        }
    }

    private void drawLine(Canvas canvas, MarkLine line) {
        float startX = line.getStartX();
        float startY = line.getStartY();
        float endX = line.getEndX();
        float endY = line.getEndY();
        canvas.drawLine(startX, startY, endX, endY, mLinePaint);
        canvas.drawCircle(startX, startY, 10.0f, mLinePaint);
        canvas.drawCircle(endX, endY, 10.0f, mLinePaint);
    }

    private Boolean deleteLine(MarkLine line) {
        Iterator iterator = markLines.iterator();
        while (iterator.hasNext()) {
            if (iterator.next() == line) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }


    public MarkLine getSelectedLine() {
        return selectedLine;
    }

    private void drawLineText(float startX, float startY, float endX, float endY, String text, Canvas canvas) {
        if (!TextUtils.isEmpty(text)) {
            Rect rect = new Rect();
            textPaint.getTextBounds(text, 0, text.length(), rect);
            int w = rect.width();
            float centerX = (endX + startX) / 2 + w;
            float centerY = (endY + startY) / 2;

            canvas.drawText(text, centerX, centerY, textPaint);
        }
    }

    /**
     * @param x
     * @param y
     */
    private void tryDeleteTouchLine(float x, float y) {
        float p = 35;
        //文字（"X"）的坐标
        float tx = selectedLine.getCurrentDrawTextX();
        float ty = selectedLine.getCurrentDrawTextY();

        Log.d(TAG, "tryDeleteTouchLine() called with: x = [" + x + "], y = [" + y + "]");
        Log.d(TAG, "tryDeleteTouchLine() called with: tx = [" + tx + "], ty = [" + ty + "]");

        if (Math.abs(tx - x) < p && Math.abs(ty - y) < p) {
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

    private void drawLineText(MarkLine line, Canvas canvas) {
        String text = line.getText();
        float startX = line.getStartX();
        float startY = line.getStartY();
        float endX = line.getEndX();
        float endY = line.getEndY();

        if (!TextUtils.isEmpty(text)) {
            Rect rect = new Rect();
            textPaint.getTextBounds(text, 0, text.length(), rect);
            int w = rect.width();
            float centerX = (endX + startX) / 2 + w;
            float centerY = (endY + startY) / 2;
            line.setCurrentDrawTextX(centerX);
            line.setCurrentDrawTextY(centerY);
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
                    invalidate();
                }
            }
        });
    }


    public void updateMarkView(String text) {
        if (selectedLine != null) {
            selectedLine.setText(text);
            invalidate();
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

    /**
     * 查找触摸区域内是否存在连线
     *
     * @param x
     * @param y
     * @return
     */
    public MarkLine findTouchedLine(float x, float y) {
        Log.d(TAG, "findTouchedLine() called with: x = [" + x + "], y = [" + y + "]");
        //避免触摸Anchor时也判定为选择到线段端
        float paddingX = 20f;
        float paddingY = 20f;
        int count = 0;
        for (MarkLine line : markLines) {
            float startX = line.getStartX();
            float endX = line.getEndX();

            float startY = line.getStartY();
            float endY = line.getEndY();

            //接近为横线
            if (Math.abs(startY - endY) < paddingY*2) {
                if (Math.abs(y - startY) < 30) {
                    count++;
                }
            } else {
                if (startY > endY) {
                    if (y > endY + paddingY && y < startY - paddingY) {
                        count++;
                    }
                } else {
                    if (y < endY - paddingY && y > startY + paddingY) {
                        count++;
                    }
                }
            }


            //接近为竖线
            if (Math.abs(startX - endX) < paddingX*2) {
                if (Math.abs(x - startX) < 30) {
                    count++;
                }
            } else {
                if (startX > endX) {
                    if (x > endX + paddingX && x < startX - paddingX) {
                        count++;
                    }
                } else {
                    if (x < endX - paddingX && x > startX + paddingX) {
                        count++;
                    }
                }
            }


            if (count == 2) {
                //斜率
                float k = (line.getEndY() - line.getStartY()) / (line.getEndX() - line.getStartX());
                Log.d(TAG, "findTouchedLine() called with: k = [" + k + "]");
                float b = y - k * x;
                //y=kx+b  b+10 b-10 在线段两侧构建两条平行线，这两条平行线到线段的距离为10
                float y1 = k * x + b + 40;
                float y2 = k * x + b - 40;
                //说明坐标落在构建的两条平行之间
                if (y > y2 && y < y1) {
                    return line;
                }
            }
            count = 0;

        }
        return null;
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

    public void setOnDeleteLineListener(OnDeleteLineListener onDeleteLineListener) {
        this.onDeleteLineListener = onDeleteLineListener;
    }

    public interface DragEventInterceptor {
        boolean intercept(float x, float y);
    }

    public interface OnDeleteLineListener {
        boolean onDelete(MarkLine line);
    }
}


