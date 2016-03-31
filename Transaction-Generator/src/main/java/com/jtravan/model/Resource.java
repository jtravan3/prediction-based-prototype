package com.jtravan.model;

/**
 * Created by johnravan on 3/30/16.
 */
public class Resource {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
