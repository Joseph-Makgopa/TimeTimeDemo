package com.example.demo.models;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.io.Serializable;

public class Grade implements Serializable {
    private Integer number;
    private Character division;
    private transient SimpleIntegerProperty numberView;
    private transient SimpleStringProperty divisionView;
    public Grade(Integer number, Character division){
        this.number = number;
        this.division = division;
        this.numberView = new SimpleIntegerProperty(number);
        this.divisionView = new SimpleStringProperty(division.toString());
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
}
