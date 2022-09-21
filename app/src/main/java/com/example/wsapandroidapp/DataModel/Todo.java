package com.example.wsapandroidapp.DataModel;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Todo{
    private String uid, listTitle, dateCreated, titleKey;
    private List<ToDoChecklist> checklist = new ArrayList<>();
    private boolean checked;
    Date date;
    public Todo(String listTitle, String dateCreated, String uid, String titleKey, boolean checked){
        this.listTitle = listTitle;
        this.dateCreated = dateCreated;
        this.uid = uid;
        this.titleKey = titleKey;
        this.checked = checked;
    }

    public Todo( List<ToDoChecklist> checklist){
        this.checklist = checklist;
    }

//    public Todo(String listTitle, String dateCreated, String uid, String titleKey, boolean isChecked){
//        this.listTitle = listTitle;
//        this.dateCreated = dateCreated;
//        this.uid = uid;
//        this.titleKey=titleKey;
//        this.isChecked = isChecked;
//    }


    public Todo(){

    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
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

    public Date getDateFormat() throws ParseException {
        DateFormat formatDate = new SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH);
        return date = formatDate.parse(getDateCreated());
    }

}

