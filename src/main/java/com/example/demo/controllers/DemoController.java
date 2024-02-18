package com.example.demo.controllers;

import com.example.demo.DemoApplication;
import com.example.demo.models.*;
import com.example.demo.models.assignable.Assignable;
import com.example.demo.models.assignable.PairAssignable;
import com.example.demo.models.commands.Command;
import com.example.demo.models.commands.CommandManager;
import com.example.demo.models.commands.UpdateStructureCommand;
import com.example.demo.services.*;
import com.example.demo.utilities.Filter;
import com.example.demo.utilities.Notification;
import com.example.demo.utilities.Triplet;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.print.*;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.InputMethodEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.transform.Scale;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class DemoController implements Initializable {
    private Stage stage;
    private Filter filter;
    @FXML
    TextField txtSearch;
    @FXML
    private TableView<Assignable> tableAssign;
    @FXML
    private TableColumn<Assignable, String> columnGrade, columnDetail, columnRemain;
    @FXML
    private TabPane paneTimeTable;
    @FXML
    private CheckBox checkMonday, checkTuesday, checkWednesday, checkThursday, checkFriday, checkSaturday, checkSunday;
    @FXML
    private Spinner<Integer> spinnerMondayPeriods, spinnerTuesdayPeriods, spinnerWednesdayPeriods, spinnerThursdayPeriods, spinnerFridayPeriods, spinnerSaturdayPeriods, spinnerSundayPeriods, spinnerBreak, spinnerPeriod;
    @FXML
    private ComboBox<Integer> comboNumber;
    @FXML
    private ComboBox<Character> comboDivision;
    @FXML
    private ComboBox<Subject> comboSubject;
    @FXML
    private ComboBox<Educator> comboEducator;
    @FXML
    private ComboBox<WeekDay> comboDay;
    @FXML
    private CheckMenuItem menuWeekDays, menuGrades, menuEducators;
    private View view = View.WeekDayView;
    private DemoService service = new WeekDayViewService();
    private ToolBarService toolbarService = new ToolBarService();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        filter = new Filter();

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

        service.setupTable(paneTimeTable);
        service.populateTable();

        menuWeekDays.setSelected(true);
        menuWeekDays.setDisable(true);
    }
    public void updateGradeFilterOptions(){
        comboNumber.getItems().clear();
        comboDivision.getItems().clear();

        State.getInstance().grades.forEach(grade ->{
            if(!comboNumber.getItems().contains(grade.getNumber()))
                comboNumber.getItems().add(grade.getNumber());

            if(!comboDivision.getItems().contains(grade.getDivision()))
                comboDivision.getItems().add(grade.getDivision());
        });
    }

    public void updateSubjectFilterOptions(){
        comboSubject.getItems().clear();

        State.getInstance().subjects.forEach(subject ->{
            if(!comboSubject.getItems().contains(subject))
                comboSubject.getItems().add(subject);
        });
    }

    public void updateEducatorFilterOptions(){
        comboEducator.getItems().clear();

        State.getInstance().educators.forEach((post, educator) ->{
            if(!comboEducator.getItems().contains(educator))
                comboEducator.getItems().add(educator);
        });
    }
    @FXML
    public void clearFilter(ActionEvent event){
        comboEducator.setValue(null);
        comboSubject.setValue(null);
        comboNumber.setValue(null);
        comboDivision.setValue(null);
        filter.clear();
        service.populateTable();
    }
    @FXML
    public void updateFilter(ActionEvent event){
        filter.set(comboNumber.getValue(), comboDivision.getValue(), comboSubject.getValue(), comboEducator.getValue());
        service.updateFilter(filter, paneTimeTable);
    }
    @FXML
    public void search(KeyEvent event){
        tableAssign.setItems(service.search(txtSearch.getText().toUpperCase()));
        event.consume();
    }
    public void updateTableAssign(){
        tableAssign.getItems().clear();
        tableAssign.getItems().addAll(State.getInstance().assignables.values());
        tableAssign.refresh();
    }
    @FXML
    public void applyStructure(ActionEvent event){
        /**
         * add the selected day and period number to the days map
         * call setupWeeklyTable function
         * **/

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
        comboDay.setItems(FXCollections.observableArrayList(State.getInstance().days.keySet().stream().toList()));

        setupWeeklyTable();
        State.getInstance().saveRequired = true;
    }
    public void setupWeeklyTable(){
        /**
         * Add tabs to the timetable pane
         * Call the function to set up the timetable for each day
         * **/

        service.setupTable(paneTimeTable);
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
    @FXML
    void random(ActionEvent event){

    }
    @FXML
    void arrange(ActionEvent event){

    }
    @FXML
    void position(ActionEvent event){
        WeekDay day = comboDay.getValue();
        Assignable selected = tableAssign.getSelectionModel().getSelectedItem();

        if(day == null){
            Notification.show("Assignment error","You did not select the day.", Alert.AlertType.ERROR);
            return;
        }

        if(selected == null){
            Notification.show("Assignment error","You did not select a lesson.", Alert.AlertType.ERROR);
            return;
        }else if (selected.getRemain() == 0){
            Notification.show("Assignment error","This lesson has been assigned.", Alert.AlertType.ERROR);
            return;
        }

        Integer period = spinnerPeriod.getValue() - 1;

        if(period < 0 || period >= State.getInstance().days.get(day)){
            Notification.show("Assignment error","Enter the correct value for period. Must be between 0 and " + (State.getInstance().days.get(day) + 1), Alert.AlertType.ERROR);
            return;
        }

        service.position(paneTimeTable, selected, day, period);
        tableAssign.refresh();
    }
    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
    @FXML
    public void showSubjectDialog(ActionEvent event){
        toolbarService.showDataDialog("Subjects", "subject-dialog-view.fxml");
        updateTableAssign();
        updateSubjectFilterOptions();
    }
    @FXML
    public void showGradeDialog(ActionEvent event){
        toolbarService.showDataDialog("Grades", "grade-dialog-view.fxml");
        updateTableAssign();
        updateGradeFilterOptions();
    }
    @FXML
    public void showEducatorDialog(ActionEvent event){
        toolbarService.showDataDialog("Educators", "educator-dialog-view.fxml");
        updateTableAssign();
        updateEducatorFilterOptions();
    }
    @FXML
    public void showSessionDialog(ActionEvent event){
        toolbarService.showDataDialog("Sessions", "session-dialog-view.fxml");
        updateTableAssign();
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
    public void viewWeekDays(ActionEvent event){
        if(menuGrades.isSelected()){
            menuGrades.setSelected(false);
            menuGrades.setDisable(false);
        }else if(menuEducators.isSelected()){
            menuEducators.setSelected(false);
            menuEducators.setDisable(false);
        }

        menuWeekDays.setSelected(true);
        menuWeekDays.setDisable(true);
        service = new WeekDayViewService();
        service.populateTable();
        service.setupTable(paneTimeTable);
    }
    @FXML
    public void viewGrades(ActionEvent event){
        if(menuWeekDays.isSelected()){
            menuWeekDays.setSelected(false);
            menuWeekDays.setDisable(false);
        }else if(menuEducators.isSelected()){
            menuEducators.setSelected(false);
            menuEducators.setDisable(false);
        }

        menuGrades.setSelected(true);
        menuGrades.setDisable(true);
        service = new GradeViewService();
        service.populateTable();
        service.setupTable(paneTimeTable);
    }
    @FXML
    public void viewEducators(ActionEvent event){
        if(menuGrades.isSelected()){
            menuGrades.setSelected(false);
            menuGrades.setDisable(false);
        }else if(menuWeekDays.isSelected()){
            menuWeekDays.setSelected(false);
            menuWeekDays.setDisable(false);
        }

        menuEducators.setSelected(true);
        menuEducators.setDisable(true);
        service = new EducatorViewService();
        service.populateTable();
        service.setupTable(paneTimeTable);
    }
    @FXML
    public void createFile(ActionEvent event){
        toolbarService.createFile(stage);
    }
    public void populateStructure(){
        State.getInstance().days.forEach((day, periods) -> {
            switch(day){
                case MONDAY :{
                    checkMonday.setSelected(true);
                    spinnerMondayPeriods.getValueFactory().setValue(periods);
                }break;
                case TUESDAY:{
                    checkTuesday.setSelected(true);
                    spinnerTuesdayPeriods.getValueFactory().setValue(periods);
                }break;
                case WEDNESDAY:{
                    checkWednesday.setSelected(true);
                    spinnerWednesdayPeriods.getValueFactory().setValue(periods);
                }break;
                case THURSDAY:{
                    checkThursday.setSelected(true);
                    spinnerThursdayPeriods.getValueFactory().setValue(periods);
                }break;
                case FRIDAY:{
                    checkFriday.setSelected(true);
                    spinnerFridayPeriods.getValueFactory().setValue(periods);
                }break;
                case SATURDAY:{
                    checkSaturday.setSelected(true);
                    spinnerSaturdayPeriods.getValueFactory().setValue(periods);
                }break;
                case SUNDAY:{
                    checkSunday.setSelected(true);
                    spinnerSundayPeriods.getValueFactory().setValue(periods);
                }break;
            }
        });
    }
    @FXML
    public void openFile(ActionEvent event){
        File file = toolbarService.openFile(stage);

        if(file != null){
            State.getInstance().setFields(file);
            stage.setTitle(file.getName() + " - TimeTable");
            service.populateTable();
            populateStructure();
            applyStructure(event);
            updateTableAssign();
            updateEducatorFilterOptions();
            updateGradeFilterOptions();
            updateSubjectFilterOptions();
            comboDay.setItems(FXCollections.observableArrayList(State.getInstance().days.keySet().stream().toList()));
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
    public void print(ActionEvent event){
        service.print(paneTimeTable, stage);
    }
    @FXML
    public void close(ActionEvent event){
        stage.close();
    }
}