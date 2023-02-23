package com.example.testrta.Model;

import java.io.Serializable;

public class Data implements Serializable {
    private String name ;
    private boolean isSelected = false;
    private String path;
    private String instanceid ;


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

    public String getInstanceid() {
        return instanceid;
    }

    public void setInstanceid(String instanceid) {
        this.instanceid = instanceid;
    }



    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }


    public void setPath(String path) {
        this.path = path;
    }

    public Data() {
    }
    public Data(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public Data( String instanceid,String path,String name ) {
        this.instanceid = instanceid;
        this.path = path;
        this.name = name;

    }
}
