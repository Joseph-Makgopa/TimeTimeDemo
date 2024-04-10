package com.example.demo.comparators;

import com.example.demo.models.Assignable;
import com.example.demo.models.Educator;
import com.example.demo.utilities.Pair;

import java.util.Comparator;
import java.util.Map;

public class EducatorBasedComparator implements Comparator<Assignable> {

    private Map<Educator, Integer> educatorLessonCount;
    private Integer getCount(Assignable assignable){
        Pair<Educator, Educator> educators = assignable.getEducators();
        Integer count = educatorLessonCount.get(educators.getFirst());

        if(educators.getSecond() != null && educatorLessonCount.get(educators.getSecond()) > count){
            count = educatorLessonCount.get(educators.getSecond());
        }

        return count;
    }

    public EducatorBasedComparator(Map<Educator, Integer> educatorLessonCount){
        this.educatorLessonCount = educatorLessonCount;
    }
    @Override
    public int compare(Assignable o1, Assignable o2) {
        return getCount(o1).compareTo(getCount(o2)) * -1;
    }
}