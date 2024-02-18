package com.example.demo.models.commands;

import com.example.demo.models.Grade;
import com.example.demo.models.State;
import com.example.demo.models.WeekDay;
import com.example.demo.models.assignable.Assignable;
import com.example.demo.utilities.Triplet;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;

import java.util.ArrayList;
import java.util.function.Consumer;

public class PositionCommand implements Command{
    private Assignable oldAssignable;
    private Assignable freshAssignable;
    private TabPane pane;
    private Triplet<WeekDay, Grade, Integer> triplet;
    private ArrayList<Integer> periods;
    private String tabRef;
    public PositionCommand(Assignable assignable, Triplet<WeekDay, Grade, Integer> triplet, TabPane pane, ArrayList<Integer> periods, String tabRef){
        this.oldAssignable = null;
        this.freshAssignable = assignable;
        this.triplet = triplet;
        this.pane = pane;
        this.periods = periods;
        this.tabRef = tabRef;
    }

    @Override
    public void execute() {
        if(State.getInstance().timetable.get(triplet) != null){
            oldAssignable = State.getInstance().assignables.get(State.getInstance().timetable.get(triplet));

            if(oldAssignable.getId().equals(freshAssignable.getId()))
                return;

            oldAssignable.setRemain(oldAssignable.getRemain() + 1);
        }

        State.getInstance().timetable.put(triplet, freshAssignable.getId());

        periods.set(triplet.getThird(),freshAssignable.getId());
        freshAssignable.setRemain(freshAssignable.getRemain() - 1);
        State.getInstance().saveRequired = true;

        for(Tab tab: pane.getTabs()){
            if(tab.getText().toUpperCase().equals(tabRef.toUpperCase().toString())){
                ((TableView)((AnchorPane)tab.getContent()).getChildren().get(0)).refresh();
                break;
            }
        }
    }

    @Override
    public void reverse() {
        if(oldAssignable == null){
            State.getInstance().timetable.remove(triplet);
            periods.set(triplet.getThird(), null);
        }else if(oldAssignable.getId().equals(freshAssignable.getId())) {
            return;
        }else{
            State.getInstance().timetable.put(triplet, oldAssignable.getId());
            periods.set(triplet.getThird(), oldAssignable.getId());
            oldAssignable.setRemain(oldAssignable.getRemain() + 1);
        }

        freshAssignable.setRemain(freshAssignable.getRemain() - 1);
        State.getInstance().saveRequired = true;

        for(Tab tab: pane.getTabs()){
            if(tab.getText().equals(tabRef)){
                ((TableView)((AnchorPane)tab.getContent()).getChildren().get(0)).refresh();
                break;
            }
        }
    }
}
