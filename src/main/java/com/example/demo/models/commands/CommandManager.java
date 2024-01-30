package com.example.demo.models.commands;

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
    }
    public void undo(){

    }
    public void redo(){

    }
    public void clear(){
        undoStack.clear();
        redoStack.clear();
    }
}
