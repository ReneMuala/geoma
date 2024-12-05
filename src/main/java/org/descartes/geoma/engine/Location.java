package org.descartes.geoma.engine;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.awt.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Location extends Entity {
    protected Point origin;

    @Override
    public Point getTopLeft() {
        return origin;
    }

    @Override
    public Point getBottomRight() {
        return origin;
    }

    @Override
    public void draw(Graphics2D g) {
        final int size1 = 4,size2 = 8;
        final Point start1 = origin.minus((double) size1 /2), start2 = origin.minus((double) size2 /2);
        g.setColor(Color.white);
        g.fillArc((int)start2.getX(), (int)start2.getY(), size2, size2, 0, 360);
        g.setColor(Color.blue);
        g.fillArc((int)start1.getX(), (int)start1.getY(), size1, size1, 0, 360);
    }
}
