package com.nook.model;

public class Review {
    private int id;
    private int bookId;
    private int userId;
    private int rating;
    private String reviewText;
    private boolean hasSpoiler;
    private int likesCount;
    private String createdAt;

    public Review() {}

    public Review(int id, int bookId, int userId, int rating, String reviewText, boolean hasSpoiler, int likesCount, String createdAt) {
        this.id = id;
        this.bookId = bookId;
        this.userId = userId;
        this.rating = rating;
        this.reviewText = reviewText;
        this.hasSpoiler = hasSpoiler;
        this.likesCount = likesCount;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getReviewText() { return reviewText; }
    public void setReviewText(String reviewText) { this.reviewText = reviewText; }

    public boolean isHasSpoiler() { return hasSpoiler; }
    public void setHasSpoiler(boolean hasSpoiler) { this.hasSpoiler = hasSpoiler; }

    public int getLikesCount() { return likesCount; }
    public void setLikesCount(int likesCount) { this.likesCount = likesCount; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}