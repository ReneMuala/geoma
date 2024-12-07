package org.descartes.geoma.engine;

import lombok.Getter;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Component
public class Scene implements MeasurableBox, Drawable {
    private final List<Entity> entities = new ArrayList<>();

    public List<Path> getPaths() {
        return entities.stream()
                .filter(e -> e instanceof Path)
                .map(e -> (Path) e).toList();
    }

    public List<Obstacle> getObstacles() {
        return entities.stream()
                .filter(e -> e instanceof Obstacle)
                .map(e -> (Obstacle) e).toList();
    }

    @Deprecated(forRemoval = true)
    public void drawPNG() {
        final Point bottomRight = getBottomRight();
        final int padding = 0;
        final int paddedWidth = (int) bottomRight.getX() + padding * 2;
        final int paddedHeight = (int) bottomRight.getY() + padding * 2;
        BufferedImage image = new BufferedImage(paddedWidth, paddedHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setBackground(Color.BLACK);
        g.clearRect(0, 0, paddedWidth, paddedHeight);
        g.translate(padding, padding);
        draw(g);

        try {
            ImageIO.write(image, "png", new File("GeoMa.png"));
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public void drawSVG(double deg) {
        try {
            final Point bottomRight = getBottomRight();
            final int padding = 10;
            final int paddedWidth = (int) bottomRight.getX() + padding * 2;
            final int paddedHeight = (int) bottomRight.getY() + padding * 2;
            SVGGraphics2D g = new SVGGraphics2D(paddedWidth, paddedHeight);
            g.setBackground(new Color(240, 240, 240, 255));
            g.clearRect(0, 0, paddedWidth, paddedHeight);
            g.rotate(deg * Math.PI / 180, (double) paddedWidth / 2, (double) paddedHeight / 2);
            g.translate(padding, padding);
            draw(g);
            PrintStream printStream = new PrintStream(new FileOutputStream("GeoMa.svg"));
            printStream.print(g.getSVGDocument());
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    public void printEntities() {
        for (Entity entity : entities) {
            if (entity instanceof Path) {
                System.out.println("Path: " + entity);
            } else if (entity instanceof Obstacle) {
                System.out.println("Obstacle: " + entity);
            } else if (entity instanceof Location) {
                System.out.println("Location: " + entity);
            }
        }
    }

    @Override
    public Point getTopLeft() {
        return new Point(0, 0);
    }

    @Override
    public Point getBottomRight() {
        Point bottomRight = entities.isEmpty() ? null : entities.getFirst().getBottomRight().clone();
        for (Entity entity : entities.stream().skip(1).toList()) {
            Point point = entity.getBottomRight();
            if (point.isGreaterX(bottomRight))
                bottomRight.setX(point.getX());
            if (point.isGreaterY(bottomRight))
                bottomRight.setY(point.getY());
        }
        return bottomRight;
    }

    @Override
    public void draw(Graphics2D g) {
        for (Entity entity : entities) {
            entity.draw(g);
        }
    }

    public int addOSM(File fXmlFile, PathValidator validator, double xoffset, double yoffset, double zoom) {
        Map<String, Point> points = new HashMap<>();
        List<Path> paths = new ArrayList<>();

        try {
            final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            final DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            final Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();
            Element root = doc.getDocumentElement();
            NodeList children = root.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                switch (child.getNodeName()) {
                    case "node" -> {
                        Point point = new Point(
                                zoom * ((xoffset + Double.parseDouble(child.getAttributes().getNamedItem("lat").getTextContent()))),
                                zoom * ((yoffset + Double.parseDouble(child.getAttributes().getNamedItem("lon").getTextContent()))));
                        points.put(
                                child.getAttributes().getNamedItem("id").getTextContent(),
                                point);
                    }
                    case "way" -> {
                        NodeList wayChildren = child.getChildNodes();
                        Map<String, String> tags = new HashMap<>();
                        Path path = new Path();
                        for (int j = 0; j < wayChildren.getLength(); j++) {
                            Node wayChild = wayChildren.item(j);
                            switch (wayChild.getNodeName()) {
                                case "nd" -> {
                                    Point subPoint = points.get(wayChild.getAttributes().getNamedItem("ref").getTextContent());
                                    path.addPoint(subPoint);
                                }
                                case "tag" ->
                                        tags.put(wayChild.getAttributes().getNamedItem("k").getTextContent(), wayChild.getAttributes().getNamedItem("v").getTextContent());
                            }
                        }
                        if (validator.validate(tags)) paths.add(path);
                    }
                    case "bounds" -> {
                        if (xoffset == 0)
                            xoffset = -Double.parseDouble(child.getAttributes().getNamedItem("minlat").getTextContent());
                        if (yoffset == 0)
                            yoffset = -Double.parseDouble(child.getAttributes().getNamedItem("minlon").getTextContent());
                        System.out.println(fXmlFile.getName() + " [min-bounds] " + new Point(xoffset, yoffset).times(-1));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return 0;
        }
        this.entities.addAll(paths);

        return paths.size();
    }
}
