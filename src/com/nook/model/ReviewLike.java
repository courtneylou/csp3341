package com.nook.model;

public class ReviewLike {
    private int id;
    private int reviewId;
    private int userId;

    public ReviewLike() {}

    public ReviewLike(int id, int reviewId, int userId) {
        this.id = id;
        this.reviewId = reviewId;
        this.userId = userId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getReviewId() { return reviewId; }
    public void setReviewId(int reviewId) { this.reviewId = reviewId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
}