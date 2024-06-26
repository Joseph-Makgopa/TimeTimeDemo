package com.example.demo.models.commands;

import com.example.demo.controllers.DemoController;
import com.example.demo.models.Grade;
import com.example.demo.models.Session;
import com.example.demo.models.State;
import com.example.demo.models.WeekDay;
import com.example.demo.models.Assignable;
import com.example.demo.services.DemoService;
import com.example.demo.utilities.Job;
import com.example.demo.utilities.Pair;
import com.example.demo.utilities.Triplet;
import com.example.demo.utilities.TripletManager;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.HashSet;
import java.util.Set;

public class PositionCommand implements Command{
    private Set<Triplet<WeekDay, Grade, Integer>> oldClashes;
    private Set<Triplet<WeekDay, Grade, Integer>> freshClashes;
    private Assignable oldAssignable;
    private Assignable oldPairAssignable;
    private Assignable freshAssignable;
    private Assignable freshPairAssignable;
    private Triplet<WeekDay, Grade, Integer> triplet;
    public PositionCommand(Assignable assignable, Triplet<WeekDay, Grade, Integer> triplet){
        this.oldClashes = new HashSet<>(State.getInstance().clashes);
        this.freshClashes = null;
        this.oldAssignable = null;
        this.oldPairAssignable = null;
        this.freshAssignable = assignable;
        this.freshPairAssignable = assignable.getPair();
        this.triplet = triplet;
    }

    @Override
    public String executeDescription() {
        return "  positioning lesson.";
    }

    @Override
    public String reverseDescription() {
        return "  reversing positioned lesson.";
    }

    @Override
    public Boolean dataRefresh() {
        return true;
    }
    @Override
    public Boolean threadSafe(){
        return true;
    }
    @Override
    public void execute(Job job) {
        job.progress(0, 4);

        if(State.getInstance().timetable.get(triplet) != null){
            oldAssignable = State.getInstance().assignables.get(State.getInstance().timetable.get(triplet));
            oldAssignable.setRemain(oldAssignable.getRemain() + 1);
        }

        job.progress(1, 4);

        State.getInstance().timetable.put(triplet, freshAssignable.getId());
        freshAssignable.setRemain(freshAssignable.getRemain() - 1);

        job.progress(2, 4);

        if(freshPairAssignable != null){
            Triplet<WeekDay, Grade, Integer> pairTriplet = TripletManager.get(triplet.getFirst(), freshPairAssignable.getGrade(), triplet.getThird());

            if(State.getInstance().timetable.get(pairTriplet) != null){
                oldPairAssignable = State.getInstance().assignables.get(State.getInstance().timetable.get(pairTriplet));
                oldPairAssignable.setRemain(oldPairAssignable.getRemain() + 1);
            }

            State.getInstance().timetable.put(pairTriplet, freshPairAssignable.getId());
            freshPairAssignable.setRemain(freshPairAssignable.getRemain() - 1);
        }

        job.progress(3, 4);

        if(freshClashes == null) {
            State.getInstance().setClashes();
            freshClashes = new HashSet<>(State.getInstance().clashes);
        }else{
            State.getInstance().clashes.clear();
            State.getInstance().clashes.addAll(freshClashes);
        }

        job.progress(4, 4);

        State.getInstance().saveRequired = true;
    }

    @Override
    public void reverse(Job job) {
        job.progress(0, 3);

        if(oldAssignable == null){
            State.getInstance().timetable.remove(triplet);
            freshAssignable.setRemain(freshAssignable.getRemain() + 1);
        }else if(!oldAssignable.getId().equals(freshAssignable.getId())){
            State.getInstance().timetable.put(triplet, oldAssignable.getId());
            oldAssignable.setRemain(oldAssignable.getRemain() - 1);
            freshAssignable.setRemain(freshAssignable.getRemain() + 1);
        }

        job.progress(1, 3);

        if(freshPairAssignable != null) {
            Triplet<WeekDay, Grade, Integer> pairTriplet = triplet.clone();
            Pair<Session, Session> session = freshPairAssignable.getSessions();
            pairTriplet.setSecond(session.getFirst().getGrade());

            if(oldPairAssignable == null) {
                State.getInstance().timetable.remove(pairTriplet);
                freshPairAssignable.setRemain(freshPairAssignable.getRemain() + 1);
            }else if(!oldPairAssignable.getId().equals(freshPairAssignable.getId())){
                State.getInstance().timetable.put(pairTriplet, oldPairAssignable.getId());
                oldPairAssignable.setRemain(oldPairAssignable.getRemain() - 1);
                freshPairAssignable.setRemain(freshPairAssignable.getRemain() + 1);
            }
        }

        job.progress(2, 3);

        State.getInstance().clashes.clear();
        State.getInstance().clashes.addAll(oldClashes);

        job.progress(3, 3);

        State.getInstance().saveRequired = true;
    }
}
