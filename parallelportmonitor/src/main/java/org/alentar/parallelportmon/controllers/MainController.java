package org.alentar.parallelportmon.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import org.alentar.parallelportmon.components.GraphTab;
import org.alentar.parallelportmon.manager.ResourceManager;
import org.alentar.parallelportmon.stream.StreamManager;
import org.alentar.parallelportmon.tcp.ParaMonClient;

import java.util.concurrent.TimeUnit;

public class MainController {
    public Button btnConnect;
    public Label lblStatus;
    public TabPane tabPane;
    StreamManager streamManager;

    private boolean isConnected = false;

    @FXML
    private void initialize(){
        updateStatus(isConnected);
        tabPane.getTabs().add(new GraphTab(0, "Channel 0", "Sensor 0", "Time/s", "Voltage/v"));
        tabPane.getTabs().add(new GraphTab(1, "Channel 1", "Sensor 1", "Time/s", "Voltage/v"));
        tabPane.getTabs().add(new GraphTab(3, "Channel 3", "Sensor 3", "Time/s", "Voltage/v"));

        try {
            ParaMonClient paraMonClient = new ParaMonClient("127.0.0.1", 2335);
            streamManager = new StreamManager(paraMonClient);
            ResourceManager.getInstance().setStreamManager(streamManager);

            streamManager.scheduleChannelStream(0, 1, 1, TimeUnit.SECONDS);
            streamManager.scheduleChannelStream(1, 2, 5, TimeUnit.SECONDS);
            streamManager.scheduleChannelStream(3, 2, 5, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void updateStatus(boolean isConnected){
        String txt = (isConnected ? "Connected" : "Disconnected");
        lblStatus.setText(txt);
    }

    public void connect(ActionEvent actionEvent) {
        isConnected = !isConnected;
        String txt = (isConnected? "Disconnect" : "Connect");
        updateStatus(isConnected);
        btnConnect.setText(txt);
        streamManager.cancelChannelStream(0);
    }


}
