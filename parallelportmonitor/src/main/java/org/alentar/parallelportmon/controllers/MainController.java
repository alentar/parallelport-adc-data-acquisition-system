package org.alentar.parallelportmon.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import org.alentar.parallelportmon.components.GraphTab;

public class MainController {
    public Button btnConnect;
    public Label lblStatus;
    public TabPane tabPane;

    private boolean isConnected = false;

    @FXML
    private void initialize(){
        updateStatus(isConnected);
        tabPane.getTabs().add(new GraphTab("Sensor 1","Sensor1 reading", "Time", "Voltage"));
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
    }
}
