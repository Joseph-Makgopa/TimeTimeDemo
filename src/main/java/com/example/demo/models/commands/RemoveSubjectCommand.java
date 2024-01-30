package com.example.demo.models.commands;

import com.example.demo.models.State;
import com.example.demo.models.Subject;

public class RemoveSubjectCommand implements Command{
    private Subject subject;
    public RemoveSubjectCommand(Subject subject){
        this.subject = subject;
    }
    public void execute() {
        State.getInstance().subjects.remove(subject);
    }

    public void reverse() {
        State.getInstance().subjects.add(subject);
    }
}
