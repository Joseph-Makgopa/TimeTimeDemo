package com.example.demo.models.commands;

import com.example.demo.controllers.DemoController;
import com.example.demo.models.Session;
import com.example.demo.models.State;
import com.example.demo.models.Subject;
import com.example.demo.utilities.Job;

import java.util.HashMap;
import java.util.Map;

public class RemoveSubjectCommand implements Command{
    private Subject subject;
    private Map<Integer, Session> oldSessions = new HashMap<>();
    public RemoveSubjectCommand(Subject subject){
        this.subject = subject;
        oldSessions.putAll(State.getInstance().sessions);
    }

    @Override
    public String executeDescription() {
        return "";
    }

    @Override
    public String reverseDescription() {
        return "";
    }

    @Override
    public Boolean dataRefresh() {
        return null;
    }
    @Override
    public Boolean threadSafe(){
        return true;
    }
    @Override
    public void execute(Job job) {
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
    @Override
    public void reverse(Job job) {
        State.getInstance().subjects.add(subject);
        State.getInstance().sessions.clear();
        State.getInstance().sessions.putAll(oldSessions);
    }
}
