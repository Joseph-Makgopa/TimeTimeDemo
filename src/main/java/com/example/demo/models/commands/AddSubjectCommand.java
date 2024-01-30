package com.example.demo.models.commands;

import com.example.demo.models.State;
import com.example.demo.models.Subject;

public class AddSubjectCommand implements Command{
    private Subject subject;
    public AddSubjectCommand(Subject subject){
        this.subject = subject;
    }
    public void execute(){
        State.getInstance().subjects.add(subject);
    }
    public void reverse(){
        State.getInstance().subjects.remove(subject);
    }
}
