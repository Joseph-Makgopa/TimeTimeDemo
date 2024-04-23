package com.example.demo.models.commands;

import com.example.demo.controllers.DemoController;
import com.example.demo.models.Grade;
import com.example.demo.models.Session;
import com.example.demo.models.State;
import com.example.demo.models.WeekDay;
import com.example.demo.models.Assignable;
import com.example.demo.services.DemoService;
import com.example.demo.services.GradeViewService;
import com.example.demo.utilities.Pair;
import com.example.demo.utilities.Triplet;

import java.util.*;

public class UpdateGradesCommand extends Command {
    private Map<Integer, Session> oldSessions;
    private Map<Pair<Integer, Integer>, Assignable> oldAssignables;
    private Map<Triplet<WeekDay, Grade, Integer>, Pair<Integer, Integer>> oldTimeTable;
    private Set<Triplet<WeekDay, Grade, Integer>> oldClashes, freshClashes;
    private CommandList commands;
    public UpdateGradesCommand(DemoController demoController, CommandList commands){
        super(demoController);
        oldSessions = new HashMap<>();
        oldSessions.putAll(State.getInstance().sessions);

        oldAssignables = new HashMap<>();
        oldAssignables.putAll(State.getInstance().assignables);

        oldTimeTable = new HashMap<>();
        oldTimeTable.putAll(State.getInstance().timetable);

        oldClashes = new HashSet<>(State.getInstance().clashes);
        freshClashes = null;

        this.commands = commands;
    }
    @Override
    public void executeCode() {
        commands.executeCode();
        State.getInstance().grades.sort(Comparator.naturalOrder());

        State.getInstance().sessions.clear();
        oldSessions.forEach((id, session) -> {
            if(State.getInstance().grades.contains(session.getGrade())){
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
            if(State.getInstance().grades.contains(triplet.getSecond())){
                State.getInstance().timetable.put(triplet, id);
            }
        });

        if(freshClashes == null){
            State.getInstance().setClashes();
            freshClashes = new HashSet<>(State.getInstance().clashes);
        }else{
            State.getInstance().clashes.clear();
            State.getInstance().clashes.addAll(freshClashes);
        }

        State.getInstance().saveRequired = true;
        if(getDemoController().getService() instanceof GradeViewService) {
            getDemoController().getService().refresh();
        }else{
            getDemoController().getService().refreshData();
        }
    }

    @Override
    public void reverseCode() {
        commands.reverseCode();
        State.getInstance().grades.sort(Comparator.naturalOrder());

        State.getInstance().sessions.clear();
        State.getInstance().sessions.putAll(oldSessions);

        State.getInstance().assignables.clear();
        State.getInstance().assignables.putAll(oldAssignables);

        State.getInstance().timetable.clear();
        State.getInstance().timetable.putAll(oldTimeTable);

        State.getInstance().clashes.clear();
        State.getInstance().clashes.addAll(oldClashes);

        State.getInstance().saveRequired = true;
        if(getDemoController().getService() instanceof GradeViewService) {
            getDemoController().getService().refresh();
        }else{
            getDemoController().getService().refreshData();
        }
    }
}
