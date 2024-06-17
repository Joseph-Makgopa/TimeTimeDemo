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
    private final Map<Triplet<WeekDay, Grade, Integer>, Pair<Integer, Integer>> oldTimeTable;
    private final LinkedList<Assignable> oldAssignable;
    private final Set<Triplet<WeekDay, Grade, Integer>> oldClashes;

    public ResetCommand(){
        oldTimeTable = new HashMap<>(State.getInstance().timetable);

        oldAssignable = new LinkedList<>();
        for(Assignable assignable: State.getInstance().assignables.values()){
            oldAssignable.add(assignable.clone());
        }

        oldClashes = new HashSet<>(State.getInstance().clashes);
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
        job.progress(0, 3);
        State.getInstance().timetable.clear();

        job.progress(1, 3);
        State.getInstance().assignables.forEach((key, value) -> value.setRemain(value.getAmount()));

        job.progress(2, 3);
        State.getInstance().clashes.clear();

        job.progress(3, 3);
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
