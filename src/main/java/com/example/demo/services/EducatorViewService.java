package com.example.demo.services;

import com.example.demo.models.*;
import com.example.demo.models.assignable.Assignable;
import com.example.demo.models.assignable.PairAssignable;
import com.example.demo.models.assignable.SplitAssignable;
import com.example.demo.models.commands.CommandList;
import com.example.demo.models.commands.CommandManager;
import com.example.demo.models.commands.PositionCommand;
import com.example.demo.utilities.Filter;
import com.example.demo.utilities.Notification;
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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class EducatorViewService extends DemoService{
    private Map<Educator, ObservableList<Row<WeekDay>>> educatorTable = new HashMap<>();

    public ObservableList<Row<WeekDay>> filter(Filter filter, Educator educator){
        return FXCollections.observableArrayList(educatorTable.get(educator).stream().filter(daySchedule -> {
            if(filter.subject != null){
                ArrayList<Integer> periods = daySchedule.getPeriods();
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
                        Session session = State.getInstance().sessions.get(assignable.getSessionRef());

                        if(session.getSubject().equals(filter.subject)){
                            foundSubject = true;
                        }else if(assignable.shareSingleSlot()){
                            SplitAssignable splitAssignable = (SplitAssignable) assignable;
                            session = State.getInstance().sessions.get(splitAssignable.getSplitRef());

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

        TableView<Row<WeekDay>> table = (TableView<Row<WeekDay>>)((AnchorPane)tab.getContent()).getChildren().get(0);
        table.setItems(filter(filter, State.getInstance().educators.get(index)));
        table.refresh();
    }

    @Override
    public void setupTable(TabPane pane) {
        pane.getTabs().clear();

        State.getInstance().educators.forEach((post, educator) -> {
            TableView<Row<WeekDay>> daySchedule = new TableView<>();

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

    public void setupEducatorTable(Educator educator, TableView<Row<WeekDay>> table){
        TableColumn<Row<WeekDay>,?> column = table.getColumns().get(0);
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
                Integer id = null;

                try {
                    id = entry.getValue().getPeriods().get(index);
                }catch(IndexOutOfBoundsException error){

                }

                if(id == null){
                    return new SimpleObjectProperty("");
                }

                Assignable assignable = State.getInstance().assignables.get(id);
                Session session = State.getInstance().sessions.get(assignable.getSessionRef());

                if(!session.getEducator().equals(educator)){
                    if(assignable.getOtherRef() == null){
                        return new SimpleObjectProperty("");
                    }

                    session = State.getInstance().sessions.get(assignable.getOtherRef());

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
        State.getInstance().educators.forEach((post, educator) -> {
            ObservableList<Row<WeekDay>> daySchedules = FXCollections.observableArrayList();

            for(WeekDay day: WeekDay.values()){
                Integer numPeriods = State.getInstance().days.get(day);

                if(numPeriods != null){
                    Row<WeekDay> daySchedule = new Row<>(day, numPeriods);
                    ArrayList<Integer> periods = daySchedule.getPeriods();

                    for(int period = 0; period < periods.size(); period++){
                        final Integer finalPeriod = period;

                        State.getInstance().timetable.forEach((triplet, id) -> {
                            if(triplet.getFirst().equals(day) && triplet.getThird().equals(finalPeriod)){
                                Assignable assignable = State.getInstance().assignables.get(id);
                                Session session = State.getInstance().sessions.get(assignable.getSessionRef());

                                if(!session.getEducator().equals(educator)){
                                    if(assignable.getOtherRef() != null){
                                        session = State.getInstance().sessions.get(assignable.getOtherRef());

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
        Session session = State.getInstance().sessions.get(selected.getSessionRef());
        Session sessionOther = State.getInstance().sessions.get(selected.getOtherRef());
        CommandList commandList = new CommandList();

        ObservableList<Row<WeekDay>> daySchedules = educatorTable.get(session.getEducator());

        for(Row<WeekDay> daySchedule: daySchedules){
            if(daySchedule.getHeader().equals(day)){
                ArrayList<Integer> periods = daySchedule.getPeriods();
                Triplet<WeekDay, Grade, Integer> triplet = new Triplet<>(daySchedule.getHeader(), session.getGrade(), period);

                PositionCommand command = new PositionCommand(selected, triplet, paneTimeTable, periods, session.getEducator().toString());
                commandList.add(command);

                break;
            }
        }

        if(sessionOther != null){
            daySchedules = educatorTable.get(sessionOther.getEducator());

            for(Row<WeekDay> daySchedule: daySchedules){
                if(daySchedule.getHeader().equals(day)){
                    ArrayList<Integer> periods = daySchedule.getPeriods();
                    Triplet<WeekDay, Grade, Integer> triplet = new Triplet<>(daySchedule.getHeader(), session.getGrade(), period);

                    PositionCommand command = new PositionCommand(selected, triplet, paneTimeTable, periods, sessionOther.getEducator().toString());
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
        TableView<Row<WeekDay>> table = (TableView<Row<WeekDay>>)((AnchorPane)tab.getContent()).getChildren().get(0);

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
}