package com.pcyfox.lib_elc.markview;

/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.os.Build;
import android.util.Size;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import java.io.PrintWriter;


/**
 * Point holds two integer coordinates
 */
public class Point {
    public float x;
    public float y;

    public Point() {
    }

    public Point(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Point(@NonNull android.graphics.Point src) {
        this.x = src.x;
        this.y = src.y;
    }

    /**
     * Set the point's x and y coordinates
     */
    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Negate the point's coordinates
     */
    public final void negate() {
        x = -x;
        y = -y;
    }

    /**
     * Offset the point's coordinates by dx, dy
     */
    public final void offset(float dx, float dy) {
        x += dx;
        y += dy;
    }

    /**
     * Returns true if the point's coordinates equal (x,y)
     */
    public final boolean equals(int x, int y) {
        return this.x == x && this.y == y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        android.graphics.Point point = (android.graphics.Point) o;

        if (x != point.x) return false;
        if (y != point.y) return false;

        return true;
    }

    @Override
    public int hashCode() {
        float result = x;
        result = 31 * result + y;
        return (int) result;
    }

    @Override
    public String toString() {
        return "Point(" + x + ", " + y + ")";
    }

    /**
     * @hide
     */
    public void printShortString(@NonNull PrintWriter pw) {
        pw.print("[");
        pw.print(x);
        pw.print(",");
        pw.print(y);
        pw.print("]");
    }


    /**
     * {@hide}
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static @NonNull
    android.graphics.Point convert(@NonNull Size size) {
        return new android.graphics.Point(size.getWidth(), size.getHeight());
    }

    /**
     * {@hide}
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static @NonNull
    Size convert(@NonNull android.graphics.Point point) {
        return new Size(point.x, point.y);
    }
}

