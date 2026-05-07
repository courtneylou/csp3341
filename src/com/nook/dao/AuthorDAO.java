package com.nook.dao;

import com.nook.model.Author;
import com.nook.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuthorDAO {

    public Author getOrCreateAuthor(String name) throws SQLException {
        String selectSql = "SELECT * FROM authors WHERE name = ?";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(selectSql)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Author(rs.getInt("id"), rs.getString("name"));
            }
        }

        String insertSql = "INSERT INTO authors (name) VALUES (?)";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, name);
            stmt.executeUpdate();
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                return new Author(keys.getInt(1), name);
            }
        }

        throw new SQLException("Failed to create author: " + name);
    }

    public void linkAuthorToBook(int bookId, int authorId) throws SQLException {
        String sql = "INSERT OR IGNORE INTO book_authors (book_id, author_id) VALUES (?, ?)";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, bookId);
            stmt.setInt(2, authorId);
            stmt.executeUpdate();
        }
    }

    public void unlinkAuthorsFromBook(int bookId) throws SQLException {
        String sql = "DELETE FROM book_authors WHERE book_id = ?";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, bookId);
            stmt.executeUpdate();
        }
    }

    public List<Author> getAuthorsByBookId(int bookId) throws SQLException {
        List<Author> authors = new ArrayList<>();
        String sql = """
                SELECT a.id, a.name FROM authors a
                JOIN book_authors ba ON a.id = ba.author_id
                WHERE ba.book_id = ?
                """;
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, bookId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                authors.add(new Author(rs.getInt("id"), rs.getString("name")));
            }
        }
        return authors;
    }

    public List<Author> getAllAuthors() throws SQLException {
        List<Author> authors = new ArrayList<>();
        String sql = "SELECT * FROM authors ORDER BY name";
        try (Statement stmt = DatabaseUtil.getConnection().createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                authors.add(new Author(rs.getInt("id"), rs.getString("name")));
            }
        }
        return authors;
    }
}