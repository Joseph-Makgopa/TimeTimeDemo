package com.example.demo.controllers;

import com.example.demo.models.Educator;
import com.example.demo.models.Grade;
import com.example.demo.models.State;
import com.example.demo.models.commands.*;
import com.example.demo.utilities.Notification;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class EducatorDialogController extends BaseDataDialogController implements Initializable {
    @FXML
    TextField txtInitials;
    @FXML
    TextField txtSurname;
    @FXML
    Spinner<Integer> spinnerPost;
    @FXML
    private Button btnOk;
    @FXML
    TableView tableEducators;
    @FXML
    private TableColumn<Educator, String> columnPost, columnInitials, columnSurname;
    private final ObservableList<Educator> listEducators = FXCollections.observableArrayList();
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        SpinnerValueFactory<Integer> spinnerValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 500);
        spinnerValueFactory.setValue(1);
        spinnerPost.setValueFactory(spinnerValueFactory);

        btnOk.setDisable(true);

        columnPost.setCellValueFactory(new PropertyValueFactory<>("postView"));
        columnInitials.setCellValueFactory(new PropertyValueFactory<>("initialsView"));
        columnSurname.setCellValueFactory(new PropertyValueFactory<>("surnameView"));
        tableEducators.setItems(listEducators);

        listEducators.addAll(State.getInstance().educators.values());

    }
    @FXML
    public void add(ActionEvent event){
        String initials = txtInitials.getText().toUpperCase();
        String surname = txtSurname.getText();
        Integer post = spinnerPost.getValue();

        if(initials == null || initials.isEmpty()){
            Notification.show("Educator error", "Initials are missing.", Alert.AlertType.ERROR);
            return;
        }

        if(surname == null || surname.isEmpty()){
            Notification.show("Educator error", "Surname is missing.", Alert.AlertType.ERROR);
            return;
        }

        Educator educator = new Educator(post, initials, surname);

        if(listEducators.contains(educator)){
            Notification.show("Educator error", "Duplicate educator is detected.", Alert.AlertType.ERROR);
            return;
        }

        for(int count = 0; count < listEducators.size(); count++){
            Educator entry = listEducators.get(count);

            if(entry.getPost().equals(post)){
                listEducators.set(count, educator);
                commandList.add(new UpdateEducatorCommand(entry, educator));
                btnOk.setDisable(false);
                return;
            }
        }


        listEducators.add(educator);
        commandList.add(new AddEducatorCommand(educator));
        txtInitials.clear();
        txtSurname.clear();
        btnOk.setDisable(false);
    }
    @FXML
    public void remove(ActionEvent event){
        ObservableList<Educator> selection =  tableEducators.getSelectionModel().getSelectedItems();

        for(Educator educator: selection)
            commandList.add(new RemoveEducatorCommand(educator));

        listEducators.removeAll(selection);
        btnOk.setDisable(false);
    }

}
