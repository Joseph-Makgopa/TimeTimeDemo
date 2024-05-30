package com.example.demo.models.commands;

import com.example.demo.controllers.DemoController;
import com.example.demo.services.DemoService;
import com.example.demo.services.GradeViewService;
import com.example.demo.services.WeekDayViewService;
import com.example.demo.utilities.Job;

public class SwitchViewCommand implements Command{
    DemoService oldService, freshService;
    DemoController demoController;
    public SwitchViewCommand(DemoController demoController, DemoService service){
        this.demoController = demoController;
        oldService = demoController.getService();
        freshService = service;
    }
    @Override
    public String executeDescription() {
        if(freshService instanceof WeekDayViewService)
            return "  switching to weekdays view.";
        else if(freshService instanceof GradeViewService)
            return "  switching to grades view.";

        return "  switching to educators view.";
    }
    @Override
    public String reverseDescription() {
        if(oldService instanceof WeekDayViewService)
            return "  switching to weekdays view.";
        else if(oldService instanceof GradeViewService)
            return "  switching to grades view.";

        return "  switching to educators view.";
    }
    @Override
    public Boolean dataRefresh() {
        return false;
    }
    @Override
    public Boolean threadSafe(){
        return false;
    }
    @Override
    public void execute(Job job) {
        if(freshService instanceof WeekDayViewService){
            job.progress(0,4);

            demoController.getMenuWeekDays().setSelected(true);
            demoController.getMenuGrades().setSelected(false);
            demoController.getMenuEducators().setSelected(false);

            job.progress(1,4);

            demoController.getMenuWeekDays().setDisable(true);
            demoController.getMenuGrades().setDisable(false);
            demoController.getMenuEducators().setDisable(false);

            job.progress(2,4);

            demoController.getClearTab().setText("Clear Day");
            demoController.getClearRow().setText("Clear Grade");

            job.progress(3,4);

            demoController.setService(freshService);

            job.progress(4,4);
        }else if(freshService instanceof GradeViewService){
            job.progress(0,4);

            demoController.getMenuWeekDays().setSelected(false);
            demoController.getMenuGrades().setSelected(true);
            demoController.getMenuEducators().setSelected(false);

            job.progress(1,4);

            demoController.getMenuWeekDays().setDisable(false);
            demoController.getMenuGrades().setDisable(true);
            demoController.getMenuEducators().setDisable(false);

            job.progress(2,4);

            demoController.getClearTab().setText("Clear Grade");
            demoController.getClearRow().setText("Clear Day");

            job.progress(3,4);

            demoController.setService(freshService);

            job.progress(4,4);
        }else{
            job.progress(0,4);

            demoController.getMenuWeekDays().setSelected(false);
            demoController.getMenuGrades().setSelected(false);
            demoController.getMenuEducators().setSelected(true);

            job.progress(1,4);

            demoController.getMenuWeekDays().setDisable(false);
            demoController.getMenuGrades().setDisable(false);
            demoController.getMenuEducators().setDisable(true);

            job.progress(2,4);

            demoController.getClearTab().setText("Clear Educator");
            demoController.getClearRow().setText("Clear Day");

            job.progress(3,4);

            demoController.setService(freshService);

            job.progress(4,4);
        }

        demoController.updateFilterOptions();
    }
    @Override
    public void reverse(Job job) {
        if(oldService instanceof WeekDayViewService){
            job.progress(0,4);

            demoController.getMenuWeekDays().setSelected(true);
            demoController.getMenuGrades().setSelected(false);
            demoController.getMenuEducators().setSelected(false);

            job.progress(1,4);

            demoController.getMenuWeekDays().setDisable(true);
            demoController.getMenuGrades().setDisable(false);
            demoController.getMenuEducators().setDisable(false);

            job.progress(2,4);

            demoController.getClearTab().setText("Clear Day");
            demoController.getClearRow().setText("Clear Grade");

            job.progress(3,4);

            demoController.setService(oldService);

            job.progress(4,4);
        }else if(oldService instanceof GradeViewService){
            job.progress(0,4);

            demoController.getMenuWeekDays().setSelected(false);
            demoController.getMenuGrades().setSelected(true);
            demoController.getMenuEducators().setSelected(false);

            job.progress(1,4);

            demoController.getMenuWeekDays().setDisable(false);
            demoController.getMenuGrades().setDisable(true);
            demoController.getMenuEducators().setDisable(false);

            job.progress(2,4);

            demoController.getClearTab().setText("Clear Grade");
            demoController.getClearRow().setText("Clear Day");

            job.progress(3,4);

            demoController.setService(oldService);

            job.progress(4,4);
        }else{
            job.progress(0,4);

            demoController.getMenuWeekDays().setSelected(false);
            demoController.getMenuGrades().setSelected(false);
            demoController.getMenuEducators().setSelected(true);

            job.progress(1,4);

            demoController.getMenuWeekDays().setDisable(false);
            demoController.getMenuGrades().setDisable(false);
            demoController.getMenuEducators().setDisable(true);

            job.progress(2,4);

            demoController.getClearTab().setText("Clear Educator");
            demoController.getClearRow().setText("Clear Day");

            job.progress(3,4);

            demoController.setService(oldService);

            job.progress(4,4);
        }

        demoController.updateFilterOptions();
    }
}
