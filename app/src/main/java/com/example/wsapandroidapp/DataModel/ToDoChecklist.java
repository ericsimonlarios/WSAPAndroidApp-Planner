package com.example.wsapandroidapp.DataModel;

public class ToDoChecklist {
    private String listText;
    private boolean checked, titleChecked;
    private String listKey, getListKey;

    public ToDoChecklist(String listText, boolean checked, String listKey, String getListKey){
        this.listText = listText;
        this.checked = checked;
        this.listKey = listKey;
        this.getListKey = getListKey;
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

    public String getGetListKey() {
        return getListKey;
    }

    public void setGetListKey(String getListKey) {
        this.getListKey = getListKey;
    }
}
