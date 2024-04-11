package com.example.demo.models.commands;

import com.example.demo.controllers.DemoController;
import com.example.demo.models.Assignable;
import com.example.demo.models.Grade;
import com.example.demo.models.State;
import com.example.demo.models.WeekDay;
import com.example.demo.utilities.Pair;
import com.example.demo.utilities.Triplet;
import com.example.demo.utilities.TripletManager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

public class ResetSelectionCommand implements Command{
    private Assignable assignable, oldAssignable, freshAssignable;
    private Map<Triplet<WeekDay, Grade, Integer>, Pair<Integer, Integer>> oldTimeTable, freshTimeTable;
    private DemoController demoController;
    public ResetSelectionCommand(Assignable lesson, DemoController demoController){
        this.demoController = demoController;
        this.assignable = lesson;
        oldAssignable = lesson.clone();
        freshAssignable = null;
        oldTimeTable = new HashMap<>(State.getInstance().timetable);
        freshTimeTable = null;
    }
    @Override
    public void execute() {
        if(freshAssignable == null) {
            Iterator<Map.Entry<Triplet<WeekDay, Grade, Integer>, Pair<Integer, Integer>>> iterator = State.getInstance().timetable.entrySet().iterator();
            LinkedList<Triplet<WeekDay, Grade, Integer>> pairs = new LinkedList<>();

            while (iterator.hasNext()) {
                Map.Entry<Triplet<WeekDay, Grade, Integer>, Pair<Integer, Integer>> entry = iterator.next();

                if (assignable.getId().equals(entry.getValue())) {
                    assignable.setRemain(assignable.getRemain() + 1);
                    iterator.remove();

                    Assignable pair = assignable.getPair();

                    if (pair != null) {
                        pairs.add(TripletManager.get(entry.getKey().getFirst(), pair.getGrade(), entry.getKey().getThird()));
                        pair.setRemain(pair.getRemain() + 1);
                    }
                }
            }

            for (Triplet<WeekDay, Grade, Integer> pair : pairs)
                State.getInstance().timetable.remove(pair);

            freshAssignable = assignable.clone();
            freshTimeTable = new HashMap<>(State.getInstance().timetable);
        }else{
            State.getInstance().assignables.put(freshAssignable.getId(), freshAssignable.clone());
            State.getInstance().timetable.clear();
            State.getInstance().timetable.putAll(freshTimeTable);
        }

        demoController.getService().refresh();
        State.getInstance().saveRequired = true;
    }

    @Override
    public void reverse() {
        State.getInstance().assignables.put(oldAssignable.getId(), oldAssignable.clone());
        State.getInstance().timetable.clear();
        State.getInstance().timetable.putAll(oldTimeTable);

        demoController.getService().refresh();
        State.getInstance().saveRequired = true;
    }
}
