package com.example.demo.models;

import java.util.ArrayList;

public class Row <T>{
    private T header;
    private ArrayList<Integer> periods = new ArrayList<>();
    public Row(T header, Integer periods){
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

    public ArrayList<Integer> getPeriods() {
        return periods;
    }

    public void setPeriods(ArrayList<Integer> periods) {
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
