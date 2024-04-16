package com.example.demo.controllers;

import com.example.demo.models.State;
import com.example.demo.models.Subject;
import com.example.demo.models.commands.*;
import com.example.demo.services.DemoService;
import com.example.demo.utilities.Notification;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class SubjectDialogController extends BaseDataDialogController implements Initializable{
    @FXML
    private TextField txtName;
    @FXML
    private ListView<String> listSubject;
    @FXML
    private Button btnOk;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listSubject.getItems().addAll(State.getInstance().subjects.stream().map(subject -> subject.getName()).toList());
        btnOk.setDisable(true);
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
            Notification.show("Name error", "Subject name is missing.", Alert.AlertType.ERROR);
            return;
        }

        name = name.toUpperCase();
        Subject subject = new Subject(name);

        if(listSubject.getItems().contains(name)){
            Notification.show("Name error", "Duplicate subject is detected.", Alert.AlertType.ERROR);

            return;
        }

        listSubject.getItems().add(name);
        listSubject.getSelectionModel().select(name);
        listSubject.scrollTo(name);
        commandList.add(new AddSubjectCommand(subject));
        txtName.clear();
        btnOk.setDisable(false);
    }
    @FXML
    public void remove(ActionEvent event){
        String selection =  listSubject.getSelectionModel().getSelectedItem();
        listSubject.getItems().remove(selection);
        commandList.add(new RemoveSubjectCommand(new Subject(selection)));
        btnOk.setDisable(false);
    }
    @Override
    public void ok(ActionEvent event){
        Command command = new UpdateSubjectsCommand(demoController, commandList);
        command.execute();
        CommandManager.getInstance().addCommand(command);

        super.ok(event);
    }
}
