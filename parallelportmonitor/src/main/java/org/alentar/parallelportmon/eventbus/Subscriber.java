package org.alentar.parallelportmon.eventbus;

public interface Subscriber {
    void onData(String topic, Object data);
}
