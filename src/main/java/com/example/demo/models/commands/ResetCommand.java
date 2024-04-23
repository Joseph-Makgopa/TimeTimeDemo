package com.example.demo.models.commands;

import com.example.demo.controllers.DemoController;
import com.example.demo.models.Assignable;
import com.example.demo.models.Grade;
import com.example.demo.models.State;
import com.example.demo.models.WeekDay;
import com.example.demo.utilities.Pair;
import com.example.demo.utilities.Triplet;
import com.example.demo.utilities.TripletManager;

import java.util.*;

public class ResetCommand extends Command{
    private Map<Pair<Integer, Integer>, Assignable> lessons;
    private Map<Triplet<WeekDay, Grade, Integer>, Pair<Integer, Integer>> oldTimeTable, freshTimeTable;
    private LinkedList<Assignable> oldAssignable, freshAssignable;
    private Set<Triplet<WeekDay, Grade, Integer>> oldClashes, freshClashes;
    public ResetCommand(LinkedList<Assignable> lessons, DemoController demoController){
        super(demoController);
        setLessons(lessons);

        oldTimeTable = new HashMap<>(State.getInstance().timetable);
        freshTimeTable = null;

        oldAssignable = new LinkedList<>();
        for(Assignable assignable: lessons){
            oldAssignable.add(assignable.clone());
        }
        freshAssignable = null;

        oldClashes = new HashSet<>(State.getInstance().clashes);
        freshClashes = null;
    }

    void setLessons(LinkedList<Assignable> lessons){
        this.lessons = new HashMap<>();

        for(Assignable assignable: lessons)
            this.lessons.put(assignable.getId(), assignable.clone());
    }
    @Override
    public void executeCode() {
        if(freshTimeTable == null){
            Iterator<Map.Entry<Triplet<WeekDay, Grade, Integer>, Pair<Integer, Integer>>> iterator = State.getInstance().timetable.entrySet().iterator();
            LinkedList<Triplet<WeekDay, Grade, Integer>> pairs = new LinkedList<>();

            while (iterator.hasNext()) {
                Map.Entry<Triplet<WeekDay, Grade, Integer>, Pair<Integer, Integer>> entry = iterator.next();

                if (lessons.containsKey(entry.getValue())) {
                    Assignable assignable = lessons.get(entry.getValue());
                    assignable.setRemain(assignable.getRemain() + 1);
                    iterator.remove();

                    Assignable pair = assignable.getPair();

                    if (pair != null) {
                        pairs.add(TripletManager.get(entry.getKey().getFirst(), pair.getGrade(), entry.getKey().getThird()));
                        pair.setRemain(pair.getRemain() + 1);
                    }
                }
            }

            for (Triplet<WeekDay, Grade, Integer> pair: pairs)
                State.getInstance().timetable.remove(pair);

            freshAssignable = new LinkedList<>();
            for(Assignable assignable: lessons.values()) {
                freshAssignable.add(assignable.clone());
                State.getInstance().assignables.put(assignable.getId(), assignable.clone());
            }

            freshTimeTable = new HashMap<>(State.getInstance().timetable);

            State.getInstance().setClashes();
            freshClashes = new HashSet<>(State.getInstance().clashes);
        }else{
            State.getInstance().timetable.clear();
            State.getInstance().timetable.putAll(freshTimeTable);

            for(Assignable assignable: freshAssignable)
                State.getInstance().assignables.put(assignable.getId(), assignable.clone());

            State.getInstance().clashes.clear();
            State.getInstance().clashes.addAll(freshClashes);
        }

        getDemoController().getService().refreshData();
        State.getInstance().saveRequired = true;
    }

    @Override
    public void reverseCode() {
        State.getInstance().timetable.clear();
        State.getInstance().timetable.putAll(oldTimeTable);

        for(Assignable assignable: oldAssignable)
            State.getInstance().assignables.put(assignable.getId(), assignable.clone());

        State.getInstance().clashes.clear();
        State.getInstance().clashes.addAll(oldClashes);

        getDemoController().getService().refreshData();
        State.getInstance().saveRequired = true;
    }
}
