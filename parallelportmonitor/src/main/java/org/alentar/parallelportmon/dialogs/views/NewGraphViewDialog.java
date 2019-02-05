package org.alentar.parallelportmon.dialogs.views;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import org.alentar.parallelportmon.components.GraphViewTab;
import org.alentar.parallelportmon.manager.ResourceManager;
import org.alentar.parallelportmon.scripts.TemplateManager;
import org.alentar.parallelportmon.scripts.TemplateScript;
import org.alentar.parallelportmon.stream.ChannelStream;

import java.util.Set;

public class NewGraphViewDialog extends Dialog<GraphViewTab> {
    public NewGraphViewDialog() {
        setTitle("Add a new graph view");
        setHeaderText("A graph view is a visualization of data.\n" +
                "It can be used to monitor a data stream like a channel from ADC system.");

        ButtonType createButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        Image image = new Image("icons/icons8-chart-96.png", 64.0, 64.0, true, true);
        setGraphic(new ImageView(image));

        // Create the username and password labels and fields.
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 10, 10, 10));

        ComboBox<String> topicComboBox = new ComboBox<>();
        Set<ChannelStream> channelStreamSet = ResourceManager.getInstance().getStreamManager().getChannelStreams();
        if (channelStreamSet.size() == 0) topicComboBox.setDisable(true);

        for (ChannelStream stream : channelStreamSet) {
            topicComboBox.getItems().add(stream.getTopic());
        }
        topicComboBox.getSelectionModel().select(0);
        grid.add(new Label("Stream:"), 0, 0);
        grid.add(topicComboBox, 1, 0);
        GridPane.setHgrow(topicComboBox, Priority.ALWAYS);

        TextField titleTextField = new TextField();
        titleTextField.setPromptText("A title which describes data");
        grid.add(new Label("Title:"), 0, 2);
        grid.add(titleTextField, 1, 2);

        TextField seriesNameTextField = new TextField();
        seriesNameTextField.setPromptText("A name for a series");
        grid.add(new Label("Series Name:"), 0, 3);
        grid.add(seriesNameTextField, 1, 3);

        TextField xLabelTextField = new TextField();
        xLabelTextField.setPromptText("Label for X-Axis");
        xLabelTextField.setText("Time/s");
        grid.add(new Label("X-Label:"), 0, 4);
        grid.add(xLabelTextField, 1, 4);

        TextField yLabelTextField = new TextField();
        yLabelTextField.setPromptText("Label for Y-Axis");
        yLabelTextField.setText("Voltage/V");
        grid.add(new Label("Y-Label:"), 0, 5);
        grid.add(yLabelTextField, 1, 5);

        TextField xTickFormatTextField = new TextField();
        xTickFormatTextField.setPromptText("Customize X-Axis(JAVA date formats are supported) default: HH:mm:ss");
        xTickFormatTextField.setText("HH:mm:ss");
        grid.add(new Label("Ticking format:"), 0, 6);
        grid.add(xTickFormatTextField, 1, 6);

        Spinner<Integer> windowSpinner = new Spinner<>();
        SpinnerValueFactory<Integer> spinnerValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE, 20);
        windowSpinner.setValueFactory(spinnerValueFactory);
        grid.add(new Label("Window Size(Data Points):"), 0, 7);
        grid.add(windowSpinner, 1, 7);

        TextArea customScript = new TextArea();
        customScript.setPromptText("Plot y-axis using a custom merit.\n You can use common math functions to customize your y-axis.\n" +
                "Example: y = log10(v) - 1;\n" +
                "The language used here is javascript");
        grid.add(new Label("Customized Y-Axis"), 0, 8);
        grid.add(customScript, 1, 8);

        getDialogPane().setContent(grid);
        Platform.runLater(topicComboBox::requestFocus);

        setResultConverter(buttonType -> {
            if (buttonType == createButtonType) {
                String topic = topicComboBox.getSelectionModel().getSelectedItem();
                if (topic == null || topic.isEmpty()) return null;

                String title = titleTextField.getText().trim().isEmpty() ? "Chart" : titleTextField.getText();
                String seriesName = seriesNameTextField.getText().trim().isEmpty() ? "Series" : seriesNameTextField.getText();
                String xLabel = xLabelTextField.getText();
                String yLabel = yLabelTextField.getText();
                String xTickFormat = xTickFormatTextField.getText().trim().isEmpty() ? "HH:mm:ss" : xTickFormatTextField.getText().trim();
                int windowSize = windowSpinner.getValue();
                String scriptText = customScript.getText();
                TemplateScript templateScript = TemplateManager.getInstance().getTemplateScript("y_val");
                templateScript.inject("__fn__block__injectable__", scriptText);
                return new GraphViewTab(topic, title, seriesName, xLabel, yLabel, xTickFormat, windowSize, templateScript);
            }

            return null;
        });
    }
}
