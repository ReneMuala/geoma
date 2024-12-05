package org.descartes.geoma.engine;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Entity implements MeasurableBox, Drawable {
    Long id;
    String name;
}
