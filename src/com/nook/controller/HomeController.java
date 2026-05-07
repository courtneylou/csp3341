package com.nook.controller;

import com.nook.model.Book;
import com.nook.service.BookService;
import com.nook.session.SessionManager;
import com.nook.util.NavigationUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;

public class HomeController {

    @FXML private FlowPane booksGrid;
    @FXML private TextField searchField;
    @FXML private Label welcomeLabel;
    @FXML private Button adminButton;

    private final BookService bookService = new BookService();

    @FXML
    public void initialize() {
        if (SessionManager.getInstance().isLoggedIn()) {
            welcomeLabel.setText("Welcome back, " + SessionManager.getInstance().getCurrentUser().getUsername() + "!");
        } else {
            welcomeLabel.setText("Discover your next great read.");
        }

        if (SessionManager.getInstance().isAdmin()) {
            adminButton.setVisible(true);
            adminButton.setManaged(true);
        } else {
            adminButton.setVisible(false);
            adminButton.setManaged(false);
        }

        loadRecentBooks();
    }

    @FXML
    private void handleBookLists() {
        NavigationUtil.navigateTo("/com/nook/views/booklists.fxml");
    }

    @FXML
    private void handleWishlistPage() {
        NavigationUtil.navigateTo("/com/nook/views/wishlist-page.fxml");
    }

    private void loadRecentBooks() {
        try {
            List<Book> books = bookService.getRecentBooks(20);
            displayBooks(books);
        } catch (Exception e) {
            System.out.println("Error loading books: " + e.getMessage());
        }
    }

    private void displayBooks(List<Book> books) {
        booksGrid.getChildren().clear();
        for (Book book : books) {
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

    @FXML
    private void handleSearch() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            loadRecentBooks();
            return;
        }
        try {
            List<Book> results = bookService.searchBooks(query);
            displayBooks(results);
        } catch (Exception e) {
            System.out.println("Error searching books: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddBook() {
        NavigationUtil.navigateTo("/com/nook/views/add-book.fxml");
    }

    @FXML
    private void handleProfile() {
        NavigationUtil.navigateTo("/com/nook/views/profile.fxml");
    }

    @FXML
    private void handleLogout() {
        SessionManager.getInstance().clearSession();
        NavigationUtil.navigateTo("/com/nook/views/login.fxml");
    }

    @FXML
    private void handleAdminDashboard() {
        NavigationUtil.navigateTo("/com/nook/views/admin/admin-dashboard.fxml");
    }

}
