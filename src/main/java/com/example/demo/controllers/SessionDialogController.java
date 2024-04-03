package com.example.demo.controllers;

import com.example.demo.models.*;
import com.example.demo.models.commands.*;
import com.example.demo.utilities.Notification;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class SessionDialogController extends BaseDataDialogController implements Initializable {
    @FXML
    TextField txtSearch;
    @FXML
    ComboBox<Grade> comboGrade;
    @FXML
    ComboBox<Subject> comboSubject;
    @FXML
    ComboBox<Educator> comboEducator;
    @FXML
    ComboBox<String> comboPair, comboSplit;
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
        comboPair.getItems().addAll(State.getInstance().sessions.keySet().stream().map(id -> id.toString()).toList());
        comboPair.getItems().add(0, "");
        comboSplit.getItems().addAll(State.getInstance().sessions.keySet().stream().map(id -> id.toString()).toList());
        comboSplit.getItems().add(0, "");
        btnOk.setDisable(true);

        columnId.setCellValueFactory(entry -> new SimpleObjectProperty<>(entry.getValue().getId().toString()));
        columnGrade.setCellValueFactory(entry -> new SimpleObjectProperty<>(entry.getValue().getGrade().toString()));
        columnSubject.setCellValueFactory(entry -> new SimpleObjectProperty<>(entry.getValue().getSubject().toString()));
        columnEducator.setCellValueFactory(entry -> new SimpleObjectProperty<>(entry.getValue().getEducator().toString()));
        columnAmount.setCellValueFactory(entry -> new SimpleObjectProperty<>(entry.getValue().getAmount().toString()));
        columnPair.setCellValueFactory(entry -> {
            if(entry.getValue().getPair() == null)
                return new SimpleObjectProperty<>("");

            return new SimpleObjectProperty<>(entry.getValue().getPair().toString());
        });
        columnSplit.setCellValueFactory(entry -> {
            if(entry.getValue().getSplit() == null)
                return new SimpleObjectProperty<>("");

            return new SimpleObjectProperty<>(entry.getValue().getSplit().toString());
        });

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
            comboPair.setValue(session.getPair() == null ? "" : session.getPair().toString());
            comboSplit.setValue(session.getSplit() == null ? "" : session.getSplit().toString());
        }
    }
    @FXML
    public void search(KeyEvent event){
        String text = txtSearch.getText().toUpperCase();

        if(text.isEmpty()){
            tableSessions.setItems(listSessions);
            return;
        }

        tableSessions.setItems(FXCollections.observableArrayList(listSessions.stream().filter(entry -> {
            if(entry.getId().toString().contains(text))
                return true;

            if(entry.getGrade().toString().toUpperCase().contains(text))
                return true;

            if(entry.getSubject().toString().toUpperCase().contains(text))
                return true;

            if(entry.getEducator().toString().toUpperCase().contains(text))
                return true;

            if(entry.getAmount().toString().contains(text))
                return true;

            if(entry.getPair() != null) {
                if (entry.getPair().toString().contains(text))
                    return true;
            }

            return entry.getSplit() != null && entry.getSplit().toString().contains(text);
        }).toList()));
    }
    @FXML
    public void add(ActionEvent event){
        Grade grade = comboGrade.getValue();
        Subject subject = comboSubject.getValue();
        Educator educator = comboEducator.getValue();
        Integer amount = spinnerAmount.getValue();
        Integer pair = (comboPair.getValue() == null || comboPair.getValue().isEmpty()) ? null : Integer.parseInt(comboPair.getValue());
        Integer split = (comboSplit.getValue() == null || comboSplit.getValue().isEmpty()) ? null : Integer.parseInt(comboSplit.getValue());

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

        if(split != null){
            Session splitSession = null;

            for(Session entry: listSessions){
                if(entry.getId().equals(split)){
                    splitSession = entry;
                    break;
                }
            }

            if(splitSession == null){
                Notification.show("Session error", "Split session is missing.", Alert.AlertType.ERROR);
                return;
            }

            if(!splitSession.getAmount().equals(amount)){
                Notification.show("Session error", "Amount of sessions must be equal to the split session amounts.", Alert.AlertType.ERROR);
                return;
            }

            if(!splitSession.getGrade().equals(grade)){
                Notification.show("Session error", "Split sessions must be of the same grade.", Alert.AlertType.ERROR);
                return;
            }
        }

        if(pair != null){
            Session pairSession = null;

            for(Session entry: listSessions){
                if(entry.getId().equals(pair)){
                    pairSession = entry;
                    break;
                }
            }

            if(pairSession == null){
                Notification.show("Session error", "Pair session is missing.", Alert.AlertType.ERROR);
                return;
            }

            if(!pairSession.getAmount().equals(amount)){
                Notification.show("Session error", "Amount of sessions must be equal to the pair session amounts.", Alert.AlertType.ERROR);
                return;
            }

            if(pairSession.getGrade().equals(grade)){
                Notification.show("Session error", "Pair sessions must be of the different grades.", Alert.AlertType.ERROR);
                return;
            }
        }

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

                if(pair != null){
                    for(Session value: listSessions){
                        if(pair.equals(value.getId())){
                            if(value.getPair() != null){
                                for(Session other: listSessions){
                                    if(other.getId().equals(value.getPair())){
                                        other.setPair(null);
                                        break;
                                    }
                                }
                            }

                            value.setPair(entry.getId());
                            break;
                        }
                    }
                }

                if(split != null){
                    for(Session value: listSessions){
                        if(split.equals(value.getId())){
                            if(value.getSplit() != null){
                                for(Session other: listSessions){
                                    if(other.getId().equals(value.getSplit())){
                                        other.setSplit(null);
                                        break;
                                    }
                                }
                            }

                            value.setSplit(entry.getId());
                            break;
                        }
                    }
                }

                session.setId(entry.getId());
                listSessions.set(count, session);
                commandList.add(new UpdateSessionCommand(entry, session));
                btnOk.setDisable(false);
                return;
            }
        }

        if(split != null){
            for(Session value: listSessions){
                if(split.equals(value.getId())){
                    if(value.getSplit() != null){
                        for(Session other: listSessions){
                            if(other.getId().equals(value.getSplit())){
                                other.setSplit(null);
                                break;
                            }
                        }
                    }
                    value.setSplit(session.getId());
                    break;
                }
            }
        }

        if(pair != null){
            for(Session value: listSessions){
                if(pair.equals(value.getId())){
                    if(value.getPair() != null){
                        for(Session other: listSessions){
                            if(other.getId().equals(value.getPair())){
                                other.setPair(null);
                                break;
                            }
                        }
                    }

                    value.setPair(session.getId());
                    break;
                }
            }
        }

        listSessions.add(session);
        comboPair.getItems().add(session.getId().toString());
        comboSplit.getItems().add(session.getId().toString());
        commandList.add(new AddSessionCommand(session));
        tableSessions.refresh();
        btnOk.setDisable(false);
        txtSearch.clear();
        search(null);
    }
    @FXML
    public void remove(ActionEvent event){
        ObservableList<Session> selection =  tableSessions.getSelectionModel().getSelectedItems();

        for(Session session: selection) {
            commandList.add(new RemoveSessionCommand(session));
            comboPair.getItems().remove(session.getId().toString());
            comboSplit.getItems().remove(session.getId().toString());
        }

        listSessions.removeAll(selection);
        btnOk.setDisable(false);
        txtSearch.clear();
        search(null);
    }
    @Override
    public void ok(ActionEvent event){
        Command command = new SetAssignablesCommand(service, commandList);
        command.execute();
        CommandManager.getInstance().addCommand(command);

        super.ok(event);
    }
}
