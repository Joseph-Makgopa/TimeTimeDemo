package com.example.demo.comparators;

import com.example.demo.models.Assignable;

import java.util.Comparator;
import java.util.Map;

public class LessonAssignmentComparator implements Comparator<Assignable> {
    private final Map<Assignable, Integer> lessonAssignments;

    public LessonAssignmentComparator(Map<Assignable, Integer> lessonAssignments){
        this.lessonAssignments = lessonAssignments;
    }

    @Override
    public int compare(Assignable o1, Assignable o2) {
        return lessonAssignments.get(o1).compareTo(lessonAssignments.get(o2));
    }
}
