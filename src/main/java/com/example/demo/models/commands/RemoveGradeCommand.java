package com.example.demo.models.commands;

import com.example.demo.controllers.DemoController;
import com.example.demo.models.Grade;
import com.example.demo.models.Session;
import com.example.demo.models.State;
import com.example.demo.models.Subject;

import java.util.HashMap;
import java.util.Map;

public class RemoveGradeCommand extends Command{
    private Grade grade;
    private Map<Integer, Session> oldSessions = new HashMap<>();
    public RemoveGradeCommand(DemoController demoController, Grade grade){
        super(demoController);
        this.grade = grade;
        this.oldSessions.putAll(State.getInstance().sessions);
    }
    public void executeCode() {
        State.getInstance().grades.remove(grade);
        Map<Integer, Session> sessions = new HashMap<>();

        State.getInstance().sessions.forEach((id, session) -> {
            if(!session.getGrade().equals(grade)){
                sessions.put(id,session);
            }
        });

        State.getInstance().sessions.clear();
        State.getInstance().sessions.putAll(sessions);
    }

    public void reverseCode() {
        State.getInstance().grades.add(grade);
        State.getInstance().sessions.clear();
        State.getInstance().sessions.putAll(oldSessions);
    }
}
