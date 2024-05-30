package com.example.demo.models.commands;

import com.example.demo.controllers.DemoController;
import com.example.demo.models.Session;
import com.example.demo.models.State;
import com.example.demo.utilities.Job;
import javafx.concurrent.Task;

public class AddSessionCommand implements Command{
    private Session session;
    public AddSessionCommand(Session session){
        this.session = session;
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
    public void execute(Job job){
        State.getInstance().sessions.put(session.getId(), session);
    }
    @Override
    public void reverse(Job job){
        State.getInstance().sessions.remove(session.getId());
    }
}
