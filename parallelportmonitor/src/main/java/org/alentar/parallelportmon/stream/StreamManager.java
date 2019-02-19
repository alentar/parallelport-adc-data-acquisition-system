package org.alentar.parallelportmon.stream;

import org.alentar.parallelportmon.eventbus.EventBus;
import org.alentar.parallelportmon.eventbus.Events;
import org.alentar.parallelportmon.tcp.DataServerClient;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

public class StreamManager implements Closeable {
    private final EventBus eventBus = EventBus.getInstance();
    private DataServerClient client;
    private ScheduledExecutorService scheduledExecutorService;
    private HashMap<String, ScheduledFuture<?>> futureHashMap = new HashMap<>();
    private Set<ChannelStream> channelStreams = new HashSet<>();

    public StreamManager(DataServerClient client) {
        this.client = client;
        this.scheduledExecutorService = Executors.newScheduledThreadPool(10);
    }

    public void scheduleChannelStream(ChannelStream channelStream) {
        if (channelStream == null) return;

        if (!futureHashMap.containsKey(channelStream.getTopic())) {
            ScheduledFuture<?> future = scheduledExecutorService.scheduleAtFixedRate(() -> {
                try {
                    switch (channelStream.getState()) {
                        case RUNNING:
                            int adc = client.getADCReading(channelStream.getChannel());
                            eventBus.publish(channelStream.getTopic(), adc);
                            break;
                        case PAUSED:
                        default:
                            break;
                    }
                } catch (IOException e) {
                    eventBus.publish(Events.Disconnect.toString(), e.getMessage());
                }
            }, channelStream.getInitialDelay(), channelStream.getUpdateInterval(), channelStream.getUpdateIntervalUnit());

            futureHashMap.put(channelStream.getTopic(), future);
            channelStreams.add(channelStream);
            eventBus.publish(StreamManagerTopics.onChannelAdded.toString(), channelStream);
        }
    }

    public void removeChannelStream(ChannelStream channelStream) {
        String topic = channelStream.getTopic();
        if (futureHashMap.containsKey(topic)) {
            futureHashMap.get(topic).cancel(true);
            futureHashMap.remove(topic);
        }

        channelStreams.remove(channelStream);
        eventBus.publish(StreamManagerTopics.onChannelRemoved.toString(), channelStream);
    }

    public DataServerClient getClient() {
        return client;
    }

    public Set<ChannelStream> getChannelStreams() {
        return channelStreams;
    }

    @Override
    public void close() throws IOException {
        client.close();
        scheduledExecutorService.shutdownNow();
    }
}
