package com.example.wsapandroidapp.DataModel;

import java.util.ArrayList;
import java.util.List;

public class Todo{
    private String uid, listTitle, dateCreated, titleKey;
    private List<ToDoChecklist> checklist = new ArrayList<>();

    public Todo(String listTitle, String dateCreated, String uid){
        this.listTitle = listTitle;
        this.dateCreated = dateCreated;
        this.uid = uid;
    }

    public Todo(String listTitle, String dateCreated, String uid, String titleKey){
        this.listTitle = listTitle;
        this.dateCreated = dateCreated;
        this.uid = uid;
        this.titleKey=titleKey;
    }

    public Todo(){

    }

    public void setTitleKey(String titleKey){
        this.titleKey = titleKey;
    }

    public String getTitleKey(){
        return titleKey;
    }
    public void setListTitle(String listTitle) {
        this.listTitle = listTitle;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Todo(ArrayList<ToDoChecklist> checklist){
        this.checklist = checklist;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setChecklist(ArrayList<ToDoChecklist> checklist){
        this.checklist = checklist;
    }

    public String getListTitle(){
        return listTitle;
    }

    public List<ToDoChecklist> getChecklist() {
        return checklist;
    }
}

