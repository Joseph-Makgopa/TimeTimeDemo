package com.example.demo.models;

import javafx.beans.property.SimpleStringProperty;

import java.io.Serializable;
import java.util.Objects;

public class Subject implements Serializable {
    private String name;
    public Subject(String name){
        this.name = name;
    }
    public Subject clone(){
        return new Subject(name);
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    @Override
    public String toString() {
        return name;
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
