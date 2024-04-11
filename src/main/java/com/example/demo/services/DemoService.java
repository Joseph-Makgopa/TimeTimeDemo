package com.example.demo.services;

import com.example.demo.comparators.AssignableComparator;
import com.example.demo.comparators.EducatorBasedComparator;
import com.example.demo.comparators.LessonComparator;
import com.example.demo.controllers.DemoController;
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
    protected TabPane pane;
    protected TableView<Assignable> tableAssign;
    protected DemoController demoController;
    protected Filter filterOptions;
    public DemoService(TabPane pane, TableView<Assignable> tableAssign, DemoController demoController){
        this.pane = pane;
        this.tableAssign = tableAssign;
        this.demoController = demoController;
        this.filterOptions = new Filter();
        ClickableTableCell.lastSelectedCell = null;
    }
    public void setFilterOptions(Filter filterOptions){
        this.filterOptions = filterOptions;
    }
    public void refresh(){
        Integer index = pane.getSelectionModel().getSelectedIndex();

        populateTable();
        setupTable();

        if(index > 0 && index < pane.getTabs().size())
            pane.getSelectionModel().select(index);

        Assignable selection = tableAssign.getSelectionModel().getSelectedItem();
        tableAssign.getItems().clear();
        tableAssign.getItems().addAll(State.getInstance().assignables.values());
        tableAssign.getItems().sort(new AssignableComparator());
        tableAssign.refresh();

        if(selection != null)
            tableAssign.getSelectionModel().select(selection);

        ClickableTableCell.lastSelectedCell = null;
    }
    public abstract void clearTab();
    public abstract void clearRow();
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
    public void highlightOptions(ClickableTableCell<?,?> oldCell, ClickableTableCell<?,?> newCell){

    }
    public abstract void setupTable();
    public abstract void populateTable();
    public abstract void position();
    public abstract void print(Stage stage);
    public abstract void export(File file);
    public abstract void filter();
    protected void prepare(LinkedList<Assignable> options, Queue<Assignable> lessons, Map<Educator, Integer> educatorLessonCount, Map<Triplet<WeekDay, Grade, Integer>, LinkedList<Assignable>> slotOptions){
        lessons.clear();
        educatorLessonCount.clear();
        slotOptions.clear();

        for(Assignable assignable: options) {
            if(assignable.getRemain() > 0)
                lessons.add(assignable);
        }

        for(Educator educator: State.getInstance().educators.values()){
            educatorLessonCount.put(educator, 0);
        }

        for(Assignable assignable: lessons){
            Pair<Educator, Educator> educators = assignable.getEducators();
            Integer count = educatorLessonCount.get(educators.getFirst());

            educatorLessonCount.put(educators.getFirst(), count + assignable.getRemain());

            if(educators.getSecond() != null){
                count = educatorLessonCount.get(educators.getSecond());
                educatorLessonCount.put(educators.getSecond(), count + assignable.getRemain());
            }
        }

        for(WeekDay day: State.getInstance().days.keySet()){
            for(Grade grade: State.getInstance().grades){
                LinkedList<Assignable> temp = new LinkedList<>(lessons.stream().filter(assignable -> assignable.getGrade().equals(grade)).toList());

                for(int period = 0; period < State.getInstance().days.get(day); period++){
                    Triplet<WeekDay, Grade, Integer> triplet = TripletManager.get(day, grade, period);

                    slotOptions.put(triplet, (LinkedList<Assignable>) temp.clone());
                }
            }
        }

        Iterator<Map.Entry<Triplet<WeekDay, Grade, Integer>, LinkedList<Assignable>>> iterator = slotOptions.entrySet().iterator();

        while(iterator.hasNext()){
            Map.Entry<Triplet<WeekDay, Grade, Integer>, LinkedList<Assignable>> entry = iterator.next();
            Triplet<WeekDay, Grade, Integer> triplet = entry.getKey();

            if(State.getInstance().timetable.get(triplet) != null){
                Assignable assignable = State.getInstance().assignables.get(State.getInstance().timetable.get(triplet));
                iterator.remove();

                Integer breakAfter = State.getInstance().breakAfter - 1;

                for(int period = 0; period < State.getInstance().days.get(triplet.getFirst()); period++){
                    if(period == (triplet.getThird() - 1) && triplet.getThird() != (breakAfter + 1))
                        continue;

                    if(period == triplet.getThird())
                        continue;

                    if(period == (triplet.getThird() + 1) && triplet.getThird() != breakAfter)
                        continue;

                    LinkedList<Assignable> values =  slotOptions.get(TripletManager.get(triplet.getFirst(), triplet.getSecond(), period));

                    if(values != null){
                        values.remove(assignable);
                    }
                }

                for(Grade grade: State.getInstance().grades){
                    if(assignable.getGrade().equals(grade))
                        continue;

                    LinkedList<Assignable> values = slotOptions.get(TripletManager.get(triplet.getFirst(), grade, triplet.getThird()));

                    if(values != null){
                        Iterator<Assignable> valuesIterator =  values.iterator();
                        Pair<Educator, Educator> educators = assignable.getEducators();

                        while(valuesIterator.hasNext()){
                            Assignable value = valuesIterator.next();

                            if(value.hasEducator(educators.getFirst()) || value.hasEducator(educators.getSecond()))
                                valuesIterator.remove();
                        }
                    }
                }
            }else if(entry.getValue().isEmpty()){
                iterator.remove();
            }
        }

    }
    protected void position(Assignable lesson, Triplet<WeekDay, Grade, Integer> slot, Queue<Assignable> lessons, Map<Educator, Integer> educatorLessonCount, Map<Triplet<WeekDay, Grade, Integer>, LinkedList<Assignable>> slotOptions, Boolean pair){
        State.getInstance().timetable.put(slot, lesson.getId());

        Pair<Educator, Educator> educators = lesson.getEducators();
        educatorLessonCount.put(educators.getFirst(), educatorLessonCount.get(educators.getFirst()) - 1);

        if(educators.getSecond() != null){
            educatorLessonCount.put(educators.getSecond(), educatorLessonCount.get(educators.getSecond()) - 1);
        }

        lesson.setRemain(lesson.getRemain() - 1);
        slotOptions.remove(slot);

        if(lesson.getRemain() == 0){
            slotOptions.forEach((triplet, options) -> {
                options.remove(lesson);
            });
        }else{
            lessons.add(lesson);

            int breakAfter = State.getInstance().breakAfter - 1;

            for(int period = 0; period < State.getInstance().days.get(slot.getFirst()); period++){
                if(period == (slot.getThird() - 1) && slot.getThird() != (breakAfter + 1))
                    continue;

                if(period == slot.getThird())
                    continue;

                if(period == (slot.getThird() + 1) && slot.getThird() != breakAfter)
                    continue;

                LinkedList<Assignable> values =  slotOptions.get(TripletManager.get(slot.getFirst(), slot.getSecond(), period));

                if(values != null){
                    values.remove(lesson);
                }
            }

            for(Grade grade: State.getInstance().grades){
                if(lesson.getGrade().equals(grade))
                    continue;

                LinkedList<Assignable> values = slotOptions.get(TripletManager.get(slot.getFirst(), grade, slot.getThird()));

                if(values != null){
                    Iterator<Assignable> valuesIterator =  values.iterator();
                    educators = lesson.getEducators();

                    while(valuesIterator.hasNext()){
                        Assignable value = valuesIterator.next();

                        if(value.hasEducator(educators.getFirst()) || value.hasEducator(educators.getSecond()))
                            valuesIterator.remove();
                    }
                }
            }
        }

        Assignable pairAssignable = lesson.getPair();
        if(pairAssignable != null && !pair){
            position(pairAssignable, TripletManager.get(slot.getFirst(), pairAssignable.getGrade(), slot.getThird()), lessons, educatorLessonCount, slotOptions, true);
        }
    }
    public void arrange(LinkedList<Assignable> options){
        Map<Educator, Integer> educatorLessonCount = new HashMap<>();
        Map<Triplet<WeekDay, Grade, Integer>, LinkedList<Assignable>> slotOptions = new HashMap<>();

        LessonComparator lessonComparator = new LessonComparator();
        EducatorBasedComparator educatorBasedComparator = new EducatorBasedComparator(educatorLessonCount);
        Queue<Assignable> lessonsOne = new PriorityQueue<>(lessonComparator);
        Queue<Assignable> lessonsTwo = new PriorityQueue<>(educatorBasedComparator);

        prepare(options, lessonsOne, educatorLessonCount, slotOptions);

        while(!slotOptions.isEmpty() && !lessonsOne.isEmpty()){
            lessonsTwo.clear();
            lessonsTwo.add(lessonsOne.poll());

            while(!lessonsOne.isEmpty() && lessonComparator.compare(lessonsOne.peek(), lessonsTwo.peek()) == 0)
                lessonsTwo.add(lessonsOne.poll());

            LinkedList<Assignable> lessonsThree = new LinkedList<>();
            lessonsThree.add(lessonsTwo.poll());

            while(!lessonsTwo.isEmpty() && educatorBasedComparator.compare(lessonsThree.getFirst(), lessonsTwo.peek()) == 0)
                lessonsThree.add(lessonsTwo.poll());

            lessonsOne.addAll(lessonsTwo);

            Assignable lessonChoice = null;
            Triplet<WeekDay, Grade, Integer> slotChoice = null, slotTemp;

            Iterator<Assignable> iterator = lessonsThree.iterator();

            while(iterator.hasNext()){
                Assignable assignable = iterator.next();
                slotTemp = null;

                for(Triplet<WeekDay, Grade, Integer> slot: slotOptions.keySet()){
                    LinkedList<Assignable> choices = slotOptions.get(slot);

                    if(choices.contains(assignable)){
                        if(slotTemp == null){
                            slotTemp = slot;
                        }else if(choices.size() < slotOptions.get(slotTemp).size()){
                            slotTemp = slot;
                        }
                    }
                }

                if(slotTemp == null){
                    iterator.remove();
                    continue;
                }

                if(slotChoice == null) {
                    lessonChoice = assignable;
                    slotChoice = slotTemp;
                }else if(slotOptions.get(slotTemp).size() < slotOptions.get(slotChoice).size()){
                    lessonChoice = assignable;
                    slotChoice = slotTemp;
                }
            }

            if(lessonChoice != null){
                lessonsThree.remove(lessonChoice);

                for(Assignable assignable: lessonsThree){
                    lessonsOne.add(assignable);
                }

                position(lessonChoice, slotChoice, lessonsOne, educatorLessonCount, slotOptions, false);
            }
        }

        refresh();
    }
    public void arrange(){
        arrange(new LinkedList<>(State.getInstance().assignables.values()));
    }
}
