package com.example.demo.models;

import com.example.demo.models.assignable.Assignable;
import com.example.demo.utilities.Notification;
import com.example.demo.utilities.Pair;
import com.example.demo.utilities.Triplet;
import javafx.scene.control.Alert;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class State {
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
        saveRequired = false;
        filename  = "Untitled";
    }
    public void setFields(File file){
        try(ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file))){
            this.days.clear();
            this.days.putAll((Map<WeekDay, Integer>) objectInputStream.readObject());

            this.subjects.clear();
            this.subjects.addAll((ArrayList<Subject>) objectInputStream.readObject());

            this.grades.clear();
            this.grades.addAll((ArrayList<Grade>) objectInputStream.readObject());

            this.educators.clear();
            this.educators.putAll((Map<Integer, Educator>) objectInputStream.readObject());

            this.sessions.clear();
            this.sessions.putAll((Map<Integer, Session>) objectInputStream.readObject());

            this.assignables.clear();
            this.assignables.putAll((Map<Pair<Integer, Integer>, Assignable>) objectInputStream.readObject());

            this.timetable.clear();
            this.timetable.putAll((Map<Triplet<WeekDay, Grade, Integer>, Pair<Integer,Integer>>) objectInputStream.readObject());

            this.breakAfter = (Integer) objectInputStream.readObject();
            this.saveRequired = false;
            this.filename = file.getName();
            this.filepath = file.getPath();

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
    }
    public void saveFields(File file){
        try(ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(file))){
            objectOutputStream.writeObject(this.days);
            objectOutputStream.writeObject(this.subjects);
            objectOutputStream.writeObject(this.grades);
            objectOutputStream.writeObject(this.educators);
            objectOutputStream.writeObject(this.sessions);
            objectOutputStream.writeObject(this.assignables);
            objectOutputStream.writeObject(this.timetable);
            objectOutputStream.writeObject(this.breakAfter);
            this.saveRequired = false;
            this.filename = file.getName();
            this.filepath = file.getPath();

        }catch(FileNotFoundException error){
            //error.printStackTrace();
            Notification.show("File save error","Failed to save file '" + file.getName() + "'.", Alert.AlertType.INFORMATION);
        }catch (IOException error){
            //error.printStackTrace();
            Notification.show("File save error","Failed to save file '" + file.getName() + "'.", Alert.AlertType.INFORMATION);
        }
    }
}
