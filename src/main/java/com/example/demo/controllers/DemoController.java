package com.example.demo.controllers;

import com.example.demo.DemoApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class DemoController {

    @FXML
    public void showSubjectDialog(ActionEvent event){
        FXMLLoader fxmlLoader = new FXMLLoader(DemoApplication.class.getResource("subject-dialog-view.fxml"));

        try {
            Parent parent = fxmlLoader.load();
            SubjectDialogController dialogController = fxmlLoader.getController();

            Scene scene = new Scene(parent);
            Stage stage = new Stage();
            stage.setTitle("Subjects");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.showAndWait();
        }catch(IOException error){
            error.printStackTrace();
        }
    }
}