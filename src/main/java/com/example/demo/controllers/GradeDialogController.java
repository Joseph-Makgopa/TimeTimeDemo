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
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class GradeDialogController extends BaseDataDialogController implements Initializable {
    @FXML
    private ComboBox<String> comboDivision;
    @FXML
    private Spinner<Integer> spinnerNumber;
    @FXML
    private ListView<String> listGrades;
    @FXML
    private Button btnOk;
    private ObservableList<String> subGrades = FXCollections.observableArrayList("A B C D E F G H I J K L M N O P Q R S T U V W X Y Z".split(" "));
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        comboDivision.setItems(subGrades);

        SpinnerValueFactory numberValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1,12);
        numberValueFactory.setValue(8);
        spinnerNumber.setValueFactory(numberValueFactory);

        listGrades.getItems().addAll(State.getInstance().grades.stream().map(grade -> grade.getNumber().toString() + grade.getDivision()).toList());
        btnOk.setDisable(true);
    }
    @FXML
    public void add(ActionEvent event){
        String division = comboDivision.getValue();

        if(division == null || division.isEmpty()){
            Notification.show("Selection error", "Division is missing.", Alert.AlertType.ERROR);

            return;
        }

        Grade grade = new Grade(spinnerNumber.getValue(), division.charAt(0));

        if(listGrades.getItems().contains(grade.getNumber().toString() + division)){
            Notification.show("Grade error", "Duplicate grade is detected.", Alert.AlertType.ERROR);

            return;
        }

        listGrades.getItems().add(grade.getNumber().toString() + division);
        commandList.add(new AddGradeCommand(grade));
        btnOk.setDisable(false);
    }
    @FXML
    public void remove(ActionEvent event){

        try {
            String selection =  listGrades.getSelectionModel().getSelectedItem();
            commandList.add(new RemoveGradeCommand(new Grade(Integer.parseInt(selection.substring(0, selection.length() - 1)), selection.charAt(selection.length() - 1))));

            listGrades.getItems().remove(selection);
            btnOk.setDisable(false);
        }catch(NumberFormatException error){
            Notification.show("Grade removal error", "Something went wrong. Failed to remove grade.", Alert.AlertType.ERROR);
        }
    }
}
