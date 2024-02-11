package com.example.demo.models.assignable;

import com.example.demo.models.Session;
import com.example.demo.models.State;

import java.util.Objects;

public class SplitAssignable extends Assignable{
    private Integer splitRef;
    public SplitAssignable(Integer splitRef_1, Integer splitRef_2){
        super(splitRef_1);

        if(State.getInstance().sessions.get(splitRef_2) == null){
            throw new NullPointerException("Session id '"+ splitRef_2.toString() + "' not found.");
        }

        this.splitRef = splitRef_2;
    }
    @Override
    public Boolean affectSingleSlot(){
        return true;
    }
    @Override
    public Boolean shareSingleSlot(){
        return true;
    }
    @Override
    public String getDetails(){
        Session session = State.getInstance().sessions.get(splitRef);
        return super.getDetails() + " / " + session.getEducator().getPost() + " " + session.getSubject();
    }
    @Override
    public Boolean containRef(Integer ref){
        return sessionRef.equals(ref) || splitRef.equals(ref);
    }

    public Integer getSplitRef() {
        return splitRef;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SplitAssignable that = (SplitAssignable) o;
        return Objects.equals(splitRef, that.splitRef) && Objects.equals(sessionRef, that.sessionRef);
    }

    @Override
    public int hashCode() {
        return Objects.hash(splitRef);
    }
}
