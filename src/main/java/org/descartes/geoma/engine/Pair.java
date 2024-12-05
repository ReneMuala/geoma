package org.descartes.geoma.engine;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Pair<F, S> {
    protected final F first;
    protected final S second;

    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }
}
