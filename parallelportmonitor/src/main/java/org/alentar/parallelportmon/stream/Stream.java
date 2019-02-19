package org.alentar.parallelportmon.stream;

public abstract class Stream {
    private StreamState state = StreamState.RUNNING;

    public abstract String getTopic();

    public synchronized StreamState getState() {
        return state;
    }

    public synchronized void setState(StreamState state) {
        this.state = state;
    }
}
