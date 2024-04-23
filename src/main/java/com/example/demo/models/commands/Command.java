package com.example.demo.models.commands;

import com.example.demo.controllers.DemoController;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.AnchorPane;

public abstract class Command {
    private DemoController demoController;
    private void start(){
        AnchorPane progressAnchor = demoController.getProgressAnchor();
        ProgressIndicator progress = (ProgressIndicator) progressAnchor.getChildren().get(0);
        progressAnchor.setVisible(true);
        progress.setVisible(true);

        progress.setLayoutX( (progressAnchor.getWidth() / 2) - (progress.getWidth() / 2));
        progress.setLayoutY( (progressAnchor.getHeight() / 2) - (progress.getHeight() / 2));
    }
    private void end(){
        AnchorPane progressAnchor = demoController.getProgressAnchor();
        progressAnchor.setVisible(false);
        progressAnchor.getChildren().get(0).setVisible(false);
    }
    public Command(DemoController demoController){
        this.demoController = demoController;
    }
    public DemoController getDemoController() {
        return demoController;
    }

    public abstract void executeCode();
    public abstract void reverseCode();
    public void execute(){
        start();
        executeCode();
        end();
    }
    public void reverse(){
        start();
        reverseCode();
        end();
    }
}
