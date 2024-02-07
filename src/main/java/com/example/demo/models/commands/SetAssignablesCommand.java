package com.example.demo.models.commands;

import com.example.demo.models.State;
import com.example.demo.models.assignable.Assignable;
import com.example.demo.models.assignable.PairAssignable;
import com.example.demo.models.assignable.SplitAssignable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SetAssignablesCommand implements Command{
    private Map<Integer, Assignable> old;
    public SetAssignablesCommand(){
        old = State.getInstance().assignables;
    }
    @Override
    public void execute() {
        ArrayList<Assignable> assignables = new ArrayList<>(State.getInstance().assignables.values());
        State.getInstance().assignables.clear();

        State.getInstance().sessions.forEach((id, session) ->{
            Assignable assignable;

            if(session.getPair() != null)
                assignable = new PairAssignable(session.getId(), session.getPair());
            else if(session.getSplit() != null)
                assignable = new SplitAssignable(session.getId(), session.getSplit());
            else
                assignable = new Assignable(session.getId());

            if(assignables.contains(assignable)){
                assignable = assignables.get(assignables.indexOf(assignable));
            }

            State.getInstance().assignables.put(assignable.getId(), assignable);
        });
    }

    @Override
    public void reverse() {
        State.getInstance().assignables.clear();
        State.getInstance().assignables.putAll(old);
    }
}
