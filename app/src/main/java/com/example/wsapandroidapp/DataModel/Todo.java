package com.example.wsapandroidapp.DataModel;

import java.util.ArrayList;
import java.util.List;

public class Todo{
    private String uid, listTitle, dateCreated, titleKey;
    private List<ToDoChecklist> checklist = new ArrayList<>();
    private boolean isChecked;
    public Todo(String listTitle, String dateCreated, String uid, boolean isChecked){
        this.listTitle = listTitle;
        this.dateCreated = dateCreated;
        this.uid = uid;
        this.isChecked = isChecked;
    }

    public Todo(String listTitle, String dateCreated, String uid, String titleKey, boolean isChecked){
        this.listTitle = listTitle;
        this.dateCreated = dateCreated;
        this.uid = uid;
        this.titleKey=titleKey;
        this.isChecked = isChecked;
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

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}

