package com.example.demo.models;

import com.example.demo.utilities.Pair;

import java.io.Serializable;
import java.util.Objects;

public class Assignable implements Serializable {
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
    private Assignable(Pair<Integer, Integer> id, Integer remain){
        this.id = id;
        this.remain = remain;
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
    public Assignable clone(){
        return new Assignable(id, remain);
    }
    public Pair<Session, Session> getSessions(){
        return new Pair<>(State.getInstance().sessions.get(id.getFirst()), State.getInstance().sessions.get(id.getSecond()));
    }
    public Grade getGrade(){
        return State.getInstance().sessions.get(id.getFirst()).getGrade();
    }
    public Pair<Educator, Educator> getEducators(){
        if(id.getSecond() != null){
            return new Pair<>(State.getInstance().sessions.get(id.getFirst()).getEducator(), State.getInstance().sessions.get(id.getSecond()).getEducator());
        }

        return new Pair<>(State.getInstance().sessions.get(id.getFirst()).getEducator(), null);
    }
    public Boolean hasEducator(Educator educator){
        if(id.getSecond() != null){
            return State.getInstance().sessions.get(id.getFirst()).getEducator().equals(educator) || State.getInstance().sessions.get(id.getSecond()).getEducator().equals(educator);
        }

        return State.getInstance().sessions.get(id.getFirst()).getEducator().equals(educator);
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
    public void decrement(){
        remain--;
    }
    public void increment(){
        remain++;
    }
    public void reset(){
        Pair<Session, Session> sessions = getSessions();

        remain = sessions.getFirst().getAmount();

        Assignable assignable = getPair();

        if(assignable != null){
            sessions = getSessions();
            assignable.remain = sessions.getFirst().getAmount();
        }
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
