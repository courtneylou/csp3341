package com.nook.service;

import com.nook.dao.BookDAO;
import com.nook.model.Author;
import com.nook.model.Book;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookService {

    private final BookDAO bookDAO = new BookDAO();

    public Book addBook(String isbn, String title, List<String> authorNames, String genre, String description, String coverUrl, String publishedDate, int addedByUserId) throws SQLException {
        Book existing = bookDAO.getBookByIsbn(isbn);
        if (existing != null) {
            throw new IllegalArgumentException("DUPLICATE:" + existing.getId());
        }

        List<Author> authors = new ArrayList<>();
        for (String name : authorNames) {
            if (!name.trim().isEmpty()) {
                authors.add(new Author(0, name.trim()));
            }
        }

        Book newBook = new Book();
        newBook.setIsbn(isbn);
        newBook.setTitle(title);
        newBook.setAuthors(authors);
        newBook.setGenre(genre);
        newBook.setDescription(description);
        newBook.setCoverUrl(coverUrl);
        newBook.setPublishedDate(publishedDate);
        newBook.setAddedByUserId(addedByUserId);

        bookDAO.createBook(newBook);
        return bookDAO.getBookByIsbn(isbn);
    }

    public Book getBookById(int id) throws SQLException {
        return bookDAO.getBookById(id);
    }

    public Book getBookByIsbn(String isbn) throws SQLException {
        return bookDAO.getBookByIsbn(isbn);
    }

    public List<Book> getAllBooks() throws SQLException {
        return bookDAO.getAllBooks();
    }

    public List<Book> getRecentBooks(int limit) throws SQLException {
        return bookDAO.getRecentBooks(limit);
    }

    public List<Book> searchBooks(String query) throws SQLException {
        return bookDAO.searchBooks(query);
    }

    public void updateBook(Book book, String title, List<String> authorNames, String genre, String description, String coverUrl, String publishedDate) throws SQLException {
        List<Author> authors = new ArrayList<>();
        for (String name : authorNames) {
            if (!name.trim().isEmpty()) {
                authors.add(new Author(0, name.trim()));
            }
        }

        book.setTitle(title);
        book.setAuthors(authors);
        book.setGenre(genre);
        book.setDescription(description);
        book.setCoverUrl(coverUrl);
        book.setPublishedDate(publishedDate);
        bookDAO.updateBook(book);
    }

    public void deleteBook(int id) throws SQLException {
        bookDAO.deleteBook(id);
    }
}