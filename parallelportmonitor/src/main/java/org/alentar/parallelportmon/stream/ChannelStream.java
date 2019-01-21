package org.alentar.parallelportmon.stream;

import java.util.concurrent.TimeUnit;

public class ChannelStream extends Stream {
    private TimeUnit updateIntervalUnit = TimeUnit.SECONDS;
    private int updateInterval = 1;
    private int channel;
    private String name;
    private String topic;

    public ChannelStream(int channel, String name, TimeUnit updateIntervalUnit, int updateInterval) {
        this.updateIntervalUnit = updateIntervalUnit;
        this.updateInterval = updateInterval;
        this.channel = channel;
        this.name = name;
        this.topic = String.format("/channel-%d/%s/%d-%s", channel, name, updateInterval, updateIntervalUnit.toString());
    }

    @Override
    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public TimeUnit getUpdateIntervalUnit() {
        return updateIntervalUnit;
    }

    public void setUpdateIntervalUnit(TimeUnit updateIntervalUnit) {
        this.updateIntervalUnit = updateIntervalUnit;
    }

    public int getUpdateInterval() {
        return updateInterval;
    }

    public void setUpdateInterval(int updateInterval) {
        this.updateInterval = updateInterval;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
