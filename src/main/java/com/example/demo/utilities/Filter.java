package com.example.demo.utilities;

import com.example.demo.models.*;
import com.example.demo.models.assignable.Assignable;
import com.example.demo.models.assignable.SplitAssignable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.Serializable;
import java.util.ArrayList;

public class Filter implements Serializable {
    public Integer number = null;
    public Character division = null;
    public Subject subject = null;
    public Educator educator = null;
    public void clear(){
        number = null;
        division = null;
        subject = null;
        educator = null;
    }
    public void set(Integer number, Character division, Subject subject, Educator educator){
        this.number = number;
        this.division = division;
        this.subject = subject;
        this.educator = educator;
    }
    public ObservableList<GradeSchedule> exec(ObservableList<GradeSchedule> list){
        return FXCollections.observableArrayList(list.stream().filter(gradeSchedule -> {
            if(number != null && !gradeSchedule.getGrade().getNumber().equals(number)){
                return false;
            }

            if(division != null && !gradeSchedule.getGrade().getDivision().equals(division)){
                return false;
            }

            if(subject != null){
                ArrayList<Integer> periods = gradeSchedule.getPeriods();
                Boolean found = false;

                for(int count = 0; count < periods.size() && !found; count++){
                    if(periods.get(count) != null){
                        Assignable assignable = State.getInstance().assignables.get(periods.get(count));
                        Session session = State.getInstance().sessions.get(assignable.getSessionRef());

                        if(session.getSubject().equals(subject)){
                            found = true;
                        }else if(assignable.shareSingleSlot()){
                            SplitAssignable splitAssignable = (SplitAssignable) assignable;
                            session = State.getInstance().sessions.get(splitAssignable.getSplitRef());

                            if(session.getSubject().equals(subject)){
                                found = true;
                            }
                        }
                    }
                }

                if(!found)
                    return false;
            }

            if(educator != null){
                ArrayList<Integer> periods = gradeSchedule.getPeriods();
                Boolean found = false;

                for(int count = 0; count < periods.size() && !found; count++){
                    if(periods.get(count) != null){
                        Assignable assignable = State.getInstance().assignables.get(periods.get(count));
                        Session session = State.getInstance().sessions.get(assignable.getSessionRef());

                        if(educator.equals(session.getEducator()))
                            found = true;
                    }
                }

                if(!found)
                    return false;
            }

            return true;
        }).toList());
    }
}
