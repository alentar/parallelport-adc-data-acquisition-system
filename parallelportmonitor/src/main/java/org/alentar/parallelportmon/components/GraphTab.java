package org.alentar.parallelportmon.components;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class GraphTab extends Tab {
    private String title;
    private String name;
    private String xLabel;
    private String yLabel;

    private final CategoryAxis xAxis = new CategoryAxis();
    private final NumberAxis yAxis = new NumberAxis();
    private final LineChart<String , Number> lineChart = new LineChart<>(xAxis, yAxis);
    XYChart.Series<String, Number> series = new XYChart.Series<>();

    private VBox vBox;

    private Label minLabel;
    private Label maxLabel;
    private Label averageLabel;
    private HBox hBar;

    // moving average
    long numPoints;
    double cumulativeSum;

    public GraphTab(String title, String name, String xLabel, String yLabel) {
        super(title);

        this.title = title;
        this.xLabel = xLabel;
        this.yLabel = yLabel;
        this.name = name;

        vBox = new VBox();
        vBox.setFillWidth(true);
        vBox.setSpacing(1.5);
        xAxis.setLabel(xLabel);
        yAxis.setLabel(yLabel);
        lineChart.setTitle(title);
        series.setName(name);
        lineChart.getData().add(series);

        //setup labels
        minLabel = new Label("Min: N/A");
        maxLabel = new Label("Max: N/A");
        averageLabel = new Label("Average: N/A");
        hBar = new HBox();
        hBar.setAlignment(Pos.BOTTOM_CENTER);

        hBar.setFillHeight(false);
        minLabel.setPadding(new Insets(5, 20, 5, 20));
        maxLabel.setPadding(new Insets(5, 20, 5, 20));
        averageLabel.setPadding(new Insets(5, 20, 5, 20));
        hBar.getChildren().addAll(minLabel, maxLabel, averageLabel);

        xAxis.setAnimated(false);
        yAxis.setAnimated(false);

        VBox.setVgrow(lineChart, Priority.ALWAYS);
        vBox.getChildren().addAll(lineChart, hBar);
        super.setContent(vBox);

        setOnCloseRequest(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setContentText("Do you really want to close this tab ?");
            alert.showAndWait().ifPresent(buttonType -> {
                if (buttonType == ButtonType.CANCEL) event.consume();
            });
        });
    }

    public String getTitle() {
        return title;
    }

    public void setName(String name){
        this.name = name;
        series.setName(name);
    }

    public String getName() {
        return name;
    }

    public void setTitle(String title) {
        this.title = title;
        lineChart.setTitle(title);
    }

    public String getXLabel() {
        return xLabel;
    }

    public void setXLabel(String xLabel) {
        this.xLabel = xLabel;
        xAxis.setLabel(xLabel);
    }

    public String getYLabel() {
        return yLabel;
    }

    public void setYLabel(String yLabel) {
        this.yLabel = yLabel;
        yAxis.setLabel(yLabel);
    }

    public void updateMin(double min){
        minLabel.setText(String.format("Min: %.3f", min));
    }

    public void updateMax(double max){
        maxLabel.setText(String.format("Max: %.3f", max));
    }

    public void updateAverage(double average){
        averageLabel.setText(String.format("Average: %.3f", average));
    }

    public double getSeriesAverage(double newPoint){
        cumulativeSum += newPoint;
        ++numPoints;
        return cumulativeSum/numPoints;
    }
}
