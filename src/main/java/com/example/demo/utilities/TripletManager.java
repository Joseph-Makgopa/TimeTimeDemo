package com.example.demo.utilities;

import com.example.demo.models.Grade;
import com.example.demo.models.WeekDay;

import java.util.ArrayList;

public class TripletManager {
    private ArrayList<Triplet<WeekDay, Grade, Integer>> data;
    private static TripletManager instance = null;
    private TripletManager(){
        data = new ArrayList<>();
    }
    public static TripletManager getInstance(){
        if(instance == null){
            instance = new TripletManager();
        }

        return instance;
    }
    public static Triplet<WeekDay, Grade, Integer> get(WeekDay day, Grade grade, Integer period){
        Triplet<WeekDay, Grade, Integer> triplet = new Triplet<>(day, grade, period);

        if(getInstance().data.contains(triplet)){
            return getInstance().data.get(getInstance().data.indexOf(triplet));
        }else{
            getInstance().data.add(triplet);

            return triplet;
        }
    }
}
