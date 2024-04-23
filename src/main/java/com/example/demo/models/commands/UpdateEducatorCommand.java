package com.example.demo.models.commands;

import com.example.demo.controllers.DemoController;
import com.example.demo.models.Educator;
import com.example.demo.models.State;

public class UpdateEducatorCommand extends Command{
    private Educator old;
    private Educator fresh;
    public UpdateEducatorCommand(DemoController demoController, Educator old, Educator fresh){
        super(demoController);
        this.old = old;
        this.fresh = fresh;
    }
    @Override
    public void executeCode(){
        State.getInstance().educators.put(fresh.getPost(), fresh);
    }
    @Override
    public void reverseCode(){
        State.getInstance().educators.put(old.getPost(), old);
    }
}
