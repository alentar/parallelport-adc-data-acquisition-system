package org.alentar.parallelportmon.dialogs.connection;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class ConnectionDialog extends Dialog<ConnectionData> {
    public ConnectionDialog() {
        setTitle("Open new connection");
        setHeaderText("Connect to Alentar Parallel Port Data Server");

        ButtonType connectButtonType = new ButtonType("Connect", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(connectButtonType, ButtonType.CANCEL);

        Image image = new Image("icons/icons8-connected-100.png", 64.0, 64.0, true, true);
        setGraphic(new ImageView(image));

        // Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 10, 10, 10));
        grid.setAlignment(Pos.CENTER);

        TextField host = new TextField();
        host.setPromptText("Set host name (default: 127.0.0.1)");
        host.setText("127.0.0.1");
        TextField port = new TextField();
        port.setPromptText("Port for connection (2335 will be used if empty)");
        port.setText("2335");

        grid.add(new Label("Host:"), 0, 0);
        grid.add(host, 1, 0);
        GridPane.setHgrow(host, Priority.ALWAYS);
        grid.add(new Label("Port:"), 0, 1);
        grid.add(port, 1, 1);


        getDialogPane().setContent(grid);
        Platform.runLater(host::requestFocus);

        setResultConverter(buttonType -> {
            if (buttonType == connectButtonType) {
                String hostData = "127.0.0.1";
                int portData = 2335;

                if (!host.getText().trim().isEmpty()) hostData = host.getText();

                try {
                    if (!port.getText().trim().isEmpty())
                        portData = Integer.parseInt(port.getText().trim());
                } catch (NumberFormatException ex) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Invalid port value");
                    alert.setContentText("Port should be an integer");
                    alert.showAndWait();
                    return null;
                }

                return new ConnectionData(hostData, portData);
            }

            return null;
        });
    }
}
