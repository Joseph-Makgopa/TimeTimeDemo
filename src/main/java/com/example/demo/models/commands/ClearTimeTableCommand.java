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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ClearTimeTableCommand implements Command{
    private Map<Triplet<WeekDay, Grade, Integer>, Pair<Integer, Integer>> oldTimeTable;
    private Set<Triplet<WeekDay, Grade, Integer>> oldClashes;
    public ClearTimeTableCommand(){
        oldTimeTable = new HashMap<>(State.getInstance().timetable);
        oldClashes = new HashSet<>(State.getInstance().clashes);
    }

    @Override
    public String executeDescription() {
        return "  clearing table.";
    }

    @Override
    public String reverseDescription() {
        return "  reversing cleared table.";
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
        job.progress(0, 3);
        State.getInstance().timetable.forEach((triplet, reference) -> {
            if(reference != null){
                Assignable assignable = State.getInstance().assignables.get(reference);

                assignable.setRemain(assignable.getRemain() + 1);
            }
        });

        job.progress(1, 3);
        State.getInstance().timetable.clear();

        job.progress(2, 3);
        State.getInstance().clashes.clear();

        job.progress(3, 3);
    }

    @Override
    public void reverse(Job job) {
        State.getInstance().timetable.clear();
        State.getInstance().timetable.putAll(oldTimeTable);

        State.getInstance().timetable.forEach((triplet, reference) -> {
            if(reference != null){
                Assignable assignable = State.getInstance().assignables.get(reference);

                assignable.setRemain(assignable.getRemain() - 1);
            }
        });

        State.getInstance().clashes.clear();
        State.getInstance().clashes.addAll(oldClashes);
    }
}
