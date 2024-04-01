package com.example.demo.services;

import com.example.demo.models.*;
import com.example.demo.models.Assignable;
import com.example.demo.models.commands.CommandList;
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
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class EducatorViewService extends DemoService{
    private Map<Educator, ObservableList<Rank<WeekDay>>> educatorTable = new HashMap<>();
    public EducatorViewService(TabPane pane){
        super(pane);
    }
    public ObservableList<Rank<WeekDay>> filter(Filter filter, Educator educator){
        return FXCollections.observableArrayList(educatorTable.get(educator).stream().filter(daySchedule -> {
            if(filter.subject != null){
                ArrayList<Pair<Integer, Integer>> periods = daySchedule.getPeriods();
                Boolean foundSubject = false;
                Boolean foundNumber = false;
                Boolean foundDivision = false;

                if(filter.number == null){
                    foundNumber = true;
                }

                if(filter.division == null){
                    foundDivision = true;
                }

                for(int count = 0; count < periods.size() && !foundSubject && !foundNumber && !foundDivision; count++){
                    if(periods.get(count) != null){
                        Assignable assignable = State.getInstance().assignables.get(periods.get(count));
                        Session session = State.getInstance().sessions.get(assignable.getId().getFirst());

                        if(session.getSubject().equals(filter.subject)){
                            foundSubject = true;
                        }else if(assignable.isShare()){
                            session = State.getInstance().sessions.get(assignable.getId().getSecond());

                            if(session.getSubject().equals(filter.subject)){
                                foundSubject = true;
                            }
                        }

                        if(session.getGrade().getNumber().equals(filter.number)){
                            foundNumber = true;
                        }

                        if(session.getGrade().getDivision().equals(filter.division)){
                            foundDivision = true;
                        }
                    }
                }

                if(!foundSubject || !foundNumber || !foundDivision)
                    return false;
            }

            return true;
        }).toList());
    }
    public void updateFilter(Filter filter, TabPane pane){
        Tab tab = pane.getSelectionModel().getSelectedItem();

        if(tab == null){
            Notification.show("Filter error","You did not select the grade.", Alert.AlertType.ERROR);
            return;
        }

        Integer index = pane.getSelectionModel().getSelectedIndex();

        TableView<Rank<WeekDay>> table = (TableView<Rank<WeekDay>>)((AnchorPane)tab.getContent()).getChildren().get(0);
        table.setItems(filter(filter, State.getInstance().educators.get(index)));
        table.refresh();
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
    public void position(TabPane paneTimeTable, Assignable selected, WeekDay day, Integer period){
        Session session = State.getInstance().sessions.get(selected.getId().getFirst());
        Session sessionOther = State.getInstance().sessions.get(selected.getId().getSecond());
        CommandList commandList = new CommandList();

        ObservableList<Rank<WeekDay>> daySchedules = educatorTable.get(session.getEducator());

        for(Rank<WeekDay> daySchedule: daySchedules){
            if(daySchedule.getHeader().equals(day)){
                ArrayList<Pair<Integer, Integer>> periods = daySchedule.getPeriods();
                Triplet<WeekDay, Grade, Integer> triplet = new Triplet<>(daySchedule.getHeader(), session.getGrade(), period);

                PositionCommand command = new PositionCommand(this, selected, triplet);
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

                    PositionCommand command = new PositionCommand(this, selected, triplet);
                    commandList.add(command);

                    break;
                }
            }
        }

        if(!commandList.isEmpty()){
            commandList.execute();
            CommandManager.getInstance().addCommand(commandList);
            commandList.execute();

            for(Tab tab: paneTimeTable.getTabs()){
                if(tab.getText().equals(session.getEducator().toString())){
                    ((TableView)((AnchorPane)tab.getContent()).getChildren().get(0)).refresh();
                    break;
                }
            }
        }
    }

    public void print(TabPane pane,Stage stage){
        Tab tab = pane.getSelectionModel().getSelectedItem();

        if(tab == null){
            Notification.show("Print error","You did not select the day.", Alert.AlertType.ERROR);
            return;
        }

        String[] split = tab.getText().split(" ");
        TableView<Rank<WeekDay>> table = (TableView<Rank<WeekDay>>)((AnchorPane)tab.getContent()).getChildren().get(0);

        PrinterJob printerJob = PrinterJob.createPrinterJob();
        Printer printer = Printer.getDefaultPrinter();
        PageLayout pageLayout = printer.createPageLayout(Paper.A4, PageOrientation.LANDSCAPE,0.1,0.1,0.1,0.1);

        Scale scale = new Scale(pageLayout.getPrintableWidth() / table.getWidth(), pageLayout.getPrintableHeight() / table.getHeight());

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
