package com.example.demo.models;

import com.example.demo.utilities.Pair;

import java.util.ArrayList;

public class Rank<T>{
    private T header;
    private ArrayList<Pair<Integer, Integer>> periods = new ArrayList<>();
    public Rank(T header, Integer periods){
        this.header = header;

        for(int count = 0; count < periods; count++)
            this.periods.add(null);
    }

    public T getHeader() {
        return header;
    }

    public void setHeader(T header) {
        this.header = header;
    }

    public ArrayList<Pair<Integer, Integer>> getPeriods() {
        return periods;
    }

    public void setPeriods(ArrayList<Pair<Integer, Integer>> periods) {
        this.periods = periods;
    }

    @Override
    public String toString() {
        return "Row{" +
                "header=" + header +
                ", periods=" + periods +
                '}';
    }
}
