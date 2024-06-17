package com.example.demo.comparators;

import com.example.demo.models.Assignable;
import com.example.demo.models.Educator;
import com.example.demo.utilities.Pair;

import java.util.Comparator;
import java.util.Map;

public class EducatorAssignmentComparator implements Comparator<Assignable> {
    private final Map<Educator, Integer> educatorAssignments;

    public EducatorAssignmentComparator(Map<Educator, Integer> educatorAssignments){
        this.educatorAssignments = educatorAssignments;
    }

    public Integer getLeastAssignment(Assignable assignable){
        Pair<Educator, Educator> educators = assignable.getEducators();
        Integer result = educatorAssignments.get(educators.getFirst());

        if(educators.getSecond() != null && educatorAssignments.get(educators.getSecond()).compareTo(result) < 0)
            result = educatorAssignments.get(educators.getSecond());

        return result;
    }

    @Override
    public int compare(Assignable o1, Assignable o2) {
        return getLeastAssignment(o1).compareTo(getLeastAssignment(o2));
    }
}
