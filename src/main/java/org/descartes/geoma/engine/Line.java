package org.descartes.geoma.engine;

public class Line extends Pair<Point, Point>{
    public Line(Point first, Point second) {
        super(first, second);
    }

    public Point getInterceptionPoint(Line other) {
        return lineLine(first.getX(), first.getY(), second.getX(), second.getY(),
                other.first.getX(), other.first.getY(), other.second.getX(), other.second.getY());
    }

    public boolean intersects(Point point, double buffer) {
        return linePoint(first, second, point, buffer);
    }

    public double getSlope(){
        final double dividend = second.getX() - first.getX();
        final double divisor = second.getY() - first.getY();
        return dividend != 0 ? dividend / divisor : 0;
    }

    // https://www.jeffreythompson.org/collision-detection/line-line.php#:~:text=To%20check%20if%20two%20lines,x3)*(y2%2Dy1))%3B
    // LINE/LINE
    public static Point lineLine(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {
        double dividend = (y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1);
        if(dividend == 0) return null;
        // calculate the distance to intersection point
        double uA = ((x4-x3)*(y1-y3) - (y4-y3)*(x1-x3)) / dividend;
        double uB = ((x2-x1)*(y1-y3) - (y2-y1)*(x1-x3)) / dividend;
        // if uA and uB are between 0-1, lines are colliding
        if (uA >= 0 && uA <= 1 && uB >= 0 && uB <= 1) {
            return new Point(x1 + (uA * (x2-x1)), y1 + (uA * (y2-y1)));
        }
        return null;
    }

    // LINE/POINT
    public static boolean linePoint(Point start, Point end, Point point, double buffer) {
        // get distance from the point to the two ends of the line
        double d1 = point.distance(start);
        double d2 = point.distance(end);

        // get the length of the line
        double lineLen = start.distance(end);

        // since floats are so minutely accurate, add
        // a little buffer zone that will give collision
//        double buffer = 0.1;    // higher # = less accurate

        // if the two distances are equal to the line's
        // length, the point is on the line!
        // note we use the buffer here to give a range,
        // rather than one #
        return (d1+d2 >= lineLen-buffer && d1+d2 <= lineLen+buffer);
    }

    @Override
    public int hashCode() {
        return (int)getSlope();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Line other) {
            return first.equals(other.first) && second.equals(other.second) || second.equals(other.first) && first.equals(other.second) ;
        } else {
            return false;
        }
    }
}