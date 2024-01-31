package com.example.demo.models;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.io.Serializable;
import java.util.Objects;

public class Grade implements Serializable {
    private Integer number;
    private Character division;
    private transient final SimpleIntegerProperty numberView;
    private transient final SimpleStringProperty divisionView;
    public Grade(Integer number, Character division){
        this.number = number;
        this.division = division;
        this.numberView = new SimpleIntegerProperty(number);
        this.divisionView = new SimpleStringProperty(division.toString());
    }
    public Grade clone(){
        return new Grade(number, division);
    }
    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
        numberView.set(number);
    }

    public Character getDivision() {
        return division;
    }

    public void setDivision(Character division) {
        this.division = division;
        divisionView.set(division.toString());
    }

    @Override
    public String toString() {
        return "Grade{" +
                "number=" + number +
                ", division=" + division +
                '}';
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
}
