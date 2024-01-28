package com.example.demo.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class State {
    public final ArrayList<Day> days;
    public final ArrayList<Subject> subjects;
    public final ArrayList<Grade> grades;
    public final Map<Integer, Educator> educators;
    public final Map<Integer, Session> sessions;
    private static State instance = null;
    private State(){
        days = new ArrayList<>();
        subjects = new ArrayList<>();
        grades = new ArrayList<>();
        educators = new HashMap<>();
        sessions = new HashMap<>();
    }
    public static State getInstance(){
        if(instance == null){
            instance = new State();
        }
        return instance;
    }
}
