package com.example.demo.models.commands;

import com.example.demo.models.State;
import com.example.demo.models.WeekDay;

import java.util.HashMap;
import java.util.Map;

public class UpdateStructureCommand implements Command{
    private Map<WeekDay, Integer> old;
    private Map<WeekDay, Integer> fresh;
    public UpdateStructureCommand(Map<WeekDay, Integer> days){
        old = new HashMap<>();
        State.getInstance().days.forEach((key, value) -> {
            old.put(key, value);
        });

        fresh = new HashMap<>();
        fresh.putAll(days);
    }
    @Override
    public void execute() {
        State.getInstance().days.clear();
        State.getInstance().days.putAll(fresh);
    }

    @Override
    public void reverse() {
        State.getInstance().days.clear();
        State.getInstance().days.putAll(old);
    }
}
