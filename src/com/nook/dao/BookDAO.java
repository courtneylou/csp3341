package com.nook.dao;

import com.nook.model.Author;
import com.nook.model.Book;
import com.nook.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO {

    private final AuthorDAO authorDAO = new AuthorDAO();

    public void createBook(Book book) throws SQLException {
        String sql = "INSERT INTO books (isbn, title, genre, description, cover_url, published_date, added_by_user_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, book.getIsbn());
            stmt.setString(2, book.getTitle());
            stmt.setString(3, book.getGenre());
            stmt.setString(4, book.getDescription());
            stmt.setString(5, book.getCoverUrl());
            stmt.setString(6, book.getPublishedDate());
            stmt.setInt(7, book.getAddedByUserId());
            stmt.executeUpdate();

            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                int bookId = keys.getInt(1);
                book.setId(bookId);
                linkAuthors(bookId, book.getAuthors());
            }
        }
    }

    private void linkAuthors(int bookId, List<Author> authors) throws SQLException {
        if (authors == null) return;
        for (Author author : authors) {
            Author saved = authorDAO.getOrCreateAuthor(author.getName());
            authorDAO.linkAuthorToBook(bookId, saved.getId());
        }
    }

    public Book getBookById(int id) throws SQLException {
        String sql = "SELECT * FROM books WHERE id = ?";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapBook(rs);
        }
        return null;
    }

    public Book getBookByIsbn(String isbn) throws SQLException {
        String sql = "SELECT * FROM books WHERE isbn = ?";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setString(1, isbn);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapBook(rs);
        }
        return null;
    }

    public List<Book> getAllBooks() throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books ORDER BY created_at DESC";
        try (Statement stmt = DatabaseUtil.getConnection().createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) books.add(mapBook(rs));
        }
        return books;
    }

    public List<Book> getRecentBooks(int limit) throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books ORDER BY created_at DESC LIMIT ?";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) books.add(mapBook(rs));
        }
        return books;
    }

    public List<Book> searchBooks(String query) throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = """
                SELECT DISTINCT b.* FROM books b
                LEFT JOIN book_authors ba ON b.id = ba.book_id
                LEFT JOIN authors a ON ba.author_id = a.id
                WHERE b.title LIKE ? OR a.name LIKE ? OR b.isbn LIKE ?
                """;
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            String search = "%" + query + "%";
            stmt.setString(1, search);
            stmt.setString(2, search);
            stmt.setString(3, search);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) books.add(mapBook(rs));
        }
        return books;
    }

    public void updateBook(Book book) throws SQLException {
        String sql = "UPDATE books SET isbn = ?, title = ?, genre = ?, description = ?, cover_url = ?, published_date = ? WHERE id = ?";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setString(1, book.getIsbn());
            stmt.setString(2, book.getTitle());
            stmt.setString(3, book.getGenre());
            stmt.setString(4, book.getDescription());
            stmt.setString(5, book.getCoverUrl());
            stmt.setString(6, book.getPublishedDate());
            stmt.setInt(7, book.getId());
            stmt.executeUpdate();
        }

        authorDAO.unlinkAuthorsFromBook(book.getId());
        linkAuthors(book.getId(), book.getAuthors());
    }

    public void deleteBook(int id) throws SQLException {
        authorDAO.unlinkAuthorsFromBook(id);
        String sql = "DELETE FROM books WHERE id = ?";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private Book mapBook(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        List<Author> authors = authorDAO.getAuthorsByBookId(id);
        return new Book(
                id,
                rs.getString("isbn"),
                rs.getString("title"),
                authors,
                rs.getString("genre"),
                rs.getString("description"),
                rs.getString("cover_url"),
                rs.getString("published_date"),
                rs.getInt("added_by_user_id"),
                rs.getString("created_at")
        );
    }
}