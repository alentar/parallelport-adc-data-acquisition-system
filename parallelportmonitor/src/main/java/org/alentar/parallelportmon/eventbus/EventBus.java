package org.alentar.parallelportmon.eventbus;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EventBus {
    private static EventBus eventBus;
    ConcurrentHashMap<String, Set<Subscriber>> subscribers = new ConcurrentHashMap<>();

    public static EventBus getInstance() {
        if (eventBus == null) eventBus = new EventBus();
        return eventBus;
    }

    public synchronized void subscribe(String topic, Subscriber subscriber) {
        if (subscribers.containsKey(topic)) {
            subscribers.get(topic).add(subscriber);
        } else {
            subscribers.put(topic, new HashSet<>());
            subscribers.get(topic).add(subscriber);
        }
        Logger.getGlobal().log(Level.INFO, subscriber + " subscribed to topic: " + topic);
    }

    public synchronized void unsubscribe(String topic, Subscriber subscriber) {
        if (subscribers.containsKey(topic)) {
            Set<Subscriber> subscriberSet = subscribers.get(topic);
            subscriberSet.remove(subscriber);
            Logger.getGlobal().log(Level.INFO, "Unsubscribed " + subscriber);
        }
    }

    public synchronized void publish(String topic, Object data) {
        if (!subscribers.containsKey(topic))
            subscribers.put(topic, new HashSet<>());

        Set<Subscriber> subscriberSet = subscribers.get(topic);
        for (Subscriber s : subscriberSet) {
            s.onData(topic, data);
        }
    }
}
