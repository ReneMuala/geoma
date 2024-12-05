package org.descartes.geoma;

import org.descartes.geoma.engine.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Comparator;

@SpringBootApplication
public class Geoma {
    public static void main(String[] args) {
//        SpringApplication.run(Geoma.class, args);
        Scene scene = new Scene();
        scene.addOSM(new File("C:\\Users\\dte\\Downloads\\map(2).osm"), "Route.*", -19.8180, -34.834, 100000);
        scene.addEntity(new Obstacle(new Point(210,200), 50));
        Marker marker = new Marker(new Point(120, 50));
        Target target = new Target(new Point(330, 400));
        Model model = new Model(scene.getPaths(), scene.getObstacles());
        scene.addEntity(marker);
        scene.addEntity(target);

        Navigator navigator = new Navigator(marker, target, model);
        System.out.println(LocalDateTime.now());
        for(Path solPath: navigator.navigate(new Counter(0,50)).stream().sorted(Comparator.comparingDouble(Path::getLength)).skip(0).limit(1).toList()){
            solPath.setSolution(true);
            scene.addEntity(solPath);
        }
        System.out.println(LocalDateTime.now());
        scene.drawSVG();
        scene.drawPNG();
    }
}
