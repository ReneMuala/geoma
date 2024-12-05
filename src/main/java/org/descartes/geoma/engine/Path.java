package org.descartes.geoma.engine;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Path extends Entity {
    List<Point> points = new ArrayList<>();

    private boolean isSolution = false;

    public void addPoint(Point point) {
        points.add(point);
    }

    public double getLength() {
        double length = 0;

        for(int i = 0 ; i < points.size()-1 ; i++) {
            length += points.get(i).distance(points.get(i+1));
        }

        return length;
    }

    @Override
    public Point getTopLeft() {
        Point topLeft = points.isEmpty() ? null : points.get(0).clone();

        for (Point point : points.stream().skip(1).toList()) {
            if (point.isLessX(topLeft))
                topLeft.setX(point.getX());
            if (point.isLessY(topLeft))
                topLeft.setY(point.getY());
        }
        return topLeft;
    }

    @Override
    public Point getBottomRight() {
        Point bottomRight = points.isEmpty() ? null : points.get(0).clone();

        for (Point point : points) {
            if (point.isGreaterX(bottomRight))
                bottomRight.setX(point.getX());
            if (point.isGreaterY(bottomRight))
                bottomRight.setY(point.getY());
        }
        return bottomRight;
    }

    @Override
    public void draw(Graphics2D g) {
        g.setColor(isSolution ? Color.blue : Color.WHITE);
        for(int i = 0; i < points.size() - 1; i++) {
            Point start = points.get(i);
            Point end = points.get(i + 1);
            g.drawLine((int)start.getX(),(int)start.getY(),(int)end.getX(),(int)end.getY());
        }
    }
}
