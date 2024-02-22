package com.example.demo.models.commands;

import com.example.demo.models.Session;
import com.example.demo.models.State;
import com.example.demo.models.Subject;

import java.util.HashMap;
import java.util.Map;

public class RemoveSubjectCommand implements Command{
    private Subject subject;
    private Map<Integer, Session> oldSessions = new HashMap<>();
    public RemoveSubjectCommand(Subject subject){
        this.subject = subject;
        oldSessions.putAll(State.getInstance().sessions);
    }
    public void execute() {
        State.getInstance().subjects.remove(subject);
        Map<Integer, Session> sessions = new HashMap<>();

        State.getInstance().sessions.forEach((id, session) -> {
            if(!session.getSubject().equals(subject)){
                sessions.put(id, session);
            }
        });

        State.getInstance().sessions.clear();
        State.getInstance().sessions.putAll(sessions);
    }

    public void reverse() {
        State.getInstance().subjects.add(subject);
        State.getInstance().sessions.clear();
        State.getInstance().sessions.putAll(oldSessions);
    }
}
