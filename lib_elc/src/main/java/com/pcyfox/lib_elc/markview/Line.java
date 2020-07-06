package com.pcyfox.lib_elc.markview;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import com.pcyfox.lib_elc.uitls.Utils;

import java.util.ArrayList;
import java.util.List;

public class Line {
    private List<Point> pointList = new ArrayList<>();
    private Point headPoint;
    private Point trailPoint;
    private Bitmap deleteBtn;
    private Point deleteBtnCenter;
    public Path path;//缓存路径
    private float strokeWidth = Utils.dip2px(2);
    private Paint mLinePaint;
    private boolean isSelected = false;

    {
        deleteBtnCenter = new Point();
        //线的Paint
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setAntiAlias(true);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeWidth(strokeWidth);
        mLinePaint.setColor(Color.GREEN);
    }

    public Line() {
    }

    public Line(Point headPoint, Point trailPoint) {
        pointList.add(headPoint);
        pointList.add(trailPoint);
        this.headPoint = headPoint;
        this.trailPoint = trailPoint;
    }

    public boolean addPoint(Point point) {
        if (pointList.contains(point)) {
            return false;
        }
        pointList.add(point);
        return true;
    }


    public Point getHeadPoint() {
        if (headPoint != null) {
            return headPoint;
        }
        if (pointList.isEmpty()) {
            return null;
        }
        return pointList.get(0);
    }

    public void setHeadPoint(Point point) {
        headPoint = point;
        if (pointList.isEmpty()) {
            pointList.add(point);
            return;
        }
        pointList.set(1, point);
    }


    public Point getTrailPoint() {
        if (trailPoint != null) {
            return trailPoint;
        }

        if (pointList.isEmpty()) {
            return null;
        }
        return pointList.get(pointList.size() - 1);
    }


    public boolean setTrailPoint(Point point) {
        trailPoint = point;
        if (pointList.isEmpty()) {
            return false;
        }
        pointList.set(pointList.size() - 1, point);
        return true;
    }

    public boolean addMidPoint(Point point) {
        if (pointList.isEmpty() || pointList.size() < 2) {
            return false;
        }
        pointList.add(pointList.size() - 1, point);
        return true;
    }

    public List<Point> getMidPoints() {
        if (pointList.isEmpty() || pointList.size() < 3) {
            return null;
        }
        List<Point> midPoints = new ArrayList<>(pointList.size() - 2);
        for (int i = 1; i < pointList.size() - 1; i++) {
            midPoints.add(pointList.get(i));
        }
        return midPoints;
    }


    public List<Point> getNoHeadPoints() {
        if (pointList.isEmpty() || pointList.size() < 2) {
            return null;
        }
        List<Point> midPoints = new ArrayList<>(pointList.size() - 1);
        for (int i = 1; i < pointList.size(); i++) {
            midPoints.add(pointList.get(i));
        }
        return midPoints;
    }


    public boolean isInLineRect(float x, float y, float padding/*线段两端点都要减去的部分*/) {
        Point proxyHeadPoint = getHeadPoint();
        if (proxyHeadPoint == null || pointList.size() < 2) {
            return false;
        }
        float extraSpace = padding * 2;
        for (int i = 1; i < pointList.size(); i++) {
            Point currentPoint = pointList.get(i);
            //找到离得近的竖线
            if (Math.abs(x - currentPoint.x) < extraSpace && Math.abs(proxyHeadPoint.x - currentPoint.x) < 4) {
                boolean isInMid = isInMid(y, proxyHeadPoint.y, currentPoint.y, padding);
                if (isInMid) {
                    return true;
                }
            }
            //找到离得近的横线
            if (Math.abs(y - currentPoint.y) < extraSpace && Math.abs(proxyHeadPoint.y - currentPoint.y) < 4) {
                boolean isInMid = isInMid(x, proxyHeadPoint.x, currentPoint.x, padding);
                if (isInMid) {
                    return true;
                }
            }
            proxyHeadPoint = currentPoint;
        }
        return false;
    }


    private Line finMaxLongPartLine() {
        Point proxyHeadPoint = getHeadPoint();
        if (proxyHeadPoint == null || pointList.size() < 2) {
            return null;
        }

        float length;
        for (int i = 1; i < pointList.size(); i++) {
            Point currentPoint = pointList.get(i);
            proxyHeadPoint = currentPoint;
        }
        return null;
    }


    private boolean isInMid(float input, float first, float second, float padding) {
        float min = Math.min(first, second);
        float max = Math.max(first, second);
        return input > min + padding && input < max - padding;
    }


    public void draw(Canvas canvas) {
        Point headPoint = getHeadPoint();
        Point trailPoint = getTrailPoint();
        if (path == null) {
            path = new Path();
            headPoint = getHeadPoint();
            if (headPoint == null) {
                return;
            }
            path.moveTo(headPoint.x, headPoint.y);
            List<Point> otherPoints = getNoHeadPoints();
            if (otherPoints == null) {
                return;
            }
            for (Point point : otherPoints) {
                path.lineTo(point.x, point.y);
            }
        }
        canvas.drawPath(path, mLinePaint);
        canvas.drawCircle(headPoint.x, headPoint.y, Utils.dip2px(4), mLinePaint);
        canvas.drawCircle(trailPoint.x, trailPoint.y, Utils.dip2px(4), mLinePaint);

        //绘制deleteBtn
        if (isSelected && deleteBtn != null) {
            int x = (int) (getHeadPoint().x + getTrailPoint().x) / 2;
            int y = (int) (getHeadPoint().y + getTrailPoint().y) / 2;
            if (Math.abs(getHeadPoint().x - getTrailPoint().x) < 4) {
                x += Utils.dip2px(12);
            }
            if (Math.abs(getHeadPoint().y - getTrailPoint().y) < 4) {
                y += Utils.dip2px(12);
            }
            canvas.drawBitmap(deleteBtn, x, y, mLinePaint);
            deleteBtnCenter.x = x + deleteBtn.getWidth() / 2;
            deleteBtnCenter.y = y + deleteBtn.getHeight() / 2;
        }

    }

    public void setLineIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
        if (isSelected) {
            mLinePaint.setColor(Color.RED);
        } else {
            mLinePaint.setColor(Color.GREEN);
        }
    }

    public boolean selectedLine(float x, float y, float padding) {
        boolean isInRect = isInLineRect(x, y, padding);
        setLineIsSelected(isInRect);
        return isInRect;
    }

    public Bitmap getDeleteBtn() {
        return deleteBtn;
    }

    public void setDeleteBtn(Bitmap deleteBtn) {
        this.deleteBtn = deleteBtn;
    }


    public boolean isTouchedDeleteBtn(float x, float y) {
        if (!isSelected) {
            return false;
        }
        return Math.abs(x - deleteBtnCenter.x) <= deleteBtn.getWidth() && Math.abs(y - deleteBtnCenter.y) <= deleteBtn.getHeight();
    }


    @Override
    public String toString() {
        return "Line{" +
                "pointList=" + pointList +
                '}';
    }
}
