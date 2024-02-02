package com.example.demo.controllers;

import com.example.demo.DemoApplication;
import com.example.demo.models.State;
import com.example.demo.models.commands.CommandManager;
import com.example.demo.utilities.Notification;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;

public class DemoController {
    private Stage stage;
    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
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

    @FXML
    public void redo(ActionEvent event){
        CommandManager.getInstance().redo();
    }
    @FXML
    public void createFile(ActionEvent event){
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

        }
    }
    @FXML
    public void openFile(ActionEvent event){
        if(State.getInstance().saveRequired){
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to discard changes?", ButtonType.YES, ButtonType.NO);
            ButtonType result = confirm.showAndWait().orElse(ButtonType.NO);

            if(ButtonType.NO.equals(result))
                return;
        }

        FileChooser fileChooser = new FileChooser();

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TimeTable files (*.tt)", "*.tt");
        fileChooser.getExtensionFilters().add(extFilter);

        File file = fileChooser.showOpenDialog(stage);

        if(file != null){
            State.getInstance().setFields(file);
            stage.setTitle(file.getName() + " - TimeTable");
        }
    }
    @FXML
    public void saveFile(ActionEvent event){
        if(State.getInstance().filename == "Untitled"){
            saveAsFile(event);
        }else{
            State.getInstance().saveFields(new File(State.getInstance().filepath));
        }
    }
    @FXML
    public void saveAsFile(ActionEvent event){
        FileChooser fileChooser = new FileChooser();

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TimeTable files (*.tt)", "*.tt");
        fileChooser.getExtensionFilters().add(extFilter);

        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            State.getInstance().saveFields(file);
            stage.setTitle(file.getName() + " - TimeTable");
        }
    }
    @FXML
    public void close(ActionEvent event){
        stage.close();
    }

}