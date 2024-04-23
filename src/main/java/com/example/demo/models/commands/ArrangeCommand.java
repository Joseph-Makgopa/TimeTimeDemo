package com.example.demo.models.commands;

import com.example.demo.controllers.DemoController;
import com.example.demo.models.Assignable;
import com.example.demo.models.Grade;
import com.example.demo.models.State;
import com.example.demo.models.WeekDay;
import com.example.demo.utilities.Pair;
import com.example.demo.utilities.Triplet;

import java.util.*;

public class ArrangeCommand extends Command{
    private Map<Triplet<WeekDay, Grade, Integer>, Pair<Integer, Integer>> oldTimeTable, freshTimeTable;
    private LinkedList<Assignable> oldAssignable, freshAssignable;
    private Set<Triplet<WeekDay, Grade, Integer>> oldClashes, freshClashes;
    public ArrangeCommand(LinkedList<Assignable> lessons, DemoController demoController){
        super(demoController);
        this.oldTimeTable = new HashMap<>(State.getInstance().timetable);
        oldAssignable = new LinkedList<>();
        oldClashes = new HashSet<>(State.getInstance().clashes);

        for(Assignable assignable: lessons){
            oldAssignable.add(assignable.clone());
        }

        this.freshTimeTable = null;
        this.freshAssignable = null;
        freshClashes = null;
    }

    @Override
    public void executeCode() {
        if(freshTimeTable == null){
            LinkedList<Assignable> copy = new LinkedList<>();
            for(Assignable assignable: oldAssignable)
                copy.add(assignable.clone());

            getDemoController().getService().arrange(copy);

            freshTimeTable = new HashMap<>(State.getInstance().timetable);
            freshAssignable = new LinkedList<>();
            for(Assignable assignable: copy){
                freshAssignable.add(assignable.clone());
                State.getInstance().assignables.put(assignable.getId(), assignable.clone());
            }
            State.getInstance().setClashes();
            freshClashes = new HashSet<>(State.getInstance().clashes);
        }else{
            State.getInstance().timetable.clear();
            State.getInstance().timetable.putAll(freshTimeTable);

            State.getInstance().assignables.clear();
            for(Assignable assignable: freshAssignable)
                State.getInstance().assignables.put(assignable.getId(), assignable.clone());

            State.getInstance().clashes.clear();
            State.getInstance().clashes.addAll(freshClashes);
        }

        State.getInstance().saveRequired = true;
        getDemoController().getService().refreshData();
    }

    @Override
    public void reverseCode(){
        State.getInstance().timetable.clear();
        State.getInstance().timetable.putAll(oldTimeTable);

        State.getInstance().assignables.clear();
        for(Assignable assignable: oldAssignable)
            State.getInstance().assignables.put(assignable.getId(), assignable.clone());

        State.getInstance().clashes.clear();
        State.getInstance().clashes.addAll(oldClashes);

        State.getInstance().saveRequired = true;
        getDemoController().getService().refreshData();
    }
}
