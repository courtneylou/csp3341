package com.nook.controller;

import com.nook.service.UserService;
import com.nook.util.NavigationUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class SetupAdminController {

    @FXML private Label errorLabel;
    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    private final UserService userService =
            new UserService();

    @FXML
    public void handleCreateAdmin() {

        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please fill in all fields.");
            return;
        }

        try {
            userService.createInitialAdmin(username, email, password);
            NavigationUtil.navigateTo("/com/nook/views/login.fxml");
        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Something went wrong. Please try again.");
        }
    }
}