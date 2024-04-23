package com.example.demo.models.commands;

import com.example.demo.controllers.DemoController;
import com.example.demo.models.Educator;
import com.example.demo.models.Grade;
import com.example.demo.models.State;

public class AddEducatorCommand extends Command{
    private Educator educator;
    public AddEducatorCommand(DemoController demoController, Educator educator){
        super(demoController);
        this.educator = educator;
    }
    @Override
    public void executeCode(){
        State.getInstance().educators.put(educator.getPost(), educator);
    }
    @Override
    public void reverseCode(){
        State.getInstance().educators.remove(educator);
    }
}
