package com.example.demo.models.commands;

import com.example.demo.models.State;

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
        Command command = undoStack.pop();
        redoStack.push(command);
        command.reverse();

        State.getInstance().saveRequired = true;
    }
    public void redo(){
        Command command = redoStack.pop();
        undoStack.push(command);
        command.execute();

        State.getInstance().saveRequired = true;
    }
    public void clear(){
        undoStack.clear();
        redoStack.clear();
    }
}
