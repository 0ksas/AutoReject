package com.example.rework;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class SaveClass implements Serializable {
    List<String> numberList = new LinkedList<String>();
    List<String> greenList = new LinkedList<String>();

    Boolean isBlackListActive = false;
    Boolean isGreenListActive = false;

    public void addItem(String item) {
        if(! numberList.contains(item)) numberList.add(item);
    }
    public void addGreenList(String item){if(! greenList.contains(item)) greenList.add(item);}
}
