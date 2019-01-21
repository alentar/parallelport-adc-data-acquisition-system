package org.alentar.parallelportmon.eventbus;

import org.junit.Test;

public class EventTest {
    @Test
    public void testEvents() {
        EventBus eventBus = EventBus.getInstance();

        Subscriber subscriber = new Subscriber() {
            @Override
            public void onData(String topic, Object data) {
                System.out.println("I'm channel 1 subscriber 2");
                System.out.println("I got " + data + " from " + topic);
            }
        };

        eventBus.subscribe("/chan1", (topic, data) -> {
            System.out.println("I'm channel 1 subscriber");
            System.out.println("I got " + data + " from " + topic);
        });

        eventBus.unsubscribe("/chan1", subscriber);
        eventBus.subscribe("/chan1", subscriber);

        eventBus.subscribe("/chan2", (topic, data) -> {
            System.out.println("I'm channel 2 subscriber");
            System.out.println("I got " + data + " from " + topic);
        });

        eventBus.publish("/chan1", 34);
        eventBus.publish("/chan2", "e");
        eventBus.publish("/chanx", 23.0f);
    }
}
