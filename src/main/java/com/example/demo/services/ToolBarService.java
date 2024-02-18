package com.example.demo.services;

import com.example.demo.DemoApplication;
import com.example.demo.models.State;
import com.example.demo.utilities.Notification;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;

public class ToolBarService {
    public void showDataDialog(String title, String resource){
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

    public void createFile(Stage stage){
        if(State.getInstance().saveRequired){
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to discard changes?", ButtonType.YES, ButtonType.NO);
            ButtonType result = confirm.showAndWait().orElse(ButtonType.NO);

            if(ButtonType.NO.equals(result))
                return;
        }

        TextInputDialog fileNameDialog = new TextInputDialog();
        fileNameDialog.setContentText("Name: ");
        fileNameDialog.setTitle("New File");
        fileNameDialog.setHeaderText(null);

        try {
            String filename = fileNameDialog.showAndWait().get();

            if (filename == null || filename.equals("")) {
                Notification.show("New File Error", "File name is missing.", Alert.AlertType.ERROR);
                return;
            }

            stage.setTitle(filename + " - TimeTable");

            State.getInstance().reset();
            State.getInstance().filename = filename;
            State.getInstance().saveRequired = true;
        }catch(NoSuchElementException error){
            error.printStackTrace();
        }
    }

    public File openFile(Stage stage){
        if(State.getInstance().saveRequired){
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to discard changes?", ButtonType.YES, ButtonType.NO);
            ButtonType result = confirm.showAndWait().orElse(ButtonType.NO);

            if(ButtonType.NO.equals(result))
                return null;
        }

        FileChooser fileChooser = new FileChooser();

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TimeTable files (*.tt)", "*.tt");
        fileChooser.getExtensionFilters().add(extFilter);

        File file = fileChooser.showOpenDialog(stage);

        return file;
    }
}
