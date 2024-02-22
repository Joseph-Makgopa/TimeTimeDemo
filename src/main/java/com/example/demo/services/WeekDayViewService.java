package com.example.demo.services;

import com.example.demo.models.*;
import com.example.demo.models.assignable.Assignable;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class WeekDayViewService extends DemoService{
    private Map<WeekDay, ObservableList<Row<Grade>>> weeklyTable = new HashMap<>();
    public WeekDayViewService(TabPane pane){
        super(pane);
    }
    public void setupTable(){
        pane.getTabs().clear();
        Tab[] tabs = new Tab[7];

        State.getInstance().days.forEach((day, periods) ->{

            TableView<Row<Grade>> daySchedule = new TableView<>();
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

    public void setupDayTable(WeekDay day, TableView<Row<Grade>> table){
        /**
         * setup the columns for a table and add the referenced ObservableList
         * **/

        TableColumn<Row<Grade>,?> column = table.getColumns().get(0);
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
            ObservableList<Row<Grade>> gradeSchedules = FXCollections.observableArrayList();

            State.getInstance().grades.forEach(grade -> {
                Row<Grade> gradeSchedule = new Row<>(grade, numPeriods);
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
    public ObservableList<Row<Grade>> filter(Filter filter, WeekDay day){
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
        TableView<Row<Grade>> table = (TableView<Row<Grade>>)((AnchorPane)tab.getContent()).getChildren().get(0);
        table.setItems(filter(filter, day));
        table.refresh();
    }

    public void position(TabPane paneTimeTable, Assignable selected, WeekDay day, Integer period){
        ObservableList<Row<Grade>> gradeSchedules = weeklyTable.get(day);

        for(Row<Grade> gradeSchedule: gradeSchedules){
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
        TableView<GradeSchedule> table = (TableView<GradeSchedule>)((AnchorPane)tab.getContent()).getChildren().get(0);

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
