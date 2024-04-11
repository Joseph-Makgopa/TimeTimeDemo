package com.example.demo.models.commands;

import com.example.demo.controllers.DemoController;
import com.example.demo.models.Assignable;
import com.example.demo.models.Grade;
import com.example.demo.models.State;
import com.example.demo.models.WeekDay;
import com.example.demo.services.DemoService;
import com.example.demo.utilities.Pair;
import com.example.demo.utilities.Triplet;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class ArrangeCommand implements Command{
    private DemoController demoController;
    private Map<Triplet<WeekDay, Grade, Integer>, Pair<Integer, Integer>> oldTimeTable, freshTimeTable;
    private LinkedList<Assignable> oldAssignable, freshAssignable;
    public ArrangeCommand(DemoController demoController){
        this.demoController = demoController;
        this.oldTimeTable = new HashMap<>(State.getInstance().timetable);
        oldAssignable = new LinkedList<>();

        for(Assignable assignable: State.getInstance().assignables.values()){
            oldAssignable.add(assignable.clone());
        }

        this.freshTimeTable = null;
        this.freshAssignable = null;
    }


    @Override
    public void execute() {
        if(freshTimeTable == null){
            demoController.getService().arrange();

            freshTimeTable = new HashMap<>(State.getInstance().timetable);
            freshAssignable = new LinkedList<>();
            for(Assignable assignable: State.getInstance().assignables.values()){
                freshAssignable.add(assignable.clone());
            }
        }else{
            State.getInstance().timetable.clear();
            State.getInstance().timetable.putAll(freshTimeTable);

            State.getInstance().assignables.clear();
            for(Assignable assignable: freshAssignable)
                State.getInstance().assignables.put(assignable.getId(), assignable.clone());
        }

        State.getInstance().saveRequired = true;
        demoController.getService().refresh();
    }

    @Override
    public void reverse(){
        State.getInstance().timetable.clear();
        State.getInstance().timetable.putAll(oldTimeTable);

        State.getInstance().assignables.clear();
        for(Assignable assignable: oldAssignable)
            State.getInstance().assignables.put(assignable.getId(), assignable.clone());

        State.getInstance().saveRequired = true;
        demoController.getService().refresh();
    }
}
