package org.descartes.geoma.engine;

import java.time.Duration;

public class IncrementalTimeoutCounter extends TimeoutCounter {
    private long from = 0;
    final private long to;

    public IncrementalTimeoutCounter(long size, Duration duration) {
        super(duration);
        this.to = size;
    }

    @Override
    public void count() {
        from++;
    }

    @Override
    public boolean isDone(){
        return super.isDone() || from >= to;
    }
}
