package org.alentar.parallelportmon;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.alentar.parallelportmon.manager.ResourceManager;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/scenes/monitor.fxml"));
        primaryStage.setTitle("Parallel Port Data Acquisition System");
        Scene scene = new Scene(root, 900, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
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
