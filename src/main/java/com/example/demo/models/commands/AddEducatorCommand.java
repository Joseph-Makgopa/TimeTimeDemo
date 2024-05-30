package com.example.demo.models.commands;

import com.example.demo.controllers.DemoController;
import com.example.demo.models.Educator;
import com.example.demo.models.Grade;
import com.example.demo.models.State;
import com.example.demo.utilities.Job;
import javafx.concurrent.Task;

public class AddEducatorCommand implements Command{
    private Educator educator;
    public AddEducatorCommand(Educator educator){
        this.educator = educator;
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
        State.getInstance().educators.put(educator.getPost(), educator);
    }
    @Override
    public void reverse(Job job){
        State.getInstance().educators.remove(educator);
    }
}
