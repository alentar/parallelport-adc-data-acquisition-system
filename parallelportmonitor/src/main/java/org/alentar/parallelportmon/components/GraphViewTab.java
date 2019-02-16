package org.alentar.parallelportmon.components;

import javafx.application.Platform;
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
import org.alentar.parallelportmon.adc.ADC;
import org.alentar.parallelportmon.dialogs.CommonDialogs;
import org.alentar.parallelportmon.eventbus.EventBus;
import org.alentar.parallelportmon.eventbus.Subscriber;
import org.alentar.parallelportmon.manager.ResourceManager;
import org.alentar.parallelportmon.scripts.ScriptManager;
import org.alentar.parallelportmon.scripts.TemplateScript;

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
    private boolean showAverage;

    private ADC adc;

    private final EventBus eventBus = EventBus.getInstance();
    private Subscriber subscriber;

    private final CategoryAxis xAxis = new CategoryAxis();
    private final NumberAxis yAxis = new NumberAxis();
    private final LineChart<String , Number> lineChart = new LineChart<>(xAxis, yAxis);
    private XYChart.Series<String, Number> series = new XYChart.Series<>();

    // average line
    private XYChart.Series<String, Number> avgSeries = new XYChart.Series<>();

    private VBox vBox;

    private Label minLabel;
    private Label maxLabel;
    private Label averageLabel;
    private HBox hBar;
    private Button buttonOptions;

    // templates
    private TemplateScript templateScript;

    // moving average
    private boolean init = true;
    private long numPoints;
    private double cumulativeSum;
    private double min;
    private double max;
    private double average;

    public GraphViewTab(String topic, String title, String seriesName, String xLabel, String yLabel, String xTickPattern,
                        int windowSize, TemplateScript templateScript, boolean showAverage) {
        super(title);

        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(xTickPattern);

        this.topic = topic;
        this.title = title;
        this.xLabel = xLabel;
        this.yLabel = yLabel;
        this.seriesName = seriesName;
        this.xTickPattern = xTickPattern;
        this.windowSize = windowSize;
        this.templateScript = templateScript;
        this.showAverage = showAverage;

        adc = ResourceManager.getInstance().getAdc();

        vBox = new VBox();
        vBox.setFillWidth(true);
        vBox.setSpacing(1.5);

        // options button
        buttonOptions = new Button("Options");

        xAxis.setLabel(xLabel);
        yAxis.setLabel(yLabel);
        lineChart.setTitle(title);
        lineChart.setAnimated(false);

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

        if (showAverage) initializeAverageLine();

        VBox.setVgrow(lineChart, Priority.ALWAYS);
        vBox.getChildren().addAll(lineChart, hBar);
        super.setContent(vBox);

        setTemplateScript(templateScript);

        subscriber = (t, d) -> {
            if (t.equals(this.topic)) {
                int val = (int) d;
                Date date = new Date();
                Number value;

                try {
                    value = (Number) ScriptManager.getInstance().invokeMethod(this, "y", val, adc.getInternalReferenceVoltage(), adc.getResolution());
                    Platform.runLater(() -> {
                        updateStats((Double) value);

                        XYChart.Data<String, Number> rawValue = new XYChart.Data<>(simpleDateFormat.format(date), value);
                        XYChart.Data<String, Number> averageValue = new XYChart.Data<>(simpleDateFormat.format(date), average);

                        series.getData().add(rawValue);
                        avgSeries.getData().add(averageValue);
                        updateChartWindow(series);
                        updateChartWindow(avgSeries);
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
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

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getxLabel() {
        return xLabel;
    }

    public void setxLabel(String xLabel) {
        this.xLabel = xLabel;
    }

    public String getyLabel() {
        return yLabel;
    }

    public void setyLabel(String yLabel) {
        this.yLabel = yLabel;
    }

    public int getWindowSize() {
        return windowSize;
    }

    public void setWindowSize(int windowSize) {
        this.windowSize = windowSize;
    }

    public boolean isShowAverage() {
        return showAverage;
    }

    public void setShowAverage(boolean showAverage) {
        this.showAverage = showAverage;
    }

    public TemplateScript getTemplateScript() {
        return templateScript;
    }

    public void setTemplateScript(TemplateScript templateScript) {
        this.templateScript = templateScript;

        try {
            ScriptManager.getInstance().registerMethod(this, "y", templateScript.getScript());
        } catch (Exception e) {
            CommonDialogs.ExceptionAlert(e).showAndWait();
        }
    }

    private void updateStats(double val) {
        if (!init) {
            this.min = Math.min(this.min, val);
            this.max = Math.max(this.max, val);
            this.average = getSeriesAverage(val);
        } else {
            this.min = this.max = val;
            init = false;
        }

        updateLabels();
    }

    private void updateLabels() {
        updateMinLabel();
        updateMaxLabel();
        updateAverageLabel();
    }

    private void updateMinLabel() {
        minLabel.setText(String.format("Min: %.3f", min));
    }

    private void updateMaxLabel() {
        maxLabel.setText(String.format("Max: %.3f", max));
    }

    private void updateAverageLabel() {
        averageLabel.setText(String.format("Average: %.3f", average));
    }

    private double getSeriesAverage(double newPoint) {
        cumulativeSum += newPoint;
        ++numPoints;
        return cumulativeSum/numPoints;
    }

    private void updateChartWindow(XYChart.Series series) {
        if (series.getData().size() > windowSize) series.getData().remove(0);
    }

    public void initializeAverageLine() {
        avgSeries.getData().clear();
        avgSeries.setName("Average");
        lineChart.getData().add(avgSeries);
        showAverage = true;
    }

    public void hideAverageLine() {
        showAverage = false;
        lineChart.getData().remove(avgSeries);
    }
}
