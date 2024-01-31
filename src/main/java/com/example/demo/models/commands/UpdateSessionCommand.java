package com.example.demo.models.commands;

import com.example.demo.models.Educator;
import com.example.demo.models.Session;
import com.example.demo.models.State;

public class UpdateSessionCommand implements  Command{
    private Session old;
    private Session fresh;
    public UpdateSessionCommand(Session old, Session fresh){
        this.old = old;
        this.fresh = fresh;
    }
    @Override
    public void execute(){
        State.getInstance().sessions.put(fresh.getId(), fresh);
    }
    @Override
    public void reverse(){
        State.getInstance().sessions.put(old.getId(), old);
    }
}
