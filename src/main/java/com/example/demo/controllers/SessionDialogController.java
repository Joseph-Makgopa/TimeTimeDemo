package com.example.demo.controllers;

import com.example.demo.models.*;
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

public class SessionDialogController extends BaseDataDialogController implements Initializable {
    @FXML
    ComboBox<Grade> comboGrade;
    @FXML
    ComboBox<Subject> comboSubject;
    @FXML
    ComboBox<Educator> comboEducator;
    @FXML
    ComboBox<Integer> comboPair, comboSplit;
    @FXML
    Spinner<Integer> spinnerAmount;
    @FXML
    TableView tableSessions;
    @FXML
    TableColumn<Session, String> columnId, columnGrade, columnSubject, columnEducator, columnAmount, columnSplit, columnPair;
    @FXML
    Button btnOk;
    private final ObservableList<Session> listSessions = FXCollections.observableArrayList();
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        SpinnerValueFactory<Integer> spinnerValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100);
        spinnerValueFactory.setValue(1);
        spinnerAmount.setValueFactory(spinnerValueFactory);

        comboSubject.getItems().addAll(State.getInstance().subjects.stream().toList());
        comboGrade.getItems().addAll(State.getInstance().grades.stream().toList());
        comboEducator.getItems().addAll(State.getInstance().educators.values().stream().toList());
        comboPair.getItems().addAll(State.getInstance().sessions.keySet().stream().toList());
        comboSplit.getItems().addAll(State.getInstance().sessions.keySet().stream().toList());

        btnOk.setDisable(true);

        columnId.setCellValueFactory(new PropertyValueFactory<>("idView"));
        columnGrade.setCellValueFactory(new PropertyValueFactory<>("gradeView"));
        columnSubject.setCellValueFactory(new PropertyValueFactory<>("subjectView"));
        columnAmount.setCellValueFactory(new PropertyValueFactory<>("amountView"));
        columnPair.setCellValueFactory(new PropertyValueFactory<>("pairView"));
        columnSplit.setCellValueFactory(new PropertyValueFactory<>("splitView"));

        tableSessions.setItems(listSessions);

        listSessions.addAll(State.getInstance().sessions.values());
    }
    public void rowSelection(){
        Session session = (Session)tableSessions.getSelectionModel().getSelectedItem();

        if(session != null){
            comboGrade.setValue(session.getGrade());
            comboSubject.setValue(session.getSubject());
            comboEducator.setValue(session.getEducator());
            spinnerAmount.getValueFactory().setValue(session.getAmount());
            comboPair.setValue(session.getPair());
            comboSplit.setValue(session.getSplit());
        }
    }
    @FXML
    public void add(ActionEvent event){
        Grade grade = comboGrade.getValue();
        Subject subject = comboSubject.getValue();
        Educator educator = comboEducator.getValue();
        Integer amount = spinnerAmount.getValue();
        Integer pair = comboPair.getValue();
        Integer split = comboSplit.getValue();

        if(grade == null){
            Notification.show("Session error", "Educator is missing.", Alert.AlertType.ERROR);
            return;
        }

        if(subject == null){
            Notification.show("Session error", "Subject is missing.", Alert.AlertType.ERROR);
            return;
        }

        if(educator == null){
            Notification.show("Session error", "Educator is missing.", Alert.AlertType.ERROR);
            return;
        }

        Session session = new Session(grade, educator, subject, amount, split, pair);

        if(listSessions.contains(session)){
            Notification.show("Session error", "Duplicate session is detected.", Alert.AlertType.ERROR);
            return;
        }

        for(int count = 0; count < listSessions.size(); count++){
            Session entry = listSessions.get(count);

            if(entry.getGrade().equals(session.getGrade()) && entry.getSubject().equals(session.getSubject()) && entry.getEducator().equals(session.getEducator())){
                if(pair != null && pair.equals(entry.getId())){
                    Notification.show("Session error", "Can not set pair to the same value as the id.", Alert.AlertType.ERROR);
                    return;
                }

                if(split != null && split.equals(entry.getId())){
                    Notification.show("Session error", "Can not set split to the same value as the id.", Alert.AlertType.ERROR);
                    return;
                }

                session.setId(entry.getId());
                listSessions.set(count, session);
                commandList.add(new UpdateSessionCommand(entry, session));
                btnOk.setDisable(false);
                return;
            }
        }

        listSessions.add(session);
        comboPair.getItems().add(session.getId());
        comboSplit.getItems().add(session.getId());
        commandList.add(new AddSessionCommand(session));
        btnOk.setDisable(false);
    }
    @FXML
    public void remove(ActionEvent event){
        ObservableList<Session> selection =  tableSessions.getSelectionModel().getSelectedItems();

        for(Session session: selection) {
            commandList.add(new RemoveSessionCommand(session));
            comboPair.getItems().remove(session.getId());
            comboSplit.getItems().remove(session.getId());
        }

        listSessions.removeAll(selection);
        btnOk.setDisable(false);
    }
}
