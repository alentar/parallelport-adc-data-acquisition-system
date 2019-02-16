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
import org.alentar.parallelportmon.dialogs.connection.ConnectionDialog;
import org.alentar.parallelportmon.dialogs.streams.NewChannelStreamDialog;
import org.alentar.parallelportmon.dialogs.views.NewGraphViewDialog;
import org.alentar.parallelportmon.manager.ResourceManager;
import org.alentar.parallelportmon.stream.StreamManager;
import org.alentar.parallelportmon.tcp.DataServerClient;

public class MainController {
    private final Image connectedImage = new Image("icons/icons8-connected-100.png");
    private final Image disconnectedImage = new Image("icons/icons8-disconnected-100.png");
    private final Color RED = new Color(1, 0, .1, 1);
    private final Color GREEN = new Color(.2, 1, 0, 1);

    public Label lblStatus;
    public Button btnConnect;
    public TabPane tabPane;
    public ImageView connectButtonImageView;
    public Circle connectionIndicator;
    public Button addStreamButton;
    public Button addGraphViewButton;

    private boolean isConnected = false;

    @FXML
    private void initialize() {
        updateIsConnected(isConnected);
    }

    private void updateIsConnected(boolean isConnected) {
        this.isConnected = isConnected;
        updateStatusLabel(isConnected);
        updateConnectButton(isConnected);
        updateAddStreamButton(isConnected);
        updateAddGraphViewButton(isConnected);
    }

    private void updateStatusLabel(boolean isConnected) {
        lblStatus.setText(isConnected ? "Connected" : "Disconnected");
        connectionIndicator.setFill(isConnected ? GREEN : RED);
    }

    private void updateConnectButton(boolean isConnected) {
        btnConnect.setText(isConnected ? "Disconnect" : "Connect");
        connectButtonImageView.setImage(isConnected ? disconnectedImage : connectedImage);
    }

    private void updateAddStreamButton(boolean isConnected) {
        addStreamButton.setDisable(!isConnected);
    }

    private void updateAddGraphViewButton(boolean isConnected) {
        addGraphViewButton.setDisable(!isConnected);
    }

    public void connect(ActionEvent actionEvent) {
        if (!isConnected) {
            ConnectionDialog connectionDialog = new ConnectionDialog();
            connectionDialog.showAndWait().ifPresent(connectionData -> {
                try {
                    DataServerClient dataServerClient = new DataServerClient(connectionData.getHost(), connectionData.getPort());
                    StreamManager streamManager = new StreamManager(dataServerClient);
                    ResourceManager.getInstance().registerStreamManager(streamManager);
                    updateIsConnected(true);
                } catch (Exception ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Connection error");
                    alert.setContentText(String.format("Error connecting to %s:%d", connectionData.getHost(), connectionData.getPort()));
                    alert.showAndWait();
                    updateIsConnected(false);
                }
            });
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you really want to disconnect from data server ?\nAll streams will be halted!", ButtonType.NO, ButtonType.YES);
            alert.setTitle("Disconnect from Data Server");
            alert.showAndWait().ifPresent(buttonType -> {
                if (buttonType == ButtonType.YES) {
                    try {
                        ResourceManager.getInstance().shutdownStreamManager();
                        updateIsConnected(false);
                    } catch (Exception ex) {
                        updateIsConnected(false);
                        CommonDialogs.ExceptionAlert(ex).showAndWait();
                        Platform.exit();
                    }
                }
            });
        }
    }

    public void openAddNewStreamDialog(ActionEvent actionEvent) {
        NewChannelStreamDialog newChannelStreamDialog = new NewChannelStreamDialog();
        newChannelStreamDialog.showAndWait().ifPresent(channelStream -> {
            StreamManager streamManager = ResourceManager.getInstance().getStreamManager();
            if (streamManager != null)
                streamManager.scheduleChannelStream(channelStream);
        });
    }

    public void openAddNewGraphViewDialog(ActionEvent actionEvent) {
        NewGraphViewDialog newGraphViewDialog = new NewGraphViewDialog();
        newGraphViewDialog.showAndWait().ifPresent(graphViewTab -> {
            tabPane.getTabs().add(graphViewTab);
            tabPane.getSelectionModel().select(graphViewTab);
        });
    }
}
