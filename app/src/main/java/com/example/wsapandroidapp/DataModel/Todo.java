package com.example.wsapandroidapp.DataModel;

import java.util.ArrayList;

public class Todo{
    private String listTitle, listDate;
    private ArrayList<ToDoChecklist> checklist;

    public Todo(String listTitle, String listDate){
        this.listTitle = listTitle;
        this.listDate = listDate;

    }

    public Todo(ArrayList<ToDoChecklist> checklist){
        this.checklist = checklist;
    }

    public void setlistTitle(String listTitle){
        this.listTitle = listTitle;
    }

    public void setlistDate(String listDate){
        this.listDate = listDate;
    }

    public void setChecklist(ArrayList<ToDoChecklist> checklist){
        this.checklist = checklist;
    }

    public String getListTitle(){
        return listTitle;
    }

    public String getlistDate(){
        return listDate;
    }

    public ArrayList<ToDoChecklist> getChecklist(){
        return checklist;
    }


}

