package com.example.demo.services;

public class HighlightingTableCell<S, T> extends javafx.scene.control.cell.TextFieldTableCell<S, T> {
    private static HighlightingTableCell<?, ?> lastSelectedCell;

    public HighlightingTableCell() {
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