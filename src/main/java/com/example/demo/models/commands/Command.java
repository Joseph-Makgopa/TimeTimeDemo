package com.example.demo.models.commands;

import com.example.demo.controllers.DemoController;
import com.example.demo.utilities.Job;
import javafx.concurrent.Task;
import javafx.scene.Cursor;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.AnchorPane;

public interface Command {
    String executeDescription();
    String reverseDescription();
    Boolean dataRefresh();
    Boolean threadSafe();
    void execute(Job job);
    void reverse(Job job);
}
