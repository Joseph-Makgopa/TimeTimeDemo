package com.example.demo.models;

import com.example.demo.utilities.Triplet;

import java.util.*;
import java.util.function.BiConsumer;

public class LessonSlotOptions implements Iterable<SlotOptions>{
    private TreeMap<Assignable, SlotOptions> data;
    public LessonSlotOptions(){
        data = new TreeMap<>(new SlotOptionsComparator());
    }
    public SlotOptions get(Assignable assignable){
        return data.get(assignable);
    }
    public LinkedList<SlotOptions> pop(){
        LinkedList<SlotOptions> result = new LinkedList<>();

        if(!data.isEmpty()){
            result.add(data.pollFirstEntry().getValue());

            for(SlotOptions slotOption: data.values()){
                if(data.comparator().compare(result.getFirst().getAssignable(), slotOption.getAssignable()) == 0)
                    result.add(slotOption);
                else
                    break;
            }
        }

        return result;
    }
    public LinkedList<SlotOptions> popLimits(){
        LinkedList<SlotOptions> result = new LinkedList<>();

        data.forEach((assignable, slotOptions) -> {
            if(slotOptions.limitReached())
                result.add(slotOptions);
        });

        return result;
    }
    public int removeOption(Triplet<WeekDay, Grade, Integer> option){
        int result = 0;

        for(Assignable assignable: data.keySet()){
            SlotOptions slotOptions = data.get(assignable);

            if(slotOptions.removeOption(option))
                result++;

            if(slotOptions.isEmpty())
                data.remove(assignable);
        }


        return result;
    }
    public void putList(LinkedList<SlotOptions> list){
        for(SlotOptions slotOption: list){
            putFast(slotOption);
        }
    }
    public void put(SlotOptions options){
        data.put(options.getAssignable(), options);
    }
    public void putFast(SlotOptions options){
        data.put(options.getAssignable(), options);
    }
    public boolean isEmpty(){
        return data.isEmpty();
    }

    @Override
    public Iterator iterator() {
        return data.values().iterator();
    }
}

class SlotOptionsComparator implements Comparator<Assignable>{
    @Override
    public int compare(Assignable o1, Assignable o2) {
        return o1.getRemain().compareTo(o2.getRemain()) * -1;
    }
}
