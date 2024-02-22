package com.example.demo.services;

import com.example.demo.models.Grade;
import com.example.demo.models.GradeSchedule;
import com.example.demo.models.State;
import com.example.demo.models.WeekDay;
import com.example.demo.models.assignable.Assignable;
import com.example.demo.utilities.Filter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public abstract class DemoService {
    protected TabPane pane;
    public DemoService(TabPane pane){
        this.pane = pane;
    }
    public void refresh(){
        populateTable();
        setupTable();
        for(Tab tab: pane.getTabs()){
            ((TableView)((AnchorPane)tab.getContent()).getChildren().get(0)).refresh();
        }
    }
    public ObservableList<Assignable> search(String text){
        if(text == null || text.isEmpty())
            return FXCollections.observableArrayList(State.getInstance().assignables.values());

        ObservableList<Assignable> result = FXCollections.observableArrayList();

        State.getInstance().assignables.values().forEach(entry -> {
            if(entry.getDetails().toUpperCase().contains(text)){
                result.add(entry);
                return;
            }

            if(entry.getRemain().toString().contains(text)){
                result.add(entry);
                return;
            }

            if(State.getInstance().sessions.get(entry.getId().getFirst()).getGrade().toString().contains(text)){
                result.add(entry);
                return;
            }
        });

        return result;
    }
    public abstract void setupTable();
    public abstract void populateTable();
    public abstract void position(TabPane paneTimeTable, Assignable selected, WeekDay day, Integer period);
    public abstract void print(TabPane pane, Stage stage);
    public abstract void updateFilter(Filter filter, TabPane pane);

}
