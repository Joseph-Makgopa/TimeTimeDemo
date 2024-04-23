package com.example.demo.models.commands;

import com.example.demo.controllers.DemoController;

import java.util.ArrayList;
import java.util.Queue;

public class CommandList extends Command{
    ArrayList<Command> commands;
    public ArrayList<Command> getCommands(){
        return commands;
    }
    public CommandList(DemoController demoController){
        super(demoController);
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
    public void executeCode(){
        for(int count = 0; count < commands.size(); count++){
            commands.get(count).executeCode();
        }
    }
    public void reverseCode(){
        for(int count = commands.size() - 1; count >= 0; count--){
            commands.get(count).reverseCode();
        }
    }
}
