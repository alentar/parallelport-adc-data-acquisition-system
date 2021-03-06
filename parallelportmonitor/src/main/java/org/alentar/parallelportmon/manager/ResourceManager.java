package org.alentar.parallelportmon.manager;

import org.alentar.parallelportmon.adc.ADC;
import org.alentar.parallelportmon.stream.StreamManager;

import java.io.Closeable;
import java.util.HashSet;
import java.util.Set;

public class ResourceManager {
    private static ResourceManager instance;
    private Set<Closeable> closeables = new HashSet<>();
    private StreamManager streamManager;
    private ADC adc;

    public ADC getAdc() {
        return adc;
    }

    public void setAdc(ADC adc) {
        this.adc = adc;
    }

    public static ResourceManager getInstance() {
        if (instance == null) instance = new ResourceManager();
        return instance;
    }

    public void registerForDispose(Closeable closeable) {
        closeables.add(closeable);
    }

    public void unregisterFromDispose(Closeable closeable) {
        closeables.remove(closeable);
    }

    public void dispose() throws Exception {
        for (Closeable closeable : closeables) {
            if (closeable != null)
                closeable.close();
        }
    }

    public void shutdownStreamManager() throws Exception {
        if (this.streamManager != null) {
            this.closeables.remove(this.streamManager);
            this.streamManager.close();
            this.streamManager = null;
        }
    }

    public StreamManager getStreamManager() {
        return streamManager;
    }

    public void registerStreamManager(StreamManager streamManager) {
        if (this.streamManager == null) {
            this.streamManager = streamManager;
            registerForDispose(this.streamManager);
        }
    }
}
