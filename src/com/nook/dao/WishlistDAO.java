package com.nook.dao;

import com.nook.model.Wishlist;
import com.nook.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WishlistDAO {

    public void addToWishlist(int userId, int bookId) throws SQLException {
        String sql = "INSERT INTO wishlists (user_id, book_id) VALUES (?, ?)";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, bookId);
            stmt.executeUpdate();
        }
    }

    public void removeFromWishlist(int userId, int bookId) throws SQLException {
        String sql = "DELETE FROM wishlists WHERE user_id = ? AND book_id = ?";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, bookId);
            stmt.executeUpdate();
        }
    }

    public List<Wishlist> getWishlistByUserId(int userId) throws SQLException {
        List<Wishlist> wishlist = new ArrayList<>();
        String sql = "SELECT * FROM wishlists WHERE user_id = ?";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) wishlist.add(mapWishlist(rs));
        }
        return wishlist;
    }

    public boolean isBookInWishlist(int userId, int bookId) throws SQLException {
        String sql = "SELECT id FROM wishlists WHERE user_id = ? AND book_id = ?";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, bookId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }

    private Wishlist mapWishlist(ResultSet rs) throws SQLException {
        return new Wishlist(
                rs.getInt("id"),
                rs.getInt("user_id"),
                rs.getInt("book_id")
        );
    }
}