package com.example.demo.utilities;

import com.example.demo.controllers.DemoController;
import com.example.demo.models.*;
import javafx.beans.property.SimpleBooleanProperty;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class ClickableTableCell<S, T> extends javafx.scene.control.cell.TextFieldTableCell<S, T> {
    public static ClickableTableCell<?, ?> lastSelectedCell;
    public static final Map<Educator, LinkedList<ClickableTableCell>> instances = new HashMap<>();
    public Boolean deadCell = false;
    private WeekDay day = null;
    public ClickableTableCell() {
        setOnMouseClicked(event -> {
            if (!isEmpty()) {
                if (getTableView() != null && getTableRow() != null && getTableView().getSelectionModel() != null) {
                    if (lastSelectedCell != null) {
                        lastSelectedCell.setStyle("");
                    }

                    getTableView().getSelectionModel().select(getTableRow().getIndex());

                    if(lastSelectedCell == this){
                        lastSelectedCell = null;
                        event.consume();
                        return;
                    }

                    lastSelectedCell = this;
                    setStyle("-fx-background-color: white;-fx-text-fill: black;");
                    event.consume();
                }
            }
        });
    }
    public ClickableTableCell(Educator educator) {
        LinkedList<ClickableTableCell> cells = instances.get(educator);

        if(cells == null){
            cells = new LinkedList<>();
            cells.add(this);

            instances.put(educator, cells);
        }else
            cells.add(this);

        setOnMouseClicked(event -> {
            if (!isEmpty()) {
                if (getTableView() != null && getTableRow() != null && getTableView().getSelectionModel() != null) {
                    if(lastSelectedCell != null) {
                        if(lastSelectedCell.getDeadCell()){
                            lastSelectedCell.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);-fx-text-fill: rgba(0, 0, 0, 0.5);");
                        }else
                            lastSelectedCell.setStyle("");
                    }

                    if(lastSelectedCell == this){
                        lastSelectedCell = null;
                        event.consume();
                        return;
                    }

                    lastSelectedCell = this;
                    setStyle("-fx-background-color: white;-fx-text-fill: black;");

                    event.consume();
                }
            }
        });
    }
    public ClickableTableCell(WeekDay day) {
        this.day = day;

        setOnMouseClicked(event -> {
            if (!isEmpty()) {
                if (getTableView() != null && getTableRow() != null && getTableView().getSelectionModel() != null) {
                    if (lastSelectedCell != null) {
                        if(isClash()){
                            lastSelectedCell.setStyle("-fx-background-color: rgba(1, 0, 0, 0.5);-fx-text-fill: black;");
                        }else
                            lastSelectedCell.setStyle("");
                    }

                    getTableView().getSelectionModel().select(getTableRow().getIndex());

                    if(lastSelectedCell == this){
                        lastSelectedCell = null;
                        event.consume();
                        return;
                    }

                    lastSelectedCell = this;
                    setStyle("-fx-background-color: white;-fx-text-fill: black;");
                    event.consume();
                }
            }
        });
    }
    @Override
    public void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || isSelected()) {
            if (this == lastSelectedCell) {
                setStyle("-fx-background-color: white;-fx-text-fill: black;");
            } else {
                if(deadCell)
                    setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);-fx-text-fill: rgba(0, 0, 0, 0.5);");
                else if(isClash())
                    setStyle("-fx-background-color: rgba(1, 0, 0, 0.5);-fx-text-fill: black;");
                else
                    setStyle("");
            }
        }
    }
    public void setDeadCell(Boolean deadCell){
        this.deadCell = deadCell;
    }
    public Boolean getDeadCell(){
        return deadCell;
    }
    public Boolean isClash(){
        if(day != null){
            if(getItem() != null){
                Rank<Grade> rank = (Rank<Grade>) getTableView().getItems().get(getTableRow().getIndex());
                Integer period = Integer.parseInt(getTableColumn().getText()) - 1;

                return State.getInstance().clashes.contains(TripletManager.get(day, rank.getHeader(), period));
            }
        }

        return false;
    }
}