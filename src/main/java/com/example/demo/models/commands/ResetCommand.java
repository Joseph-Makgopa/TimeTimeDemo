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

public class ResetCommand implements Command{
    private Map<Pair<Integer, Integer>, Assignable> lessons;
    private Map<Triplet<WeekDay, Grade, Integer>, Pair<Integer, Integer>> oldTimeTable, freshTimeTable;
    private LinkedList<Assignable> oldAssignable, freshAssignable;
    private Set<Triplet<WeekDay, Grade, Integer>> oldClashes, freshClashes;
    public ResetCommand(LinkedList<Assignable> lessons){
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
    public String executeDescription() {
        return "  resetting lessons.";
    }

    @Override
    public String reverseDescription() {
        return "  reversing reset lessons.";
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
            Iterator<Map.Entry<Triplet<WeekDay, Grade, Integer>, Pair<Integer, Integer>>> iterator = State.getInstance().timetable.entrySet().iterator();
            LinkedList<Triplet<WeekDay, Grade, Integer>> pairs = new LinkedList<>();

            job.progress(0,5);

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

            job.progress(1,5);

            for (Triplet<WeekDay, Grade, Integer> pair: pairs)
                State.getInstance().timetable.remove(pair);

            job.progress(2,5);

            freshAssignable = new LinkedList<>();
            for(Assignable assignable: lessons.values()) {
                freshAssignable.add(assignable.clone());
                State.getInstance().assignables.put(assignable.getId(), assignable.clone());
            }

            job.progress(3,5);

            freshTimeTable = new HashMap<>(State.getInstance().timetable);

            job.progress(4,5);

            State.getInstance().setClashes();
            freshClashes = new HashSet<>(State.getInstance().clashes);

            job.progress(5,5);
        }else{
            job.progress(0,3);

            State.getInstance().timetable.clear();
            State.getInstance().timetable.putAll(freshTimeTable);

            job.progress(1,3);

            for(Assignable assignable: freshAssignable)
                State.getInstance().assignables.put(assignable.getId(), assignable.clone());

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

        State.getInstance().timetable.clear();
        State.getInstance().timetable.putAll(oldTimeTable);

        job.progress(1,3);

        for(Assignable assignable: oldAssignable)
            State.getInstance().assignables.put(assignable.getId(), assignable.clone());

        job.progress(2,3);

        State.getInstance().clashes.clear();
        State.getInstance().clashes.addAll(oldClashes);

        job.progress(3,3);

        State.getInstance().saveRequired = true;
    }
}
