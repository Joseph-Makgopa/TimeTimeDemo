package com.example.demo.controllers;

import com.example.demo.models.State;
import com.example.demo.models.commands.CommandList;
import com.example.demo.models.commands.CommandManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.Stage;

public class BaseDataDialogController {
    protected CommandList commandList = new CommandList();
    @FXML
    public void ok(ActionEvent event){
        commandList.execute();
        CommandManager.getInstance().addCommand(commandList);

        Node node = (Node)event.getSource();
        Stage stage = (Stage)node.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void cancel(ActionEvent event){
        Node node = (Node)event.getSource();
        Stage stage = (Stage)node.getScene().getWindow();
        stage.close();
    }
}
