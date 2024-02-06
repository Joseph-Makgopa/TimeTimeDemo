package com.example.demo.models;

import java.util.Objects;

public class Day {
    public WeekDay day;
    public Integer periods;
    public Day(WeekDay day, Integer periods){
        this.day = day;
        this.periods = periods;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Day day1 = (Day) o;
        return day == day1.day && Objects.equals(periods, day1.periods);
    }

    @Override
    public int hashCode() {
        return Objects.hash(day, periods);
    }
}
