package com.example.elcapplication.markview;

import androidx.annotation.Nullable;

public class MarkLine {

    private float startX, startY, endX, endY;
    private String text;

    public MarkLine(float startX, float startY, float endX, float endY, String text) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public float getStartX() {
        return startX;
    }

    public void setStartX(float startX) {
        this.startX = startX;
    }

    public float getStartY() {
        return startY;
    }

    public void setStartY(float startY) {
        this.startY = startY;
    }

    public float getEndX() {
        return endX;
    }

    public void setEndX(float endX) {
        this.endX = endX;
    }

    public float getEndY() {
        return endY;
    }

    public void setEndY(float endY) {
        this.endY = endY;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null) {
            return false;
        }

        if (obj instanceof MarkLine) {
            MarkLine input = (MarkLine) obj;
            return input.startX == startX && input.startY == startY && input.endX == endX && endY == input.endY;
        }
        return false;
    }
}
