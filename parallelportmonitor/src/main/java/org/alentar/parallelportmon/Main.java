package org.alentar.parallelportmon;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import org.alentar.parallelportmon.adc.MAX186ADC;
import org.alentar.parallelportmon.dialogs.CommonDialogs;
import org.alentar.parallelportmon.manager.ResourceManager;
import org.alentar.parallelportmon.scripts.ScriptManager;
import org.alentar.parallelportmon.scripts.TemplateManager;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/scenes/monitor.fxml"));
        primaryStage.setTitle("Parallel Port Data Acquisition System");
        Scene scene = new Scene(root, 900, 500);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Load templates
        try {
            ScriptManager.getInstance();
            TemplateManager.getInstance().loadScriptFromResources("y_val");
        } catch (IOException ex) {
            CommonDialogs.ExceptionAlert(ex).showAndWait();
        }

        primaryStage.setOnCloseRequest(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure do you want to exit?", ButtonType.YES, ButtonType.NO);
            alert.showAndWait().ifPresent(buttonType -> {
                if (buttonType == ButtonType.NO) {
                    event.consume();
                }
            });
        });

        // setup ADC to MAX186
        ResourceManager.getInstance().setAdc(new MAX186ADC());
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        ResourceManager.getInstance().dispose();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
