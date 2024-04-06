package com.example.demo.services;

import com.example.demo.controllers.DemoController;
import com.example.demo.models.*;
import com.example.demo.models.Assignable;
import com.example.demo.models.commands.ClearSlotsCommand;
import com.example.demo.models.commands.Command;
import com.example.demo.models.commands.CommandManager;
import com.example.demo.utilities.Notification;
import com.example.demo.utilities.Pair;
import com.example.demo.utilities.Triplet;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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

public class GradeViewService extends DemoService{
    private Map<Grade, ObservableList<Rank<WeekDay>>> gradeTable = new HashMap<>();

    public GradeViewService(TabPane pane, TableView<Assignable> tableAssign, DemoController controller){
        super(pane, tableAssign, controller);
        ClickableTableCell.lastSelectedCell = null;
    }
    public ObservableList<Rank<WeekDay>> filter(Grade grade){
        return FXCollections.observableArrayList(gradeTable.get(grade).stream().filter(gradeSchedule -> {
            if(filterOptions.subject != null){
                ArrayList<Pair<Integer, Integer>> periods = gradeSchedule.getPeriods();
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

            if(filterOptions.educator != null){
                ArrayList<Pair<Integer, Integer>> periods = gradeSchedule.getPeriods();
                Boolean found = false;

                for(int count = 0; count < periods.size() && !found; count++){
                    if(periods.get(count) != null){
                        Assignable assignable = State.getInstance().assignables.get(periods.get(count));
                        Session session = State.getInstance().sessions.get(assignable.getId().getFirst());

                        if(filterOptions.educator.equals(session.getEducator()))
                            found = true;
                    }
                }

                if(!found)
                    return false;
            }

            if(filterOptions.day != null && !gradeSchedule.getHeader().equals(filterOptions.day)){
                return false;
            }

            return true;
        }).toList());
    }
    public void filter(){
        for(Tab tab: pane.getTabs()){
            String[] split = tab.getText().split(" ");
            Grade grade = new Grade(Integer.parseInt(split[0]), split[1].charAt(0));
            TableView<Rank<WeekDay>> table = (TableView<Rank<WeekDay>>)((AnchorPane)tab.getContent()).getChildren().get(0);
            table.setItems(filter(grade));
            table.refresh();
        }
    }
    public void clearTab(){
        Tab tab = pane.getSelectionModel().getSelectedItem();

        if(tab != null){
            String[] split = tab.getText().split(" ");
            Grade grade = new Grade(Integer.parseInt(split[0]), split[1].charAt(0));

            LinkedList <Map.Entry<Triplet<WeekDay, Grade, Integer>, Pair<Integer, Integer>>> trash = new LinkedList<>();

            for(Map.Entry<Triplet<WeekDay, Grade, Integer>, Pair<Integer, Integer>> entry: State.getInstance().timetable.entrySet()){
                if(entry.getKey().getSecond().equals(grade)){
                    trash.add(entry);
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
                String[] split = tab.getText().split(" ");
                Grade grade = new Grade(Integer.parseInt(split[0]), split[1].charAt(0));

                LinkedList <Map.Entry<Triplet<WeekDay, Grade, Integer>, Pair<Integer, Integer>>> trash = new LinkedList<>();

                for(Map.Entry<Triplet<WeekDay, Grade, Integer>, Pair<Integer, Integer>> entry: State.getInstance().timetable.entrySet()){
                    if(entry.getKey().getSecond().equals(grade) && entry.getKey().getFirst().equals(row.getHeader())){
                        trash.add(entry);
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

        State.getInstance().grades.forEach(grade -> {
            TableView<Rank<WeekDay>> daySchedule = new TableView<>();

            AnchorPane anchorPane = new AnchorPane(daySchedule);
            AnchorPane.setTopAnchor(daySchedule, 0.0);
            AnchorPane.setLeftAnchor(daySchedule, 0.0);
            AnchorPane.setBottomAnchor(daySchedule, 0.0);
            AnchorPane.setRightAnchor(daySchedule, 0.0);

            Tab tab = new Tab(grade.toString(),anchorPane);

            daySchedule.getColumns().add(new TableColumn<>("Day"));
            Integer max = State.getInstance().days.values().stream().max(Comparator.naturalOrder()).get();

            for(int count = 0; count < max; count++)
                daySchedule.getColumns().add(new TableColumn<>(Integer.toString(count + 1)));

            setupGradeTable(grade, daySchedule);
            pane.getTabs().add(tab);
        });
    }

    public void setupGradeTable(Grade grade, TableView<Rank<WeekDay>> table){
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

                return new SimpleObjectProperty(State.getInstance().assignables.get(id).getDetails());
            });
            column.setCellFactory(value -> new ClickableTableCell<>());
        }

        table.setItems(gradeTable.get(grade));
    }

    @Override
    public void refresh() {
        Integer index = pane.getSelectionModel().getSelectedIndex();

        populateTable();
        setupTable();

        if(index > 0 && index < pane.getTabs().size()){
            pane.getSelectionModel().select(index);

            String[] split = pane.getTabs().get(index).getText().split(" ");
            Grade grade = new Grade(Integer.parseInt(split[0]), split[1].charAt(0));

            tableAssign.setItems(FXCollections.observableArrayList(State.getInstance().assignables.values().stream().filter(value -> value.getGrade().equals(grade)).toList()));
            tableAssign.refresh();
        }
    }

    @Override
    public void populateTable() {
        gradeTable.clear();
        State.getInstance().grades.forEach(grade -> {
            ObservableList<Rank<WeekDay>> daySchedules = FXCollections.observableArrayList();

            for(WeekDay day: WeekDay.values()){
                Integer numPeriods = State.getInstance().days.get(day);

                if(numPeriods != null){
                    Rank<WeekDay> daySchedule = new Rank<>(day, numPeriods);
                    ArrayList<Pair<Integer, Integer>> periods = daySchedule.getPeriods();

                    for(int period = 0; period < periods.size(); period++){
                        Triplet<WeekDay, Grade, Integer> index = new Triplet<>(day, grade, period);
                        periods.set(period, State.getInstance().timetable.get(index));
                    }

                    daySchedules.add(daySchedule);
                }
            }

            gradeTable.put(grade, daySchedules);
        });
    }

    public void position(){
//        Grade grade = State.getInstance().sessions.get(selected.getId().getFirst()).getGrade();
//        ObservableList<Rank<WeekDay>> daySchedules = gradeTable.get(grade);
//
//        for(Rank<WeekDay> daySchedule: daySchedules){
//            if(daySchedule.getHeader().equals(day)){
//                ArrayList<Pair<Integer, Integer>> periods = daySchedule.getPeriods();
//                Triplet<WeekDay, Grade, Integer> triplet = new Triplet(daySchedule.getHeader(), grade, period);
//
//                PositionCommand command = new PositionCommand(demoController, selected, triplet);
//                CommandManager.getInstance().addCommand(command);
//                command.execute();
//
//                break;
//            }
//        }
    }

    public void print(Stage stage){
        Tab tab = pane.getSelectionModel().getSelectedItem();

        if(tab == null){
            Notification.show("Print error","You did not select the day.", Alert.AlertType.ERROR);
            return;
        }

        String[] split = tab.getText().split(" ");
        Grade grade = new Grade(Integer.parseInt(split[0]), split[1].charAt(0));
        TableView<Rank<WeekDay>> original = (TableView<Rank<WeekDay>>)((AnchorPane)tab.getContent()).getChildren().get(0);
        TableView<Rank<WeekDay>> table = new TableView<>();
        table.setMinWidth(original.getWidth());
        table.setMinHeight(original.getHeight());

        table.getColumns().add(new TableColumn<>("Day"));
        Integer max = State.getInstance().days.values().stream().max(Comparator.naturalOrder()).get();

        for(int count = 0; count < max; count++)
            table.getColumns().add(new TableColumn<>(Integer.toString(count + 1)));

        setupGradeTable(grade, table);

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

        gradeTable.forEach((grade, ranks) -> {
            Sheet sheet = workbook.createSheet(grade.toString());

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
