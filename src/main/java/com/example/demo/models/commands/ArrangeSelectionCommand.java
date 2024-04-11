package com.example.demo.models.commands;

import com.example.demo.controllers.DemoController;
import com.example.demo.models.Assignable;
import com.example.demo.models.Grade;
import com.example.demo.models.State;
import com.example.demo.models.WeekDay;
import com.example.demo.utilities.Pair;
import com.example.demo.utilities.Triplet;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class ArrangeSelectionCommand implements Command{
    private DemoController demoController;
    private Map<Triplet<WeekDay, Grade, Integer>, Pair<Integer, Integer>> oldTimeTable, freshTimeTable;
    private Assignable current, freshAssignable;
    public ArrangeSelectionCommand(DemoController demoController, Assignable current){
        this.demoController = demoController;
        this.oldTimeTable = new HashMap<>(State.getInstance().timetable);

        this.freshTimeTable = null;
        this.freshAssignable = null;
        this.current = current;
    }
    @Override
    public void execute() {
        if(freshTimeTable == null){
            LinkedList<Assignable> list = new LinkedList<>();
            Assignable copy = current.clone();
            list.add(copy);
            demoController.getService().arrange(list);

            freshTimeTable = new HashMap<>(State.getInstance().timetable);
            State.getInstance().assignables.put(copy.getId(), copy);
            freshAssignable = copy;
        }else{
            State.getInstance().timetable.clear();
            State.getInstance().timetable.putAll(freshTimeTable);

            State.getInstance().assignables.put(freshAssignable.getId(), freshAssignable.clone());
        }

        State.getInstance().saveRequired = true;
        demoController.getService().refresh();
    }
    @Override
    public void reverse(){
        State.getInstance().timetable.clear();
        State.getInstance().timetable.putAll(oldTimeTable);

        State.getInstance().assignables.put(current.getId(), current.clone());

        State.getInstance().saveRequired = true;
        demoController.getService().refresh();
    }
}
