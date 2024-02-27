package com.example.demo.models;

import com.example.demo.utilities.Triplet;

import java.util.*;
import java.util.function.Consumer;

public class SlotOptions implements Iterable<Triplet<WeekDay, Grade, Integer>>, Comparable<SlotOptions>{
    private Assignable assignable;
    private ArrayList<Triplet<WeekDay, Grade, Integer>> options;
    private Map<WeekDay, Integer> optionsPerDay;
    public SlotOptions(Assignable assignable){
        this.assignable = assignable;
        this.options = new ArrayList<>();
        this.optionsPerDay = new HashMap<>();

        State.getInstance().days.forEach((day, period) -> {
            optionsPerDay.put(day, 0);
        });
    }
    public SlotOptions(Assignable assignable, ArrayList<Triplet<WeekDay, Grade, Integer>> options){
        this.assignable = assignable;
        this.options = options;

        State.getInstance().days.forEach((day, period) -> {
            optionsPerDay.put(day, 0);
        });

        Integer count = 0;

        for(Triplet<WeekDay, Grade, Integer> option : options) {
            count = optionsPerDay.get(option.getFirst());
            optionsPerDay.put(option.getFirst(), count + 1);
        }
    }
    public Assignable getAssignable() {
        return assignable;
    }
    public void setAssignable(Assignable assignable) {
        this.assignable = assignable;
    }
    public Integer getPossibleAssignments(){
        Integer result = 0, count;

        for(WeekDay day:optionsPerDay.keySet()){
            count = optionsPerDay.get(day);
            result += count >= 2 ? 2 : count;
        }

        return result;
    }
    public boolean limitReached(){
        return getPossibleAssignments() <= assignable.getRemain();
    }
    public ArrayList<Triplet<WeekDay, Grade, Integer>> getOptions() {
        return options;
    }
    public Triplet<WeekDay, Grade, Integer> get(int index) {
        return options.get(index);
    }
    public void setOptions(ArrayList<Triplet<WeekDay, Grade, Integer>> options) {
        this.options = options;
    }
    public boolean hasOption(Triplet<WeekDay, Grade, Integer> option) {
        return this.options.contains(option);
    }
    public void addOption(Triplet<WeekDay, Grade, Integer> option){
        this.options.add(option);

        int count = optionsPerDay.get(option.getFirst());
        optionsPerDay.put(option.getFirst(), count + 1);
    }
    public boolean removeOption(Triplet<WeekDay, Grade, Integer> option){
        int count = optionsPerDay.get(option.getFirst());
        boolean result = this.options.remove(option);

        if(result) {
            optionsPerDay.put(option.getFirst(), count - 1);
        }

        return result;
    }
    public int optionCount(){
        return this.options.size();
    }
    public boolean isEmpty(){
        return this.options.isEmpty();
    }

    public void sortOptions(Map<Triplet<WeekDay, Grade, Integer>, Integer> slotAvailability){
        int n = options.size();

        for (int i = 0; i < n - 1; i++){
            int minIndex = i;

            for (int j = i + 1; j < n; j++) {
                if(slotAvailability.get(options.get(j)) < slotAvailability.get(options.get(minIndex))) {
                    minIndex = j;
                }
            }

            Triplet<WeekDay, Grade, Integer> temp = options.get(minIndex);
            options.set(minIndex, options.get(i));
            options.set(i, temp);
        }
    }

    @Override
    public Iterator<Triplet<WeekDay, Grade, Integer>> iterator() {
        return options.iterator();
    }

    @Override
    public void forEach(Consumer<? super Triplet<WeekDay, Grade, Integer>> action) {
        options.forEach(action);
    }

    @Override
    public Spliterator<Triplet<WeekDay, Grade, Integer>> spliterator() {
        return options.spliterator();
    }
    @Override
    public int compareTo(SlotOptions o) {
        return assignable.getId().compareTo(o.assignable.getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SlotOptions that = (SlotOptions) o;
        return Objects.equals(assignable, that.assignable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(assignable);
    }

    @Override
    public String toString() {
        return "SlotOptions{" +
                "assignable=" + assignable.getId() +
                ", optionSize=" + options.size() +
                ", remain=" + assignable.getRemain() +
                '}';
    }
}
