package com.example.demo.services;

import com.example.demo.comparators.AssignableComparator;
import com.example.demo.controllers.DemoController;
import com.example.demo.models.*;
import com.example.demo.models.Assignable;
import com.example.demo.models.commands.*;
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

public class EducatorViewService extends DemoService{
    private Map<Educator, ObservableList<Rank<WeekDay>>> educatorTable = new HashMap<>();
    public EducatorViewService(DemoController controller){
        super(controller);
    }
    @Override
    public void lessonViewRefresh() {
        Integer index = demoController.getPane().getSelectionModel().getSelectedIndex();

        if(index >= 0 && index < demoController.getPane().getTabs().size()) {
            demoController.getPane().getSelectionModel().select(index);

            Integer post = Integer.parseInt(demoController.getPane().getTabs().get(index).getText().split(",")[0]);
            Educator educator = State.getInstance().educators.get(post);

            Assignable selection = demoController.getTableAssign().getSelectionModel().getSelectedItem();
            demoController.getTableAssign().setItems(FXCollections.observableArrayList(State.getInstance().assignables.values().stream().filter(value -> value.hasEducator(educator)).toList()));
            demoController.getTableAssign().setItems(search(demoController.getTxtSearch().getText().toUpperCase(), demoController.getTableAssign().getItems()));
            demoController.getTableAssign().getItems().sort(new AssignableComparator());
            demoController.getTableAssign().refresh();

            if(selection != null)
                demoController.getTableAssign().getSelectionModel().select(selection);
        }
    }
    @Override
    public void dataRefresh(Job job){
        ClickableTableCell.instances.clear();
        int count = 0;

        for(Integer post: State.getInstance().educators.keySet()){
            Educator educator = State.getInstance().educators.get(post);
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

                count++;
                job.progress(count, (long)State.getInstance().educators.size() * WeekDay.values().length);
            }

            educatorTable.get(educator).clear();
            educatorTable.get(educator).addAll(daySchedules);
        }
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
        for(Tab tab: demoController.getPane().getTabs()){
            Integer post = Integer.parseInt(tab.getText().split(",")[0]);

            TableView<Rank<WeekDay>> table = (TableView<Rank<WeekDay>>)((AnchorPane)tab.getContent()).getChildren().get(0);
            table.setItems(filter(State.getInstance().educators.get(post)));
            table.refresh();
        }
    }
    public void clearTab(){
        Tab tab = demoController.getPane().getSelectionModel().getSelectedItem();

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

            CommandManager.getInstance().addCommand(new ClearSlotsCommand(trash));
        }
    }
    public void clearRow(){
        Tab tab = demoController.getPane().getSelectionModel().getSelectedItem();

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

                CommandManager.getInstance().addCommand(new ClearSlotsCommand(trash));
            }
        }
    }
    public void clearCell(){
        if(ClickableTableCell.lastSelectedCell == null){
            Notification.show("Clear error", "Cell has not been selected.", Alert.AlertType.ERROR);
            return;
        }

        if(ClickableTableCell.lastSelectedCell.getItem() == null || ((String)ClickableTableCell.lastSelectedCell.getItem()).isEmpty()){
            return;
        }

        String item = (String) ClickableTableCell.lastSelectedCell.getItem();
        item = item.split(" ")[0];
        Grade grade = new Grade(Integer.parseInt(item.substring(0, item.length() - 1)), item.charAt(item.length() - 1));
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
            CommandManager.getInstance().addCommand(new ClearSlotsCommand(trash));
        }
    }
    @Override
    public ObservableList<Tab> setupTable(Job job) {
        ObservableList<Tab> result = FXCollections.observableArrayList();
        int workDone = 0;

        if(job != null)
            job.progress(workDone, State.getInstance().educators.size());

        for(Integer post: State.getInstance().educators.keySet()){
            Educator educator = State.getInstance().educators.get(post);
            TableView<Rank<WeekDay>> daySchedule = new TableView<>();

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
            result.add(tab);

            if(job != null){
                workDone++;
                job.progress(workDone, State.getInstance().educators.size());
            }
        }

        return result;
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
            column.setCellFactory(value -> new ClickableTableCell<>(educator));
        }

        table.setItems(educatorTable.get(educator));
    }
    @Override
    public void populateTable(Job job) {
        educatorTable.clear();
        int size = State.getInstance().educators.size() * WeekDay.values().length;
        long workDone = 0;

        if(job != null){
            job.progress(workDone, size);
        }

        for(Integer post: State.getInstance().educators.keySet()){
            Educator educator = State.getInstance().educators.get(post);
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

            if(job != null){
                workDone++;
                job.progress(workDone, size);
            }
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


        Integer period = Integer.parseInt(ClickableTableCell.lastSelectedCell.getTableColumn().getText()) - 1;
        Integer itemIndex = ClickableTableCell.lastSelectedCell.getTableView().getSelectionModel().getSelectedIndex();
        Rank<WeekDay> item = (Rank<WeekDay>)ClickableTableCell.lastSelectedCell.getTableView().getItems().get(itemIndex);
        WeekDay day = item.getHeader();
        Grade grade = assignable.getGrade();

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

        CommandManager.getInstance().addCommand(new PositionCommand(assignable, triplet));
    }
    public void print(Stage stage){
        Tab tab = demoController.getPane().getSelectionModel().getSelectedItem();

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
