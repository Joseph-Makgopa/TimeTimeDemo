package com.example.demo.controllers;

import com.example.demo.DemoApplication;
import com.example.demo.models.*;
import com.example.demo.models.assignable.Assignable;
import com.example.demo.models.assignable.PairAssignable;
import com.example.demo.models.commands.Command;
import com.example.demo.models.commands.CommandManager;
import com.example.demo.models.commands.UpdateStructureCommand;
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
    private Map<WeekDay, ObservableList<GradeSchedule>> weeklyTable = new HashMap<>();
    private View view;
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

        setupWeeklyTable();
        populateTable();
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
        populateTable();
    }
    @FXML
    public void updateFilter(ActionEvent event){
        filter.set(comboNumber.getValue(), comboDivision.getValue(), comboSubject.getValue(), comboEducator.getValue());

        Tab tab = paneTimeTable.getSelectionModel().getSelectedItem();

        if(tab == null){
            Notification.show("Filter error","You did not select the day.", Alert.AlertType.ERROR);
            return;
        }

        WeekDay day = WeekDay.valueOf(tab.getText().toUpperCase());
        TableView<GradeSchedule> table = (TableView<GradeSchedule>)((AnchorPane)tab.getContent()).getChildren().get(0);
        table.setItems(filter.exec(weeklyTable.get(day)));
        table.refresh();
    }
    @FXML
    public void search(KeyEvent event){
        String text = txtSearch.getText().toUpperCase();
        tableAssign.getItems().clear();

        if(text == null || text.isEmpty()){
            tableAssign.getItems().addAll(State.getInstance().assignables.values());
            return;
        }

        State.getInstance().assignables.values().forEach(entry -> {
            if(entry.getDetails().toUpperCase().contains(text)){
                tableAssign.getItems().add(entry);
                return;
            }

            if(entry.getRemain().toString().contains(text)){
                tableAssign.getItems().add(entry);
                return;
            }

            if(State.getInstance().sessions.get(entry.getSessionRef()).getGrade().toString().contains(text)){
                tableAssign.getItems().add(entry);
                return;
            }

            if(!entry.affectSingleSlot()){
                PairAssignable pairAssignable = (PairAssignable) entry;
                if(State.getInstance().sessions.get(pairAssignable.getPairRef()).getGrade().toString().contains(text)){
                    tableAssign.getItems().add(entry);
                }
            }
        });

        event.consume();
    }
    public void updateTableAssign(){
        tableAssign.getItems().clear();
        tableAssign.getItems().addAll(State.getInstance().assignables.values());
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
        setupWeeklyTable();
        State.getInstance().saveRequired = true;
    }
    public void setupWeeklyTable(){
        /**
         * Add tabs to the timetable pane
         * Call the function to set up the timetable for each day
         * **/

        paneTimeTable.getTabs().clear();
        Tab[] tabs = new Tab[7];

        State.getInstance().days.forEach((day, periods) ->{

            TableView<GradeSchedule> daySchedule = new TableView<>();
            daySchedule.setId(day.toString());

            AnchorPane anchorPane = new AnchorPane(daySchedule);
            AnchorPane.setTopAnchor(daySchedule, 0.0);
            AnchorPane.setLeftAnchor(daySchedule, 0.0);
            AnchorPane.setBottomAnchor(daySchedule, 0.0);
            AnchorPane.setRightAnchor(daySchedule, 0.0);

            Tab tab = new Tab("",anchorPane);

            daySchedule.getColumns().add(new TableColumn<>("Grade"));

            for(int count = 0; count < periods; count++)
                daySchedule.getColumns().add(new TableColumn<>(Integer.toString(count + 1)));

            setupDayTable(day, daySchedule);

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

    public void setupDayTable(WeekDay day, TableView<GradeSchedule> table){
        /**
         * setup the columns for a table and add the referenced ObservableList
         * **/

        TableColumn<GradeSchedule,?> column = table.getColumns().get(0);
        column.setCellValueFactory(entry -> new SimpleObjectProperty(entry.getValue().getGrade().toString()));

        DoubleBinding width = table.widthProperty().subtract(table.getColumns().size() - 3).divide(table.getColumns().size());
        column.prefWidthProperty().bind(width);
        column.setStyle("-fx-alignment: center;-fx-font-weight: bold");

        for(int count = 1; count < table.getColumns().size(); count++){
            column = table.getColumns().get(count);
            column.prefWidthProperty().bind(width);
            column.setStyle("-fx-alignment: center");

            final Integer index = count - 1;

            column.setCellValueFactory(entry ->{
                Integer id = entry.getValue().getPeriods().get(index);

                if(id == null){
                    return new SimpleObjectProperty("");
                }

                return new SimpleObjectProperty(State.getInstance().assignables.get(id).getDetails());
            });
        }

        table.setItems(weeklyTable.get(day));
    }
    public void populateTable(){
        /**
         * Add data to the ObservableList that is referenced by the tableview
         * **/

        State.getInstance().days.forEach((day, numPeriods) -> {
            ObservableList<GradeSchedule> gradeSchedules = FXCollections.observableArrayList();

            State.getInstance().grades.forEach(grade -> {
                GradeSchedule gradeSchedule = new GradeSchedule(grade, numPeriods);
                ArrayList<Integer> periods = gradeSchedule.getPeriods();

                for(int period = 0; period < periods.size(); period++){
                    Triplet<WeekDay, Grade, Integer> index = new Triplet<>(day, grade, period);
                    periods.set(period, State.getInstance().timetable.get(index));
                }

                gradeSchedules.add(gradeSchedule);
            });

            weeklyTable.put(day, gradeSchedules);
        });

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
    void random(ActionEvent event){

    }
    @FXML
    void arrange(ActionEvent event){

    }
    @FXML
    void position(ActionEvent event){
        Assignable selected = tableAssign.getSelectionModel().getSelectedItem();
        Tab tab = paneTimeTable.getSelectionModel().getSelectedItem();

        if(tab == null){
            Notification.show("Assignment error","You did not select the day.", Alert.AlertType.ERROR);
            return;
        }

        WeekDay day = WeekDay.valueOf(tab.getText().toUpperCase());

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

        ObservableList<GradeSchedule> gradeSchedules = weeklyTable.get(day);

        for(GradeSchedule gradeSchedule: gradeSchedules){
            if(gradeSchedule.getGrade().equals(State.getInstance().sessions.get(selected.getSessionRef()).getGrade())){
                ArrayList<Integer> periods = gradeSchedule.getPeriods();
                Assignable assignable = null;
                Triplet<WeekDay, Grade, Integer> triplet = new Triplet(day, gradeSchedule.getGrade(), period);

                if(State.getInstance().timetable.get(triplet) != null){
                    if(State.getInstance().timetable.get(triplet).equals(selected.getId()))
                        break;

                    assignable = State.getInstance().assignables.get(State.getInstance().timetable.get(triplet));
                    assignable.setRemain(assignable.getRemain() + 1);
                }

                State.getInstance().timetable.put(triplet, selected.getId());

                periods.set(period,selected.getId());
                selected.setRemain(selected.getRemain() - 1);

                AnchorPane pane = (AnchorPane)tab.getContent();
                ((TableView<GradeSchedule>)pane.getChildren().get(0)).refresh();
                tableAssign.refresh();
                State.getInstance().saveRequired = true;

                break;
            }
        }
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
        updateSubjectFilterOptions();
    }

    @FXML
    public void showGradeDialog(ActionEvent event){
        showDataDialog("Grades", "grade-dialog-view.fxml");
        updateGradeFilterOptions();
    }
    @FXML
    public void showEducatorDialog(ActionEvent event){
        showDataDialog("Educators", "educator-dialog-view.fxml");
        updateEducatorFilterOptions();
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
    public void populateAssign(){

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
            populateTable();
            populateStructure();
            applyStructure(event);
            updateTableAssign();
            updateEducatorFilterOptions();
            updateGradeFilterOptions();
            updateSubjectFilterOptions();
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
        System.out.println("action");

        Tab tab = paneTimeTable.getSelectionModel().getSelectedItem();

        if(tab == null){
            Notification.show("Print error","You did not select the day.", Alert.AlertType.ERROR);
            return;
        }

        WeekDay day = WeekDay.valueOf(tab.getText().toUpperCase());
        TableView<GradeSchedule> table = (TableView<GradeSchedule>)((AnchorPane)tab.getContent()).getChildren().get(0);

        PrinterJob printerJob = PrinterJob.createPrinterJob();
        Printer printer = Printer.getDefaultPrinter();
        PageLayout pageLayout = printer.createPageLayout(Paper.A4, PageOrientation.LANDSCAPE,0.1,0.1,0.1,0.1);

        Scale scale = new Scale(pageLayout.getPrintableWidth() / table.getWidth(), pageLayout.getPrintableHeight() / table.getHeight());

        table.getTransforms().add(scale);

        if(printerJob != null && printerJob.showPrintDialog(this.stage)){
            boolean success = printerJob.printPage(pageLayout, table);

            if(success){
                printerJob.endJob();

            }

        }
    }
    @FXML
    public void close(ActionEvent event){
        stage.close();
    }
}