package com.example.demo.utilities;

import javafx.scene.control.Alert;

public class Notification {
    private Alert alert;
    public Notification(String title, String header, String content, Alert.AlertType type){
        alert = new Alert(type);
        alert.setHeaderText(header);
        alert.setTitle(title);
        alert.setContentText(content);
    }
    public Notification(String title, String content, Alert.AlertType type){
        alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setTitle(title);
        alert.setContentText(content);
    }
    public void show(){
        alert.showAndWait();
    }
    public static void show(String title, String content, Alert.AlertType type){
        Notification notification = new Notification(title, content, type);
        notification.show();
    }
    public static void show(String title, String header, String content, Alert.AlertType type){
        Notification notification = new Notification(title, header, content, type);
        notification.show();
    }
}
