
package com.gui.hotelapp;

import atlantafx.base.theme.PrimerLight;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        Application.setUserAgentStylesheet(
                new PrimerLight().getUserAgentStylesheet()
        );

        FXMLLoader loader = new FXMLLoader(
                HelloApplication.class.getResource("vista.fxml")
        );

        Scene scene = new Scene(loader.load(), 850, 650);

        scene.getStylesheets().add(
                Objects.requireNonNull(HelloApplication.class.getResource("estilos.css")).toExternalForm()
        );

        stage.setTitle("HOTEL APP");
        stage.setMinWidth(700);
        stage.setMinHeight(550);
        stage.setScene(scene);
        stage.show();
    }
}
