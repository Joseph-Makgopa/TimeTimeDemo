package com.example.demo.models.commands;

import com.example.demo.controllers.DemoController;
import com.example.demo.utilities.Job;

import java.util.ArrayList;
import java.util.Queue;

public class CommandList implements Command{
    ArrayList<Command> commands;
    public ArrayList<Command> getCommands(){
        return commands;
    }
    public CommandList(){
        commands = new ArrayList<>();
    }
    public void add(Command command){
        commands.add(command);
    }
    public Integer size(){
        return commands.size();
    }
    public Boolean isEmpty(){
        return commands.isEmpty();
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
    public void execute(Job job){
        for(int count = 0; count < commands.size(); count++){
            commands.get(count).execute(job);

            job.progress(count, commands.size());
        }

        job.progress(commands.size(), commands.size());
    }
    public void reverse(Job job){
        for(int count = commands.size() - 1; count >= 0; count--){
            commands.get(count).reverse(job);

            job.progress(commands.size() - count , commands.size());
        }

        job.progress(commands.size(), commands.size());
    }
}
