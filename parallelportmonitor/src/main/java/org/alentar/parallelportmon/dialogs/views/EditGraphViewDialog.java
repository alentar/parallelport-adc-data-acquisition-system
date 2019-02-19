package org.alentar.parallelportmon.dialogs.views;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import org.alentar.parallelportmon.components.GraphViewTab;

public class EditGraphViewDialog extends Dialog<Void> {
    public EditGraphViewDialog(GraphViewTab graphViewTab) {
        setTitle("Edit graph view");
        setHeaderText("Edit the graph view");

        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        Image image = new Image("icons/icons8-compose-100.png", 64.0, 64.0, true, true);
        setGraphic(new ImageView(image));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 10, 10, 10));

        TextField titleTextField = new TextField();
        titleTextField.setPromptText("A title which describes data");
        titleTextField.setText(graphViewTab.getTitle());
        grid.add(new Label("Title:"), 0, 0);
        grid.add(titleTextField, 1, 0);

        TextField seriesNameTextField = new TextField();
        seriesNameTextField.setPromptText("A name for a series");
        seriesNameTextField.setText(graphViewTab.getSeriesName());
        grid.add(new Label("Series Name:"), 0, 1);
        grid.add(seriesNameTextField, 1, 1);

        TextField xLabelTextField = new TextField();
        xLabelTextField.setPromptText("Label for X-Axis");
        xLabelTextField.setText(graphViewTab.getXLabel());
        grid.add(new Label("X-Label:"), 0, 2);
        grid.add(xLabelTextField, 1, 2);

        TextField yLabelTextField = new TextField();
        yLabelTextField.setPromptText("Label for Y-Axis");
        yLabelTextField.setText(graphViewTab.getYLabel());
        grid.add(new Label("Y-Label:"), 0, 3);
        grid.add(yLabelTextField, 1, 3);


        CheckBox showAverage = new CheckBox("Show average");
        showAverage.setSelected(graphViewTab.isShowAverage());
        grid.add(showAverage, 1, 4);

        getDialogPane().setContent(grid);

        setResultConverter(buttonType -> {
            if (buttonType == okButtonType) {
                String title = titleTextField.getText();
                String seriesName = seriesNameTextField.getText();
                String xLabel = xLabelTextField.getText();
                String yLabel = yLabelTextField.getText();
                boolean showAverageLine = showAverage.isSelected();

                if (!title.equals(graphViewTab.getTitle())) graphViewTab.setTitle(title);
                if (!seriesName.equals(graphViewTab.getSeriesName())) graphViewTab.setSeriesName(seriesName);
                if (!xLabel.equals(graphViewTab.getXLabel())) graphViewTab.setXLabel(xLabel);
                if (!yLabel.equals(graphViewTab.getYLabel())) graphViewTab.setYLabel(yLabel);
                graphViewTab.setShowAverage(showAverageLine);
            }

            return null;
        });
    }
}
