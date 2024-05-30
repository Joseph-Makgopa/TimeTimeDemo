package com.example.demo.models.commands;

import com.example.demo.controllers.DemoController;
import com.example.demo.models.Educator;
import com.example.demo.models.State;
import com.example.demo.utilities.Job;

public class UpdateEducatorCommand implements Command{
    private Educator old;
    private Educator fresh;
    public UpdateEducatorCommand(Educator old, Educator fresh){
        this.old = old;
        this.fresh = fresh;
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
        State.getInstance().educators.put(fresh.getPost(), fresh);
    }
    @Override
    public void reverse(Job job){
        State.getInstance().educators.put(old.getPost(), old);
    }
}
