package com.example.demo.services;

import com.example.demo.controllers.DemoController;
import com.example.demo.models.*;
import com.example.demo.models.Assignable;
import com.example.demo.models.commands.ClearSlotsCommand;
import com.example.demo.models.commands.Command;
import com.example.demo.models.commands.CommandManager;
import com.example.demo.models.commands.PositionCommand;
import com.example.demo.utilities.*;
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
import java.util.*;

public class WeekDayViewService extends DemoService{
    private Map<WeekDay, ObservableList<Rank<Grade>>> weeklyTable = new HashMap<>();
    public WeekDayViewService(DemoController demoController){
        super(demoController);
    }
    public void clearTab(){
        Tab tab = demoController.getPane().getSelectionModel().getSelectedItem();

        if(tab != null){
            WeekDay day = WeekDay.valueOf(tab.getText().toUpperCase());

            LinkedList <Map.Entry<Triplet<WeekDay, Grade, Integer>, Pair<Integer, Integer>>> trash = new LinkedList<>();

            for(Map.Entry<Triplet<WeekDay, Grade, Integer>, Pair<Integer, Integer>> entry: State.getInstance().timetable.entrySet()){
                if(entry.getKey().getFirst().equals(day)){
                    trash.add(entry);
                }
            }

            Command command = new ClearSlotsCommand(trash, demoController);
            command.execute();
            CommandManager.getInstance().addCommand(command);
        }
    }
    public void clearRow(){
        Tab tab = demoController.getPane().getSelectionModel().getSelectedItem();

        if(tab != null){
            Rank<Grade> row = ((TableView<Rank<Grade>>)((AnchorPane)tab.getContent()).getChildren().get(0)).getSelectionModel().getSelectedItem();

            if(row != null){
                WeekDay day = WeekDay.valueOf(tab.getText().toUpperCase());

                LinkedList <Map.Entry<Triplet<WeekDay, Grade, Integer>, Pair<Integer, Integer>>> trash = new LinkedList<>();

                for(Map.Entry<Triplet<WeekDay, Grade, Integer>, Pair<Integer, Integer>> entry: State.getInstance().timetable.entrySet()){
                    if(entry.getKey().getFirst().equals(day) && entry.getKey().getSecond().equals(row.getHeader())){
                        trash.add(entry);
                    }
                }

                Command command = new ClearSlotsCommand(trash, demoController);
                command.execute();
                CommandManager.getInstance().addCommand(command);
            }
        }
    }
    public void setupTable(){
        demoController.getPane().getTabs().clear();
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

        demoController.getPane().getTabs().addAll(Arrays.stream(tabs).filter(value -> value != null).toList());

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
            column.setStyle("-fx-alignment: center");
            column.prefWidthProperty().bind(width);

            final Integer index = count - 1;

            column.setCellValueFactory(entry ->{
                Pair<Integer, Integer> id = entry.getValue().getPeriods().get(index);

                if(id == null){
                    return new SimpleObjectProperty("");
                }

                return new SimpleObjectProperty(State.getInstance().assignables.get(id).getDetails());
            });
            column.setCellFactory(value -> new ClickableTableCell<>());
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
                    Triplet<WeekDay, Grade, Integer> index = TripletManager.get(day, grade, period);
                    periods.set(period, State.getInstance().timetable.get(index));
                }

                gradeSchedules.add(gradeSchedule);
            });

            weeklyTable.put(day, gradeSchedules);
        });
    }
    public ObservableList<Rank<Grade>> filter(WeekDay day){
        return FXCollections.observableArrayList(weeklyTable.get(day).stream().filter(gradeSchedule -> {
            if(filterOptions.number != null && !gradeSchedule.getHeader().getNumber().equals(filterOptions.number)){
                return false;
            }

            if(filterOptions.division != null && !gradeSchedule.getHeader().getDivision().equals(filterOptions.division)){
                return false;
            }

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

            return true;
        }).toList());
    }
    public void filter(){
        for(Tab tab: demoController.getPane().getTabs()){
            TableView<Rank<Grade>> table = (TableView<Rank<Grade>>)((AnchorPane)tab.getContent()).getChildren().get(0);
            WeekDay day = WeekDay.valueOf(tab.getText().toUpperCase());
            table.setItems(filter(day));
            table.refresh();
        }
    }
    public void position(){
        Assignable assignable = demoController.getTableAssign().getSelectionModel().getSelectedItem();

        if(assignable == null){
            Notification.show("Position error.", "Lesson not selected.", Alert.AlertType.ERROR);
            return;
        }

        if(ClickableTableCell.lastSelectedCell == null){
            Notification.show("Position error.", "Table cell not selected.", Alert.AlertType.ERROR);
            return;
        }

        if(demoController.getPane().getSelectionModel().getSelectedItem() == null){
            return;
        }

        if(assignable.getRemain() == 0){
            return;
        }

        WeekDay day = WeekDay.valueOf(demoController.getPane().getSelectionModel().getSelectedItem().getText().toUpperCase());
        Integer period = Integer.parseInt(ClickableTableCell.lastSelectedCell.getTableColumn().getText()) - 1;
        Integer itemIndex = ClickableTableCell.lastSelectedCell.getTableView().getSelectionModel().getSelectedIndex();
        Rank<Grade> item = (Rank<Grade>)ClickableTableCell.lastSelectedCell.getTableView().getItems().get(itemIndex);

        if(!assignable.getGrade().equals(item.getHeader())){
            Notification.show("Position error.", "Cell grade must be equal to lesson grade.", Alert.AlertType.ERROR);
            return;
        }

        Triplet<WeekDay, Grade, Integer> triplet = TripletManager.get(day, item.getHeader(), period);

        if(State.getInstance().timetable.get(triplet) != null){
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Replace the contents of the cell ?", ButtonType.YES, ButtonType.NO);

            ButtonType result = confirm.showAndWait().orElse(ButtonType.NO);

            if(ButtonType.NO.equals(result))
                return;
        }

        Assignable pair = assignable.getPair();

        if(pair != null){
            Triplet<WeekDay, Grade, Integer> pairTriplet = TripletManager.get(triplet.getFirst(), pair.getGrade(), triplet.getThird());

            if(State.getInstance().timetable.get(pairTriplet) != null){
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "This lesson has a pair which occupies a cell with contents. Replace the contents of the cell ?", ButtonType.YES, ButtonType.NO);

                ButtonType result = confirm.showAndWait().orElse(ButtonType.NO);

                if(ButtonType.NO.equals(result))
                    return;
            }

        }

        Command command = new PositionCommand(demoController, assignable, triplet);
        command.execute();
        CommandManager.getInstance().addCommand(command);
    }
    public void print(Stage stage){
        Tab tab = demoController.getPane().getSelectionModel().getSelectedItem();

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

