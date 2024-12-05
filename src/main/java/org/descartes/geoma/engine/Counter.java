package org.descartes.geoma.engine;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Counter {
    private long from;
    private long to;

    @Override
    public String toString() {
        return "Counter [from=" + from + ", to=" + to + "]";
    }

    public void count() {
        from++;
    }

    public boolean isDone(){
        return from >= to;
    }
}
