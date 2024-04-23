package com.example.demo.models.commands;

import com.example.demo.controllers.DemoController;
import com.example.demo.models.Educator;
import com.example.demo.models.Grade;
import com.example.demo.models.Session;
import com.example.demo.models.State;

import java.util.HashMap;
import java.util.Map;

public class RemoveEducatorCommand extends Command{
    private Educator educator;
    private Map<Integer, Session> oldSessions = new HashMap<>();
    public RemoveEducatorCommand(DemoController demoController, Educator educator){
        super(demoController);
        this.educator = educator;
        this.oldSessions.putAll(State.getInstance().sessions);
    }
    @Override
    public void executeCode() {
        State.getInstance().educators.remove(educator.getPost());
        Map<Integer, Session> sessions = new HashMap<>();

        State.getInstance().sessions.forEach((id, session) -> {
            if(!session.getEducator().equals(educator)){
                sessions.put(id,session);
            }
        });

        State.getInstance().sessions.clear();
        State.getInstance().sessions.putAll(sessions);
    }
    @Override
    public void reverseCode() {
        State.getInstance().educators.put(educator.getPost(), educator);
        State.getInstance().sessions.clear();
        State.getInstance().sessions.putAll(oldSessions);
    }
}
