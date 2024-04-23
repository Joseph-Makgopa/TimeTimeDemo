package com.example.demo.controllers;

import com.example.demo.models.Educator;
import com.example.demo.models.Grade;
import com.example.demo.models.State;
import com.example.demo.models.commands.*;
import com.example.demo.utilities.Notification;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Comparator;
import java.util.NoSuchElementException;
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

        columnPost.setCellValueFactory(entry -> new SimpleObjectProperty<>(entry.getValue().getPost().toString()));
        columnInitials.setCellValueFactory(entry -> new SimpleObjectProperty<>(entry.getValue().getInitials()));
        columnSurname.setCellValueFactory(entry -> new SimpleObjectProperty<>(entry.getValue().getSurname()));
        tableEducators.setItems(listEducators);

        listEducators.addAll(State.getInstance().educators.values());

        try {
            Integer max = listEducators.stream().map(entry -> entry.getPost()).max(Comparator.naturalOrder()).get();
            spinnerPost.getValueFactory().setValue(max + 1);
        }catch(NoSuchElementException error){
        }

    }
    @Override
    public void ok(ActionEvent event){
        Command command = new UpdateEducatorsCommand(demoController, commandList);
        command.execute();
        CommandManager.getInstance().addCommand(command);

        super.ok(event);
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
        }else {
            String[] surnames = surname.split(" ");
            surname = "";

            for(int count = 0; count < surnames.length; count++){
                surname += Character.toString(surnames[count].charAt(0)).toUpperCase();

                if(surnames[count].length() > 1)
                    surname += surnames[count].substring(1, surnames[count].length()).toLowerCase();

                if(count != (surnames.length - 1))
                    surname += " ";
            }
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
                commandList.add(new UpdateEducatorCommand(demoController, entry, educator));
                btnOk.setDisable(false);
                return;
            }
        }

        txtInitials.clear();
        txtSurname.clear();
        listEducators.add(educator);
        tableEducators.refresh();
        tableEducators.getSelectionModel().select(educator);
        tableEducators.scrollTo(educator);
        txtInitials.requestFocus();

        commandList.add(new AddEducatorCommand(demoController, educator));

        try {
            Integer max = listEducators.stream().map(entry -> entry.getPost()).max(Comparator.naturalOrder()).get();
            spinnerPost.getValueFactory().setValue(max + 1);
        }catch(NoSuchElementException error){
             spinnerPost.getValueFactory().setValue(post + 1);
        }

        btnOk.setDisable(false);
    }
    @FXML
    public void remove(ActionEvent event){
        ObservableList<Educator> selection =  tableEducators.getSelectionModel().getSelectedItems();

        for(Educator educator: selection)
            commandList.add(new RemoveEducatorCommand(demoController, educator));

        listEducators.removeAll(selection);
        btnOk.setDisable(false);
    }

}
