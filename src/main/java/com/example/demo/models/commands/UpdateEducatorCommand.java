package com.example.demo.models.commands;

import com.example.demo.models.Educator;
import com.example.demo.models.State;

public class UpdateEducatorCommand implements Command{
    private Educator old;
    private Educator fresh;
    public UpdateEducatorCommand(Educator old, Educator fresh){
        this.old = old;
        this.fresh = fresh;
    }
    @Override
    public void execute(){
        State.getInstance().educators.put(fresh.getPost(), fresh);
    }
    @Override
    public void reverse(){
        State.getInstance().educators.put(old.getPost(), old);
    }
}
