package com.example.demo.models;

public class State {
    private static State instance = null;
    private State(){

    }
    public static State getInstance(){
        if(instance == null){
            instance = new State();
        }

        return instance;
    }
}
