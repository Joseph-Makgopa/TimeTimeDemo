package com.example.demo.models.commands;

import com.example.demo.controllers.DemoController;
import com.example.demo.models.Educator;
import com.example.demo.models.Session;
import com.example.demo.models.State;

public class UpdateSessionCommand extends Command{
    private Session old;
    private Session fresh;
    public UpdateSessionCommand(DemoController demoController, Session old, Session fresh){
        super(demoController);
        this.old = old;
        this.fresh = fresh;
    }
    @Override
    public void executeCode(){
        State.getInstance().sessions.put(fresh.getId(), fresh);
        Session session;

        if(old.getSplit() != null){
            session = State.getInstance().sessions.get(old.getSplit());
            session.setSplit(null);
        }

        if(fresh.getSplit() != null){
            session = State.getInstance().sessions.get(fresh.getSplit());
            session.setSplit(fresh.getId());
        }

        if(old.getPair() != null){
            session = State.getInstance().sessions.get(old.getPair());
            session.setPair(null);
        }

        if(fresh.getPair() != null){
            session = State.getInstance().sessions.get(fresh.getPair());
            session.setPair(fresh.getId());
        }
    }
    @Override
    public void reverseCode(){
        State.getInstance().sessions.put(old.getId(), old);
        Session session;

        if(fresh.getSplit() != null){
            session = State.getInstance().sessions.get(fresh.getSplit());
            session.setSplit(null);
        }

        if(old.getSplit() != null){
            session = State.getInstance().sessions.get(old.getSplit());
            session.setSplit(old.getId());
        }

        if(fresh.getPair() != null){
            session = State.getInstance().sessions.get(fresh.getPair());
            session.setPair(null);
        }

        if(old.getPair() != null){
            session = State.getInstance().sessions.get(old.getPair());
            session.setPair(old.getId());
        }
    }
}
