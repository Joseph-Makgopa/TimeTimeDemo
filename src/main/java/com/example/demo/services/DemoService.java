package com.example.demo.services;

import com.example.demo.models.*;
import com.example.demo.models.Assignable;
import com.example.demo.utilities.Filter;
import com.example.demo.utilities.Pair;
import com.example.demo.utilities.Triplet;
import com.example.demo.utilities.TripletManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.File;
import java.util.*;

public abstract class DemoService {
    private Map<Educator, Integer> teacherLessonCount;
    private Map<Assignable, Integer> lessonAssignments;
    private LessonSlotOptions lessonSlotOptions;
    private Map<Triplet<WeekDay, Grade, Integer>, Integer> slotAvailability;
    protected TabPane pane;
    public DemoService(TabPane pane){
        this.pane = pane;
    }
    public void refresh(){
        populateTable();
        setupTable();
        for(Tab tab: pane.getTabs()){
            ((TableView)((AnchorPane)tab.getContent()).getChildren().get(0)).refresh();
        }
    }
    public ObservableList<Assignable> search(String text){
        if(text == null || text.isEmpty())
            return FXCollections.observableArrayList(State.getInstance().assignables.values());

        ObservableList<Assignable> result = FXCollections.observableArrayList();

        State.getInstance().assignables.values().forEach(entry -> {
            if(entry.getDetails().toUpperCase().contains(text)){
                result.add(entry);
                return;
            }

            if(entry.getRemain().toString().contains(text)){
                result.add(entry);
                return;
            }

            if(State.getInstance().sessions.get(entry.getId().getFirst()).getGrade().toString().contains(text)){
                result.add(entry);
                return;
            }
        });

        return result;
    }
    public abstract void setupTable();
    public abstract void populateTable();
    public abstract void position(TabPane paneTimeTable, Assignable selected, WeekDay day, Integer period);
    public abstract void print(TabPane pane, Stage stage);
    public abstract void export(File file);
    public abstract void updateFilter(Filter filter, TabPane pane);
    protected void setTeacherLessonCount(){
        teacherLessonCount = new HashMap<>();
        Integer count;

        for(Educator educator: State.getInstance().educators.values()){
            teacherLessonCount.put(educator, 0);
        }

        for(Session session: State.getInstance().sessions.values()){
            count = teacherLessonCount.get(session.getEducator());

            teacherLessonCount.put(session.getEducator(), count + session.getAmount());
        }
    }
    protected void setLessonSlotOptions(){
        lessonSlotOptions = new LessonSlotOptions();
        Map<Grade, ArrayList<Triplet<WeekDay, Grade, Integer>>> options = new HashMap<>();
        ArrayList<Triplet<WeekDay, Grade, Integer>> list;

        for(Grade grade: State.getInstance().grades){
            list = new ArrayList<>();

            for(WeekDay day: State.getInstance().days.keySet()){
                for(int period = 0; period < State.getInstance().days.get(day); period++){
                    list.add(TripletManager.get(day, grade, period));
                }
            }

            options.put(grade, list);
        }

        for(Assignable assignable: State.getInstance().assignables.values()){
            lessonSlotOptions.putFast(new SlotOptions(assignable,(ArrayList<Triplet<WeekDay, Grade, Integer>>) options.get(State.getInstance().sessions.get(assignable.getId().getFirst()).getGrade()).clone()));
        }
    }
    protected void setLessonAssignments(){
        lessonAssignments = new HashMap<>();

        State.getInstance().assignables.forEach((id, assignable) -> {
            lessonAssignments.put(assignable, 0);
        });
    }
    protected void setSlotAvailability(LessonSlotOptions lessonSlotOptions){
        Map<Triplet<WeekDay, Grade, Integer>, Integer> slotAvailability = new HashMap<>();

        for(WeekDay day: State.getInstance().days.keySet()){
            for(Grade grade: State.getInstance().grades){
                for(int period = 0; period < State.getInstance().days.get(day); period++){
                    Triplet<WeekDay, Grade, Integer> triplet = TripletManager.get(day, grade, period);
                    int count = 0;

                    for(SlotOptions slotOptions: lessonSlotOptions){
                        if(slotOptions.hasOption(triplet)){
                            count++;
                        }
                    }

                    slotAvailability.put(triplet, count);
                }
            }
        }
    }
    protected ArrayList<Comparator<SlotOptions>> getComparators(){
        ArrayList<Comparator<SlotOptions>> result = new ArrayList<>();

        result.add(new OptionsCountComparator());
        result.add(new EducatorBasedComparator(teacherLessonCount));
        result.add(new AssignmentBasedComparator(lessonAssignments));

        return result;
    }
    public void arrange(){
        setTeacherLessonCount();
        setLessonSlotOptions();
        setSlotAvailability(lessonSlotOptions);

        ArrayList<Comparator<SlotOptions>> comparators = getComparators();

        State.getInstance().timetable.clear();
        for(Assignable assignable: State.getInstance().assignables.values()){
            assignable.reset();
        }

        while(!lessonSlotOptions.isEmpty()){
            LinkedList<SlotOptions> slotOptions = lessonSlotOptions.popLimits(), copy;

            if(slotOptions.isEmpty()){
                slotOptions = lessonSlotOptions.pop();
            }

            copy = (LinkedList<SlotOptions>) slotOptions.clone();

            for(Comparator<SlotOptions> comparator: comparators){
                slotOptions = arrangeFilter(slotOptions, comparator);

                if(slotOptions.size() <= 1)
                    break;
            }

            Pair<SlotOptions, Triplet<WeekDay, Grade, Integer>> selection = selectSlot(slotOptions);

            if(selection != null){
                if(selection.getFirst().getAssignable().getRemain() == 0)
                    copy.remove(selection.getFirst());

                lessonSlotOptions.putList(copy);
            }
        }

        refresh();
    }
    protected LinkedList<SlotOptions> arrangeFilter(LinkedList<SlotOptions> list, Comparator<SlotOptions> comparator){
        if(!list.isEmpty()){
            list.sort(comparator);
            LinkedList<SlotOptions> filter = new LinkedList<>();

            filter.add(list.removeFirst());

            while(comparator.compare(filter.getFirst(), list.getFirst()) == 0){
                filter.add(list.removeFirst());
            }

            return filter;
        }

        return list;
    }

    public Pair<SlotOptions, Triplet<WeekDay, Grade, Integer>> selectSlot(LinkedList<SlotOptions> subset){
        if(!subset.isEmpty()){
            Comparator<Triplet<WeekDay, Grade, Integer>> slotAvailabilityComparator = new SlotAvailabilityComparator(slotAvailability);
            Pair<SlotOptions, Triplet<WeekDay, Grade, Integer>> result = null;
            ArrayList<Triplet<WeekDay, Grade, Integer>> options;

            for(SlotOptions slotOption: subset){
                options = slotOption.getOptions();
                options.sort(slotAvailabilityComparator);

                for(Triplet<WeekDay, Grade, Integer> option: options){
                    Assignable assignable = slotOption.getAssignable();
                    Assignable pair = assignable.getPair();
                    Triplet<WeekDay, Grade, Integer> pairOption = TripletManager.get(option.getFirst(), pair.getSessions().getFirst().getGrade(), option.getThird());

                    if(State.getInstance().timetable.get(option) == null){
                        if(pair == null){
                            State.getInstance().timetable.put(option, assignable.getId());
                            assignable.decrement();
                            int start = option.getThird().intValue() + (State.getInstance().breakAfter == option.getThird().intValue() ? 1 : 2);

                            for(; start < State.getInstance().days.get(option.getFirst()); start++){
                                slotOption.removeOption(TripletManager.get(option.getFirst(), option.getSecond(), option.getThird()));
                            }

                            return new Pair<>(slotOption, option);
                        }else if(State.getInstance().timetable.get(pairOption) == null){
                            State.getInstance().timetable.put(option, assignable.getId());
                            State.getInstance().timetable.put(pairOption, pair.getId());

                            assignable.decrement();
                            pair.decrement();

                            int start = option.getThird().intValue() + (State.getInstance().breakAfter == option.getThird().intValue() ? 1 : 2);

                            for(; start < State.getInstance().days.get(option.getFirst()); start++){
                                slotOption.removeOption(TripletManager.get(option.getFirst(), option.getSecond(), option.getThird()));
                                lessonSlotOptions.get(pair).removeOption(pairOption);
                            }

                            return new Pair<>(slotOption, option);
                        }
                    }
                }
            }
        }

        return null;
    }
}

class OptionsCountComparator implements Comparator<SlotOptions>{
    @Override
    public int compare(SlotOptions o1, SlotOptions o2) {
        return Integer.compare(o1.optionCount(), o2.optionCount());
    }
}

class EducatorBasedComparator implements Comparator<SlotOptions>{

    private Map<Educator, Integer> teacherLessonCount;
    public EducatorBasedComparator(Map<Educator, Integer> teacherLessonCount){
        this.teacherLessonCount = teacherLessonCount;
    }
    @Override
    public int compare(SlotOptions o1, SlotOptions o2) {
        Educator educatorOne = o1.getAssignable().getSessions().getFirst().getEducator();
        Educator educatorTwo = o2.getAssignable().getSessions().getFirst().getEducator();

        return teacherLessonCount.get(educatorOne).compareTo(teacherLessonCount.get(educatorTwo)) * -1;
    }
}

class AssignmentBasedComparator implements Comparator<SlotOptions>{
    private Map<Assignable, Integer> lessonAssignments;
    public AssignmentBasedComparator(Map<Assignable, Integer> lessonAssignments){
        this.lessonAssignments = lessonAssignments;
    }
    @Override
    public int compare(SlotOptions o1, SlotOptions o2) {
        return lessonAssignments.get(o1.getAssignable()).compareTo(lessonAssignments.get(o2.getAssignable()));
    }
}

class SlotAvailabilityComparator implements Comparator<Triplet<WeekDay, Grade, Integer>>{
    private Map<Triplet<WeekDay, Grade, Integer>, Integer> slotAvailability;
    public SlotAvailabilityComparator(Map<Triplet<WeekDay, Grade, Integer>, Integer> slotAvailability){
        this.slotAvailability = slotAvailability;
    }
    @Override
    public int compare(Triplet<WeekDay, Grade, Integer> o1, Triplet<WeekDay, Grade, Integer> o2) {
        return slotAvailability.get(o1).compareTo(slotAvailability.get(o2));
    }
}
