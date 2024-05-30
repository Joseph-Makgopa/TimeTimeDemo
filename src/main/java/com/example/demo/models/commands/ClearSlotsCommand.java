package com.example.demo.models.commands;

import com.example.demo.controllers.DemoController;
import com.example.demo.models.Assignable;
import com.example.demo.models.Grade;
import com.example.demo.models.State;
import com.example.demo.models.WeekDay;
import com.example.demo.services.DemoService;
import com.example.demo.utilities.Job;
import com.example.demo.utilities.Pair;
import com.example.demo.utilities.Triplet;

import java.util.*;

public class ClearSlotsCommand implements Command{
    private LinkedList<Map.Entry<Triplet<WeekDay, Grade, Integer>, Pair<Integer, Integer>>> trash;
    private Map<Triplet<WeekDay, Grade, Integer>, Pair<Integer, Integer>> oldTimeTable, freshTimeTable;
    private LinkedList<Assignable> oldAssignables, freshAssignables;
    private Set<Triplet<WeekDay, Grade, Integer>> oldClashes, freshClashes;
    public ClearSlotsCommand(LinkedList <Map.Entry<Triplet<WeekDay, Grade, Integer>, Pair<Integer, Integer>>> trash){
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
    public String executeDescription() {
        return "  clearing slots.";
    }

    @Override
    public String reverseDescription() {
        return "  reversing cleared slots.";
    }

    @Override
    public Boolean dataRefresh() {
        return true;
    }
    @Override
    public Boolean threadSafe(){
        return true;
    }
    @Override
    public void execute(Job job) {
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
            job.progress(0, freshAssignables.size() + 3);

            State.getInstance().timetable.clear();
            State.getInstance().timetable.putAll(freshTimeTable);

            job.progress(1, freshAssignables.size() + 3);

            State.getInstance().assignables.clear();
            for(Assignable assignable: freshAssignables){
                State.getInstance().assignables.put(assignable.getId(), assignable.clone());

                job.progress(2 + State.getInstance().assignables.size(), freshAssignables.size() + 3);
            }

            State.getInstance().clashes.clear();
            State.getInstance().clashes.addAll(freshClashes);

            job.progress(3 + freshAssignables.size(), freshAssignables.size() + 3);
        }
    }

    @Override
    public void reverse(Job job) {
        job.progress(0, oldAssignables.size() + 3);

        State.getInstance().timetable.clear();
        State.getInstance().timetable.putAll(oldTimeTable);

        job.progress(1, oldAssignables.size() + 3);

        State.getInstance().assignables.clear();
        for(Assignable assignable: oldAssignables){
            State.getInstance().assignables.put(assignable.getId(), assignable.clone());

            job.progress(2 + State.getInstance().assignables.size(), oldAssignables.size() + 3);
        }

        State.getInstance().clashes.clear();
        State.getInstance().clashes.addAll(oldClashes);

        job.progress(3 + oldAssignables.size(), oldAssignables.size() + 3);
    }
}
