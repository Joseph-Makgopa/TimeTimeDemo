package com.example.demo.models.assignable;

import com.example.demo.models.Grade;
import com.example.demo.models.Session;
import com.example.demo.models.State;

import java.io.Serializable;
import java.util.Objects;

public class Assignable implements Serializable {
    private static Integer counter = 0;
    private Integer id;
    protected Integer sessionRef;
    private Integer remain;
    public Assignable(Integer sessionRef) throws NullPointerException{
        id = counter++;

        if(State.getInstance().sessions.get(sessionRef) == null){
            throw new NullPointerException("Session id '"+ sessionRef.toString() + "' not found.");
        }

        this.sessionRef = sessionRef;
        remain = State.getInstance().sessions.get(sessionRef).getAmount();
    }
    public Boolean affectSingleSlot(){
        return true;
    }
    public Boolean shareSingleSlot(){
        return false;
    }
    public String getDetails(){
        Session session = State.getInstance().sessions.get(sessionRef);
        return session.getEducator().getPost() + " " + session.getSubject();
    }
    public String getDetailsByGrade(Grade grade){
        Session session = State.getInstance().sessions.get(sessionRef);

        if(session.getGrade().equals(grade)){
            return session.getEducator().getPost() + " " + session.getSubject();
        }

        return "";
    }
    public void setRemain(Integer remain){
        this.remain = remain;
    }
    public Integer getRemain(){
        return remain;
    }
    public Boolean containRef(Integer ref){
        return sessionRef.equals(ref);
    }

    public Integer getId() {
        return id;
    }

    public Integer getSessionRef() {
        return sessionRef;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Assignable that = (Assignable) o;
        return Objects.equals(sessionRef, that.sessionRef);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sessionRef);
    }
}
