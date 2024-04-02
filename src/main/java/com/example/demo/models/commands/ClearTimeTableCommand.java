package com.example.demo.models.commands;

import com.example.demo.models.Assignable;
import com.example.demo.models.Grade;
import com.example.demo.models.State;
import com.example.demo.models.WeekDay;
import com.example.demo.services.DemoService;
import com.example.demo.utilities.Pair;
import com.example.demo.utilities.Triplet;

import java.util.HashMap;
import java.util.Map;

public class ClearTimeTableCommand implements Command{
    private DemoService service;
    private Map<Triplet<WeekDay, Grade, Integer>, Pair<Integer, Integer>> oldTimeTable;
    public ClearTimeTableCommand(DemoService service){
        this.service = service;
        oldTimeTable = new HashMap<>(State.getInstance().timetable);
    }
    @Override
    public void execute() {
        State.getInstance().timetable.forEach((triplet, reference) -> {
            if(reference != null){
                Assignable assignable = State.getInstance().assignables.get(reference);

                assignable.setRemain(assignable.getRemain() + 1);
            }
        });

        State.getInstance().timetable.clear();
        service.refresh();
    }

    @Override
    public void reverse() {
        State.getInstance().timetable.clear();
        State.getInstance().timetable.putAll(oldTimeTable);

        State.getInstance().timetable.forEach((triplet, reference) -> {
            if(reference != null){
                Assignable assignable = State.getInstance().assignables.get(reference);

                assignable.setRemain(assignable.getRemain() - 1);
            }
        });

        service.refresh();
    }
}
