package com.example.demo.models.commands;

import com.example.demo.models.State;
import com.example.demo.models.WeekDay;

import java.util.HashMap;
import java.util.Map;

public class UpdateStructureCommand implements Command{
    private Map<WeekDay, Integer> oldDays;
    private Integer oldBreakAfter;
    private Map<WeekDay, Integer> freshDays;
    private Integer freshBreakAfter;
    public UpdateStructureCommand(Map<WeekDay, Integer> days, Integer breakAfter){
        oldDays = new HashMap<>();
        oldBreakAfter = State.getInstance().breakAfter;

        State.getInstance().days.forEach((key, value) -> {
            oldDays.put(key, value);
        });

        freshDays = new HashMap<>();
        freshDays.putAll(days);
        freshBreakAfter = breakAfter;
    }
    @Override
    public void execute() {
        State.getInstance().days.clear();
        State.getInstance().days.putAll(freshDays);
        State.getInstance().breakAfter = freshBreakAfter;
    }

    @Override
    public void reverse() {
        State.getInstance().days.clear();
        State.getInstance().days.putAll(oldDays);
        State.getInstance().breakAfter = oldBreakAfter;
    }
}
