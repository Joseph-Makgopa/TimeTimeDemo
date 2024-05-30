package com.example.demo.models.commands;

import com.example.demo.controllers.DemoController;
import com.example.demo.models.Educator;
import com.example.demo.models.Session;
import com.example.demo.models.State;
import com.example.demo.utilities.Job;

public class UpdateSessionCommand implements Command{
    private Session old;
    private Session fresh;
    public UpdateSessionCommand(Session old, Session fresh){
        this.old = old.clone();
        this.fresh = fresh.clone();
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
        State.getInstance().sessions.put(fresh.getId(), fresh.clone());
        Session session;

        if(old.getSplit() != null){
            session = State.getInstance().sessions.get(old.getSplit());
            session.setSplit(null);
        }


        if(fresh.getSplit() != null){
            session = State.getInstance().sessions.get(fresh.getSplit());

            if(session.getSplit() != null)
                State.getInstance().sessions.get(session.getSplit()).setSplit(null);

            session.setSplit(fresh.getId());
        }


        if(old.getPair() != null){
            session = State.getInstance().sessions.get(old.getPair());
            session.setPair(null);
        }


        if(fresh.getPair() != null){
            session = State.getInstance().sessions.get(fresh.getPair());

            if(session.getPair() != null)
                State.getInstance().sessions.get(session.getPair()).setPair(null);

            session.setPair(fresh.getId());
        }

    }
    @Override
    public void reverse(Job job){
        State.getInstance().sessions.put(old.getId(), old.clone());
        Session session;

        if(fresh.getSplit() != null){
            session = State.getInstance().sessions.get(fresh.getSplit());
            session.setSplit(null);
        }

        if(old.getSplit() != null){
            session = State.getInstance().sessions.get(old.getSplit());

            if(session.getSplit() != null)
                State.getInstance().sessions.get(session.getSplit()).setSplit(null);

            session.setSplit(old.getId());
        }

        if(fresh.getPair() != null){
            session = State.getInstance().sessions.get(fresh.getPair());
            session.setPair(null);
        }

        if(old.getPair() != null){
            session = State.getInstance().sessions.get(old.getPair());

            if(session.getPair() != null)
                State.getInstance().sessions.get(session.getPair()).setPair(null);

            session.setPair(old.getId());
        }
    }
}
