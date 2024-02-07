package com.example.demo.models.assignable;

import com.example.demo.models.Grade;
import com.example.demo.models.Session;
import com.example.demo.models.State;

import java.util.Objects;

public class PairAssignable extends Assignable{
    private Integer pairRef;
    public PairAssignable(Integer pairRef_1, Integer pairRef_2){
        super(pairRef_1);

        if(State.getInstance().sessions.get(pairRef_2) == null){
            throw new NullPointerException("Session id '"+ pairRef_2.toString() + "' not found.");
        }

        this.pairRef = pairRef_2;
    }
    @Override
    public Boolean affectSingleSlot(){
        return false;
    }

    public Integer getPairRef() {
        return pairRef;
    }

    @Override
    public String getDetails(){
        Session session = State.getInstance().sessions.get(pairRef);
        return super.getDetails() + " / " + session.getEducator().getPost() + " " + session.getSubject();
    }
    @Override
    public String getDetailsByGrade(Grade grade){
        String result = super.getDetailsByGrade(grade);

        if(result == ""){
            Session session = State.getInstance().sessions.get(pairRef);

            if(session.getGrade().equals(grade)){
                return session.getEducator().getPost() + " " + session.getSubject();
            }

            return "";
        }

        return result;
    }
    @Override
    public Boolean containRef(Integer ref){
        return sessionRef.equals(ref) || pairRef.equals(ref);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PairAssignable that = (PairAssignable) o;
        return Objects.equals(pairRef, that.pairRef) && Objects.equals(sessionRef, that.sessionRef);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pairRef);
    }
}
