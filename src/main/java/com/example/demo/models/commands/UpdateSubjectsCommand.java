package com.example.demo.models.commands;

import com.example.demo.controllers.DemoController;
import com.example.demo.models.Grade;
import com.example.demo.models.Session;
import com.example.demo.models.State;
import com.example.demo.models.WeekDay;
import com.example.demo.models.Assignable;
import com.example.demo.services.DemoService;
import com.example.demo.utilities.Pair;
import com.example.demo.utilities.Triplet;

import java.util.HashMap;
import java.util.Map;

public class UpdateSubjectsCommand implements Command{
    private Map<Integer, Session> oldSessions;
    private Map<Pair<Integer, Integer>, Assignable> oldAssignables;
    private Map<Triplet<WeekDay, Grade, Integer>, Pair<Integer, Integer>> oldTimeTable;
    private CommandList commands;
    private DemoController demoController;
    public UpdateSubjectsCommand(DemoController demoController, CommandList commands){
        oldSessions = new HashMap<>(State.getInstance().sessions);

        oldAssignables = new HashMap<>(State.getInstance().assignables);

        oldTimeTable = new HashMap<>(State.getInstance().timetable);

        this.commands = commands;
        this.demoController = demoController;
    }
    @Override
    public void execute() {
        commands.execute();

        State.getInstance().sessions.clear();
        oldSessions.forEach((id, session) -> {
            if(State.getInstance().subjects.contains(session.getSubject())){
                State.getInstance().sessions.put(id, session);
            }
        });

        State.getInstance().assignables.clear();
        oldAssignables.forEach((id, assignable) -> {
            if(State.getInstance().sessions.get(id.getFirst()) != null){
                State.getInstance().assignables.put(id, assignable);
            }
        });

        State.getInstance().timetable.clear();
        oldTimeTable.forEach((triplet, id) -> {
            if(State.getInstance().assignables.containsKey(id)){
                State.getInstance().timetable.put(triplet, id);
            }
        });

        demoController.getService().refresh();
    }

    @Override
    public void reverse() {
        commands.reverse();

        State.getInstance().sessions.clear();
        State.getInstance().sessions.putAll(oldSessions);

        State.getInstance().assignables.clear();
        State.getInstance().assignables.putAll(oldAssignables);

        State.getInstance().timetable.clear();
        State.getInstance().timetable.putAll(oldTimeTable);

        demoController.getService().refresh();
    }
}
