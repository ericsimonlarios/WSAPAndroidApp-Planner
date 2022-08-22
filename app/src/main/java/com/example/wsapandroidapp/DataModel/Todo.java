package com.example.wsapandroidapp.DataModel;

import java.util.ArrayList;

public class Todo{
    private String listTitle, listDesc;
    private ArrayList<ToDoChecklist> checklist;

    public Todo(String listTitle, String listDesc, ArrayList<ToDoChecklist> checklist){
        this.listTitle = listTitle;
        this.listDesc = listDesc;
        this.checklist = checklist;
    }

    public void setlistTitle(String listTitle){
        this.listTitle = listTitle;
    }

    public void setListDesc(String listDesc){
        this.listDesc = listDesc;
    }

    public void setChecklist(ArrayList<ToDoChecklist> checklist){
        this.checklist = checklist;
    }

    public String getListTitle(){
        return listTitle;
    }

    public String getListDesc(){
        return listDesc;
    }

    public ArrayList<ToDoChecklist> getChecklist(){
        return checklist;
    }


}

