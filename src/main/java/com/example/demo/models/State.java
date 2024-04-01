package com.example.demo.models;

import com.example.demo.utilities.Notification;
import com.example.demo.utilities.Pair;
import com.example.demo.utilities.Triplet;
import javafx.scene.control.Alert;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class State implements Serializable{
    public final Map<WeekDay, Integer> days;
    public final ArrayList<Subject> subjects;
    public final ArrayList<Grade> grades;
    public final Map<Integer, Educator> educators;
    public final Map<Integer, Session> sessions;
    public final Map<Pair<Integer, Integer>, Assignable> assignables;
    public final Map<Triplet<WeekDay, Grade, Integer>, Pair<Integer, Integer>> timetable;
    public Integer breakAfter = 1;
    public Boolean saveRequired = false;
    public String filename  = "Untitled";
    public String filepath = "";
    private static State instance = null;
    private State(){
        days = new HashMap<>();
        subjects = new ArrayList<>();
        grades = new ArrayList<>();
        educators = new HashMap<>();
        sessions = new HashMap<>();
        assignables = new HashMap<>();
        timetable = new HashMap<>();
    }
    public static State getInstance(){
        if(instance == null){
            instance = new State();
        }
        return instance;
    }
    public void reset(){
        days.clear();
        subjects.clear();
        grades.clear();
        educators.clear();
        sessions.clear();
        assignables.clear();
        timetable.clear();
        breakAfter = 1;
        saveRequired = false;
        filename  = "Untitled";
        filepath = "";
    }
    public Boolean open(File file){
        try(ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file))){
            reset();
            State state = (State) objectInputStream.readObject();

            days.putAll(state.days);
            subjects.addAll(state.subjects);
            grades.addAll(state.grades);
            educators.putAll(state.educators);
            sessions.putAll(state.sessions);
            assignables.putAll(state.assignables);
            timetable.putAll(state.timetable);

            breakAfter = state.breakAfter;
            filename = file.getName();
            filepath = file.getPath();

            return true;
        }catch(FileNotFoundException error){
            Notification.show("File open error","Failed to open file '" + file.getName() + "'.", Alert.AlertType.INFORMATION);
            error.printStackTrace();
        }catch (IOException error){
            Notification.show("File open error","Failed to open file '" + file.getName() + "'.", Alert.AlertType.INFORMATION);
            error.printStackTrace();
        }catch (ClassNotFoundException error){
            Notification.show("File open error","Failed to open file '" + file.getName() + "'.", Alert.AlertType.INFORMATION);
            error.printStackTrace();
        }

        return false;
    }
    public void save(File file){
        try(ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(file))){
            objectOutputStream.writeObject(this);
        }catch(FileNotFoundException error){
            error.printStackTrace();
            Notification.show("File save error","Failed to save file '" + file.getName() + "'.", Alert.AlertType.INFORMATION);
        }catch (IOException error){
            error.printStackTrace();
            Notification.show("File save error","Failed to save file '" + file.getName() + "'.", Alert.AlertType.INFORMATION);
        }
    }
}
