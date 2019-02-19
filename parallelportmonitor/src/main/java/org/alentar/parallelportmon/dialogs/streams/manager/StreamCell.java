package org.alentar.parallelportmon.dialogs.streams.manager;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import org.alentar.parallelportmon.manager.ResourceManager;
import org.alentar.parallelportmon.stream.ChannelStream;
import org.alentar.parallelportmon.stream.StreamState;
import org.controlsfx.control.ToggleSwitch;

public class StreamCell extends ListCell<ChannelStream> {
    private HBox hBox;
    private Label label;
    private ToggleSwitch toggleStream;

    public StreamCell() {
        super();
        hBox = new HBox();
        Pane pane = new Pane();
        label = new Label();
        toggleStream = new ToggleSwitch();

        ImageView deleteImageView = new ImageView(new Image("icons/icons8-delete-bin-96.png", 24, 24, true, true));
        deleteImageView.setPreserveRatio(true);
        deleteImageView.setFitHeight(24);
        deleteImageView.setFitWidth(24);
        Button removeButton = new Button("", deleteImageView);
        removeButton.setPadding(Insets.EMPTY);

        hBox.getChildren().addAll(label, pane, toggleStream, removeButton);
        HBox.setHgrow(pane, Priority.ALWAYS);
        HBox.setMargin(removeButton, new Insets(5, 5, 5, 5));
        HBox.setMargin(toggleStream, new Insets(5, 5, 5, 5));
        HBox.setMargin(label, new Insets(5, 5, 5, 5));

        toggleStream.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) getItem().setState(StreamState.RUNNING);
            else getItem().setState(StreamState.PAUSED);
        });

        removeButton.setOnAction(event -> {
            ResourceManager.getInstance().getStreamManager().removeChannelStream(getItem());
            getListView().getItems().remove(getItem());
        });
    }

    @Override
    protected void updateItem(ChannelStream item, boolean empty) {
        super.updateItem(item, empty);
        setText(null);

        if (empty) {
            setGraphic(null);
        } else {
            label.setText(item.getTopic());

            if (item.getState() == StreamState.RUNNING) toggleStream.selectedProperty().setValue(true);
            else toggleStream.selectedProperty().setValue(false);

            setGraphic(hBox);
        }
    }
}
