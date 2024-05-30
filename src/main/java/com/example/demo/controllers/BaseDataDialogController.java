package com.example.demo.controllers;

import com.example.demo.models.State;
import com.example.demo.models.commands.CommandList;
import com.example.demo.models.commands.CommandManager;
import com.example.demo.services.DemoService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.Stage;

public class BaseDataDialogController {
    protected CommandList commandList;
    protected DemoController demoController;
    public void setController(DemoController demoController) {
        commandList = new CommandList();
        this.demoController = demoController;
    }

    @FXML
    public void ok(ActionEvent event){
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
