package com.example.demo.models;

import com.example.demo.utilities.Notification;
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
            for (Subject subject : this.subjects)
                subject.refreshView();

            this.grades.clear();
            this.grades.addAll((ArrayList<Grade>) objectInputStream.readObject());
            for(Grade grade: this.grades)
                grade.refreshView();

            this.educators.clear();
            this.educators.putAll((Map<Integer, Educator>) objectInputStream.readObject());
            for(Educator educator: this.educators.values())
                educator.refreshView();

            this.sessions.clear();
            this.sessions.putAll((Map<Integer, Session>) objectInputStream.readObject());
            for(Session session: this.sessions.values())
                session.refreshView();

            this.saveRequired = false;
            this.filename = file.getName();
            this.filepath = file.getPath();

        }catch(FileNotFoundException error){
            Notification.show("File open error","Failed to open file '" + file.getName() + "'.", Alert.AlertType.INFORMATION);
        }catch (IOException error){
            Notification.show("File open error","Failed to open file '" + file.getName() + "'.", Alert.AlertType.INFORMATION);
        }catch (ClassNotFoundException error){
            Notification.show("File open error","Failed to open file '" + file.getName() + "'.", Alert.AlertType.INFORMATION);
        }
    }
    public void saveFields(File file){
        try(ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(file))){
            objectOutputStream.writeObject(this.days);
            objectOutputStream.writeObject(this.subjects);
            objectOutputStream.writeObject(this.grades);
            objectOutputStream.writeObject(this.educators);
            objectOutputStream.writeObject(this.sessions);
            this.saveRequired = false;
            this.filename = file.getName().split(".")[0];
            this.filepath = file.getPath();

        }catch(FileNotFoundException error){
            Notification.show("File save error","Failed to save file '" + file.getName() + "'.", Alert.AlertType.INFORMATION);
        }catch (IOException error){
            Notification.show("File save error","Failed to save file '" + file.getName() + "'.", Alert.AlertType.INFORMATION);
        }
    }
}
