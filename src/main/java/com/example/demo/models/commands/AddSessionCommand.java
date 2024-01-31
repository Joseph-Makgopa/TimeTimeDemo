package com.example.demo.models.commands;

import com.example.demo.models.Session;
import com.example.demo.models.State;

public class AddSessionCommand implements Command{
    private Session session;
    public AddSessionCommand(Session session){
        this.session = session;
    }
    @Override
    public void execute(){
        State.getInstance().sessions.put(session.getId(), session);
    }
    @Override
    public void reverse(){
        State.getInstance().sessions.remove(session.getId());
    }
}
