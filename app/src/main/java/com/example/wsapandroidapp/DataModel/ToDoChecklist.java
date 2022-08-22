package com.example.wsapandroidapp.DataModel;

public class ToDoChecklist {
    private String listText;
    private boolean checked;

    public ToDoChecklist(String listText, boolean checked){
        this.listText = listText;
        this.checked = checked;
    }

    public void setText(String listText){
        this.listText = listText;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getListText(){
        return listText;
    }
}
