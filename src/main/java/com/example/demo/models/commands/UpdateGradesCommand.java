package com.example.demo.models.commands;

import com.example.demo.models.Grade;
import com.example.demo.models.Session;
import com.example.demo.models.State;
import com.example.demo.models.WeekDay;
import com.example.demo.models.assignable.Assignable;
import com.example.demo.utilities.Pair;
import com.example.demo.utilities.Triplet;

import java.util.HashMap;
import java.util.Map;

public class UpdateGradesCommand implements Command {
    private Map<Integer, Session> oldSessions;
    private Map<Pair<Integer, Integer>, Assignable> oldAssignables;
    private Map<Triplet<WeekDay, Grade, Integer>, Pair<Integer, Integer>> oldTimeTable;
    public UpdateGradesCommand(){
        oldSessions = new HashMap<>();
        oldSessions.putAll(State.getInstance().sessions);

        oldAssignables = new HashMap<>();
        oldAssignables.putAll(State.getInstance().assignables);

        oldTimeTable = new HashMap<>();
        oldTimeTable.putAll(State.getInstance().timetable);
    }
    @Override
    public void execute() {
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
    }

    @Override
    public void reverse() {
        State.getInstance().sessions.clear();
        State.getInstance().sessions.putAll(oldSessions);

        State.getInstance().assignables.clear();
        State.getInstance().assignables.putAll(oldAssignables);

        State.getInstance().timetable.clear();
        State.getInstance().timetable.putAll(oldTimeTable);
    }
}
