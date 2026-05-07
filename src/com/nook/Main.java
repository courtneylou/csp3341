package com.nook;

import com.nook.util.DatabaseUtil;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        DatabaseUtil.initializeDatabase();
        stage.setTitle("Nook");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}