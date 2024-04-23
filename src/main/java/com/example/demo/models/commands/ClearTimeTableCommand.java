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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ClearTimeTableCommand extends Command{
    private Map<Triplet<WeekDay, Grade, Integer>, Pair<Integer, Integer>> oldTimeTable;
    private Set<Triplet<WeekDay, Grade, Integer>> oldClashes;
    public ClearTimeTableCommand(DemoController demoController){
        super(demoController);
        oldTimeTable = new HashMap<>(State.getInstance().timetable);
        oldClashes = new HashSet<>(State.getInstance().clashes);
    }
    @Override
    public void executeCode() {
        State.getInstance().timetable.forEach((triplet, reference) -> {
            if(reference != null){
                Assignable assignable = State.getInstance().assignables.get(reference);

                assignable.setRemain(assignable.getRemain() + 1);
            }
        });

        State.getInstance().timetable.clear();
        State.getInstance().clashes.clear();
        getDemoController().getService().refreshData();
    }

    @Override
    public void reverseCode() {
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
        getDemoController().getService().refreshData();
    }
}
