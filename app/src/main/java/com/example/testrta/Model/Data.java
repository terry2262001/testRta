package com.example.testrta.Model;

public class Data {
    private String name ;
    private boolean isSelected = false;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Data(String name) {
        this.name = name;
    }

    public Data() {
    }

//    public void setSelected(boolean selected) {
//        isSelected = selected;
//    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }


//    public boolean isSelected() {
//        return isSelected;
//    }
}
