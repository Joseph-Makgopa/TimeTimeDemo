package com.example.demo.models;

import javafx.beans.property.SimpleStringProperty;

import java.io.Serializable;
import java.util.Objects;

public class Subject implements Serializable {
    private String name;
    private transient final SimpleStringProperty nameView;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subject subject = (Subject) o;
        return Objects.equals(name, subject.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
