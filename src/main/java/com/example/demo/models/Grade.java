package com.example.demo.models;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.io.Serializable;
import java.util.Objects;

public class Grade implements Serializable, Comparable<Grade> {
    private Integer number;
    private Character division;
    public Grade(Integer number, Character division){
        this.number = number;
        this.division = division;
    }
    public Grade clone(){
        return new Grade(number, division);
    }
    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Character getDivision() {
        return division;
    }

    public void setDivision(Character division) {
        this.division = division;
    }

    public void refreshView(){
    }

    @Override
    public String toString() {
        return number + " " + division;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Grade grade = (Grade) o;
        return Objects.equals(number, grade.number) && Objects.equals(division, grade.division);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, division);
    }

    @Override
    public int compareTo(Grade grade) {
        int result = number.compareTo(grade.number);

        if(result == 0){
            result = division.compareTo(grade.division);
        }

        return result;
    }
}
