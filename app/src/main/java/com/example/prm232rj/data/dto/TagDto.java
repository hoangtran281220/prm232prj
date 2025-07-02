package com.example.prm232rj.data.dto;

public class TagDto {
    private String id;
    private String Name;

    public TagDto(String id, String name) {
        this.id = id;
        Name = name;
    }

    public TagDto() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}
