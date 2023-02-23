package com.example.testrta.Model;

public class Data {
    private String name ;
    private boolean isSelected = false;
    private String path;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Data(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public Data(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public void setPath(String path) {
        this.path = path;
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
