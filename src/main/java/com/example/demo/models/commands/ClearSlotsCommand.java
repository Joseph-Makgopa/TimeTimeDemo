package com.example.demo.models.commands;

import com.example.demo.controllers.DemoController;
import com.example.demo.models.Assignable;
import com.example.demo.models.Grade;
import com.example.demo.models.State;
import com.example.demo.models.WeekDay;
import com.example.demo.services.DemoService;
import com.example.demo.utilities.Pair;
import com.example.demo.utilities.Triplet;

import java.util.LinkedList;
import java.util.Map;

public class ClearSlotsCommand implements Command{
    private LinkedList<Map.Entry<Triplet<WeekDay, Grade, Integer>, Pair<Integer, Integer>>> trash;
    DemoController demoController;
    public ClearSlotsCommand(LinkedList <Map.Entry<Triplet<WeekDay, Grade, Integer>, Pair<Integer, Integer>>> trash, DemoController demoController){
        this.trash = trash;
        this.demoController = demoController;
    }
    @Override
    public void execute() {
        for(Map.Entry<Triplet<WeekDay, Grade, Integer>, Pair<Integer, Integer>> entry: trash){
            if(entry.getValue() != null){
                Assignable assignable = State.getInstance().assignables.get(entry.getValue());

                assignable.setRemain(assignable.getRemain() + 1);
                State.getInstance().timetable.remove(entry.getKey());
            }
        }

        demoController.getService().refresh();
    }

    @Override
    public void reverse() {
        for(Map.Entry<Triplet<WeekDay, Grade, Integer>, Pair<Integer, Integer>> entry: trash){
            if(entry.getValue() != null){
                Assignable assignable = State.getInstance().assignables.get(entry.getValue());

                assignable.setRemain(assignable.getRemain() - 1);
                State.getInstance().timetable.put(entry.getKey(), entry.getValue());
            }
        }

        demoController.getService().refresh();
    }
}
