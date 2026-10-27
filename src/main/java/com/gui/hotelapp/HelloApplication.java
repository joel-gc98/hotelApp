package com.gui.hotelapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("vista.fxml"));
        Scene scene = new Scene(loader.load(), 600, 480);
        stage.setTitle("HOTEL APP");
        stage.setScene(scene);
        stage.show();
    }
}
