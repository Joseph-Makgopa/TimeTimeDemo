package com.example.demo.models.commands;

import com.example.demo.controllers.DemoController;
import com.example.demo.models.Grade;
import com.example.demo.models.Session;
import com.example.demo.models.State;
import com.example.demo.models.WeekDay;
import com.example.demo.models.Assignable;
import com.example.demo.services.DemoService;
import com.example.demo.services.GradeViewService;
import com.example.demo.utilities.Job;
import com.example.demo.utilities.Pair;
import com.example.demo.utilities.Triplet;

import java.util.*;

public class UpdateGradesCommand implements Command {
    private Map<Integer, Session> oldSessions;
    private Map<Pair<Integer, Integer>, Assignable> oldAssignables;
    private Map<Triplet<WeekDay, Grade, Integer>, Pair<Integer, Integer>> oldTimeTable;
    private Set<Triplet<WeekDay, Grade, Integer>> oldClashes, freshClashes;
    private CommandList commands;
    public UpdateGradesCommand(CommandList commands){
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
    public String executeDescription() {
        return "  updating grades.";
    }

    @Override
    public String reverseDescription() {
        return "  reversing grade updates.";
    }

    @Override
    public Boolean dataRefresh() {
        return false;
    }
    @Override
    public Boolean threadSafe(){
        return true;
    }
    @Override
    public void execute(Job job) {
        commands.execute(job);

        job.progress(0, 5);

        State.getInstance().grades.sort(Comparator.naturalOrder());

        job.progress(1,5);

        State.getInstance().sessions.clear();
        oldSessions.forEach((id, session) -> {
            if(State.getInstance().grades.contains(session.getGrade())){
                State.getInstance().sessions.put(id, session);
            }
        });

        job.progress(2,5);

        State.getInstance().assignables.clear();
        oldAssignables.forEach((id, assignable) -> {
            if(State.getInstance().sessions.get(id.getFirst()) != null){
                State.getInstance().assignables.put(id, assignable);
            }
        });

        job.progress(3,5);

        State.getInstance().timetable.clear();
        oldTimeTable.forEach((triplet, id) -> {
            if(State.getInstance().grades.contains(triplet.getSecond())){
                State.getInstance().timetable.put(triplet, id);
            }
        });

        job.progress(4,5);

        if(freshClashes == null){
            State.getInstance().setClashes();
            freshClashes = new HashSet<>(State.getInstance().clashes);
        }else{
            State.getInstance().clashes.clear();
            State.getInstance().clashes.addAll(freshClashes);
        }

        job.progress(5,5);

        State.getInstance().saveRequired = true;
    }

    @Override
    public void reverse(Job job) {
        commands.reverse(job);

        job.progress(0, 5);

        State.getInstance().grades.sort(Comparator.naturalOrder());

        job.progress(1, 5);

        State.getInstance().sessions.clear();
        State.getInstance().sessions.putAll(oldSessions);

        job.progress(2, 5);

        State.getInstance().assignables.clear();
        State.getInstance().assignables.putAll(oldAssignables);

        job.progress(3, 5);

        State.getInstance().timetable.clear();
        State.getInstance().timetable.putAll(oldTimeTable);

        job.progress(4,5);

        State.getInstance().clashes.clear();
        State.getInstance().clashes.addAll(oldClashes);

        job.progress(5,5);

        State.getInstance().saveRequired = true;
    }
}
