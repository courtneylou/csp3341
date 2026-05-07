package com.nook.controller.admin;

import com.nook.service.BookService;
import com.nook.service.UserService;
import com.nook.session.SessionManager;
import com.nook.util.NavigationUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class AdminDashboardController {

    @FXML private Label totalUsersLabel;
    @FXML private Label totalBooksLabel;
    @FXML private Label adminNameLabel;

    private final UserService userService = new UserService();
    private final BookService bookService = new BookService();

    @FXML
    public void initialize() {
        adminNameLabel.setText("Welcome, " + SessionManager.getInstance().getCurrentUser().getUsername());

        try {
            totalUsersLabel.setText("Total Users: " + userService.getAllUsers().size());
        } catch (Exception e) {
            totalUsersLabel.setText("Total Users: N/A");
        }

        try {
            totalBooksLabel.setText("Total Books: " + bookService.getAllBooks().size());
        } catch (Exception e) {
            totalBooksLabel.setText("Total Books: N/A");
        }
    }

    @FXML
    private void handleManageUsers() {
        NavigationUtil.navigateTo("/com/nook/views/admin/admin-users.fxml");
    }

    @FXML
    private void handleManageBooks() {
        NavigationUtil.navigateTo("/com/nook/views/admin/admin-books.fxml");
    }

    @FXML
    private void handleGoHome() {
        NavigationUtil.navigateTo("/com/nook/views/home.fxml");
    }

    @FXML
    private void handleLogout() {
        SessionManager.getInstance().clearSession();
        NavigationUtil.navigateTo("/com/nook/views/login.fxml");
    }
}