package com.example.demo.controllers;

import com.example.demo.models.*;
import com.example.demo.models.Assignable;
import com.example.demo.models.commands.ClearTimeTableCommand;
import com.example.demo.models.commands.Command;
import com.example.demo.models.commands.CommandManager;
import com.example.demo.models.commands.UpdateStructureCommand;
import com.example.demo.services.*;
import com.example.demo.utilities.Filter;
import com.example.demo.utilities.Notification;
import com.example.demo.utilities.Pair;
import com.example.demo.utilities.Triplet;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
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
    private Spinner<Integer> spinnerMondayPeriods, spinnerTuesdayPeriods, spinnerWednesdayPeriods, spinnerThursdayPeriods, spinnerFridayPeriods, spinnerSaturdayPeriods, spinnerSundayPeriods, spinnerBreak;
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
    @FXML
    private Menu fileMenu;
    @FXML
    private MenuItem clearTab, clearRow;
    @FXML
    private BorderPane borderPane;
    private Boolean showPanel = true;
    private Menu openRecentMenu = new Menu("Open Recent");
    private DemoService service;
    private ToolBarService toolbarService = new ToolBarService();
    private Accordion rightPanel;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        paneTimeTable.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
            ClickableTableCell.lastSelectedCell = null;

            if(service instanceof GradeViewService){
                if(newTab != null && oldTab != newTab){
                    String[] split = newTab.getText().split(" ");
                    Grade grade = new Grade(Integer.parseInt(split[0]), split[1].charAt(0));

                    tableAssign.setItems(FXCollections.observableArrayList(State.getInstance().assignables.values().stream().filter(value -> value.getGrade().equals(grade)).toList()));
                }
            }else if(service instanceof EducatorViewService){
                if(newTab != null && oldTab != newTab){
                    Integer post = Integer.parseInt(newTab.getText().split(",")[0]);
                    Educator educator = State.getInstance().educators.get(post);

                    tableAssign.setItems(FXCollections.observableArrayList(State.getInstance().assignables.values().stream().filter(value -> value.hasEducator(educator)).toList()));
                }
            }
        });

        filter = new Filter();
        service = new WeekDayViewService(paneTimeTable, tableAssign, this);

        updateFilterOptions();

        columnGrade.setCellValueFactory(entry ->{
            String result = State.getInstance().sessions.get(entry.getValue().getId().getFirst()).getGrade().toString();

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

        service.refresh();

        menuWeekDays.setSelected(true);
        menuWeekDays.setDisable(true);

        ContextMenu contextMenu = new ContextMenu();
        MenuItem clearMenuItem = new MenuItem("Clear");
        MenuItem arrangeOneMenuItem = new MenuItem("Arrange one");
        MenuItem arrangeAllMenuItem = new MenuItem("Arrange All");

        clearMenuItem.setOnAction(event -> {
            Assignable assignable = tableAssign.getSelectionModel().getSelectedItem();

            if(assignable != null) {
                for(Triplet<WeekDay, Grade, Integer> triplet: State.getInstance().timetable.keySet()){
                    Pair<Integer, Integer> reference  = State.getInstance().timetable.get(triplet);

                    if(assignable.getId().equals(reference)){
                        assignable.setRemain(assignable.getRemain() + 1);
                        State.getInstance().timetable.remove(triplet);

                        Assignable pair = assignable.getPair();

                        if(pair != null){
                            Pair<Session, Session> session = pair.getSessions();
                            triplet = new Triplet<>(triplet.getFirst(), session.getFirst().getGrade(), triplet.getThird());

                            pair.setRemain(pair.getRemain() + 1);
                            State.getInstance().timetable.remove(triplet);
                        }
                    }
                }
            }

            tableAssign.refresh();
            service.refresh();
        });

        arrangeOneMenuItem.setOnAction(event -> {

        });

        arrangeAllMenuItem.setOnAction(event -> {

        });

        contextMenu.getItems().addAll(clearMenuItem, arrangeOneMenuItem, arrangeAllMenuItem);

        tableAssign.setRowFactory(tableView -> {
            final TableRow<Assignable> row = new TableRow<>();

//            row.setOnMouseClicked(event -> {
//                if (event.getClickCount() == 2 && !row.isEmpty()) {
//                    // Perform action on double click (if needed)
//                    System.out.println("Double click on: " + row.getItem());
//                }
//            });

            row.setOnContextMenuRequested(event -> {
                contextMenu.show(row, event.getScreenX(), event.getScreenY());
            });

            return row;
        });

        fileMenu.getItems().add(2, openRecentMenu);
        setupRecentMenu();

        clearTab.setText("Clear Day");
        clearRow.setText("Clear Grade");
    }
    public DemoService getService(){
        return service;
    }
    public void setupRecentMenu(){
        openRecentMenu.getItems().clear();

        LinkedList<String> recent = State.getRecent();

        MenuItem item;

        for(int count = 0; count < recent.size(); count++){
            item = new MenuItem((count + 1) + ". " + recent.get(count));
            final String path = recent.get(count);

            item.setOnAction(event -> {
                File file = new File(path);

                if(State.getInstance().open(file)){
                    stage.setTitle(file.getName() + " - TimeTable");
                    service.populateTable();
                    populateStructure();
                    applyStructure(event);
                    updateTableAssign();
                    updateFilterOptions();
                    State.getInstance().saveRequired = false;

                    recent.remove(path);
                    recent.addFirst(path);
                    setupRecentMenu();
                }
            });

            openRecentMenu.getItems().add(item);
        }
    }
    public void updateGradeFilterOptions(){
        comboNumber.getItems().clear();
        comboDivision.getItems().clear();

        comboNumber.getItems().add(null);
        comboDivision.getItems().add(null);

        State.getInstance().grades.forEach(grade ->{
            if(!comboNumber.getItems().contains(grade.getNumber()))
                comboNumber.getItems().add(grade.getNumber());

            if(!comboDivision.getItems().contains(grade.getDivision()))
                comboDivision.getItems().add(grade.getDivision());
        });
    }
    public void updateSubjectFilterOptions(){
        comboSubject.getItems().clear();
        comboSubject.getItems().add(null);

        State.getInstance().subjects.forEach(subject ->{
            if(!comboSubject.getItems().contains(subject))
                comboSubject.getItems().add(subject);
        });
    }
    public void updateEducatorFilterOptions(){
        comboEducator.getItems().clear();
        comboEducator.getItems().add(null);

        State.getInstance().educators.forEach((post, educator) ->{
            if(!comboEducator.getItems().contains(educator))
                comboEducator.getItems().add(educator);
        });
    }
    public void updateFilterOptions(){
        updateSubjectFilterOptions();
        updateEducatorFilterOptions();
        updateGradeFilterOptions();
        updateDayFilterOptions();

        if(service instanceof WeekDayViewService){
            comboDay.getItems().clear();
            comboDay.getItems().add(null);
        }else if(service instanceof GradeViewService){
            comboNumber.getItems().clear();
            comboDivision.getItems().clear();

            comboNumber.getItems().add(null);
            comboDivision.getItems().add(null);
        }else if(service instanceof EducatorViewService){
            comboEducator.getItems().clear();
            comboEducator.getItems().add(null);
        }
    }
    public void updateDayFilterOptions(){
        comboDay.getItems().clear();
        comboDay.getItems().add(null);

        comboDay.getItems().addAll(State.getInstance().days.keySet());
    }
    @FXML
    public void clearFilter(ActionEvent event){
        comboEducator.setValue(null);
        comboSubject.setValue(null);
        comboNumber.setValue(null);
        comboDivision.setValue(null);
        comboDay.setValue(null);

        filter.clear();
        service.setFilterOptions(filter);
        service.filter();
    }
    @FXML
    public void updateFilter(ActionEvent event){
        filter.set(comboNumber.getValue(), comboDivision.getValue(), comboSubject.getValue(), comboEducator.getValue(), comboDay.getValue());
        service.setFilterOptions(filter);
        service.filter();
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
            days.put(WeekDay.FRIDAY, spinnerFridayPeriods.getValue());
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

        service.populateTable();
        service.setupTable();
        State.getInstance().saveRequired = true;
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
    void arrange(ActionEvent event){

    }
    @FXML
    void position(ActionEvent event){
        service.position();
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
        toolbarService.showDataDialog("Subjects", "subject-dialog-view.fxml", this);
        updateTableAssign();
        updateFilterOptions();
    }
    @FXML
    public void showGradeDialog(ActionEvent event){
        toolbarService.showDataDialog("Grades", "grade-dialog-view.fxml", this);
        updateTableAssign();
        updateFilterOptions();
    }
    @FXML
    public void showEducatorDialog(ActionEvent event){
        toolbarService.showDataDialog("Educators", "educator-dialog-view.fxml", this);
        updateTableAssign();
        updateFilterOptions();
    }
    @FXML
    public void showSessionDialog(ActionEvent event){
        toolbarService.showDataDialog("Sessions", "session-dialog-view.fxml", this);
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
    public void clearTable(ActionEvent event){
        Command command = new ClearTimeTableCommand(this);
        command.execute();
        CommandManager.getInstance().addCommand(command);
    }
    @FXML
    public void cleanTab(ActionEvent event){
        service.clearTab();
    }
    @FXML
    public void cleanRow(ActionEvent event){
        service.clearRow();
    }
    @FXML
    public void toggleLeftPanel(ActionEvent event){
        showPanel = !showPanel;

        if(borderPane.getRight() != null)
            rightPanel = (Accordion) borderPane.getRight();

        if(showPanel)
            borderPane.setRight(rightPanel);
        else
            borderPane.setRight(null);

        MenuItem item = (MenuItem)event.getSource();
        item.setText((showPanel ? "Hide":"Show") +  " Right Panel");
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

        clearTab.setText("Clear Day");
        clearRow.setText("Clear Grade");

        service = new WeekDayViewService(paneTimeTable, tableAssign, this);
        service.refresh();

        updateFilterOptions();
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

        clearTab.setText("Clear Grade");
        clearRow.setText("Clear Day");

        service = new GradeViewService(paneTimeTable, tableAssign, this);
        service.refresh();

        updateFilterOptions();
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

        clearTab.setText("Clear Educator");
        clearRow.setText("Clear Day");

        service = new EducatorViewService(paneTimeTable, tableAssign, this);
        service.refresh();

        updateFilterOptions();
    }
    @FXML
    public void newFile(ActionEvent event){
        if(State.getInstance().saveRequired){
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to discard changes?", ButtonType.YES, ButtonType.NO);
            ButtonType result = confirm.showAndWait().orElse(ButtonType.NO);

            if(ButtonType.NO.equals(result))
                return;
        }

        State.getInstance().reset();
        stage.setTitle(State.getInstance().filename + " - TimeTable");
        service.populateTable();
        populateStructure();
        applyStructure(event);
        updateTableAssign();
        updateFilterOptions();
        State.getInstance().saveRequired = false;
    }
    public void populateStructure(){
        checkMonday.setSelected(false);
        spinnerMondayPeriods.getValueFactory().setValue(1);
        checkTuesday.setSelected(false);
        spinnerTuesdayPeriods.getValueFactory().setValue(1);
        checkWednesday.setSelected(false);
        spinnerWednesdayPeriods.getValueFactory().setValue(1);
        checkThursday.setSelected(false);
        spinnerThursdayPeriods.getValueFactory().setValue(1);
        checkFriday.setSelected(false);
        spinnerFridayPeriods.getValueFactory().setValue(1);
        checkSaturday.setSelected(false);
        spinnerSaturdayPeriods.getValueFactory().setValue(1);
        checkSunday.setSelected(false);
        spinnerSundayPeriods.getValueFactory().setValue(1);

        spinnerBreak.getValueFactory().setValue(State.getInstance().breakAfter);

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

        if(file != null && State.getInstance().open(file)){
            stage.setTitle(file.getName() + " - TimeTable");
            service.populateTable();
            populateStructure();
            applyStructure(event);
            updateTableAssign();
            updateFilterOptions();
            State.getInstance().saveRequired = false;

            LinkedList<String> recent = State.getRecent();

            recent.remove(State.getInstance().filepath);
            recent.addFirst(State.getInstance().filepath);

            while(recent.size() > 10)
                recent.removeLast();

            setupRecentMenu();
        }
    }
    @FXML
    public void saveFile(ActionEvent event){
        if(State.getInstance().filename == "Untitled"){
            saveAsFile(event);
        }else{
            State.getInstance().save(new File(State.getInstance().filepath));
        }
    }
    @FXML
    public void saveAsFile(ActionEvent event){
        FileChooser fileChooser = new FileChooser();

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TimeTable files (*.tt)", "*.tt");
        fileChooser.getExtensionFilters().add(extFilter);

        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            State.getInstance().save(file);
            stage.setTitle(file.getName() + " - TimeTable");

            LinkedList<String> recent = State.getRecent();

            recent.remove(State.getInstance().filepath);
            recent.addFirst(State.getInstance().filepath);

            while(recent.size() > 10)
                recent.removeLast();

            setupRecentMenu();
        }
    }
    @FXML
    public void print(ActionEvent event){
        service.print(stage);
    }
    @FXML
    public void export(ActionEvent event){
        FileChooser fileChooser = new FileChooser();

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Microsoft Excel Files (*.xlsx)", "*.xlsx");
        fileChooser.getExtensionFilters().add(extFilter);

        service.export(fileChooser.showSaveDialog(stage));
    }
    @FXML
    public void close(ActionEvent event){
        stage.close();
    }
}