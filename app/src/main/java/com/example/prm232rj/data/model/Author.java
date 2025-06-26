package com.example.prm232rj.data.model;

public class Author {
    private String id;
    private  String name;
    private  String description;

    public Author() {

    }

    public Author(String description, String name, String id) {
        this.description = description;
        this.name = name;
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
