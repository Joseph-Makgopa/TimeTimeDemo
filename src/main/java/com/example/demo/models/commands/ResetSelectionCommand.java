package com.example.demo.models.commands;

import com.example.demo.controllers.DemoController;
import com.example.demo.models.Assignable;
import com.example.demo.models.Grade;
import com.example.demo.models.State;
import com.example.demo.models.WeekDay;
import com.example.demo.utilities.Job;
import com.example.demo.utilities.Pair;
import com.example.demo.utilities.Triplet;
import com.example.demo.utilities.TripletManager;

import java.util.*;

public class ResetSelectionCommand implements Command{
    private Assignable assignable, oldAssignable, freshAssignable;
    private Map<Triplet<WeekDay, Grade, Integer>, Pair<Integer, Integer>> oldTimeTable, freshTimeTable;
    private Set<Triplet<WeekDay, Grade, Integer>> oldClashes, freshClashes;
    public ResetSelectionCommand(Assignable lesson){
        this.assignable = lesson;
        oldAssignable = lesson.clone();
        freshAssignable = null;
        oldTimeTable = new HashMap<>(State.getInstance().timetable);
        freshTimeTable = null;
        oldClashes = new HashSet<>(State.getInstance().clashes);
        freshClashes = null;
    }

    @Override
    public String executeDescription() {
        return "  resetting lesson.";
    }

    @Override
    public String reverseDescription() {
        return "  reversing lesson reset.";
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
        if(freshAssignable == null) {
            job.progress(0,5);

            Iterator<Map.Entry<Triplet<WeekDay, Grade, Integer>, Pair<Integer, Integer>>> iterator = State.getInstance().timetable.entrySet().iterator();
            LinkedList<Triplet<WeekDay, Grade, Integer>> pairs = new LinkedList<>();

            job.progress(1,5);

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

            job.progress(2,5);

            for (Triplet<WeekDay, Grade, Integer> pair : pairs)
                State.getInstance().timetable.remove(pair);

            job.progress(3,5);

            freshAssignable = assignable.clone();
            freshTimeTable = new HashMap<>(State.getInstance().timetable);

            job.progress(4,5);

            State.getInstance().setClashes();
            freshClashes = new HashSet<>(State.getInstance().clashes);

            job.progress(5,5);
        }else{
            job.progress(0,3);
            State.getInstance().assignables.put(freshAssignable.getId(), freshAssignable.clone());

            job.progress(1,3);
            State.getInstance().timetable.clear();
            State.getInstance().timetable.putAll(freshTimeTable);

            job.progress(2,3);

            State.getInstance().clashes.clear();
            State.getInstance().clashes.addAll(freshClashes);

            job.progress(3,3);
        }

        State.getInstance().saveRequired = true;
    }

    @Override
    public void reverse(Job job) {
        job.progress(0, 3);
        State.getInstance().assignables.put(oldAssignable.getId(), oldAssignable.clone());

        job.progress(1, 3);

        State.getInstance().timetable.clear();
        State.getInstance().timetable.putAll(oldTimeTable);

        job.progress(2, 3);

        State.getInstance().clashes.clear();
        State.getInstance().clashes.addAll(oldClashes);

        job.progress(3, 3);

        State.getInstance().saveRequired = true;
    }
}
