package com.example.demo.models.commands;

import com.example.demo.controllers.DemoController;
import com.example.demo.models.State;
import com.example.demo.models.Subject;

public class AddSubjectCommand extends Command{
    private Subject subject;
    public AddSubjectCommand(DemoController demoController, Subject subject){
        super(demoController);
        this.subject = subject;
    }
    public void executeCode(){
        State.getInstance().subjects.add(subject);
    }
    public void reverseCode(){
        State.getInstance().subjects.remove(subject);
    }
}
