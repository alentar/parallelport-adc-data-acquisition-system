package org.alentar.parallelportmon.stream;

import java.util.concurrent.TimeUnit;

public class ChannelStream extends Stream {
    private TimeUnit updateIntervalUnit;
    private long updateInterval;
    private int channel;
    private String name;
    private String topic;
    private long initialDelay;

    public ChannelStream(int channel, String name, TimeUnit updateIntervalUnit, long updateInterval, long initialDelay) {
        this.updateIntervalUnit = updateIntervalUnit;
        this.updateInterval = updateInterval;
        this.channel = channel;
        this.initialDelay = initialDelay;
        this.topic = String.format("/channel/%d/interval/%d/timeunit/%s", channel, updateInterval, updateIntervalUnit.toString());
        this.name = name;
    }

    @Override
    public String getTopic() {
        return topic;
    }

    public TimeUnit getUpdateIntervalUnit() {
        return updateIntervalUnit;
    }

    public void setUpdateIntervalUnit(TimeUnit updateIntervalUnit) {
        this.updateIntervalUnit = updateIntervalUnit;
    }

    public long getUpdateInterval() {
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

    public long getInitialDelay() {
        return initialDelay;
    }

    @Override
    public String toString() {
        return "ChannelStream{" +
                "updateIntervalUnit=" + updateIntervalUnit +
                ", updateInterval=" + updateInterval +
                ", channel=" + channel +
                ", name='" + name + '\'' +
                ", topic='" + topic + '\'' +
                ", initialDelay=" + initialDelay +
                '}';
    }
}
