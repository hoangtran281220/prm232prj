package com.example.prm232rj.data.model;

public class Tag {
    private String id;
    private String Name;
    private String Description;

    public Tag() {
    }

    public Tag(String id, String description, String name) {
        this.id = id;
        Description = description;
        Name = name;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
