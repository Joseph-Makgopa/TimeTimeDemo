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

import java.util.*;

public class GradeViewService extends DemoService{
    private Map<Grade, ObservableList<Row<WeekDay>>> gradeTable = new HashMap<>();

    public GradeViewService(TabPane pane){
        super(pane);
    }
    public ObservableList<Row<WeekDay>> filter(Filter filter, Grade grade){
        return FXCollections.observableArrayList(gradeTable.get(grade).stream().filter(gradeSchedule -> {
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
            Notification.show("Filter error","You did not select the grade.", Alert.AlertType.ERROR);
            return;
        }

        String[] split = tab.getText().split(" ");
        Grade grade = new Grade(Integer.parseInt(split[0]), split[1].charAt(1));
        TableView<Row<WeekDay>> table = (TableView<Row<WeekDay>>)((AnchorPane)tab.getContent()).getChildren().get(0);
        table.setItems(filter(filter, grade));
        table.refresh();
    }

    @Override
    public void setupTable() {
        pane.getTabs().clear();

        State.getInstance().grades.forEach(grade -> {
            TableView<Row<WeekDay>> daySchedule = new TableView<>();

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

    public void setupGradeTable(Grade grade, TableView<Row<WeekDay>> table){
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
        }

        table.setItems(gradeTable.get(grade));
    }

    @Override
    public void populateTable() {
        gradeTable.clear();
        State.getInstance().grades.forEach(grade -> {
            ObservableList<Row<WeekDay>> daySchedules = FXCollections.observableArrayList();

            for(WeekDay day: WeekDay.values()){
                Integer numPeriods = State.getInstance().days.get(day);

                if(numPeriods != null){
                    Row<WeekDay> daySchedule = new Row<>(day, numPeriods);
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

    public void position(TabPane paneTimeTable, Assignable selected, WeekDay day, Integer period){
        Grade grade = State.getInstance().sessions.get(selected.getId().getFirst()).getGrade();
        ObservableList<Row<WeekDay>> daySchedules = gradeTable.get(grade);

        for(Row<WeekDay> daySchedule: daySchedules){
            if(daySchedule.getHeader().equals(day)){
                ArrayList<Pair<Integer, Integer>> periods = daySchedule.getPeriods();
                Triplet<WeekDay, Grade, Integer> triplet = new Triplet(daySchedule.getHeader(), grade, period);

                PositionCommand command = new PositionCommand(this, selected, triplet);
                CommandManager.getInstance().addCommand(command);
                command.execute();

                break;
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
        Grade grade = new Grade(Integer.parseInt(split[0]), split[1].charAt(0));
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
