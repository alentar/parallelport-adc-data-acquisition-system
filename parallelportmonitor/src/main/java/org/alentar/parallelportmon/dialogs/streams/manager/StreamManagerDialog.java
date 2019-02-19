package org.alentar.parallelportmon.dialogs.streams.manager;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.alentar.parallelportmon.dialogs.streams.NewChannelStreamDialog;
import org.alentar.parallelportmon.eventbus.EventBus;
import org.alentar.parallelportmon.eventbus.Subscriber;
import org.alentar.parallelportmon.manager.ResourceManager;
import org.alentar.parallelportmon.stream.ChannelStream;
import org.alentar.parallelportmon.stream.StreamManager;
import org.alentar.parallelportmon.stream.StreamManagerTopics;

public class StreamManagerDialog extends Dialog<Void> {
    public StreamManagerDialog() {
        StreamManager streamManager = ResourceManager.getInstance().getStreamManager();

        VBox vBox = new VBox();
        HBox hBox = new HBox();

        setTitle("Streams Editor");
        setHeaderText("Manage streams currently running");
        ImageView iconImageView = new ImageView(new Image("icons/icons8-icons8-480.png", 64.0, 64.0, true, true));
        setGraphic(iconImageView);

        setResizable(true);
        getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);
        getDialogPane().setPrefSize(600, 400);

        ListView<ChannelStream> streamListView = new ListView<>();
        streamListView.setCellFactory(param -> new StreamCell());
        streamListView.getItems().addAll(streamManager.getChannelStreams());

        Button addStreamButton = new Button("New Stream...");
        addStreamButton.setOnAction(event -> {
            NewChannelStreamDialog newChannelStreamDialog = new NewChannelStreamDialog();
            newChannelStreamDialog.showAndWait().ifPresent(streamManager::scheduleChannelStream);
        });

        hBox.getChildren().add(addStreamButton);
        VBox.setMargin(hBox, new Insets(0, 0, 5, 0));

        vBox.getChildren().addAll(hBox, streamListView);
        getDialogPane().setContent(vBox);

        Subscriber onAddedChannelSub = (topic, data) -> {
            streamListView.getItems().add((ChannelStream) data);
        };

        EventBus.getInstance().subscribe(StreamManagerTopics.onChannelAdded.toString(), onAddedChannelSub);

        setResultConverter(buttonType -> null);

        setOnCloseRequest(event -> {
            EventBus.getInstance().unsubscribe(StreamManagerTopics.onChannelAdded.toString(), onAddedChannelSub);
        });
    }
}
