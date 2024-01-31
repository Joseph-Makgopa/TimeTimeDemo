package com.example.demo.models.commands;

import com.example.demo.models.Educator;
import com.example.demo.models.Grade;
import com.example.demo.models.State;

public class AddEducatorCommand implements Command{
    private Educator educator;
    public AddEducatorCommand(Educator educator){
        this.educator = educator;
    }
    @Override
    public void execute(){
        State.getInstance().educators.put(educator.getPost(), educator);
    }
    @Override
    public void reverse(){
        State.getInstance().grades.remove(educator);
    }
}
