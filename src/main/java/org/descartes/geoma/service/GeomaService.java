package org.descartes.geoma.service;

import org.descartes.geoma.engine.Entity;
import org.descartes.geoma.engine.Scene;
import org.springframework.stereotype.Service;

@Service
public class GeomaService {

    private Scene scene;

    public GeomaService(Scene scene) {

    }

    public void addEntity(Entity entity) {
        scene.addEntity(entity);
    }
}
