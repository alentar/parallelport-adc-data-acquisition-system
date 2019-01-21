package org.alentar.parallelportmon.stream;

import org.alentar.parallelportmon.eventbus.EventBus;
import org.alentar.parallelportmon.eventbus.Events;
import org.alentar.parallelportmon.tcp.ParaMonClient;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class StreamManager implements Closeable {
    final EventBus eventBus = EventBus.getInstance();
    ParaMonClient client;
    ScheduledExecutorService scheduledExecutorService;
    HashMap<String, ScheduledFuture<?>> futureHashMap = new HashMap<>();

    public StreamManager(ParaMonClient client) {
        this.client = client;
        this.scheduledExecutorService = Executors.newScheduledThreadPool(10);
    }

    public void scheduleChannelStream(int chan, long initialDelay, long period, TimeUnit timeUnit) {
        String topic = Integer.toString(chan);
        if (!futureHashMap.containsKey(topic)) {
            ScheduledFuture<?> future = scheduledExecutorService.scheduleAtFixedRate(() -> {
                try {
                    int adc = client.getADCReading(chan);
                    eventBus.publish(topic, adc);
                } catch (IOException e) {
                    eventBus.publish(Events.Disconnect.toString(), e.getMessage());
                }
            }, initialDelay, period, timeUnit);

            futureHashMap.put(topic, future);
        }
    }

    public void cancelChannelStream(int chan) {
        String topic = Integer.toString(chan);
        if (futureHashMap.containsKey(topic)) {
            futureHashMap.get(topic).cancel(true);
            futureHashMap.remove(topic);
        }
    }

    public ParaMonClient getClient() {
        return client;
    }

    @Override
    public void close() throws IOException {
        client.close();
        scheduledExecutorService.shutdownNow();
    }
}
