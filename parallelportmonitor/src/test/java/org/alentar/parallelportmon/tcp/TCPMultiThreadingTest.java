package org.alentar.parallelportmon.tcp;

import org.junit.Test;

public class TCPMultiThreadingTest {
    @Test
    public void testMulti() {
        try {
            ParaMonClient paraMonClient = new ParaMonClient("127.0.0.1", 2335);

            Thread t1 = new Thread(() -> {
                try {
                    for (int i = 0; i < 10; i++) {
                        System.out.println(paraMonClient.getADCReading(1));
                        Thread.sleep(5);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            Thread t2 = new Thread(() -> {
                try {
                    for (int i = 0; i < 10; i++) {
                        System.out.println(paraMonClient.getADCReading(0));
                        //Thread.sleep(10);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            t1.start();
            t2.start();
            t1.join();
            t2.join();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
