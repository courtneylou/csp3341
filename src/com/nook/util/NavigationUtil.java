package com.nook.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class NavigationUtil {

    private static Stage primaryStage;

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static void navigateTo(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(NavigationUtil.class.getResource(fxmlPath));
            Pane root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(NavigationUtil.class.getResource("/com/nook/styles/main.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            System.out.println("Navigation error: " + e.getMessage());
        }
    }

    public static FXMLLoader getLoader(String fxmlPath) {
        return new FXMLLoader(NavigationUtil.class.getResource(fxmlPath));
    }
}