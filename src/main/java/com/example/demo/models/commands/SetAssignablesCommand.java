package com.example.demo.models.commands;

import com.example.demo.controllers.DemoController;
import com.example.demo.models.Grade;
import com.example.demo.models.State;
import com.example.demo.models.Assignable;
import com.example.demo.models.WeekDay;
import com.example.demo.services.DemoService;
import com.example.demo.utilities.Pair;
import com.example.demo.utilities.Triplet;

import java.util.*;

public class SetAssignablesCommand implements Command{
    private Map<Pair<Integer, Integer>, Assignable> oldAssignable;
    private Map<Triplet<WeekDay, Grade, Integer>, Pair<Integer, Integer>> oldTimeTable;
    private Set<Triplet<WeekDay, Grade, Integer>> oldClashes, freshClashes;
    private DemoController demoController;
    private CommandList commands;
    public SetAssignablesCommand(DemoController demoController, CommandList commands){
        oldAssignable = new HashMap<>(State.getInstance().assignables);
        oldTimeTable = new HashMap<>(State.getInstance().timetable);
        oldClashes = new HashSet<>(State.getInstance().clashes);
        freshClashes = null;

        this.demoController = demoController;
        this.commands = commands;
    }
    @Override
    public void execute() {
        commands.execute();

        State.getInstance().assignables.clear();

        State.getInstance().sessions.forEach((id, session) ->{
            Assignable assignable = new Assignable(id);

            State.getInstance().assignables.putIfAbsent(assignable.getId(), assignable);
        });

        Iterator<Map.Entry<Triplet<WeekDay, Grade, Integer>, Pair<Integer, Integer>>> iterator = State.getInstance().timetable.entrySet().iterator();

        while(iterator.hasNext()){
            Map.Entry<Triplet<WeekDay, Grade, Integer>, Pair<Integer, Integer>> entry = iterator.next();

            if(entry.getValue() != null && !State.getInstance().assignables.containsKey(entry.getValue())){
                iterator.remove();
            }
        }

        if(freshClashes == null){
            State.getInstance().setClashes();
            freshClashes = new HashSet<>(State.getInstance().clashes);
        }else{
            State.getInstance().clashes.clear();
            State.getInstance().clashes.addAll(freshClashes);
        }

        State.getInstance().saveRequired = true;
        demoController.getService().refresh();
    }

    @Override
    public void reverse() {
        commands.reverse();

        State.getInstance().assignables.clear();
        State.getInstance().timetable.clear();

        State.getInstance().assignables.putAll(oldAssignable);
        State.getInstance().timetable.putAll(oldTimeTable);

        State.getInstance().clashes.clear();
        State.getInstance().clashes.addAll(oldClashes);

        State.getInstance().saveRequired = true;
        demoController.getService().refresh();
    }
}
