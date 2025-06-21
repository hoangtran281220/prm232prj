package com.example.prm232rj.data.model;

public class Tag {
    private String id;
    private String name;
    private String description;

    public Tag(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public Tag() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
