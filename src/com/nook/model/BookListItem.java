package com.nook.model;

public class BookListItem {
    private int id;
    private int listId;
    private int bookId;

    public BookListItem() {}

    public BookListItem(int id, int listId, int bookId) {
        this.id = id;
        this.listId = listId;
        this.bookId = bookId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getListId() { return listId; }
    public void setListId(int listId) { this.listId = listId; }

    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }
}