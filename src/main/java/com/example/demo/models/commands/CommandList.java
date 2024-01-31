package com.example.demo.models.commands;

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
    public void execute(){
        for(int count = 0; count < commands.size(); count++){
            commands.get(count).execute();
        }
    }
    public void reverse(){
        for(int count = commands.size() - 1; count >= 0; count--){
            commands.get(count).reverse();
        }
    }
}
