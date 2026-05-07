package com.nook.dao;

import com.nook.model.Review;
import com.nook.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReviewDAO {

    public void createReview(Review review) throws SQLException {
        String sql = "INSERT INTO reviews (book_id, user_id, rating, review_text, has_spoiler) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, review.getBookId());
            stmt.setInt(2, review.getUserId());
            stmt.setInt(3, review.getRating());
            stmt.setString(4, review.getReviewText());
            stmt.setInt(5, review.isHasSpoiler() ? 1 : 0);
            stmt.executeUpdate();
        }
    }

    public List<Review> getReviewsByBookId(int bookId) throws SQLException {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT * FROM reviews WHERE book_id = ? ORDER BY created_at DESC";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, bookId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) reviews.add(mapReview(rs));
        }
        return reviews;
    }

    public List<Review> getReviewsByUserId(int userId) throws SQLException {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT * FROM reviews WHERE user_id = ? ORDER BY created_at DESC";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) reviews.add(mapReview(rs));
        }
        return reviews;
    }

    public Review getReviewById(int id) throws SQLException {
        String sql = "SELECT * FROM reviews WHERE id = ?";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapReview(rs);
        }
        return null;
    }

    public void updateReview(Review review) throws SQLException {
        String sql = "UPDATE reviews SET rating = ?, review_text = ?, has_spoiler = ? WHERE id = ?";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, review.getRating());
            stmt.setString(2, review.getReviewText());
            stmt.setInt(3, review.isHasSpoiler() ? 1 : 0);
            stmt.setInt(4, review.getId());
            stmt.executeUpdate();
        }
    }

    public void deleteReview(int id) throws SQLException {
        String sql = "DELETE FROM reviews WHERE id = ?";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public void incrementLikes(int reviewId) throws SQLException {
        String sql = "UPDATE reviews SET likes_count = likes_count + 1 WHERE id = ?";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, reviewId);
            stmt.executeUpdate();
        }
    }

    public void decrementLikes(int reviewId) throws SQLException {
        String sql = "UPDATE reviews SET likes_count = likes_count - 1 WHERE id = ?";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, reviewId);
            stmt.executeUpdate();
        }
    }

    public boolean hasUserReviewedBook(int userId, int bookId) throws SQLException {
        String sql = "SELECT id FROM reviews WHERE user_id = ? AND book_id = ?";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, bookId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }

    private Review mapReview(ResultSet rs) throws SQLException {
        return new Review(
                rs.getInt("id"),
                rs.getInt("book_id"),
                rs.getInt("user_id"),
                rs.getInt("rating"),
                rs.getString("review_text"),
                rs.getInt("has_spoiler") == 1,
                rs.getInt("likes_count"),
                rs.getString("created_at")
        );
    }

    public boolean hasUserLikedReview(int userId, int reviewId) throws SQLException {
        String sql = "SELECT id FROM review_likes WHERE user_id = ? AND review_id = ?";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, reviewId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }

    public void addReviewLike(int userId, int reviewId) throws SQLException {
        String sql = "INSERT INTO review_likes (user_id, review_id) VALUES (?, ?)";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, reviewId);
            stmt.executeUpdate();
        }
    }
}