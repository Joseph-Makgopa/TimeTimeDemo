package com.example.demo.models.commands;

import com.example.demo.controllers.DemoController;
import com.example.demo.models.Assignable;
import com.example.demo.models.Grade;
import com.example.demo.models.State;
import com.example.demo.models.WeekDay;
import com.example.demo.services.WeekDayViewService;
import com.example.demo.utilities.Job;
import com.example.demo.utilities.Pair;
import com.example.demo.utilities.Triplet;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;

import java.util.*;

public class UpdateStructureCommand implements Command{
    private Map<WeekDay, Integer> oldDays;
    private Map<Triplet<WeekDay, Grade, Integer>, Pair<Integer, Integer>> oldTimeTable, freshTimeTable;
    private LinkedList<Assignable> oldAssignable, freshAssignable;
    private Set<Triplet<WeekDay, Grade, Integer>> oldClashes, freshClashes;
    private Integer oldBreakAfter;
    private Map<WeekDay, Integer> freshDays;
    private Integer freshBreakAfter;
    private ComboBox<WeekDay> comboDay;
    public UpdateStructureCommand(Map<WeekDay, Integer> days, Integer breakAfter, ComboBox<WeekDay> comboDay, DemoController demoController){
        oldDays = new HashMap<>(State.getInstance().days);
        oldTimeTable = new HashMap<>(State.getInstance().timetable);
        oldAssignable = new LinkedList<>();
        for(Assignable assignable: State.getInstance().assignables.values())
            oldAssignable.add(assignable.clone());
        oldClashes = new HashSet<>(State.getInstance().clashes);
        oldBreakAfter = State.getInstance().breakAfter;

        freshDays = new HashMap<>(days);
        freshTimeTable = null;
        freshAssignable = null;
        freshClashes = null;
        freshBreakAfter = breakAfter;

        this.comboDay = comboDay;
    }
    @Override
    public String executeDescription() {
        return "  updating table.";
    }

    @Override
    public String reverseDescription() {
        return "  reversing table update.";
    }
    @Override
    public Boolean threadSafe(){
        return true;
    }
    @Override
    public Boolean dataRefresh() {
        return false;
    }

    @Override
    public void execute(Job job) {
        State.getInstance().days.clear();
        State.getInstance().days.putAll(freshDays);
        State.getInstance().breakAfter = freshBreakAfter;
        comboDay.setItems(FXCollections.observableArrayList(State.getInstance().days.keySet().stream().toList()));

        if(freshTimeTable == null){
            job.progress(0, 3);

            Iterator<Map.Entry<Triplet<WeekDay, Grade, Integer>, Pair<Integer, Integer>>> iterators = State.getInstance().timetable.entrySet().iterator();

            while(iterators.hasNext()){
                Map.Entry<Triplet<WeekDay, Grade, Integer>, Pair<Integer, Integer>> entry = iterators.next();

                Integer periods = freshDays.get(entry.getKey().getFirst());

                if(periods == null || entry.getKey().getThird() >= periods) {
                    iterators.remove();

                    if(entry.getValue() != null){
                        Assignable assignable = State.getInstance().assignables.get(entry.getValue());
                        assignable.setRemain(assignable.getRemain() + 1);
                    }
                }
            }

            job.progress(1, 3);

            freshTimeTable = State.getInstance().timetable;
            freshAssignable = new LinkedList<>();
            for(Assignable assignable: State.getInstance().assignables.values())
                freshAssignable.add(assignable.clone());

            job.progress(2, 3);

            State.getInstance().setClashes();
            freshClashes = new HashSet<>(State.getInstance().clashes);

            job.progress(3, 3);
        }else{
            job.progress(0, 3);

            State.getInstance().timetable.clear();
            State.getInstance().timetable.putAll(freshTimeTable);

            job.progress(1, 3);

            State.getInstance().assignables.clear();
            for(Assignable assignable: freshAssignable)
                State.getInstance().assignables.put(assignable.getId(), assignable.clone());

            job.progress(2, 3);

            State.getInstance().clashes.clear();
            State.getInstance().clashes.addAll(freshClashes);

            job.progress(3, 3);
        }

        State.getInstance().saveRequired = true;
    }
    @Override
    public void reverse(Job job) {
        job.progress(0, 5);

        State.getInstance().days.clear();
        State.getInstance().days.putAll(oldDays);

        job.progress(1, 5);

        State.getInstance().timetable.clear();
        State.getInstance().timetable.putAll(oldTimeTable);

        job.progress(2, 5);

        State.getInstance().assignables.clear();
        for(Assignable assignable: oldAssignable)
            State.getInstance().assignables.put(assignable.getId(), assignable.clone());

        job.progress(3, 5);

        State.getInstance().clashes.clear();
        State.getInstance().clashes.addAll(oldClashes);

        job.progress(4, 5);

        State.getInstance().breakAfter = oldBreakAfter;
        comboDay.setItems(FXCollections.observableArrayList(State.getInstance().days.keySet().stream().toList()));

        job.progress(5, 5);

        State.getInstance().saveRequired = true;
    }
}
