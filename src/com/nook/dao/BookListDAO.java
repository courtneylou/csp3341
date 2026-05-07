package com.nook.dao;

import com.nook.model.BookList;
import com.nook.model.BookListItem;
import com.nook.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookListDAO {

    public void createBookList(BookList bookList) throws SQLException {
        String sql = "INSERT INTO book_lists (user_id, name, is_private, list_type) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, bookList.getUserId());
            stmt.setString(2, bookList.getName());
            stmt.setInt(3, bookList.isPrivate() ? 1 : 0);
            stmt.setString(4, bookList.getListType());
            stmt.executeUpdate();
        }
    }

    public BookList getBookListById(int id) throws SQLException {
        String sql = "SELECT * FROM book_lists WHERE id = ?";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return mapBookList(rs);
        }
        return null;
    }

    public List<BookList> getBookListsByUserId(int userId) throws SQLException {
        List<BookList> lists = new ArrayList<>();
        String sql = "SELECT * FROM book_lists WHERE user_id = ?";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) lists.add(mapBookList(rs));
        }
        return lists;
    }

    public List<BookList> getPublicBookListsByUserId(int userId) throws SQLException {
        List<BookList> lists = new ArrayList<>();
        String sql = "SELECT * FROM book_lists WHERE user_id = ? AND is_private = 0";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) lists.add(mapBookList(rs));
        }
        return lists;
    }

    public void updateBookList(BookList bookList) throws SQLException {
        String sql = "UPDATE book_lists SET name = ?, is_private = ?, list_type = ? WHERE id = ?";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setString(1, bookList.getName());
            stmt.setInt(2, bookList.isPrivate() ? 1 : 0);
            stmt.setString(3, bookList.getListType());
            stmt.setInt(4, bookList.getId());
            stmt.executeUpdate();
        }
    }

    public void deleteBookList(int id) throws SQLException {
        String sql = "DELETE FROM book_lists WHERE id = ?";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public void addBookToList(int listId, int bookId) throws SQLException {
        String sql = "INSERT INTO book_list_items (list_id, book_id) VALUES (?, ?)";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, listId);
            stmt.setInt(2, bookId);
            stmt.executeUpdate();
        }
    }

    public void removeBookFromList(int listId, int bookId) throws SQLException {
        String sql = "DELETE FROM book_list_items WHERE list_id = ? AND book_id = ?";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, listId);
            stmt.setInt(2, bookId);
            stmt.executeUpdate();
        }
    }

    public List<BookListItem> getItemsByListId(int listId) throws SQLException {
        List<BookListItem> items = new ArrayList<>();
        String sql = "SELECT * FROM book_list_items WHERE list_id = ?";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, listId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) items.add(mapBookListItem(rs));
        }
        return items;
    }

    private BookList mapBookList(ResultSet rs) throws SQLException {
        return new BookList(
                rs.getInt("id"),
                rs.getInt("user_id"),
                rs.getString("name"),
                rs.getInt("is_private") == 1,
                rs.getString("list_type")
        );
    }

    private BookListItem mapBookListItem(ResultSet rs) throws SQLException {
        return new BookListItem(
                rs.getInt("id"),
                rs.getInt("list_id"),
                rs.getInt("book_id")
        );
    }
}