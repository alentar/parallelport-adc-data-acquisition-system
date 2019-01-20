package org.alentar.parallelportmon.tcp;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.Integer.parseInt;

public class ParaMonClient implements Closeable {
    private Socket socket;
    private int port;
    private String  serverAddr;
    PrintWriter writer;
    BufferedReader reader;

    public ParaMonClient(String serverAddr, int port) throws Exception{
        this.serverAddr = serverAddr;
        this.port = port;
        this.socket = new Socket(serverAddr, port);

        this.writer = new PrintWriter(socket.getOutputStream(), true);
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void writeLine(String msg){
        writer.println(msg);
    }

    public String readLine() throws IOException{
        return reader.readLine();
    }

    public int getADCReading(int chan) throws IOException{
        String command = "CHAN" + chan + ";";
        writeLine(command);
        String reply = readLine();

        try {
            String[] tokens = reply.split("=");
            return parseInt(tokens[1]);
        }catch (NumberFormatException ex){
            Logger.getGlobal().log(Level.SEVERE, ex.getMessage());
            return 0;
        }
    }

    public void shutdownServer() throws IOException{
        writeLine(Commands.SHUTDOWN.toString() + ";");
        close();
    }

    @Override
    public void close() throws IOException {
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }
}
