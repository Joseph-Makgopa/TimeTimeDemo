package com.example.demo.controllers;

import com.example.demo.DemoApplication;
import com.example.demo.models.commands.CommandManager;
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
    private void showDataDialog(String title, String resource){
        FXMLLoader fxmlLoader = new FXMLLoader(DemoApplication.class.getResource(resource));

        try {
            Parent parent = fxmlLoader.load();

            Scene scene = new Scene(parent);
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.showAndWait();
        }catch(IOException error){
            error.printStackTrace();
        }
    }

    @FXML
    public void showSubjectDialog(ActionEvent event){
        showDataDialog("Subjects", "subject-dialog-view.fxml");
    }

    @FXML
    public void showGradeDialog(ActionEvent event){
        showDataDialog("Grades", "grade-dialog-view.fxml");
    }
    @FXML
    public void showEducatorDialog(ActionEvent event){
        showDataDialog("Educators", "educator-dialog-view.fxml");
    }
    @FXML
    public void showSessionDialog(ActionEvent event){
        showDataDialog("Sessions", "session-dialog-view.fxml");
    }
    @FXML
    public void undo(ActionEvent event){
        CommandManager.getInstance().undo();
    }

    @FXML void redo(ActionEvent event){
        CommandManager.getInstance().redo();
    }

}