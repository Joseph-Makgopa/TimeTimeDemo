package com.example.demo.models.commands;

import com.example.demo.controllers.DemoController;
import com.example.demo.models.Grade;
import com.example.demo.models.State;
import com.example.demo.models.Assignable;
import com.example.demo.models.WeekDay;
import com.example.demo.services.DemoService;
import com.example.demo.utilities.Job;
import com.example.demo.utilities.Pair;
import com.example.demo.utilities.Triplet;

import java.util.*;

public class SetAssignablesCommand implements Command{
    private Map<Pair<Integer, Integer>, Assignable> oldAssignable;
    private Map<Triplet<WeekDay, Grade, Integer>, Pair<Integer, Integer>> oldTimeTable;
    private Set<Triplet<WeekDay, Grade, Integer>> oldClashes, freshClashes;
    private CommandList commands;
    public SetAssignablesCommand(CommandList commands){
        oldAssignable = new HashMap<>(State.getInstance().assignables);
        oldTimeTable = new HashMap<>(State.getInstance().timetable);
        oldClashes = new HashSet<>(State.getInstance().clashes);
        freshClashes = null;

        this.commands = commands;
    }

    @Override
    public String executeDescription() {
        return "  updating lessons.";
    }

    @Override
    public String reverseDescription() {
        return "  reversing lessons update.";
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
        commands.execute(job);

        job.progress(0, 3);

        State.getInstance().assignables.clear();
        State.getInstance().sessions.forEach((id, session) ->{
            Assignable assignable = new Assignable(id);

            State.getInstance().assignables.putIfAbsent(assignable.getId(), assignable);
        });

        job.progress(1, 3);

        Iterator<Map.Entry<Triplet<WeekDay, Grade, Integer>, Pair<Integer, Integer>>> iterator = State.getInstance().timetable.entrySet().iterator();

        while(iterator.hasNext()){
            Map.Entry<Triplet<WeekDay, Grade, Integer>, Pair<Integer, Integer>> entry = iterator.next();

            if(entry.getValue() != null && !State.getInstance().assignables.containsKey(entry.getValue())){
                iterator.remove();
            }
        }

        job.progress(2, 3);

        if(freshClashes == null){
            State.getInstance().setClashes();
            freshClashes = new HashSet<>(State.getInstance().clashes);
        }else{
            State.getInstance().clashes.clear();
            State.getInstance().clashes.addAll(freshClashes);
        }

        job.progress(3, 3);
        State.getInstance().saveRequired = true;
    }

    @Override
    public void reverse(Job job) {
        commands.reverse(job);

        job.progress(0, 3);

        State.getInstance().assignables.clear();
        State.getInstance().timetable.clear();

        job.progress(1, 3);

        State.getInstance().assignables.putAll(oldAssignable);
        State.getInstance().timetable.putAll(oldTimeTable);

        job.progress(2, 3);

        State.getInstance().clashes.clear();
        State.getInstance().clashes.addAll(oldClashes);

        job.progress(3, 3);

        State.getInstance().saveRequired = true;
    }
}
