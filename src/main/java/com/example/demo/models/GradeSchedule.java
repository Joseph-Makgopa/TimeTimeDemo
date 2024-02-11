package com.example.demo.models;

import java.util.ArrayList;

public class GradeSchedule {
    private Grade grade;
    private ArrayList<Integer> periods = new ArrayList<>();
    public GradeSchedule(Grade grade, Integer periods){
        this.grade = grade;

        for(int count = 0; count < periods; count++)
            this.periods.add(null);
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

    @Override
    public String toString() {
        return "GradeSchedule{" +
                "grade=" + grade +
                ", periods=" + periods +
                '}';
    }
}
