package com.example.demo.services;

import com.example.demo.models.*;
import com.example.demo.models.Assignable;
import com.example.demo.models.commands.CommandManager;
import com.example.demo.models.commands.PositionCommand;
import com.example.demo.utilities.Filter;
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
import org.apache.poi.xssf.usermodel.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class WeekDayViewService extends DemoService{
    private Map<WeekDay, ObservableList<Rank<Grade>>> weeklyTable = new HashMap<>();
    public WeekDayViewService(TabPane pane, TableView<Assignable> tableAssign){
        super(pane, tableAssign);
    }
    public void setupTable(){
        pane.getTabs().clear();
        Tab[] tabs = new Tab[7];

        State.getInstance().days.forEach((day, periods) ->{

            TableView<Rank<Grade>> daySchedule = new TableView<>();
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

        pane.getTabs().addAll(Arrays.stream(tabs).filter(value -> value != null).toList());
    }

    public void setupDayTable(WeekDay day, TableView<Rank<Grade>> table){
        /**
         * setup the columns for a table and add the referenced ObservableList
         * **/

        TableColumn<Rank<Grade>,?> column = table.getColumns().get(0);
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
                Pair<Integer, Integer> id = entry.getValue().getPeriods().get(index);

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

        weeklyTable.clear();
        State.getInstance().days.forEach((day, numPeriods) -> {
            ObservableList<Rank<Grade>> gradeSchedules = FXCollections.observableArrayList();

            State.getInstance().grades.forEach(grade -> {
                Rank<Grade> gradeSchedule = new Rank<>(grade, numPeriods);
                ArrayList<Pair<Integer, Integer>> periods = gradeSchedule.getPeriods();

                for(int period = 0; period < periods.size(); period++){
                    Triplet<WeekDay, Grade, Integer> index = new Triplet<>(day, grade, period);
                    periods.set(period, State.getInstance().timetable.get(index));
                }

                gradeSchedules.add(gradeSchedule);
            });

            weeklyTable.put(day, gradeSchedules);
        });
    }
    public ObservableList<Rank<Grade>> filter(Filter filter, WeekDay day){
        return FXCollections.observableArrayList(weeklyTable.get(day).stream().filter(gradeSchedule -> {
            if(filter.number != null && !gradeSchedule.getHeader().getNumber().equals(filter.number)){
                return false;
            }

            if(filter.division != null && !gradeSchedule.getHeader().getDivision().equals(filter.division)){
                return false;
            }

            if(filter.subject != null){
                ArrayList<Pair<Integer, Integer>> periods = gradeSchedule.getPeriods();
                Boolean found = false;

                for(int count = 0; count < periods.size() && !found; count++){
                    if(periods.get(count) != null){
                        Assignable assignable = State.getInstance().assignables.get(periods.get(count));
                        Session session = State.getInstance().sessions.get(assignable.getId().getFirst());

                        if(session.getSubject().equals(filter.subject)){
                            found = true;
                        }else if(assignable.isShare()){
                            session = State.getInstance().sessions.get(assignable.getId().getSecond());

                            if(session.getSubject().equals(filter.subject)){
                                found = true;
                            }
                        }
                    }
                }

                if(!found)
                    return false;
            }

            if(filter.educator != null){
                ArrayList<Pair<Integer, Integer>> periods = gradeSchedule.getPeriods();
                Boolean found = false;

                for(int count = 0; count < periods.size() && !found; count++){
                    if(periods.get(count) != null){
                        Assignable assignable = State.getInstance().assignables.get(periods.get(count));
                        Session session = State.getInstance().sessions.get(assignable.getId().getFirst());

                        if(filter.educator.equals(session.getEducator()))
                            found = true;
                    }
                }

                if(!found)
                    return false;
            }

            return true;
        }).toList());
    }
    public void updateFilter(Filter filter, TabPane pane){

        Tab tab = pane.getSelectionModel().getSelectedItem();

        if(tab == null){
            Notification.show("Filter error","You did not select the day.", Alert.AlertType.ERROR);
            return;
        }

        WeekDay day = WeekDay.valueOf(tab.getText().toUpperCase());
        TableView<Rank<Grade>> table = (TableView<Rank<Grade>>)((AnchorPane)tab.getContent()).getChildren().get(0);
        table.setItems(filter(filter, day));
        table.refresh();
    }

    public void position(TabPane paneTimeTable, Assignable selected, WeekDay day, Integer period){
        ObservableList<Rank<Grade>> gradeSchedules = weeklyTable.get(day);

        for(Rank<Grade> gradeSchedule: gradeSchedules){
            if(gradeSchedule.getHeader().equals(State.getInstance().sessions.get(selected.getId().getFirst()).getGrade())){
                ArrayList<Pair<Integer, Integer>> periods = gradeSchedule.getPeriods();
                Triplet<WeekDay, Grade, Integer> triplet = new Triplet<>(day, gradeSchedule.getHeader(), period);

                PositionCommand command = new PositionCommand(this, selected, triplet);
                CommandManager.getInstance().addCommand(command);
                command.execute();

                break;
            }
        }
    }

    public void print(TabPane pane, Stage stage){
        Tab tab = pane.getSelectionModel().getSelectedItem();

        if(tab == null){
            Notification.show("Print error","You did not select the day.", Alert.AlertType.ERROR);
            return;
        }

        WeekDay day = WeekDay.valueOf(tab.getText().toUpperCase());
        TableView<Rank<Grade>> original = (TableView<Rank<Grade>>)((AnchorPane)tab.getContent()).getChildren().get(0);
        TableView<Rank<Grade>> table = new TableView<>();
        table.setMinHeight(original.getHeight());
        table.setMinWidth(original.getWidth());

        table.getColumns().add(new TableColumn<>("Grade"));

        for(int count = 0; count < State.getInstance().days.get(day); count++)
            table.getColumns().add(new TableColumn<>(Integer.toString(count + 1)));

        setupDayTable(day, table);

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
    public void export(File file){
        Workbook workbook = new XSSFWorkbook();

        weeklyTable.forEach((day, ranks) -> {
            Sheet sheet = workbook.createSheet(day.toString());

            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 14);

            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);

            Row row = sheet.createRow(0);

            Cell cell = row.createCell(0);
            cell.setCellValue("Grade");
            cell.setCellStyle(headerCellStyle);

            Integer periods = State.getInstance().days.get(day);

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
