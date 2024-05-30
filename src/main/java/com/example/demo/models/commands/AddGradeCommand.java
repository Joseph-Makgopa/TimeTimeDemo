package com.example.demo.models.commands;

import com.example.demo.controllers.DemoController;
import com.example.demo.models.Grade;
import com.example.demo.models.State;
import com.example.demo.models.Subject;
import com.example.demo.utilities.Job;
import javafx.concurrent.Task;

public class AddGradeCommand implements   Command{
    private Grade grade;
    public AddGradeCommand(Grade grade){
        this.grade = grade;
    }
    @Override
    public void execute(Job job){
        State.getInstance().grades.add(grade);
    }
    @Override
    public void reverse(Job job){
        State.getInstance().grades.remove(grade);
    }

    @Override
    public String executeDescription() {
        return "";
    }

    @Override
    public String reverseDescription() {
        return "";
    }

    @Override
    public Boolean dataRefresh() {
        return null;
    }
    @Override
    public Boolean threadSafe(){
        return true;
    }
}
