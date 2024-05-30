package com.example.demo.utilities;

import com.example.demo.models.commands.Command;
import com.example.demo.models.commands.CommandManager;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;

import java.util.function.Consumer;

public class Job extends Task<Void> {
    private final Command command;
    private final Boolean execution;
    public Job(Command command, Boolean execution){
        this.command = command;
        this.execution = execution;
    }
    public void progress(long workDone, long max){
        //updateProgress(workDone, max);
        Platform.runLater(() -> ((ProgressBar) CommandManager.getInstance().getDemoController().getProgressAnchor().getChildren().get(0)).setProgress(workDone / (double) max));
    }
    @Override
    protected Void call() throws Exception {
        if(execution)
            command.execute(this);
        else
            command.reverse(this);

        Platform.runLater(() -> CommandManager.getInstance().getDemoController().getLoaderMessage().setText("  updating status."));
        CommandManager.getInstance().setStatus(this);

        if(command.dataRefresh()) {
            Platform.runLater(() -> CommandManager.getInstance().getDemoController().getLoaderMessage().setText("  refreshing view data."));

            CommandManager.getInstance().getDemoController().getService().dataRefresh(this);

            Platform.runLater(() -> {
                CommandManager.getInstance().getDemoController().getPane().getTabs().forEach(tab -> ((TableView)((AnchorPane)tab.getContent()).getChildren().get(0)).refresh());
                CommandManager.getInstance().getDemoController().getService().lessonViewRefresh();
            });
        }else{
            Platform.runLater(() -> CommandManager.getInstance().getDemoController().getLoaderMessage().setText("  refreshing view."));

            ClickableTableCell.instances.clear();
            ClickableTableCell.lastSelectedCell = null;

            CommandManager.getInstance().getDemoController().getService().populateTable(this);
            final ObservableList<Tab> tabs = CommandManager.getInstance().getDemoController().getService().setupTable(this);

            Platform.runLater(() -> {
                CommandManager.getInstance().getDemoController().getPane().getTabs().setAll(tabs);
                CommandManager.getInstance().getDemoController().getService().lessonViewRefresh();
            });
        }

        return null;
    }
}
