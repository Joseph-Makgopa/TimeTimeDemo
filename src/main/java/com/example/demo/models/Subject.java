package com.example.demo.models;

import javafx.beans.property.SimpleStringProperty;

import java.io.Serializable;

public class Subject implements Serializable {
    private String name;
    private transient SimpleStringProperty nameView;
    public Subject(String name){
        this.name = name;
        this.nameView = new SimpleStringProperty(name);
    }
    public void setName(String name) {
        this.name = name;
        nameView.set(name);
    }
    public String getName() {
        return name;
    }
    @Override
    public String toString() {
        return "Subject{" +
                "name='" + name + '\'' +
                '}';
    }
}
