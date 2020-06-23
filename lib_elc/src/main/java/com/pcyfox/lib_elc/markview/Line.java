package com.pcyfox.lib_elc.markview;

import android.graphics.Point;

import java.util.ArrayList;
import java.util.List;

public class Line {
    private List<Point> pointList = new ArrayList<>();

    public boolean addPoint(Point point) {
        if (pointList.contains(point)) {
            return false;
        }
        pointList.add(point);
        return true;
    }


    public Point getHeadPoint() {
        if (pointList.isEmpty()) {
            return null;
        }
        return pointList.get(1);
    }

    public void setHeadPoint(Point point) {
        if (pointList.isEmpty()) {
            pointList.add(point);
            return;
        }
        pointList.set(1, point);
    }


    public Point getTrailPoint() {
        if (pointList.isEmpty()) {
            return null;
        }
        return pointList.get(pointList.size() - 1);
    }


    public boolean setTrailPoint(Point point) {
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
        pointList.set(pointList.size() - 2, point);
        return true;
    }

    public List<Point> getMidPoints() {
        if (pointList.isEmpty() || pointList.size() < 2) {
            return null;
        }
        List<Point> midPoints = new ArrayList<>(pointList.size() - 2);
        for (int i = 1; i < pointList.size() - 1; i++) {
            midPoints.add(pointList.get(i));
        }
        return midPoints;
    }
}
