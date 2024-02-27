package com.example.demo.models.commands;

import com.example.demo.models.State;
import com.example.demo.models.Assignable;
import com.example.demo.utilities.Pair;

import java.util.Map;

public class SetAssignablesCommand implements Command{
    private Map<Pair<Integer, Integer>, Assignable> old;
    public SetAssignablesCommand(){
        old = State.getInstance().assignables;
    }
    @Override
    public void execute() {
        State.getInstance().assignables.clear();

        State.getInstance().sessions.forEach((id, session) ->{
            Assignable assignable = new Assignable(id);

            State.getInstance().assignables.putIfAbsent(assignable.getId(), assignable);
        });

        State.getInstance().timetable.clear();

        State.getInstance().timetable.forEach((triplet, reference) -> {
            if(State.getInstance().assignables.containsKey(reference)){
                State.getInstance().timetable.put(triplet, reference);
            }
        });
    }

    @Override
    public void reverse() {
        State.getInstance().assignables.clear();
        State.getInstance().assignables.putAll(old);
    }
}
