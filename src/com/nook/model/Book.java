package com.nook.model;
import java.util.List;

public class Book {
    private int id;
    private String isbn;
    private String title;
    private List<Author> authors;
    private String genre;
    private String description;
    private String coverUrl;
    private String publishedDate;
    private int addedByUserId;
    private String createdAt;

    public Book() {}

    public Book(int id, String isbn, String title, List<Author> authors, String genre, String description, String coverUrl, String publishedDate, int addedByUserId, String createdAt) {
        this.id = id;
        this.isbn = isbn;
        this.title = title;
        this.authors = authors;
        this.genre = genre;
        this.description = description;
        this.coverUrl = coverUrl;
        this.publishedDate = publishedDate;
        this.addedByUserId = addedByUserId;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public List<Author> getAuthors() { return authors; }
    public void setAuthors(List<Author> authors) { this.authors = authors; }

    public String getAuthorsAsString() {
        if (authors == null || authors.isEmpty()) return "Unknown";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < authors.size(); i++) {
            sb.append(authors.get(i).getName());
            if (i < authors.size() - 1) sb.append(", ");
        }
        return sb.toString();
    }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }

    public String getPublishedDate() { return publishedDate; }
    public void setPublishedDate(String publishedDate) { this.publishedDate = publishedDate; }

    public int getAddedByUserId() { return addedByUserId; }
    public void setAddedByUserId(int addedByUserId) { this.addedByUserId = addedByUserId; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}