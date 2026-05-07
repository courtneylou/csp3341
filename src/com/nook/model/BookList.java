package com.nook.model;

public class BookList {
    private int id;
    private int userId;
    private String name;
    private boolean isPrivate;
    private String listType;

    public BookList() {}

    public BookList(int id, int userId, String name, boolean isPrivate, String listType) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.isPrivate = isPrivate;
        this.listType = listType;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public boolean isPrivate() { return isPrivate; }
    public void setPrivate(boolean isPrivate) { this.isPrivate = isPrivate; }

    public String getListType() { return listType; }
    public void setListType(String listType) { this.listType = listType; }
}