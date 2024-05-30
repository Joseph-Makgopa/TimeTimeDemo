package com.example.demo.models.commands;

import com.example.demo.comparators.AssignableComparator;
import com.example.demo.controllers.DemoController;
import com.example.demo.models.Assignable;
import com.example.demo.models.State;
import com.example.demo.utilities.ClickableTableCell;
import com.example.demo.utilities.Job;
import com.example.demo.utilities.Notification;
import com.example.demo.utilities.Pair;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.util.EmptyStackException;
import java.util.Stack;

public class CommandManager {
    private final Stack<Command> undoStack;
    private final Stack<Command> redoStack;
    private static CommandManager instance = null;
    private Thread thread = null;
    private DemoController demoController;
    private final Pair<Integer, Integer> status = new Pair<>(0,0);
    private final ObservableList<Tab> viewUpdate = FXCollections.observableArrayList();
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
    public void setDemoController(DemoController demoController){
        this.demoController = demoController;
    }
    public DemoController getDemoController(){
        return demoController;
    }
    public Pair<Integer, Integer> getStatus(){
        return status;
    }
    public void setStatus(Job job){
        job.progress(0, State.getInstance().assignables.size() + 1);
        status.setFirst(State.getInstance().clashes.size());

        int result = 0, workDone = 0;

        job.progress(1, State.getInstance().assignables.size() + 1);

        for(Assignable assignable: State.getInstance().assignables.values()){
            result += assignable.getRemain();

            workDone++;
            job.progress(workDone + 1, State.getInstance().assignables.size() + 1);
        }

        status.setSecond(result);
    }
    public void dataRefresh(){
        demoController.getPane().getTabs().forEach(tab -> ((TableView)((AnchorPane)tab.getContent()).getChildren().get(0)).refresh());
        demoController.getService().lessonViewRefresh();
    }
    public void addCommand(Command command){
        Job job = new Job(command, true);
        ProgressBar progress = (ProgressBar) demoController.getProgressAnchor().getChildren().get(0);

        job.setOnSucceeded(event ->{
            undoStack.push(command);
            redoStack.clear();

            demoController.getLabelClashes().setText(status.getFirst() + " clashes");
            demoController.getLabelRemain().setText(status.getSecond() + " remaining");

            demoController.getProgressAnchor().getChildren().get(0).setVisible(false);
            demoController.getLoaderMessage().setVisible(false);
        });

        job.setOnCancelled(event -> {
            demoController.getProgressAnchor().getChildren().get(0).setVisible(false);
            demoController.getLoaderMessage().setVisible(false);
            Notification.show("Command Execution Error", "Failed to execute command '" + command.getClass().getName() + "'.", Alert.AlertType.ERROR);
        });

        job.setOnFailed(event -> {
            demoController.getProgressAnchor().getChildren().get(0).setVisible(false);
            demoController.getLoaderMessage().setVisible(false);
            Notification.show("Command Execution Error", "Failed to execute command '" + command.getClass().getName() + "'.", Alert.AlertType.ERROR);
        });

        try {
            if(thread != null)
                thread.join();

            progress.setVisible(true);
            //progress.progressProperty().bind(job.progressProperty());

            demoController.getLoaderMessage().setVisible(true);
            demoController.getLoaderMessage().setText(command.executeDescription());

            if(command.threadSafe()){
                thread = new Thread(job);
                thread.start();
            }else
                job.run();
        }catch(InterruptedException error){
            error.printStackTrace();
        }
    }
    public void undo(){
        Job job;
        Command command;
        ProgressBar progress = (ProgressBar) demoController.getProgressAnchor().getChildren().get(0);

        try{
            command = undoStack.pop();

            if(command == null)
                return;

            job = new Job(command, false);
        }catch(EmptyStackException error){
            return;
        }

        job.setOnSucceeded(event ->{
            redoStack.push(command);

            demoController.getLabelClashes().setText(status.getFirst() + " clashes");
            demoController.getLabelRemain().setText(status.getSecond() + " remaining");

            demoController.getProgressAnchor().getChildren().get(0).setVisible(false);
            demoController.getLoaderMessage().setVisible(false);
        });

        job.setOnCancelled(event -> {
            demoController.getProgressAnchor().getChildren().get(0).setVisible(false);
            demoController.getLoaderMessage().setVisible(false);
            Notification.show("Command Execution Error", "Failed to execute command '" + command.getClass().getName() + "'.", Alert.AlertType.ERROR);
        });

        job.setOnFailed(event -> {
            demoController.getProgressAnchor().getChildren().get(0).setVisible(false);
            demoController.getLoaderMessage().setVisible(false);
            Notification.show("Command Execution Error", "Failed to execute command '" + command.getClass().getName() + "'.", Alert.AlertType.ERROR);
        });

        try {
            if(thread != null)
                thread.join();

            progress.setVisible(true);
            //progress.progressProperty().bind(job.progressProperty());

            demoController.getLoaderMessage().setVisible(true);
            demoController.getLoaderMessage().setText(command.executeDescription());

            if(command.threadSafe()) {
                thread = new Thread(job);
                thread.start();
            }else{
                job.run();
            }

        }catch(InterruptedException error){
            error.printStackTrace();
        }
    }
    public void redo(){
        Job job;
        Command command;
        ProgressBar progress = (ProgressBar) demoController.getProgressAnchor().getChildren().get(0);

        try{
            command = redoStack.pop();

            if(command == null)
                return;

            job = new Job(command, true);
        }catch(EmptyStackException error){
            return;
        }

        job.setOnSucceeded(event ->{
            undoStack.push(command);

            demoController.getLabelClashes().setText(status.getFirst() + " clashes");
            demoController.getLabelRemain().setText(status.getSecond() + " remaining");

            demoController.getProgressAnchor().getChildren().get(0).setVisible(false);
            demoController.getLoaderMessage().setVisible(false);
        });

        job.setOnCancelled(event -> {
            demoController.getProgressAnchor().getChildren().get(0).setVisible(false);
            demoController.getLoaderMessage().setVisible(false);
            Notification.show("Command Execution Error", "Failed to execute command '" + command.getClass().getName() + "'.", Alert.AlertType.ERROR);
        });

        job.setOnFailed(event -> {
            demoController.getProgressAnchor().getChildren().get(0).setVisible(false);
            demoController.getLoaderMessage().setVisible(false);
            Notification.show("Command Execution Error", "Failed to execute command '" + command.getClass().getName() + "'.", Alert.AlertType.ERROR);
        });

        try {
            if(thread != null)
                thread.join();

            progress.setVisible(true);
            //progress.progressProperty().bind(job.progressProperty());

            demoController.getLoaderMessage().setVisible(true);
            demoController.getLoaderMessage().setText(command.executeDescription());

            if(command.threadSafe()) {
                thread = new Thread(job);
                thread.start();
            }else
                job.run();

        }catch(InterruptedException error){
            error.printStackTrace();
        }
    }
    public void clear(){
        undoStack.clear();
        redoStack.clear();
    }
}
