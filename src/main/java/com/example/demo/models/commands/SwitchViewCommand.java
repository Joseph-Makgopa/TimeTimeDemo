package com.example.demo.models.commands;

import com.example.demo.controllers.DemoController;

public class SwitchViewCommand extends Command{
    public SwitchViewCommand(DemoController demoController,Integer view){
        super(demoController);
    }
    @Override
    public void executeCode() {

    }

    @Override
    public void reverseCode() {

    }
}
