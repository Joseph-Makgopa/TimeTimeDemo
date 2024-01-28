package com.example.demo.models;

import java.util.ArrayList;

public class State {
    private ArrayList<Day> days;
    private static State instance = null;
    private State(){
        days = new ArrayList<>();
    }
    public static State getInstance(){
        if(instance == null){
            instance = new State();
        }

        return instance;
    }
}
