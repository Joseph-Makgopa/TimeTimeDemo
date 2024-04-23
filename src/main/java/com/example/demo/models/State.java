package com.example.demo.models;

import com.example.demo.utilities.Notification;
import com.example.demo.utilities.Pair;
import com.example.demo.utilities.Triplet;
import com.example.demo.utilities.TripletManager;
import javafx.scene.control.Alert;

import java.io.*;
import java.util.*;

public class State implements Serializable{
    public final Map<WeekDay, Integer> days;
    public final ArrayList<Subject> subjects;
    public final ArrayList<Grade> grades;
    public final Map<Integer, Educator> educators;
    public final Map<Integer, Session> sessions;
    public final Map<Pair<Integer, Integer>, Assignable> assignables;
    public final Map<Triplet<WeekDay, Grade, Integer>, Pair<Integer, Integer>> timetable;
    public final Set<Triplet<WeekDay, Grade, Integer>> clashes;
    public static LinkedList<String> recent = null;
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
        clashes = new HashSet<>();
    }
    public static State getInstance(){
        if(instance == null){
            instance = new State();
        }
        return instance;
    }
    public static LinkedList<String> getRecent(){
        if(recent == null){
            try(ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream("timetable_data.dat"))){
                recent = (LinkedList<String>) objectInputStream.readObject();
            }catch(FileNotFoundException error){
                recent = new LinkedList<>();
            }catch (IOException error){
                recent = new LinkedList<>();
            }catch (ClassNotFoundException error){
                recent = new LinkedList<>();
            }
        }

        return recent;
    }
    public static void saveRecent(){
        try(ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream("timetable_data.dat"))){
            if(recent == null){
                objectOutputStream.writeObject(new LinkedList<>());
                return;
            }

            objectOutputStream.writeObject(recent);
        }catch(FileNotFoundException error){
            error.printStackTrace();
        }catch (IOException error){
            error.printStackTrace();
        }
    }
    public void reset(){
        days.clear();
        subjects.clear();
        grades.clear();
        educators.clear();
        sessions.clear();
        assignables.clear();
        timetable.clear();
        clashes.clear();
        breakAfter = 1;
        saveRequired = false;
        filename = "Untitled";
        filepath = "";
    }
    public void setClashes(){
        clashes.clear();
        State.getInstance().days.forEach((day, periods) -> {
            for(int count = 0; count < periods; count++){
                Map<Educator, LinkedList<Triplet<WeekDay, Grade, Integer>>> occurrences = new HashMap<>();
                final Integer period = count;

                State.getInstance().grades.forEach(grade -> {
                    Triplet<WeekDay, Grade, Integer> triplet = TripletManager.get(day, grade, period);
                    Pair<Integer, Integer> id = State.getInstance().timetable.get(triplet);

                    if(id != null){
                        Assignable assignable = State.getInstance().assignables.get(id);
                        Pair<Educator, Educator> educators = assignable.getEducators();

                        LinkedList<Triplet<WeekDay, Grade, Integer>> triplets = occurrences.computeIfAbsent(educators.getFirst(), k -> new LinkedList<>());
                        triplets.add(triplet);

                        if(educators.getSecond() != null){
                            triplets = occurrences.computeIfAbsent(educators.getSecond(), k -> new LinkedList<>());
                            triplets.add(triplet);
                        }
                    }
                });

                occurrences.forEach((educator, triplets) -> {
                    if(triplets.size() > 1){
                        clashes.addAll(triplets);
                    }
                });
            }
        });
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
            clashes.addAll(state.clashes);

            breakAfter = state.breakAfter;
            filename = file.getName();
            filepath = file.getPath();

            Optional<Integer> max =  sessions.keySet().stream().max(Comparator.naturalOrder());

            Session.counter = max.map(integer -> integer + 1).orElse(1);

            saveRequired = false;

            return true;
        }catch(FileNotFoundException error){
            Notification.show("File open error","File '" + file.getName() + "' not found.", Alert.AlertType.INFORMATION);
        }catch (IOException error){
            Notification.show("File open error","Failed to open file '" + file.getName() + "'.", Alert.AlertType.INFORMATION);
        }catch (ClassNotFoundException error){
            Notification.show("File open error","Failed to open file '" + file.getName() + "'.", Alert.AlertType.INFORMATION);
        }

        return false;
    }
    public void save(File file){
        try(ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(file))){
            saveRequired = false;
            filename = file.getName();
            filepath = file.getAbsolutePath();
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
