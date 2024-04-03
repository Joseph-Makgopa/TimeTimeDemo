package com.example.demo.models.commands;

import com.example.demo.models.Grade;
import com.example.demo.models.State;
import com.example.demo.models.Assignable;
import com.example.demo.models.WeekDay;
import com.example.demo.services.DemoService;
import com.example.demo.utilities.Pair;
import com.example.demo.utilities.Triplet;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SetAssignablesCommand implements Command{
    private Map<Pair<Integer, Integer>, Assignable> oldAssignable;
    private Map<Triplet<WeekDay, Grade, Integer>, Pair<Integer, Integer>> oldTimeTable;
    private DemoService service;
    private CommandList commands;
    public SetAssignablesCommand(DemoService service, CommandList commands){
        oldAssignable = new HashMap<>(State.getInstance().assignables);
        oldTimeTable = new HashMap<>(State.getInstance().timetable);

        this.service = service;
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

        State.getInstance().timetable.clear();

        State.getInstance().timetable.forEach((triplet, reference) -> {
            if(State.getInstance().assignables.containsKey(reference)){
                State.getInstance().timetable.put(triplet, reference);
            }
        });

        Iterator<Map.Entry<Triplet<WeekDay, Grade, Integer>, Pair<Integer, Integer>>> iterator = State.getInstance().timetable.entrySet().iterator();

        while(iterator.hasNext()){
            Map.Entry<Triplet<WeekDay, Grade, Integer>, Pair<Integer, Integer>> entry = iterator.next();

            if(entry.getValue() != null && !State.getInstance().assignables.containsKey(entry.getValue())){
                iterator.remove();
            }
        }

        service.refresh();
    }

    @Override
    public void reverse() {
        commands.reverse();

        State.getInstance().assignables.clear();
        State.getInstance().timetable.clear();

        State.getInstance().assignables.putAll(oldAssignable);
        State.getInstance().timetable.putAll(oldTimeTable);

        service.refresh();
    }
}
