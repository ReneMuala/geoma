package org.descartes.geoma.controller;

import org.descartes.geoma.engine.Marker;
import org.descartes.geoma.engine.Path;
import org.descartes.geoma.engine.Target;
import org.descartes.geoma.service.GeomaService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class GeomaController {
    private GeomaService geomaService;

    public GeomaController(GeomaService geomaService) {

    }

    @PostMapping("/path")
    public void addPath(@RequestBody Path path){
        geomaService.addEntity(path);
    }

    @PostMapping("/marker")
    public void addMarker(@RequestBody Marker marker){
        geomaService.addEntity(marker);
    }


    @PostMapping("/target")
    public void addMarker(@RequestBody Target target){
        geomaService.addEntity(target);
    }
}
