package com.example.demo.models.commands;

import com.example.demo.controllers.DemoController;
import com.example.demo.models.Session;
import com.example.demo.models.State;

public class AddSessionCommand extends Command{
    private Session session;
    public AddSessionCommand(DemoController demoController, Session session){
        super(demoController);
        this.session = session;
    }
    @Override
    public void executeCode(){
        State.getInstance().sessions.put(session.getId(), session);
    }
    @Override
    public void reverseCode(){
        State.getInstance().sessions.remove(session.getId());
    }
}
