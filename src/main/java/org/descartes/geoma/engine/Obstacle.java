package org.descartes.geoma.engine;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.awt.*;

@Getter
@Setter
@NoArgsConstructor
public class Obstacle extends Location {
    private double length;

    @Override
    public void draw(Graphics2D g) {
        final int size = 16;
        final Point start = origin.minus(2);
        Color base = Color.red;
        Color c = new Color(base.getRed(), base.getGreen(), base.getBlue(), 150);
        g.setColor(c);
        g.fillArc((int)start.getX(), (int)start.getY(), size, size, 0, 360);
    }

    public Obstacle(Point point, double length) {
        super(point);
        this.length = length;
    }

    @Override
    public Point getTopLeft() {
        return super.origin.minus(length);
    }

    @Override
    public Point getBottomRight() {
        return origin.plus(length);
    }
}
