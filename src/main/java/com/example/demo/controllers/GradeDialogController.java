package com.example.demo.controllers;

import com.example.demo.models.Grade;
import com.example.demo.models.State;
import com.example.demo.models.Subject;
import com.example.demo.models.commands.*;
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
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class GradeDialogController extends BaseDataDialogController implements Initializable {
    @FXML
    private ComboBox<String> comboDivision;
    @FXML
    private Spinner<Integer> spinnerNumber;
    @FXML
    private ListView<Grade> listGrades;
    @FXML
    private Button btnOk;
    private ObservableList<String> subGrades = FXCollections.observableArrayList("A B C D E F G H I J K L M N O P Q R S T U V W X Y Z".split(" "));
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        comboDivision.setItems(subGrades);

        SpinnerValueFactory numberValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1,12);
        numberValueFactory.setValue(8);
        spinnerNumber.setValueFactory(numberValueFactory);

        listGrades.getItems().addAll(State.getInstance().grades);
        btnOk.setDisable(true);
    }
    @FXML
    public void enter(KeyEvent event){
        if(event.getCode().equals(KeyCode.ENTER)){
            add(null);
        }
        event.consume();
    }
    @FXML
    public void add(ActionEvent event){
        String division = comboDivision.getValue();

        if(division == null || division.isEmpty()){
            Notification.show("Selection error", "Division is missing.", Alert.AlertType.ERROR);

            return;
        }

        Grade grade = new Grade(spinnerNumber.getValue(), division.charAt(0));

        if(listGrades.getItems().contains(grade)){
            Notification.show("Grade error", "Duplicate grade is detected.", Alert.AlertType.ERROR);

            return;
        }

        listGrades.getItems().add(grade);
        listGrades.getSelectionModel().select(grade);
        listGrades.scrollTo(grade);
        commandList.add(new AddGradeCommand(grade));
        btnOk.setDisable(false);
    }
    @FXML
    public void remove(ActionEvent event){

        try {
            Grade selection =  listGrades.getSelectionModel().getSelectedItem();
            commandList.add(new RemoveGradeCommand(selection));

            listGrades.getItems().remove(selection);
            btnOk.setDisable(false);
        }catch(NumberFormatException error){
            Notification.show("Grade removal error", "Something went wrong. Failed to remove grade.", Alert.AlertType.ERROR);
        }
    }
    @Override
    public void ok(ActionEvent event){
        Command command = new UpdateGradesCommand(demoController, commandList);
        command.execute();
        CommandManager.getInstance().addCommand(command);

        super.ok(event);
    }
}
