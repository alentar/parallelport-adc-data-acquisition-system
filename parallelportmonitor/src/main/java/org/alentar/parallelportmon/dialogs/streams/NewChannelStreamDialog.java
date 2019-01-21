package org.alentar.parallelportmon.dialogs.streams;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import org.alentar.parallelportmon.adc.ADC;
import org.alentar.parallelportmon.manager.ResourceManager;
import org.alentar.parallelportmon.stream.ChannelStream;

import java.util.concurrent.TimeUnit;

public class NewChannelStreamDialog extends Dialog<ChannelStream> {
    public NewChannelStreamDialog() {
        setTitle("Create a channel stream");
        setHeaderText("Channel streams allows you to acquire data from ADC channels\nYou can later create listeners for this channel");

        ButtonType createButtonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        Image image = new Image("icons/icons8-artificial-intelligence-96.png", 64.0, 64.0, true, true);
        setGraphic(new ImageView(image));


        // Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 10, 10, 10));

        ComboBox<String> channelComboBox = new ComboBox<>();
        ADC adc = ResourceManager.getInstance().getAdc();
        for (int i = 0; i < adc.getChannels(); i++) {
            channelComboBox.getItems().add("Channel " + i);
        }

        channelComboBox.getSelectionModel().select(0);
        grid.add(new Label("Channel:"), 0, 0);
        grid.add(channelComboBox, 1, 0);
        GridPane.setHgrow(channelComboBox, Priority.ALWAYS);

        TextField nameTextField = new TextField();
        nameTextField.setPromptText("A friendly name for stream");
        grid.add(new Label("Name:"), 0, 1);
        grid.add(nameTextField, 1, 1);

        TextField updateIntervalTextField = new TextField();
        updateIntervalTextField.setText("1");
        updateIntervalTextField.setPromptText("Update interval(eg: 10)");
        grid.add(new Label("Update Interval:"), 0, 2);
        grid.add(updateIntervalTextField, 1, 2);

        ComboBox<String> timeUnitComboBox = new ComboBox<>();
        timeUnitComboBox.getItems().addAll("Second", "Minute", "Hour", "Day");
        timeUnitComboBox.getSelectionModel().select(0);
        grid.add(new Label("Update per:"), 0, 3);
        grid.add(timeUnitComboBox, 1, 3);

        getDialogPane().setContent(grid);
        Platform.runLater(channelComboBox::requestFocus);

        setResultConverter(buttonType -> {
            if (buttonType == createButtonType) {
                try {
                    int updateInterval = Integer.parseInt(updateIntervalTextField.getText().trim());
                    int chan = channelComboBox.getSelectionModel().getSelectedIndex();
                    String name = nameTextField.getText();
                    TimeUnit timeUnit;
                    switch (timeUnitComboBox.getSelectionModel().getSelectedIndex()) {
                        case 0:
                            timeUnit = TimeUnit.SECONDS;
                            break;
                        case 1:
                            timeUnit = TimeUnit.MINUTES;
                            break;
                        case 2:
                            timeUnit = TimeUnit.HOURS;
                            break;
                        case 3:
                            timeUnit = TimeUnit.DAYS;
                            break;
                        default:
                            timeUnit = TimeUnit.SECONDS;
                            break;
                    }

                    // STOPSHIP: 2019-01-21 TODO add delays
                    return new ChannelStream(chan, name, timeUnit, updateInterval, 0);
                } catch (NumberFormatException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Invalid update interval");
                    alert.setContentText("Update interval should be an integer");
                    alert.showAndWait();
                    return null;
                }
            }

            return null;
        });
    }
}
