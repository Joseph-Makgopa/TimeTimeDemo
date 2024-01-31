package com.example.demo.models.commands;

import com.example.demo.models.Educator;
import com.example.demo.models.Grade;
import com.example.demo.models.State;

public class RemoveEducatorCommand implements Command{
    private Educator educator;
    public RemoveEducatorCommand(Educator educator){
        this.educator = educator;
    }
    @Override
    public void execute() {
        State.getInstance().educators.remove(educator.getPost());
    }
    @Override
    public void reverse() {
        State.getInstance().educators.put(educator.getPost(), educator);
    }
}
