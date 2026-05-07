package com.nook.service;

import com.nook.dao.ReviewDAO;
import com.nook.dao.UserDAO;
import com.nook.model.Review;
import com.nook.model.User;

import java.sql.SQLException;
import java.util.List;

public class ReviewService {

    private final ReviewDAO reviewDAO = new ReviewDAO();
    private final UserDAO userDAO = new UserDAO();

    public void addReview(int bookId, int userId, int rating, String reviewText, boolean hasSpoiler) throws SQLException {
        if (reviewDAO.hasUserReviewedBook(userId, bookId)) {
            throw new IllegalArgumentException("You have already reviewed this book.");
        }

        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5.");
        }

        Review review = new Review();
        review.setBookId(bookId);
        review.setUserId(userId);
        review.setRating(rating);
        review.setReviewText(reviewText);
        review.setHasSpoiler(hasSpoiler);

        reviewDAO.createReview(review);
    }

    public List<Review> getReviewsForBook(int bookId) throws SQLException {
        return reviewDAO.getReviewsByBookId(bookId);
    }

    public List<Review> getReviewsByUser(int userId) throws SQLException {
        return reviewDAO.getReviewsByUserId(userId);
    }

    public void deleteReview(int reviewId) throws SQLException {
        reviewDAO.deleteReview(reviewId);
    }

    public void likeReview(int reviewId) throws SQLException {
        reviewDAO.incrementLikes(reviewId);
    }

    public void unlikeReview(int reviewId) throws SQLException {
        reviewDAO.decrementLikes(reviewId);
    }

    public String getReviewerUsername(int userId) throws SQLException {
        User user = userDAO.getUserById(userId);
        return user != null ? user.getUsername() : "Unknown";
    }

    public boolean hasUserLikedReview(int userId, int reviewId) throws SQLException {
        return reviewDAO.hasUserLikedReview(userId, reviewId);
    }

    public void addReviewLike(int userId, int reviewId) throws SQLException {
        reviewDAO.addReviewLike(userId, reviewId);
    }
}