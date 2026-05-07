package com.nook.service;

import com.nook.dao.BookListDAO;
import com.nook.dao.WishlistDAO;
import com.nook.model.BookList;
import com.nook.model.BookListItem;
import com.nook.model.Wishlist;

import java.sql.SQLException;
import java.util.List;

public class BookListService {

    private final BookListDAO bookListDAO = new BookListDAO();
    private final WishlistDAO wishlistDAO = new WishlistDAO();

    public void createBookList(int userId, String name, boolean isPrivate, String listType) throws SQLException {
        BookList bookList = new BookList();
        bookList.setUserId(userId);
        bookList.setName(name);
        bookList.setPrivate(isPrivate);
        bookList.setListType(listType);
        bookListDAO.createBookList(bookList);
    }

    public List<BookList> getUserBookLists(int userId) throws SQLException {
        return bookListDAO.getBookListsByUserId(userId);
    }

    public List<BookList> getPublicBookLists(int userId) throws SQLException {
        return bookListDAO.getPublicBookListsByUserId(userId);
    }

    public BookList getBookListById(int id) throws SQLException {
        return bookListDAO.getBookListById(id);
    }

    public void updateBookList(int listId, String name, boolean isPrivate) throws SQLException {
        BookList bookList = bookListDAO.getBookListById(listId);
        if (bookList == null) {
            throw new IllegalArgumentException("Book list not found.");
        }
        bookList.setName(name);
        bookList.setPrivate(isPrivate);
        bookListDAO.updateBookList(bookList);
    }

    public void deleteBookList(int listId) throws SQLException {
        bookListDAO.deleteBookList(listId);
    }

    public void addBookToList(int listId, int bookId) throws SQLException {
        bookListDAO.addBookToList(listId, bookId);
    }

    public void removeBookFromList(int listId, int bookId) throws SQLException {
        bookListDAO.removeBookFromList(listId, bookId);
    }

    public List<BookListItem> getBooksInList(int listId) throws SQLException {
        return bookListDAO.getItemsByListId(listId);
    }

    public void addToWishlist(int userId, int bookId) throws SQLException {
        if (wishlistDAO.isBookInWishlist(userId, bookId)) {
            throw new IllegalArgumentException("Book is already in your wishlist.");
        }
        wishlistDAO.addToWishlist(userId, bookId);
    }

    public void removeFromWishlist(int userId, int bookId) throws SQLException {
        wishlistDAO.removeFromWishlist(userId, bookId);
    }

    public List<Wishlist> getWishlist(int userId) throws SQLException {
        return wishlistDAO.getWishlistByUserId(userId);
    }

    public boolean isBookInWishlist(int userId, int bookId) throws SQLException {
        return wishlistDAO.isBookInWishlist(userId, bookId);
    }
}