package com.example.demo.models.commands;

import com.example.demo.models.State;

import java.util.EmptyStackException;
import java.util.Stack;

public class CommandManager {
    private Stack<Command> undoStack;
    private Stack<Command> redoStack;
    private static CommandManager instance = null;
    private CommandManager(){
        undoStack = new Stack<>();
        redoStack = new Stack<>();
    }
    public static CommandManager getInstance(){
        if(instance == null){
            instance = new CommandManager();
        }

        return instance;
    }
    public void addCommand(Command command){
        undoStack.push(command);
        redoStack.clear();
        State.getInstance().saveRequired = true;
    }
    public void undo(){
        try{
            Command command = undoStack.pop();

            if(command == null)
                return;

            redoStack.push(command);
            command.reverse();

            State.getInstance().saveRequired = true;
        }catch(EmptyStackException error){

        }
    }
    public void redo(){
        try {
            Command command = redoStack.pop();

            if(command == null)
                return;

            undoStack.push(command);
            command.execute();

            State.getInstance().saveRequired = true;
        }catch(EmptyStackException error){

        }
    }
    public void clear(){
        undoStack.clear();
        redoStack.clear();
    }
}
