package com.example.demo.models.commands;

import com.example.demo.models.Grade;
import com.example.demo.models.State;
import com.example.demo.models.Subject;

public class AddGradeCommand implements  Command{
    private Grade grade;
    public AddGradeCommand(Grade grade){
        this.grade = grade;
    }
    public void execute(){
        State.getInstance().grades.add(grade);
    }
    public void reverse(){
        State.getInstance().grades.remove(grade);
    }
}
