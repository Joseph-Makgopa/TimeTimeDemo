package com.example.demo.models;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.io.Serializable;

public class Session implements Serializable {
    private Integer id;
    private Grade grade;
    private Educator educator;
    private Subject subject;
    private Integer amount;
    private Integer split;
    private Integer pair;
    private transient final SimpleIntegerProperty idView;
    private transient final SimpleStringProperty gradeView;
    private transient final SimpleStringProperty educatorView;
    private transient final SimpleStringProperty subjectView;
    private transient final SimpleIntegerProperty amountView;
    private transient final SimpleIntegerProperty splitView;
    private transient final SimpleIntegerProperty pairView;
    public static int counter = 0;
    public Session(Integer id, Grade grade, Educator educator, Subject subject, Integer amount, Integer split, Integer pair){
        this.id = id;
        this.grade = grade;
        this.educator = educator;
        this.subject = subject;
        this.amount = amount;

        this.idView = new SimpleIntegerProperty(id);
        this.gradeView = new SimpleStringProperty(grade.getNumber() + " " + grade.getDivision());
        this.educatorView = new SimpleStringProperty(educator.getInitials() + " " + educator.getSurname() + ", " + educator.getPost());
        this.subjectView = new SimpleStringProperty(subject.getName());
        this.amountView = new SimpleIntegerProperty(amount);
        this.splitView = new SimpleIntegerProperty(split);
        this.pairView = new SimpleIntegerProperty(pair);
        counter++;
    }
    public Session(Grade grade, Educator educator, Subject subject, Integer amount, Integer split, Integer pair){
        this.id = counter++;
        this.grade = grade;
        this.educator = educator;
        this.subject = subject;
        this.amount = amount;

        this.idView = new SimpleIntegerProperty(id);
        this.gradeView = new SimpleStringProperty(grade.getNumber().toString() + grade.getDivision());
        this.educatorView = new SimpleStringProperty(educator.getInitials() + " " + educator.getSurname() + ", " + educator.getPost());
        this.subjectView = new SimpleStringProperty(subject.getName());
        this.amountView = new SimpleIntegerProperty(amount);
        this.splitView = new SimpleIntegerProperty(split);
        this.pairView = new SimpleIntegerProperty(pair);
    }

    public Integer getId() {
        return id;
    }

    public Educator getEducator() {
        return educator;
    }

    public void setEducator(Educator educator) {
        this.educator = educator;
        this.educatorView.set(educator.getInitials() + " " + educator.getSurname() + ", " + educator.getPost());
    }

    public Grade getGrade() {
        return grade;
    }

    public void setGrade(Grade grade) {
        this.grade = grade;
        this.gradeView.set(grade.getNumber().toString() + grade.getDivision());
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
        this.subjectView.set(subject.getName());
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
        this.amountView.set(amount);
    }

    public Integer getPair() {
        return pair;
    }

    public void setPair(Integer pair) {
        this.pair = pair;
        this.pairView.set(pair);
    }

    public Integer getSplit() {
        return split;
    }

    public void setSplit(Integer split) {
        this.split = split;
        this.splitView.set(split);
    }

    @Override
    public String toString() {
        return "Session{" +
                "id=" + id +
                ", grade=" + grade +
                ", educator=" + educator +
                ", subject=" + subject +
                ", amount=" + amount +
                ", split=" + split +
                ", pair=" + pair +
                '}';
    }
}
