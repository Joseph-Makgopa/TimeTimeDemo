package com.example.demo.controllers;

import com.example.demo.models.State;
import com.example.demo.models.Subject;
import com.example.demo.models.commands.AddSubjectCommand;
import com.example.demo.models.commands.CommandList;
import com.example.demo.models.commands.CommandManager;
import com.example.demo.models.commands.RemoveSubjectCommand;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class SubjectDialogController implements Initializable {
    @FXML
    private TextField txtName;
    @FXML
    private ListView<String> listSubject;
    private CommandList commandList = new CommandList();
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listSubject.getItems().addAll(State.getInstance().subjects.stream().map(subject -> subject.getName()).toList());
    }
    @FXML
    public void textFieldKeyPress(KeyEvent event){
        if (event.getCode() == KeyCode.ENTER) {
            add(null);
            event.consume();
        }
    }
    @FXML
    public void add(ActionEvent event){
        String name = txtName.getText();

        if(name == null || name.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setTitle("Name error");
            alert.setContentText("Subject name is missing.");
            alert.showAndWait();

            return;
        }

        name = name.toUpperCase();
        Subject subject = new Subject(name);

        if(listSubject.getItems().contains(name)){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setTitle("Name error");
            alert.setContentText("Duplicate subject is detected.");
            alert.showAndWait();

            return;
        }

        listSubject.getItems().add(name);
        commandList.add(new AddSubjectCommand(subject));
    }

    @FXML
    public void remove(ActionEvent event){
        String selection =  listSubject.getSelectionModel().getSelectedItem();
        listSubject.getItems().remove(selection);
        System.out.println(selection);
        commandList.add(new RemoveSubjectCommand(new Subject(selection)));
    }
    @FXML
    public void ok(ActionEvent event){
        commandList.execute();
        CommandManager.getInstance().addCommand(commandList);

        Node node = (Node)event.getSource();
        Stage stage = (Stage)node.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void cancel(ActionEvent event){
        Node node = (Node)event.getSource();
        Stage stage = (Stage)node.getScene().getWindow();
        stage.close();
    }
}
