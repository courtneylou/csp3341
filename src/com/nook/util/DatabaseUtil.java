package com.nook.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseUtil {

    private static final String DB_URL = "jdbc:sqlite:nook.db";
    private static Connection connection = null;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_URL);
        }
        return connection;
    }

    public static void initializeDatabase() {
        try (Statement stmt = getConnection().createStatement()) {

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT NOT NULL UNIQUE,
                    email TEXT NOT NULL UNIQUE,
                    password_hash TEXT NOT NULL,
                    role TEXT NOT NULL DEFAULT 'user',
                    is_banned INTEGER NOT NULL DEFAULT 0,
                    bio TEXT,
                    avatar_url TEXT,
                    created_at TEXT DEFAULT CURRENT_TIMESTAMP
                )""");

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS books (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    isbn TEXT NOT NULL UNIQUE,
                    title TEXT NOT NULL,
                    genre TEXT,
                    description TEXT,
                    cover_url TEXT,
                    published_date TEXT,
                    added_by_user_id INTEGER,
                    created_at TEXT DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (added_by_user_id) REFERENCES users(id)
                )""");

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS authors (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL UNIQUE
                )""");

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS book_authors (
                    book_id INTEGER NOT NULL,
                    author_id INTEGER NOT NULL,
                    PRIMARY KEY (book_id, author_id),
                    FOREIGN KEY (book_id) REFERENCES books(id),
                    FOREIGN KEY (author_id) REFERENCES authors(id)
                )""");

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS reviews (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    book_id INTEGER NOT NULL,
                    user_id INTEGER NOT NULL,
                    rating INTEGER NOT NULL,
                    review_text TEXT,
                    has_spoiler INTEGER NOT NULL DEFAULT 0,
                    likes_count INTEGER NOT NULL DEFAULT 0,
                    created_at TEXT DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (book_id) REFERENCES books(id),
                    FOREIGN KEY (user_id) REFERENCES users(id)
                )""");

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS review_likes (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    review_id INTEGER NOT NULL,
                    user_id INTEGER NOT NULL,
                    FOREIGN KEY (review_id) REFERENCES reviews(id),
                    FOREIGN KEY (user_id) REFERENCES users(id)
                )""");

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS wishlists (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    book_id INTEGER NOT NULL,
                    FOREIGN KEY (user_id) REFERENCES users(id),
                    FOREIGN KEY (book_id) REFERENCES books(id)
                )""");

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS book_lists (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    name TEXT NOT NULL,
                    is_private INTEGER NOT NULL DEFAULT 0,
                    list_type TEXT NOT NULL DEFAULT 'custom',
                    FOREIGN KEY (user_id) REFERENCES users(id)
                )""");

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS book_list_items (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    list_id INTEGER NOT NULL,
                    book_id INTEGER NOT NULL,
                    FOREIGN KEY (list_id) REFERENCES book_lists(id),
                    FOREIGN KEY (book_id) REFERENCES books(id)
                )""");

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS follows (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    follower_id INTEGER NOT NULL,
                    following_id INTEGER NOT NULL,
                    FOREIGN KEY (follower_id) REFERENCES users(id),
                    FOREIGN KEY (following_id) REFERENCES users(id)
                )""");

            System.out.println("Database initialised successfully.");

        } catch (SQLException e) {
            System.out.println("Database initialisation failed: " + e.getMessage());
        }
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.out.println("Failed to close connection: " + e.getMessage());
        }
    }
}