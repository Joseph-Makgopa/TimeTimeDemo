package com.example.demo.models;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import java.io.Serializable;
import java.util.Objects;

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

        if(split == null) {
            this.splitView = new SimpleIntegerProperty(0);
            this.split = null;
        }else {
            this.splitView = new SimpleIntegerProperty(split);
            this.split = split;
        }

        if(pair == null) {
            this.pairView = new SimpleIntegerProperty(0);
            this.pair = null;
        }else {
            this.pairView = new SimpleIntegerProperty(pair);
            this.pair = pair;
        }

        counter++;
    }
    public Session(Grade grade, Educator educator, Subject subject, Integer amount, Integer split, Integer pair){
        this.id = ++counter;
        this.grade = grade;
        this.educator = educator;
        this.subject = subject;
        this.amount = amount;

        this.idView = new SimpleIntegerProperty(id);
        this.gradeView = new SimpleStringProperty(grade.getNumber().toString() + grade.getDivision());
        this.educatorView = new SimpleStringProperty(educator.getInitials() + " " + educator.getSurname() + ", " + educator.getPost());
        this.subjectView = new SimpleStringProperty(subject.getName());
        this.amountView = new SimpleIntegerProperty(amount);

        if(split == null) {
            this.splitView = new SimpleIntegerProperty(0);
            this.split = null;
        }else {
            this.splitView = new SimpleIntegerProperty(split);
            this.split = split;
        }

        if(pair == null) {
            this.pairView = new SimpleIntegerProperty(0);
            this.pair = null;
        }else {
            this.pairView = new SimpleIntegerProperty(pair);
            this.pair = pair;
        }
    }
    public Session clone(){
        return new Session(grade, educator, subject, amount, split, pair);
    }
    public void setId(Integer id){
        this.id = id;
        idView.set(id);
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
    public Integer getIdView(){
        return idView.get();
    }

    public String getGradeView() {
        return gradeView.get();
    }

    public String getEducatorView() {
        return educatorView.get();
    }

    public String getSubjectView() {
        return subjectView.get();
    }

    public int getAmountView() {
        return amountView.get();
    }

    public int getSplitView() {
        return splitView.get();
    }

    public int getPairView() {
        return pairView.get();
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
}
