package org.descartes.geoma.engine;

import java.time.Duration;
import java.time.LocalDateTime;

public class TimeoutCounter implements Counter {
    final private Duration duration;
    final private LocalDateTime startTime = LocalDateTime.now();

    public TimeoutCounter(Duration duration) {
        this.duration = duration;
    }

    @Override
    public void count() {}

    @Override
    public boolean isDone() {
        return Duration.between(startTime, LocalDateTime.now()).compareTo(duration) > 0;
    }
}
