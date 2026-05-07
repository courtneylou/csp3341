package com.nook.controller;

import com.nook.model.Book;
import com.nook.service.BookListService;
import com.nook.session.SessionManager;
import com.nook.util.NavigationUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class BookCardController {

    @FXML private Label titleLabel;
    @FXML private Label authorLabel;
    @FXML private Button wishlistButton;
    @FXML private ImageView coverImage;

    private Book book;
    private final BookListService bookListService = new BookListService();

    public void setBook(Book book) {
        this.book = book;
        titleLabel.setText(book.getTitle());
        authorLabel.setText(book.getAuthorsAsString());

        if (book.getCoverUrl() != null && !book.getCoverUrl().isEmpty()) {
            try {
                coverImage.setImage(new Image(book.getCoverUrl(), true));
            } catch (Exception e) {
                System.out.println("Could not load cover image.");
            }
        }

        if (!SessionManager.getInstance().isLoggedIn()) {
            wishlistButton.setVisible(false);
        } else {
            updateWishlistButton();
        }
    }

    private void updateWishlistButton() {
        try {
            int userId = SessionManager.getInstance().getCurrentUser().getId();
            boolean inWishlist = bookListService.isBookInWishlist(userId, book.getId());
            wishlistButton.setText(inWishlist ? "♥" : "♡");
        } catch (Exception e) {
            System.out.println("Error checking wishlist: " + e.getMessage());
        }
    }

    @FXML
    private void handleViewBook() {
        BookDetailController.setSelectedBook(book);
        NavigationUtil.navigateTo("/com/nook/views/book-detail.fxml");
    }

    @FXML
    private void handleWishlist() {
        if (!SessionManager.getInstance().isLoggedIn()) return;
        try {
            int userId = SessionManager.getInstance().getCurrentUser().getId();
            boolean inWishlist = bookListService.isBookInWishlist(userId, book.getId());
            if (inWishlist) {
                bookListService.removeFromWishlist(userId, book.getId());
            } else {
                bookListService.addToWishlist(userId, book.getId());
            }
            updateWishlistButton();
        } catch (Exception e) {
            System.out.println("Error updating wishlist: " + e.getMessage());
        }
    }
}