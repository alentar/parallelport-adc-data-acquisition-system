package org.alentar.parallelportmon.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.alentar.parallelportmon.dialogs.CommonDialogs;
import org.alentar.parallelportmon.dialogs.ConnectionDialog;
import org.alentar.parallelportmon.manager.ResourceManager;
import org.alentar.parallelportmon.stream.StreamManager;
import org.alentar.parallelportmon.tcp.ParaMonClient;

import java.io.IOException;

public class MainController {
    final Image connectedImage = new Image("icons/icons8-connected-100.png");
    public Button btnConnect;
    final Image disconnectedImage = new Image("icons/icons8-disconnected-100.png");
    final Color RED = new Color(1, 0, .1, 1);
    final Color GREEN = new Color(.2, 1, 0, 1);
    public Label lblStatus;

    public TabPane tabPane;
    public ImageView connectButtonImageView;
    public Circle connectionIndicator;



    private boolean isConnected = false;

    @FXML
    private void initialize() {

    }

    private void updateStatusLabel(boolean isConnected) {
        lblStatus.setText(isConnected ? "Connected" : "Disconnected");
        connectionIndicator.setFill(isConnected ? GREEN : RED);
    }

    private void updateConnectButton(boolean isConnected) {
        btnConnect.setText(isConnected ? "Disconnect" : "Connect");
        connectButtonImageView.setImage(isConnected ? disconnectedImage : connectedImage);
    }

    public void connect(ActionEvent actionEvent) {
        if (!isConnected) {
            ConnectionDialog connectionDialog = new ConnectionDialog();
            connectionDialog.showAndWait().ifPresent(connectionData -> {
                try {
                    ParaMonClient paraMonClient = new ParaMonClient(connectionData.getHost(), connectionData.getPort());
                    StreamManager streamManager = new StreamManager(paraMonClient);
                    ResourceManager.getInstance().setStreamManager(streamManager);
                    isConnected = true;
                } catch (Exception ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Connection error");
                    alert.setContentText(String.format("Error connecting to %s:%d", connectionData.getHost(), connectionData.getPort()));
                    alert.showAndWait();
                    isConnected = false;
                }
            });
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you really want to disconnect from data server ?\nAll streams will be halted!", ButtonType.NO, ButtonType.YES);
            alert.setTitle("Disconnect from Data Server");
            alert.showAndWait().ifPresent(buttonType -> {
                if (buttonType == ButtonType.YES) {
                    try {
                        StreamManager streamManager = ResourceManager.getInstance().getStreamManager();
                        if (streamManager != null) {
                            streamManager.close();
                        }
                        isConnected = false;
                    } catch (IOException ex) {
                        CommonDialogs.ExceptionAlert(ex).showAndWait();
                        Platform.exit();
                    }
                }
            });
        }

        updateStatusLabel(isConnected);
        updateConnectButton(isConnected);
    }
}
