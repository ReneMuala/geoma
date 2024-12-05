package org.descartes.geoma.engine;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class Point implements Cloneable {
    private double x;
    private double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Point p) {
            return x == p.getX() && y == p.getY();
        } return false;
    }

    public double distance(Point p) {
        return Math.sqrt(Math.pow(x - p.getX(), 2) + Math.pow(y - p.getY(), 2));
    }

    public Point minus(double scalar) {
        return new Point(
                x - scalar,
                y - scalar
        );
    }

    public Point plus(double scalar) {
        return new Point(
                x + scalar,
                y + scalar
        );
    }

    public boolean isGreaterX(Point point) {
        return this.x > point.x;
    }

    public boolean isGreaterY(Point point) {
        return this.y > point.y;
    }

    public boolean isLessX(Point point) {
        return this.x < point.x;
    }

    public boolean isLessY(Point point) {
        return this.y < point.y;
    }

    @Override
    public Point clone() {
        try {
            return (Point) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
