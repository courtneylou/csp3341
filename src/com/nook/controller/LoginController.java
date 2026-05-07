package com.nook.controller;

import com.nook.model.User;
import com.nook.service.AuthService;
import com.nook.session.SessionManager;
import com.nook.util.NavigationUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private final AuthService authService = new AuthService();

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please fill in all fields.");
            return;
        }

        try {
            User user = authService.login(username, password);
            SessionManager.getInstance().setCurrentUser(user);

            if (user.isAdmin()) {
                NavigationUtil.navigateTo("/com/nook/views/admin/admin-dashboard.fxml");
            } else {
                NavigationUtil.navigateTo("/com/nook/views/home.fxml");
            }

        } catch (IllegalArgumentException | IllegalStateException e) {
            errorLabel.setText(e.getMessage());
        } catch (Exception e) {
            errorLabel.setText("Something went wrong. Please try again.");
        }
    }

    @FXML
    private void handleGoToRegister() {
        NavigationUtil.navigateTo("/com/nook/views/register.fxml");
    }
}