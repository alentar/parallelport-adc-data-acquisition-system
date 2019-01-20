package org.alentar.parallelportmon.tcp;

import org.junit.Test;

public class TCPTest {
    @Test
    public void connectsToServer(){
        try {
            System.out.println(1 << 12);
            ParaMonClient client = new ParaMonClient("127.0.0.1", 2335);
            int reply = client.getADCReading(0);
            System.out.println(reply);
            client.close();


        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
