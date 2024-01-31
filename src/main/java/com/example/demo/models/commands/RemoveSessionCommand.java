package com.example.demo.models.commands;

import com.example.demo.models.Educator;
import com.example.demo.models.Session;
import com.example.demo.models.State;

public class RemoveSessionCommand implements Command{
    private Session session;
    public RemoveSessionCommand(Session session){
        this.session = session;
    }
    @Override
    public void execute() {
        State.getInstance().sessions.remove(session.getId());
    }
    @Override
    public void reverse() {
        State.getInstance().sessions.put(session.getId(), session);
    }
}
