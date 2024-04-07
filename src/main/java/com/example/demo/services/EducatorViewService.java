package com.example.demo.services;

import com.example.demo.controllers.DemoController;
import com.example.demo.models.*;
import com.example.demo.models.Assignable;
import com.example.demo.models.commands.*;
import com.example.demo.utilities.Notification;
import com.example.demo.utilities.Pair;
import com.example.demo.utilities.Triplet;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.print.*;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class EducatorViewService extends DemoService{
    private Map<Educator, ObservableList<Rank<WeekDay>>> educatorTable = new HashMap<>();
    public EducatorViewService(TabPane pane, TableView<Assignable> tableAssign, DemoController controller){
        super(pane, tableAssign, controller);
    }
    @Override
    public void refresh() {
        Integer index = pane.getSelectionModel().getSelectedIndex();

        populateTable();
        setupTable();

        if(index >= 0 && index < pane.getTabs().size()) {
            pane.getSelectionModel().select(index);

            Integer post = Integer.parseInt(pane.getTabs().get(index).getText().split(",")[0]);
            Educator educator = State.getInstance().educators.get(post);

            tableAssign.setItems(FXCollections.observableArrayList(State.getInstance().assignables.values().stream().filter(value -> value.hasEducator(educator)).toList()));
            tableAssign.refresh();
        }
        ClickableTableCell.lastSelectedCell = null;
    }
    public ObservableList<Rank<WeekDay>> filter(Educator educator){
        return FXCollections.observableArrayList(educatorTable.get(educator).stream().filter(daySchedule -> {
            if(filterOptions.subject != null){
                ArrayList<Pair<Integer, Integer>> periods = daySchedule.getPeriods();
                Boolean found = false;

                for(int count = 0; count < periods.size() && !found; count++){
                    if(periods.get(count) != null){
                        Assignable assignable = State.getInstance().assignables.get(periods.get(count));
                        Session session = State.getInstance().sessions.get(assignable.getId().getFirst());

                        if(session.getSubject().equals(filterOptions.subject)){
                            found = true;
                        }else if(assignable.isShare()){
                            session = State.getInstance().sessions.get(assignable.getId().getSecond());

                            if(session.getSubject().equals(filterOptions.subject)){
                                found = true;
                            }
                        }
                    }
                }

                if(!found)
                    return false;
            }

            if(filterOptions.number != null){
                ArrayList<Pair<Integer, Integer>> periods = daySchedule.getPeriods();
                Boolean found = false;

                for(int count = 0; count < periods.size() && !found; count++){
                    if(periods.get(count) != null){
                        Assignable assignable = State.getInstance().assignables.get(periods.get(count));
                        Session session = State.getInstance().sessions.get(assignable.getId().getFirst());

                        if(session.getGrade().getNumber().equals(filterOptions.number)){
                            found = true;
                        }
                    }
                }

                if(!found)
                    return false;
            }

            if(filterOptions.division != null){
                ArrayList<Pair<Integer, Integer>> periods = daySchedule.getPeriods();
                Boolean found = false;

                for(int count = 0; count < periods.size() && !found; count++){
                    if(periods.get(count) != null){
                        Assignable assignable = State.getInstance().assignables.get(periods.get(count));
                        Session session = State.getInstance().sessions.get(assignable.getId().getFirst());

                        if(session.getGrade().getDivision().equals(filterOptions.division)){
                            found = true;
                        }
                    }
                }

                if(!found)
                    return false;
            }

            if(filterOptions.day != null && !daySchedule.getHeader().equals(filterOptions.day)){
                return false;
            }


            return true;
        }).toList());
    }
    public void filter(){
        for(Tab tab: pane.getTabs()){
            Integer post = Integer.parseInt(tab.getText().split(",")[0]);

            TableView<Rank<WeekDay>> table = (TableView<Rank<WeekDay>>)((AnchorPane)tab.getContent()).getChildren().get(0);
            table.setItems(filter(State.getInstance().educators.get(post)));
            table.refresh();
        }
    }
    public void clearTab(){
        Tab tab = pane.getSelectionModel().getSelectedItem();

        if(tab != null){
            Integer post = Integer.parseInt(tab.getText().split(",")[0]);
            Educator educator = State.getInstance().educators.get(post);

            LinkedList<Map.Entry<Triplet<WeekDay, Grade, Integer>, Pair<Integer, Integer>>> trash = new LinkedList<>();

            for(Map.Entry<Triplet<WeekDay, Grade, Integer>, Pair<Integer, Integer>> entry: State.getInstance().timetable.entrySet()){
                if(entry.getValue() != null) {
                    if(State.getInstance().assignables.get(entry.getValue()).hasEducator(educator)) {
                        trash.add(entry);
                    }
                }
            }

            Command command = new ClearSlotsCommand(trash, demoController);
            command.execute();
            CommandManager.getInstance().addCommand(command);
        }
    }
    public void clearRow(){
        Tab tab = pane.getSelectionModel().getSelectedItem();

        if(tab != null){
            Rank<WeekDay> row = ((TableView<Rank<WeekDay>>)((AnchorPane)tab.getContent()).getChildren().get(0)).getSelectionModel().getSelectedItem();

            if(row != null){
                Integer post = Integer.parseInt(tab.getText().split(",")[0]);
                Educator educator = State.getInstance().educators.get(post);

                LinkedList <Map.Entry<Triplet<WeekDay, Grade, Integer>, Pair<Integer, Integer>>> trash = new LinkedList<>();

                for(Map.Entry<Triplet<WeekDay, Grade, Integer>, Pair<Integer, Integer>> entry: State.getInstance().timetable.entrySet()){
                    if(entry.getValue() != null) {
                        if(State.getInstance().assignables.get(entry.getValue()).hasEducator(educator) && row.getHeader().equals(entry.getKey().getFirst())) {
                            trash.add(entry);
                        }
                    }
                }

                Command command = new ClearSlotsCommand(trash, demoController);
                command.execute();
                CommandManager.getInstance().addCommand(command);
            }
        }
    }
    @Override
    public void setupTable() {
        pane.getTabs().clear();

        State.getInstance().educators.forEach((post, educator) -> {
            TableView<Rank<WeekDay>> daySchedule = new TableView<>();

//            ContextMenu contextMenu = new ContextMenu();
//            MenuItem clearCellMenuItem = new MenuItem("Clear cell");
//            MenuItem clearRowMenuItem = new MenuItem("Clear row");
//            MenuItem clearColMenuItem = new MenuItem("Clear column");
//            MenuItem clearTableMenuItem = new MenuItem("Clear table");
//
//            clearCellMenuItem.setOnAction(event -> {
//                TablePosition<Row<WeekDay>, ?> editEvent = daySchedule.getEditingCell();
//                if (editEvent != null) {
//                    editEvent.
//                }
//            });
//            clearRowMenuItem.setOnAction(event -> {});
//            clearColMenuItem.setOnAction(event -> {});
//            clearTableMenuItem.setOnAction(event -> {});

            AnchorPane anchorPane = new AnchorPane(daySchedule);
            AnchorPane.setTopAnchor(daySchedule, 0.0);
            AnchorPane.setLeftAnchor(daySchedule, 0.0);
            AnchorPane.setBottomAnchor(daySchedule, 0.0);
            AnchorPane.setRightAnchor(daySchedule, 0.0);

            Tab tab = new Tab(educator.toString(), anchorPane);

            daySchedule.getColumns().add(new TableColumn<>("Day"));
            Integer max = State.getInstance().days.values().stream().max(Comparator.naturalOrder()).get();

            for(int count = 0; count < max; count++)
                daySchedule.getColumns().add(new TableColumn<>(Integer.toString(count + 1)));

            setupEducatorTable(educator, daySchedule);
            pane.getTabs().add(tab);
        });
    }
    public void setupEducatorTable(Educator educator, TableView<Rank<WeekDay>> table){
        TableColumn<Rank<WeekDay>,?> column = table.getColumns().get(0);
        column.setCellValueFactory(entry -> new SimpleObjectProperty(entry.getValue().getHeader().toString()));

        DoubleBinding width = table.widthProperty().subtract(table.getColumns().size() - 3).divide(table.getColumns().size());
        column.prefWidthProperty().bind(width);
        column.setStyle("-fx-alignment: center;-fx-font-weight: bold");

        for(int count = 1; count < table.getColumns().size(); count++){
            column = table.getColumns().get(count);
            column.prefWidthProperty().bind(width);
            column.setStyle("-fx-alignment: center");

            final Integer index = count - 1;

            column.setCellValueFactory(entry ->{
                Pair<Integer, Integer> id = null;

                try {
                    id = entry.getValue().getPeriods().get(index);
                }catch(IndexOutOfBoundsException error){

                }

                if(id == null){
                    return new SimpleObjectProperty("");
                }

                Assignable assignable = State.getInstance().assignables.get(id);
                Session session = State.getInstance().sessions.get(assignable.getId().getFirst());

                if(!session.getEducator().equals(educator)){
                    if(assignable.getId().getFirst() == null){
                        return new SimpleObjectProperty("");
                    }

                    session = State.getInstance().sessions.get(assignable.getId().getFirst());

                    if(!session.getEducator().equals(educator)){
                        return new SimpleObjectProperty("");
                    }
                }

                return new SimpleObjectProperty(session.getGrade().getNumber() + session.getGrade().getDivision().toString() + " " + session.getSubject());
            });
            column.setCellFactory(value -> new ClickableTableCell<>(true));
        }

        table.setItems(educatorTable.get(educator));
    }
    @Override
    public void populateTable() {
        educatorTable.clear();
        State.getInstance().educators.forEach((post, educator) -> {
            ObservableList<Rank<WeekDay>> daySchedules = FXCollections.observableArrayList();

            for(WeekDay day: WeekDay.values()){
                Integer numPeriods = State.getInstance().days.get(day);

                if(numPeriods != null){
                    Rank<WeekDay> daySchedule = new Rank<>(day, numPeriods);
                    ArrayList<Pair<Integer, Integer>> periods = daySchedule.getPeriods();

                    for(int period = 0; period < periods.size(); period++){
                        final Integer finalPeriod = period;

                        State.getInstance().timetable.forEach((triplet, id) -> {
                            if(triplet.getFirst().equals(day) && triplet.getThird().equals(finalPeriod)){
                                Assignable assignable = State.getInstance().assignables.get(id);
                                Session session = State.getInstance().sessions.get(assignable.getId().getFirst());

                                if(!session.getEducator().equals(educator)){
                                    if(assignable.getId().getFirst() != null){
                                        session = State.getInstance().sessions.get(assignable.getId().getFirst());

                                        if(session.getEducator().equals(educator)){
                                            periods.set(finalPeriod, State.getInstance().timetable.get(new Triplet<>(day, session.getGrade(), finalPeriod)));
                                        }
                                    }
                                }else{
                                    periods.set(finalPeriod, State.getInstance().timetable.get(new Triplet<>(day, session.getGrade(), finalPeriod)));
                                }
                            }
                        });
                    }

                    daySchedules.add(daySchedule);
                }
            }

            educatorTable.put(educator, daySchedules);
        });
    }
    public void position(){
        Assignable selected = tableAssign.getSelectionModel().getSelectedItem();

        if(selected == null){
            Notification.show("Position Error","Lesson has not been selected.", Alert.AlertType.ERROR);
            return;
        }

        if(ClickableTableCell.lastSelectedCell == null){
            Notification.show("Position Error", "Time table slot has not been selected.", Alert.AlertType.ERROR);
            return;
        }

        Integer period  = Integer.parseInt(ClickableTableCell.lastSelectedCell.getTableColumn().getText()) - 1;
        Rank<WeekDay> row = (Rank<WeekDay>) ClickableTableCell.lastSelectedCell.getTableView().getSelectionModel().getSelectedItem();
        WeekDay day = row.getHeader();

        if(period >= State.getInstance().days.get(day)){
            Notification.show("Position Error", "Week day '"+day+"' only has " + State.getInstance().days.get(day) + " periods.", Alert.AlertType.ERROR);
            return;
        }

        Session session = State.getInstance().sessions.get(selected.getId().getFirst());
        Session sessionOther = State.getInstance().sessions.get(selected.getId().getSecond());
        CommandList commandList = new CommandList();

        ObservableList<Rank<WeekDay>> daySchedules = educatorTable.get(session.getEducator());

        for(Rank<WeekDay> daySchedule: daySchedules){
            if(daySchedule.getHeader().equals(day)){
                ArrayList<Pair<Integer, Integer>> periods = daySchedule.getPeriods();
                Triplet<WeekDay, Grade, Integer> triplet = new Triplet<>(daySchedule.getHeader(), session.getGrade(), period);

                PositionCommand command = new PositionCommand(demoController, selected, triplet);
                commandList.add(command);

                break;
            }
        }

        if(sessionOther != null){
            daySchedules = educatorTable.get(sessionOther.getEducator());

            for(Rank<WeekDay> daySchedule: daySchedules){
                if(daySchedule.getHeader().equals(day)){
                    ArrayList<Pair<Integer, Integer>> periods = daySchedule.getPeriods();
                    Triplet<WeekDay, Grade, Integer> triplet = new Triplet<>(daySchedule.getHeader(), session.getGrade(), period);

                    PositionCommand command = new PositionCommand(demoController, selected, triplet);
                    commandList.add(command);

                    break;
                }
            }
        }

        if(!commandList.isEmpty()){
            commandList.execute();
            CommandManager.getInstance().addCommand(commandList);
            commandList.execute();

            for(Tab tab: pane.getTabs()){
                if(tab.getText().equals(session.getEducator().toString())){
                    ((TableView)((AnchorPane)tab.getContent()).getChildren().get(0)).refresh();
                    break;
                }
            }
        }
    }
    public void print(Stage stage){
        Tab tab = pane.getSelectionModel().getSelectedItem();

        if(tab == null){
            Notification.show("Print error","You did not select the day.", Alert.AlertType.ERROR);
            return;
        }

        TableView<Rank<WeekDay>> original = (TableView<Rank<WeekDay>>)((AnchorPane)tab.getContent()).getChildren().get(0);
        TableView<Rank<WeekDay>> table = new TableView<>();
        table.setMinWidth(original.getWidth());
        table.setMinHeight(original.getHeight());

        table.getColumns().add(new TableColumn<>("Day"));
        Integer max = State.getInstance().days.values().stream().max(Comparator.naturalOrder()).get();

        for(int count = 0; count < max; count++)
            table.getColumns().add(new TableColumn<>(Integer.toString(count + 1)));

        setupEducatorTable(State.getInstance().educators.get(Integer.parseInt(tab.getText().split(",")[0])), table);

        PrinterJob printerJob = PrinterJob.createPrinterJob();
        Printer printer = Printer.getDefaultPrinter();
        PageLayout pageLayout = printer.createPageLayout(Paper.A4, PageOrientation.LANDSCAPE,0.1,0.1,0.1,0.1);

        Scale scale = new Scale(pageLayout.getPrintableWidth() / original.getWidth(), pageLayout.getPrintableHeight() / original.getHeight());

        table.getTransforms().add(scale);

        if(printerJob != null && printerJob.showPrintDialog(stage)){
            boolean success = printerJob.printPage(pageLayout, table);

            if(success)
                printerJob.endJob();

        }
    }
    @Override
    public void export(File file) {
        Workbook workbook = new XSSFWorkbook();

        educatorTable.forEach((educator, ranks) -> {
            Sheet sheet = workbook.createSheet(educator.toString());

            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 14);

            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);

            Row row = sheet.createRow(0);

            Cell cell = row.createCell(0);
            cell.setCellValue("Day");
            cell.setCellStyle(headerCellStyle);

            Integer periods = State.getInstance().days.values().stream().max(Comparator.naturalOrder()).get();

            for(Integer i = 1; i <= periods; i++) {
                cell = row.createCell(i);
                cell.setCellValue(i);
                cell.setCellStyle(headerCellStyle);
            }

            for(Integer i = 0; i < ranks.size(); i++){
                row = sheet.createRow(i + 1);

                cell = row.createCell(0);
                cell.setCellValue(ranks.get(i).getHeader().toString());
                cell.setCellStyle(headerCellStyle);
                ArrayList<Pair<Integer, Integer>> references = ranks.get(i).getPeriods();

                for(Integer col = 1; col <= periods; col++){
                    cell = row.createCell(col);
                    Assignable assignable = State.getInstance().assignables.get(references.get(col - 1));

                    if(assignable == null)
                        cell.setCellValue("");
                    else
                        cell.setCellValue(assignable.getDetails());
                }
            }

            for(int i = 0; i <= periods; i ++){
                sheet.autoSizeColumn(i);
            }
        });

        try(FileOutputStream fileOutputStream = new FileOutputStream(file)){
            workbook.write(fileOutputStream);
            fileOutputStream.close();

            workbook.close();
        }catch(FileNotFoundException error){
            error.printStackTrace();
            Notification.show("Export error.", "Failed to export.", Alert.AlertType.ERROR);
        }catch(IOException error){
            error.printStackTrace();
            Notification.show("Export error.", "Failed to export.", Alert.AlertType.ERROR);
        }
    }
}
