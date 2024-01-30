package com.example.demo.models;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.io.Serializable;
import java.util.Objects;

public class Educator implements Serializable {
    private Integer post;
    private String initials;
    private String surname;
    private transient final SimpleIntegerProperty postView;
    private transient final SimpleStringProperty initialsView;
    private transient final SimpleStringProperty surnameView;
    public Educator(Integer post, String initials, String surname){
        this.post = post;
        this.initials = initials;
        this.surname = surname;

        this.postView = new SimpleIntegerProperty(post);
        this.initialsView = new SimpleStringProperty(initials);
        this.surnameView = new SimpleStringProperty(surname);
    }
    public Integer getPost() {
        return post;
    }

    public void setPost(Integer post) {
        this.post = post;
        postView.set(post);
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
        surnameView.set(surname);
    }

    public String getInitials() {
        return initials;
    }

    public void setInitials(String initials) {
        this.initials = initials;
        initialsView.set(initials);
    }

    @Override
    public String toString() {
        return "Educator{" +
                "post=" + post +
                ", initials='" + initials + '\'' +
                ", surname='" + surname + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Educator educator = (Educator) o;
        return Objects.equals(post, educator.post) && Objects.equals(initials, educator.initials) && Objects.equals(surname, educator.surname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(post, initials, surname);
    }
}
