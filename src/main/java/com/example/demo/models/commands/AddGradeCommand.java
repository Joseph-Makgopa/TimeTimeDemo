package com.example.demo.models.commands;

import com.example.demo.controllers.DemoController;
import com.example.demo.models.Grade;
import com.example.demo.models.State;
import com.example.demo.models.Subject;

public class AddGradeCommand extends  Command{
    private Grade grade;
    public AddGradeCommand(DemoController demoController, Grade grade){
        super(demoController);
        this.grade = grade;
    }
    public void executeCode(){
        State.getInstance().grades.add(grade);
    }
    public void reverseCode(){
        State.getInstance().grades.remove(grade);
    }
}
