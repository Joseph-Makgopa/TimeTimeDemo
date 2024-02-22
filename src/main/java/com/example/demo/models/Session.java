package com.example.demo.models;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.io.Serializable;
import java.util.Objects;

public class Session implements Serializable,Comparable<Session> {
    private Integer id;
    private Grade grade;
    private Educator educator;
    private Subject subject;
    private Integer amount;
    private Integer split;
    private Integer pair;
    public static int counter = 0;
    public Session(Integer id, Grade grade, Educator educator, Subject subject, Integer amount, Integer split, Integer pair){
        this.id = id;
        this.grade = grade;
        this.educator = educator;
        this.subject = subject;
        this.amount = amount;
        this.split = split;
        this.pair = pair;

        counter++;
    }
    public Session(Grade grade, Educator educator, Subject subject, Integer amount, Integer split, Integer pair){
        this.id = ++counter;
        this.grade = grade;
        this.educator = educator;
        this.subject = subject;
        this.amount = amount;
        this.split = split;
        this.pair = pair;
    }
    public Session clone(){
        return new Session(grade, educator, subject, amount, split, pair);
    }
    public void setId(Integer id){
        this.id = id;
    }
    public Integer getId() {
        return id;
    }

    public Educator getEducator() {
        return educator;
    }

    public void setEducator(Educator educator) {
        this.educator = educator;
    }

    public Grade getGrade() {
        return grade;
    }

    public void setGrade(Grade grade) {
        this.grade = grade;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getPair() {
        return pair;
    }

    public void setPair(Integer pair) {
        this.pair = pair;
    }

    public Integer getSplit() {
        return split;
    }

    public void setSplit(Integer split) {
        this.split = split;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Session session = (Session) o;
        return Objects.equals(grade, session.grade) && Objects.equals(educator, session.educator) && Objects.equals(subject, session.subject) && Objects.equals(amount, session.amount) && Objects.equals(split, session.split) && Objects.equals(pair, session.pair);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, grade, educator, subject, amount, split, pair);
    }

    @Override
    public int compareTo(Session o) {
        return id.compareTo(o.getId());
    }
}
