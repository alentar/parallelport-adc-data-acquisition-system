package org.alentar.parallelportmon.components;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.alentar.parallelportmon.eventbus.EventBus;
import org.alentar.parallelportmon.eventbus.Subscriber;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GraphViewTab extends Tab {
    private String topic;
    private String title;
    private String seriesName;
    private String xLabel;
    private String yLabel;
    private int windowSize;
    private String xTickPattern;

    private final EventBus eventBus = EventBus.getInstance();
    private Subscriber subscriber;

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

    public GraphViewTab(String topic, String title, String seriesName, String xLabel, String yLabel, String xTickPattern, int windowSize) {
        super(title);

        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(xTickPattern);

        this.topic = topic;
        this.title = title;
        this.xLabel = xLabel;
        this.yLabel = yLabel;
        this.seriesName = seriesName;
        this.xTickPattern = xTickPattern;
        this.windowSize = windowSize;

        vBox = new VBox();
        vBox.setFillWidth(true);
        vBox.setSpacing(1.5);
        xAxis.setLabel(xLabel);
        yAxis.setLabel(yLabel);
        lineChart.setTitle(title);
        series.setName(seriesName);
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

        subscriber = (t, d) -> {
            if (t.equals(this.topic)) {
                int val = (int) d;
                double voltage = (double) val * 4.069 / 4096;
                Date date = new Date();

                Platform.runLater(() -> {
                    series.getData().add(new XYChart.Data<>(simpleDateFormat.format(date), val));
                    if (series.getData().size() > windowSize) series.getData().remove(0);
                });
            }
        };

        // subscribe to channelStream for ui updates
        eventBus.subscribe(this.topic, subscriber);

        setOnCloseRequest(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you really want to close this tab ?", ButtonType.NO, ButtonType.YES);
            alert.setTitle("Confirmation");
            alert.showAndWait().ifPresent(buttonType -> {
                if (buttonType == ButtonType.NO) event.consume();
                else {
                    eventBus.unsubscribe(topic, subscriber);
                    Logger.getGlobal().log(Level.INFO, "Unsubscribed: " + subscriber);
                }

            });
        });
    }

    public String getTitle() {
        return title;
    }

    public String getSeriesName() {
        return seriesName;
    }

    public void setSeriesName(String seriesName) {
        this.seriesName = seriesName;
        series.setName(seriesName);
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
