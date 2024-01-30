package com.example.demo.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;

import java.net.URL;
import java.util.ResourceBundle;

public class GradeDialogController implements Initializable {
    @FXML
    private ComboBox<String> comboSubGrade;
    private ObservableList<String> subGrades = FXCollections.observableArrayList("A B C D E F G H I J K L M N O P Q R S T U V W X Y Z".split(" "));

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        comboSubGrade.setItems(subGrades);
    }
}
