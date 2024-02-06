package com.example.demo.models;

import java.util.ArrayList;

public class GradeSchedule {
    private Grade grade;
    private ArrayList<Integer> periods;
    public GradeSchedule(Grade grade, Integer periods){
        this.grade = grade;
        this.periods = new ArrayList<>(periods);
    }

    public Grade getGrade() {
        return grade;
    }

    public void setGrade(Grade grade) {
        this.grade = grade;
    }

    public ArrayList<Integer> getPeriods() {
        return periods;
    }

    public void setPeriods(ArrayList<Integer> periods) {
        this.periods = periods;
    }
}
