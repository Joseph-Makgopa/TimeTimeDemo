package com.example.demo.services;

import com.example.demo.models.Grade;
import com.example.demo.models.Rank;
import com.example.demo.models.State;
import com.example.demo.models.WeekDay;
import com.example.demo.utilities.Triplet;
import com.example.demo.utilities.TripletManager;

public class ClickableTableCell<S, T> extends javafx.scene.control.cell.TextFieldTableCell<S, T> {
    private Boolean educatorCell = false;
    private Boolean deadCell = false;
    public static ClickableTableCell<?, ?> lastSelectedCell;

    public ClickableTableCell() {
        getTableRow();

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

    public ClickableTableCell(Boolean educatorCell) {
        this.educatorCell = educatorCell;
        setOnMouseClicked(event -> {
                if (!isEmpty()) {
                    if (getTableView() != null && getTableRow() != null && getTableView().getSelectionModel() != null) {
                        if (lastSelectedCell != null) {
                            lastSelectedCell.setStyle("");
                        }

                        getTableView().getSelectionModel().select(getTableRow().getIndex());

                        if (lastSelectedCell == this) {
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
                setStyle("");
            }
        }
    }
}