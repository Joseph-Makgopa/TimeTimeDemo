package com.example.demo;

import com.example.demo.controllers.DemoController;
import com.example.demo.models.*;
import com.example.demo.utilities.Pair;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.*;
import java.util.*;

public class DemoApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(DemoApplication.class.getResource("demo-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Untitled - TimeTable");
        stage.setMaximized(true);
        stage.setScene(scene);
        stage.show();
        ((DemoController)fxmlLoader.getController()).setStage(stage);

        stage.setOnCloseRequest(event -> {
            if(State.getInstance().saveRequired) {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to discard changes?", ButtonType.YES, ButtonType.NO);

                ButtonType result = confirm.showAndWait().orElse(ButtonType.NO);

                if (ButtonType.NO.equals(result)) {
                    event.consume();
                    return;
                }
            }

            State.saveRecent();
        });
    }

    public static void main(String[] args) {
        launch();
    }
}