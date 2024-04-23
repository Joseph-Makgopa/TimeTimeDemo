package com.example.demo.models.commands;

import com.example.demo.controllers.DemoController;
import com.example.demo.models.Assignable;
import com.example.demo.models.Grade;
import com.example.demo.models.State;
import com.example.demo.models.WeekDay;
import com.example.demo.services.DemoService;
import com.example.demo.utilities.Pair;
import com.example.demo.utilities.Triplet;

import java.util.*;

public class ClearSlotsCommand extends Command{
    private LinkedList<Map.Entry<Triplet<WeekDay, Grade, Integer>, Pair<Integer, Integer>>> trash;
    private Map<Triplet<WeekDay, Grade, Integer>, Pair<Integer, Integer>> oldTimeTable, freshTimeTable;
    private LinkedList<Assignable> oldAssignables, freshAssignables;
    private Set<Triplet<WeekDay, Grade, Integer>> oldClashes, freshClashes;
    public ClearSlotsCommand(LinkedList <Map.Entry<Triplet<WeekDay, Grade, Integer>, Pair<Integer, Integer>>> trash, DemoController demoController){
        super(demoController);
        this.trash = trash;
        oldTimeTable = new HashMap<>(State.getInstance().timetable);
        freshTimeTable = null;

        oldAssignables = new LinkedList<>();
        for(Assignable assignable: State.getInstance().assignables.values()){
            oldAssignables.add(assignable.clone());
        }
        freshAssignables = null;

        oldClashes = new HashSet<>(State.getInstance().clashes);
        freshClashes = null;
    }
    @Override
    public void executeCode() {
        if(freshTimeTable == null){
            for(Map.Entry<Triplet<WeekDay, Grade, Integer>, Pair<Integer, Integer>> entry: trash){
                if(entry.getValue() != null){
                    Assignable assignable = State.getInstance().assignables.get(entry.getValue());

                    assignable.setRemain(assignable.getRemain() + 1);
                    State.getInstance().timetable.remove(entry.getKey());
                }
            }

            freshTimeTable = new HashMap<>(State.getInstance().timetable);

            freshAssignables = new LinkedList<>();
            for(Assignable assignable: State.getInstance().assignables.values()){
                freshAssignables.add(assignable.clone());
            }

            State.getInstance().setClashes();
            freshClashes = new HashSet<>(State.getInstance().clashes);

        }else{
            State.getInstance().timetable.clear();
            State.getInstance().timetable.putAll(freshTimeTable);

            State.getInstance().assignables.clear();
            for(Assignable assignable: freshAssignables){
                State.getInstance().assignables.put(assignable.getId(), assignable.clone());
            }

            State.getInstance().clashes.clear();
            State.getInstance().clashes.addAll(freshClashes);
        }

        getDemoController().getService().refreshData();
    }

    @Override
    public void reverseCode() {
        State.getInstance().timetable.clear();
        State.getInstance().timetable.putAll(oldTimeTable);

        State.getInstance().assignables.clear();
        for(Assignable assignable: oldAssignables){
            State.getInstance().assignables.put(assignable.getId(), assignable.clone());
        }

        State.getInstance().clashes.clear();
        State.getInstance().clashes.addAll(oldClashes);

        getDemoController().getService().refreshData();
    }
}
