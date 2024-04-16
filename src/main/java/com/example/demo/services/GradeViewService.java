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
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class GradeViewService extends DemoService{
    private Map<Grade, ObservableList<Rank<WeekDay>>> gradeTable = new HashMap<>();

    public GradeViewService(DemoController controller){
        super(controller);
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
        for(Tab tab: demoController.getPane().getTabs()){
            String[] split = tab.getText().split(" ");
            Grade grade = new Grade(Integer.parseInt(split[0]), split[1].charAt(0));
            TableView<Rank<WeekDay>> table = (TableView<Rank<WeekDay>>)((AnchorPane)tab.getContent()).getChildren().get(0);
            table.setItems(filter(grade));
            table.refresh();
        }
    }
    public void clearTab(){
        Tab tab = demoController.getPane().getSelectionModel().getSelectedItem();

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
        Tab tab = demoController.getPane().getSelectionModel().getSelectedItem();

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
    public void clearCell(){
        if(ClickableTableCell.lastSelectedCell == null){
            Notification.show("Clear error", "Cell has not been selected.", Alert.AlertType.ERROR);
            return;
        }


        String[] split = demoController.getPane().getSelectionModel().getSelectedItem().getText().split(" ");
        Grade grade = new Grade(Integer.parseInt(split[0]), split[1].charAt(0));
        Integer period = Integer.parseInt(ClickableTableCell.lastSelectedCell.getTableColumn().getText()) - 1;
        Integer rowIndex = ClickableTableCell.lastSelectedCell.getTableRow().getIndex();
        Rank<WeekDay> rank = (Rank<WeekDay>) ClickableTableCell.lastSelectedCell.getTableView().getItems().get(rowIndex);

        LinkedList<Map.Entry<Triplet<WeekDay, Grade, Integer>, Pair<Integer, Integer>>> trash = new LinkedList<>();
        Triplet<WeekDay, Grade, Integer> triplet = TripletManager.get(rank.getHeader(), grade, period);

        for(Map.Entry<Triplet<WeekDay, Grade, Integer>, Pair<Integer, Integer>> entry: State.getInstance().timetable.entrySet()){
            if(entry.getKey().equals(triplet)){
                trash.add(entry);
                break;
            }
        }

        if(!trash.isEmpty()) {
            Command command = new ClearSlotsCommand(trash, demoController);
            command.execute();
            CommandManager.getInstance().addCommand(command);
        }
    }
    @Override
    public void setupTable() {
        demoController.getPane().getTabs().clear();

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
            demoController.getPane().getTabs().add(tab);
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
        ClickableTableCell.instances.clear();
        Integer index = demoController.getPane().getSelectionModel().getSelectedIndex();

        populateTable();
        setupTable();

        if(index >= 0 && index < demoController.getPane().getTabs().size()){
            demoController.getPane().getSelectionModel().select(index);

            String[] split = demoController.getPane().getTabs().get(index).getText().split(" ");
            Grade grade = new Grade(Integer.parseInt(split[0]), split[1].charAt(0));

            Assignable selection = demoController.getTableAssign().getSelectionModel().getSelectedItem();
            demoController.getTableAssign().setItems(FXCollections.observableArrayList(State.getInstance().assignables.values().stream().filter(value -> value.getGrade().equals(grade)).toList()));
            demoController.getTableAssign().setItems(search(demoController.getTxtSearch().getText().toUpperCase(), demoController.getTableAssign().getItems()));
            demoController.getTableAssign().refresh();

            if(selection != null)
                demoController.getTableAssign().getSelectionModel().select(selection);
        }

        ClickableTableCell.lastSelectedCell = null;
    }
    @Override
    public void refreshData(){
        ClickableTableCell.instances.clear();

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

            gradeTable.get(grade).clear();
            gradeTable.get(grade).addAll(daySchedules);
        });

        super.refreshData();
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


        Integer period = Integer.parseInt(ClickableTableCell.lastSelectedCell.getTableColumn().getText()) - 1;
        Integer itemIndex = ClickableTableCell.lastSelectedCell.getTableView().getSelectionModel().getSelectedIndex();
        Rank<WeekDay> item = (Rank<WeekDay>)ClickableTableCell.lastSelectedCell.getTableView().getItems().get(itemIndex);
        WeekDay day = item.getHeader();
        String[] split = demoController.getPane().getSelectionModel().getSelectedItem().getText().split(" ");
        Grade grade = new Grade(Integer.parseInt(split[0]), split[1].charAt(0));

        Triplet<WeekDay, Grade, Integer> triplet = TripletManager.get(day, grade, period);

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
