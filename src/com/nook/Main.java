package com.nook;

import com.nook.util.DatabaseUtil;
import com.nook.util.NavigationUtil;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        DatabaseUtil.initializeDatabase();
        NavigationUtil.setPrimaryStage(stage);
        stage.setTitle("Nook");
        stage.setWidth(1100);
        stage.setHeight(750);
        stage.setResizable(true);
        NavigationUtil.navigateTo("/com/nook/views/login.fxml");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}