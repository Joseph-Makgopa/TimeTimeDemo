package com.example.demo.models.commands;

import com.example.demo.models.Grade;
import com.example.demo.models.State;
import com.example.demo.models.Subject;

public class RemoveGradeCommand implements Command{
    private Grade grade;
    public RemoveGradeCommand(Grade grade){
        this.grade = grade;
    }
    public void execute() {
        State.getInstance().grades.remove(grade);
    }

    public void reverse() {
        State.getInstance().grades.add(grade);
    }
}
