package com.example.demo.comparators;

import com.example.demo.models.Assignable;

import java.util.Comparator;

public class LessonComparator implements Comparator<Assignable> {
    @Override
    public int compare(Assignable o1, Assignable o2) {
        return o1.getRemain().compareTo(o2.getRemain()) * -1;
    }
}