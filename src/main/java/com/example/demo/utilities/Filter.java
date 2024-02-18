package com.example.demo.utilities;

import com.example.demo.models.*;

import java.io.Serializable;

public class Filter implements Serializable {
    public Integer number = null;
    public Character division = null;
    public Subject subject = null;
    public Educator educator = null;
    public void clear(){
        number = null;
        division = null;
        subject = null;
        educator = null;
    }
    public void set(Integer number, Character division, Subject subject, Educator educator){
        this.number = number;
        this.division = division;
        this.subject = subject;
        this.educator = educator;
    }
}
