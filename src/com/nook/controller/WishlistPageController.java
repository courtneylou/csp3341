package com.nook.controller;

import com.nook.model.Book;
import com.nook.model.Wishlist;
import com.nook.service.BookListService;
import com.nook.service.BookService;
import com.nook.session.SessionManager;
import com.nook.util.NavigationUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;

public class WishlistPageController {

    @FXML private FlowPane booksGrid;
    @FXML private Label errorLabel;

    private final BookListService bookListService = new BookListService();
    private final BookService bookService = new BookService();

    @FXML
    public void initialize() {
        if (!SessionManager.getInstance().isLoggedIn()) {
            NavigationUtil.navigateTo("/com/nook/views/login.fxml");
            return;
        }
        loadWishlist();
    }

    private void loadWishlist() {
        try {
            booksGrid.getChildren().clear();
            int userId = SessionManager.getInstance().getCurrentUser().getId();
            List<Wishlist> wishlist = bookListService.getWishlist(userId);

            if (wishlist.isEmpty()) {
                Label noBooks = new Label("Your wishlist is empty. Heart a book to add it here!");
                noBooks.getStyleClass().add("no-reviews-label");
                booksGrid.getChildren().add(noBooks);
                return;
            }

            for (Wishlist item : wishlist) {
                Book book = bookService.getBookById(item.getBookId());
                if (book != null) {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/nook/views/book-card.fxml"));
                        VBox card = loader.load();
                        BookCardController controller = loader.getController();
                        controller.setBook(book);
                        booksGrid.getChildren().add(card);
                    } catch (IOException e) {
                        System.out.println("Error loading book card: " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            errorLabel.setText("Error loading wishlist: " + e.getMessage());
        }
    }

    @FXML
    private void handleBack() {
        NavigationUtil.navigateTo("/com/nook/views/home.fxml");
    }
}