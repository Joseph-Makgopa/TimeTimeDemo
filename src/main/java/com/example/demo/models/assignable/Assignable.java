package com.example.demo.models.assignable;

import com.example.demo.models.Grade;
import com.example.demo.models.Session;
import com.example.demo.models.State;
import com.example.demo.utilities.Pair;

import java.io.Serializable;
import java.util.Objects;

public class Assignable implements Serializable {
    private static Integer counter = 0;
    private Pair<Integer, Integer> id;
    private Integer remain;
    private Integer getPairId(){
        Session session = State.getInstance().sessions.get(id.getFirst());
        if(session.getPair() != null){
            return session.getPair();
        }

        if(id.getSecond() != null){
            session = State.getInstance().sessions.get(id.getSecond());
            if(session.getPair() != null){
                return session.getPair();
            }
        }

        return null;
    }
    public Assignable(Integer sessionRef) throws NullPointerException{

        if(State.getInstance().sessions.get(sessionRef) == null){
            throw new NullPointerException("Session id '"+ sessionRef.toString() + "' not found.");
        }

        id = new Pair<>(sessionRef, null);
        Integer share = State.getInstance().sessions.get(sessionRef).getSplit();

        if(share != null){
            id.setSecond(share);
        }

        remain = State.getInstance().sessions.get(sessionRef).getAmount();
    }
    public Pair<Session, Session> getSessions(){
        return new Pair<>(State.getInstance().sessions.get(id.getFirst()), State.getInstance().sessions.get(id.getSecond()));
    }
    public Boolean isShare(){
        return id.getFirst() != null && id.getSecond() != null;
    }
    public Boolean isPair(){
        Session session = State.getInstance().sessions.get(id.getFirst());
        if(session.getPair() != null){
            return true;
        }

        if(id.getSecond() != null){
            session = State.getInstance().sessions.get(id.getSecond());
            if(session.getPair() != null){
                return true;
            }
        }

        return false;
    }
    public Assignable getPair(){
        Integer pairId = getPairId();
        if(pairId != null){
            for(Pair<Integer, Integer> id: State.getInstance().assignables.keySet()){
                if(id.getFirst() != null && id.getFirst().equals(pairId)){
                    return State.getInstance().assignables.get(id);
                }

                if(id.getSecond() != null && id.getSecond().equals(pairId)){
                    return State.getInstance().assignables.get(id);
                }
            }
        }

        return null;
    }
    public String getDetails(){
        Session session = State.getInstance().sessions.get(id.getFirst());
        String result =  session.getEducator().getPost() + " " + session.getSubject();

        session = State.getInstance().sessions.get(id.getSecond());
        if(session != null){
            result += " / " + session.getEducator().getPost() + " " + session.getSubject();
        }

        return result;
    }
    public void setRemain(Integer remain){
        this.remain = remain;
    }
    public Integer getRemain(){
        return remain;
    }

    public Pair<Integer, Integer> getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Assignable that = (Assignable) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
