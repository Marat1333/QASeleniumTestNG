package com.leroy.core.util;

import org.openqa.selenium.Rectangle;

public class Delta {

    private int left;
    private int right;
    private int top;
    private int bottom;

    public Delta(int left, int right, int top, int bottom) {
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
    }

    public Delta(Rectangle mainRectangle, Rectangle slaveRectangle) {
        left = mainRectangle.x - slaveRectangle.x;
        top = mainRectangle.y - slaveRectangle.y;
        bottom = slaveRectangle.height - mainRectangle.height - top;
        right = slaveRectangle.width - mainRectangle.width - left;
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getRight() {
        return right;
    }

    public void setRight(int right) {
        this.right = right;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getBottom() {
        return bottom;
    }

    public void setBottom(int bottom) {
        this.bottom = bottom;
    }

    @Override
    public String toString() {
        return String.format("left: %+d, top: %+d, right: %+d, bottom: %+d", left, top, right, bottom);
    }

    /**
     * Calculates a new rectangle applying the Delta and returns it
     *
     * @param rectangle - the rectangle to which the Delta will be applied
     * @return Rectangle
     */
    public Rectangle getRectangleWithDelta(Rectangle rectangle) {
        return new Rectangle(
                rectangle.x - left,
                rectangle.y - top,
                rectangle.height + bottom + top,
                rectangle.width + right + left);
    }
}
