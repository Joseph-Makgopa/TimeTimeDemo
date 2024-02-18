package com.example.demo.models;

import java.util.ArrayList;
import java.util.Collections;

public class WeekDaySchedule{
    private WeekDay day;
    private ArrayList<Integer> periods = new ArrayList<>();
    public WeekDaySchedule(WeekDay day, Integer periods){
        this.day = day;

        for(int count = 0; count < periods; count++)
            this.periods.add(null);
    }

    public WeekDay getDay() {
        return day;
    }

    public void setDay(WeekDay day) {
        this.day = day;
    }

    public ArrayList<Integer> getPeriods() {
        return periods;
    }

    public void setPeriods(ArrayList<Integer> periods) {
        this.periods = periods;
    }

    @Override
    public String toString() {
        return "WeekDaySchedule{" +
                "day=" + day +
                ", periods=" + periods +
                '}';
    }
}
