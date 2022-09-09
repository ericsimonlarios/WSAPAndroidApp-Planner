package com.example.wsapandroidapp.DataModel;

public class ToDoChecklist {
    private String listText;
    private boolean checked, titleChecked;
    private String listKey;

    public ToDoChecklist(String listText, boolean checked, String listKey, boolean titleChecked){
        this.listText = listText;
        this.checked = checked;
        this.listKey = listKey;
        this.titleChecked = titleChecked;
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

    public String getListKey() {
        return listKey;
    }

    public boolean isTitleChecked() {
        return titleChecked;
    }

    public void setTitleChecked(boolean titleChecked) {
        this.titleChecked = titleChecked;
    }

    public void setListKey(String listKey) {
        this.listKey = listKey;
    }
}
