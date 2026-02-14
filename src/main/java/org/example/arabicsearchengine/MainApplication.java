package org.example.arabicsearchengine;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Load main view
        FXMLLoader loader = new FXMLLoader(getClass().getResource("views/MainView.fxml"));
        Parent root = loader.load();

        // Create scene with CSS
        Scene scene = new Scene(root, 1000, 700);
        scene.getStylesheets().add(getClass().getResource("css/application.css").toExternalForm());

        // Configure stage
        stage.setTitle("محرك البحث الصرفي العربي - Arabic Morphological Search Engine");
        stage.setScene(scene);
        stage.setMinWidth(800);
        stage.setMinHeight(600);

        // Show
        stage.show();
    }
}
