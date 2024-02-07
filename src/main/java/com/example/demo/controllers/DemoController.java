package com.example.demo.controllers;

import com.example.demo.DemoApplication;
import com.example.demo.models.Grade;
import com.example.demo.models.GradeSchedule;
import com.example.demo.models.State;
import com.example.demo.models.WeekDay;
import com.example.demo.models.assignable.Assignable;
import com.example.demo.models.assignable.PairAssignable;
import com.example.demo.models.commands.Command;
import com.example.demo.models.commands.CommandManager;
import com.example.demo.models.commands.UpdateStructureCommand;
import com.example.demo.utilities.Notification;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.control.action.Action;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class DemoController implements Initializable {
    private Stage stage;
    @FXML
    private TableView<Assignable> tableAssign;
    @FXML
    private TableColumn<Assignable, String> columnGrade, columnDetail, columnRemain;
    @FXML
    private TabPane paneTimeTable;
    @FXML
    CheckBox checkMonday, checkTuesday, checkWednesday, checkThursday, checkFriday, checkSaturday, checkSunday;
    @FXML
    Spinner<Integer> spinnerMondayPeriods, spinnerTuesdayPeriods, spinnerWednesdayPeriods, spinnerThursdayPeriods, spinnerFridayPeriods, spinnerSaturdayPeriods, spinnerSundayPeriods, spinnerBreak, spinnerPeriod;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        columnGrade.setCellValueFactory(entry ->{
            String result = State.getInstance().sessions.get(entry.getValue().getSessionRef()).getGrade().toString();

            if(!entry.getValue().affectSingleSlot()){
                PairAssignable pairAssignable = (PairAssignable) entry.getValue();
                result += " / " + State.getInstance().sessions.get(pairAssignable.getPairRef()).getGrade().toString();
            }

            return new SimpleObjectProperty<>(result);
        });
        columnDetail.setCellValueFactory(entry -> new SimpleObjectProperty<>(entry.getValue().getDetails()));
        columnRemain.setCellValueFactory(entry -> new SimpleObjectProperty<>(entry.getValue().getRemain().toString()));

        tableAssign.getItems().addAll(State.getInstance().assignables.values());

        SpinnerValueFactory<Integer> spinnerMondayPeriodsFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1,10);
        SpinnerValueFactory<Integer> spinnerTuesdayPeriodsFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1,10);
        SpinnerValueFactory<Integer> spinnerWednesdayPeriodsFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1,10);
        SpinnerValueFactory<Integer> spinnerThursdayPeriodsFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1,10);
        SpinnerValueFactory<Integer> spinnerFridayPeriodsFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1,10);
        SpinnerValueFactory<Integer> spinnerSaturdayPeriodsFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1,10);
        SpinnerValueFactory<Integer> spinnerSundayPeriodsFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1,10);
        SpinnerValueFactory<Integer> spinnerBreakFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1,10);
        SpinnerValueFactory<Integer> spinnerPeriodFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1,10);

        spinnerMondayPeriodsFactory.setValue(1);
        spinnerMondayPeriods.setValueFactory(spinnerMondayPeriodsFactory);

        spinnerTuesdayPeriodsFactory.setValue(1);
        spinnerTuesdayPeriods.setValueFactory(spinnerTuesdayPeriodsFactory);

        spinnerWednesdayPeriodsFactory.setValue(1);
        spinnerWednesdayPeriods.setValueFactory(spinnerWednesdayPeriodsFactory);

        spinnerThursdayPeriodsFactory.setValue(1);
        spinnerThursdayPeriods.setValueFactory(spinnerThursdayPeriodsFactory);

        spinnerFridayPeriodsFactory.setValue(1);
        spinnerFridayPeriods.setValueFactory(spinnerFridayPeriodsFactory);

        spinnerSaturdayPeriodsFactory.setValue(1);
        spinnerSaturdayPeriods.setValueFactory(spinnerSaturdayPeriodsFactory);

        spinnerSundayPeriodsFactory.setValue(1);
        spinnerSundayPeriods.setValueFactory(spinnerSundayPeriodsFactory);

        spinnerBreakFactory.setValue(1);
        spinnerBreak.setValueFactory(spinnerBreakFactory);

        spinnerPeriodFactory.setValue(1);
        spinnerPeriod.setValueFactory(spinnerPeriodFactory);

        updateTable();
    }
    public void updateTableAssign(){
        tableAssign.getItems().clear();
        tableAssign.getItems().addAll(State.getInstance().assignables.values());
    }
    @FXML
    public void applyStructure(ActionEvent event){
        Map<WeekDay, Integer> days = new HashMap<>();

        if(checkMonday.isSelected()){
            days.put(WeekDay.MONDAY, spinnerMondayPeriods.getValue());
        }

        if(checkTuesday.isSelected()){
            days.put(WeekDay.TUESDAY, spinnerTuesdayPeriods.getValue());
        }

        if(checkWednesday.isSelected()){
            days.put(WeekDay.WEDNESDAY, spinnerWednesdayPeriods.getValue());
        }

        if(checkThursday.isSelected()){
            days.put(WeekDay.THURSDAY, spinnerThursdayPeriods.getValue());
        }

        if(checkFriday.isSelected()){
            days.put(WeekDay.FRIDAY, spinnerWednesdayPeriods.getValue());
        }

        if(checkSaturday.isSelected()){
            days.put(WeekDay.SATURDAY, spinnerSaturdayPeriods.getValue());
        }

        if(checkSunday.isSelected()){
            days.put(WeekDay.SUNDAY, spinnerSundayPeriods.getValue());
        }

        Command command = new UpdateStructureCommand(days, spinnerBreak.getValue());
        command.execute();
        CommandManager.getInstance().addCommand(command);
        updateTable();
        State.getInstance().saveRequired = true;
    }
    public void updateTable(){
        paneTimeTable.getTabs().clear();
        Tab[] tabs = new Tab[7];

        State.getInstance().days.forEach((day, periods) ->{

            TableView<GradeSchedule> daySchedule = new TableView<>();

            AnchorPane anchorPane = new AnchorPane(daySchedule);
            AnchorPane.setTopAnchor(daySchedule, 0.0);
            AnchorPane.setLeftAnchor(daySchedule, 0.0);
            AnchorPane.setBottomAnchor(daySchedule, 0.0);
            AnchorPane.setRightAnchor(daySchedule, 0.0);

            Tab tab = new Tab("",anchorPane);

            daySchedule.getColumns().add(new TableColumn<>("Grade"));

            for(int count = 0; count < periods; count++)
                daySchedule.getColumns().add(new TableColumn<>(Integer.toString(count + 1)));

            setupTable(daySchedule);

            switch (day){
             case MONDAY : {
                tab.setText("Monday");
                tabs[0] = tab;
             }break;
             case TUESDAY : {
                 tab.setText("Tuesday");
                 tabs[1] = tab;
             }break;
             case WEDNESDAY : {
                 tab.setText("Wednesday");
                 tabs[2] = tab;
             }break;
             case THURSDAY: {
                 tab.setText("Thursday");
                 tabs[3] = tab;
             }break;
             case FRIDAY : {
                 tab.setText("Friday");
                 tabs[4] = tab;
             }break;
             case SATURDAY : {
                 tab.setText("Saturday");
                 tabs[5] = tab;
             }break;
             case SUNDAY : {
                 tab.setText("Sunday");
                 tabs[6] = tab;
             }break;
            }
        });

        paneTimeTable.getTabs().addAll(Arrays.stream(tabs).filter(value -> value != null).toList());

    }

    public void setupTable(TableView<GradeSchedule> table){
        TableColumn<GradeSchedule,?> gradeColumn = table.getColumns().get(0);
        gradeColumn.setCellValueFactory(row -> new SimpleObjectProperty(row.getValue().getGrade().toString()));
        table.getItems().clear();

        for(Grade grade: State.getInstance().grades){
            table.getItems().add(new GradeSchedule(grade, table.getColumns().size() - 1));
        }
    }
    @FXML
    public void revertStructure(ActionEvent event){
        Integer period = State.getInstance().days.get(WeekDay.MONDAY);

        if(period != null) {
            spinnerMondayPeriods.getValueFactory().setValue(period);
            checkMonday.setSelected(true);
        }

        period = State.getInstance().days.get(WeekDay.TUESDAY);
        if(period != null) {
            spinnerTuesdayPeriods.getValueFactory().setValue(period);
            checkTuesday.setSelected(true);
        }

        period = State.getInstance().days.get(WeekDay.WEDNESDAY);
        if(period != null) {
            spinnerWednesdayPeriods.getValueFactory().setValue(period);
            checkWednesday.setSelected(true);
        }

        period = State.getInstance().days.get(WeekDay.THURSDAY);
        if(period != null) {
            spinnerThursdayPeriods.getValueFactory().setValue(period);
            checkThursday.setSelected(true);
        }

        period = State.getInstance().days.get(WeekDay.FRIDAY);
        if(period != null) {
            spinnerFridayPeriods.getValueFactory().setValue(period);
            checkFriday.setSelected(true);
        }

        period = State.getInstance().days.get(WeekDay.SATURDAY);
        if(period != null) {
            spinnerSaturdayPeriods.getValueFactory().setValue(period);
            checkSaturday.setSelected(true);
        }

        period = State.getInstance().days.get(WeekDay.SUNDAY);
        if(period != null) {
            spinnerSundayPeriods.getValueFactory().setValue(period);
            checkSunday.setSelected(true);
        }
    }
    void initialiseTableStructure(){

    }
    @FXML
    void selectMondayTab(ActionEvent event){
        System.out.println("Monday");
    }
    @FXML
    void clearFilter(ActionEvent event){

    }
    @FXML
    void random(ActionEvent event){

    }
    @FXML
    void arrange(ActionEvent event){

    }
    @FXML
    void position(ActionEvent event){

    }
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
            updateTableAssign();
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