package com.example.demo.models.commands;

import com.example.demo.controllers.DemoController;
import com.example.demo.models.Assignable;
import com.example.demo.models.Grade;
import com.example.demo.models.State;
import com.example.demo.models.WeekDay;
import com.example.demo.utilities.Job;
import com.example.demo.utilities.Pair;
import com.example.demo.utilities.Triplet;

import java.util.*;

public class ArrangeSelectionCommand implements Command{
    private Map<Triplet<WeekDay, Grade, Integer>, Pair<Integer, Integer>> oldTimeTable, freshTimeTable;
    private LinkedList<Assignable> oldAssignable, freshAssignable;
    private Set<Triplet<WeekDay, Grade, Integer>> oldClashes, freshClashes;
    private Assignable current;
    private DemoController demoController;
    public ArrangeSelectionCommand(DemoController demoController, Assignable current){
        this.demoController = demoController;
        this.oldTimeTable = new HashMap<>(State.getInstance().timetable);
        oldAssignable = new LinkedList<>();

        Assignable copy;

        for(Assignable assignable: State.getInstance().assignables.values()){
            copy = assignable.clone();
            oldAssignable.add(copy);

            if(current.equals(copy))
                this.current = copy;
        }

        this.freshTimeTable = null;
        this.freshAssignable = null;

        this.oldClashes = new HashSet<>(State.getInstance().clashes);
        this.freshClashes = null;
    }

    @Override
    public String executeDescription() {
        return "  arranging lesson.";
    }

    @Override
    public String reverseDescription() {
        return "  reversing arranged lesson.";
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
            freshAssignable = new LinkedList<>();
            State.getInstance().assignables.clear();
            Assignable copy;
            LinkedList<Assignable> options = new LinkedList<>();

            for(Assignable assignable: oldAssignable){
                copy = assignable.clone();
                freshAssignable.add(copy);
                State.getInstance().assignables.put(copy.getId(), copy);

                if(current.equals(copy))
                    options.add(copy);
            }

            demoController.getService().arrange(options, job);


            State.getInstance().setClashes();
            freshClashes = new HashSet<>(State.getInstance().clashes);
        }else{
            job.progress(1, freshAssignable.size() + 3);

            State.getInstance().timetable.clear();
            State.getInstance().timetable.putAll(freshTimeTable);

            job.progress(1, freshAssignable.size() + 3);

            State.getInstance().assignables.clear();
            for(Assignable assignable: freshAssignable) {
                State.getInstance().assignables.put(assignable.getId(), assignable.clone());
                job.progress(State.getInstance().assignables.size(), freshAssignable.size());

                job.progress(2 + State.getInstance().assignables.size(), freshAssignable.size() + 3);
            }

            State.getInstance().clashes.clear();
            State.getInstance().clashes.addAll(freshClashes);

            job.progress(freshAssignable.size() + 3, freshAssignable.size() + 3);
        }

        State.getInstance().saveRequired = true;
    }
    @Override
    public void reverse(Job job){
        job.progress(0, oldAssignable.size() + 3);

        State.getInstance().timetable.clear();
        State.getInstance().timetable.putAll(oldTimeTable);

        job.progress(1, oldAssignable.size() + 3);

        State.getInstance().assignables.clear();
        for(Assignable assignable: oldAssignable) {
            State.getInstance().assignables.put(assignable.getId(), assignable.clone());

            job.progress(2 + State.getInstance().assignables.size(), oldAssignable.size() + 3);
        }

        State.getInstance().clashes.clear();
        State.getInstance().clashes.addAll(oldClashes);

        job.progress(3 + oldAssignable.size(), oldAssignable.size() + 3);
        State.getInstance().saveRequired = true;
    }
}
