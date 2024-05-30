package com.example.demo.models.commands;

import com.example.demo.controllers.DemoController;
import com.example.demo.models.State;
import com.example.demo.models.Subject;
import com.example.demo.utilities.Job;
import javafx.concurrent.Task;

public class AddSubjectCommand implements Command{
    private Subject subject;
    public AddSubjectCommand(Subject subject){
        this.subject = subject;
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
    @Override
    public void execute(Job job){
        State.getInstance().subjects.add(subject);
    }
    @Override
    public void reverse(Job job){
        State.getInstance().subjects.remove(subject);
    }
}
