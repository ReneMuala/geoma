package org.descartes.geoma.engine;

import lombok.AllArgsConstructor;


/**
 * Used to find up to {@code to} solutions, deprecated because its usage blocks the thread indefinitely until all needed solutions and found.
 * <p>
 * See {@link IncrementalTimeoutCounter}
 */
@Deprecated()
@AllArgsConstructor
public class IncrementalCounter implements Counter {
    private long from;
    private long to;

    @Override
    public String toString() {
        return "Counter [from=" + from + ", to=" + to + "]";
    }

    @Override
    public void count() {
        from++;
    }

    @Override
    public boolean isDone(){
        return from >= to;
    }
}
