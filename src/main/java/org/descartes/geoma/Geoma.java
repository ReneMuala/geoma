package org.descartes.geoma;

import org.descartes.geoma.engine.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.time.Duration;

@SpringBootApplication
public class Geoma {
    public static void main(String[] args) {
//        SpringApplication.run(Geoma.class, args);
        Scene scene = new Scene();
//        scene.addOSM(new File("Data Layer 1_20241205_184858932.osm"), (tags) -> tags.containsKey("name") && tags.get("name").matches("Route.*"), +19.8225, -34.834, 100000);
        scene.addOSM(new File("Downloads\\map(3).osm"), (tags)->!tags.containsKey("amenity") && !tags.containsKey("leisure") && !tags.containsKey("barrier") && !tags.containsKey("building") && !tags.containsKey("natural") && !tags.containsKey("waterway"), 0.0, 0.0, 100000);

        var zoom = 100000;
        var minPoint = new Point(-19.85003, 34.863869);
        var marketPointLatLon = new Point(-19.8335257, 34.8652369);
        var markerPoint = marketPointLatLon.minus(minPoint).times(zoom).abs();
        var marker = new Marker(markerPoint);
        scene.addEntity(marker);

        var targetPointLatLon = new Point(-19.8496336, 34.8643452);
        var targetPoint = targetPointLatLon.minus(minPoint).times(zoom).abs();
        var target = new Target(targetPoint);
        scene.addEntity(target);

        Model model = new Model(scene.getPaths(), scene.getObstacles());

        Navigator navigator = new Navigator(marker, target, model);

        var counter = new IncrementalTimeoutCounter(10, Duration.ofSeconds(10));
        var paths = navigator.navigate(counter);

        if(!paths.isEmpty()){
            var bestPath = paths.getFirst();
            bestPath.setSolution(true);
            scene.addEntity(bestPath);

            final double KM_PER_LAT = 111.1;
            double distanceKm = bestPath.getLength() / zoom * KM_PER_LAT;
            System.out.println(distanceKm);
        }
        scene.drawSVG(-90);
    }
}
