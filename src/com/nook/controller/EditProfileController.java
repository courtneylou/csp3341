package com.nook.controller;

import com.nook.model.User;
import com.nook.service.UserService;
import com.nook.session.SessionManager;
import com.nook.util.NavigationUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class EditProfileController {

    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private TextArea bioArea;
    @FXML private TextField avatarUrlField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label errorLabel;
    @FXML private Label successLabel;

    private final UserService userService = new UserService();
    private User currentUser;

    @FXML
    public void initialize() {
        currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            NavigationUtil.navigateTo("/com/nook/views/login.fxml");
            return;
        }

        usernameField.setText(currentUser.getUsername());
        usernameField.setDisable(true);
        emailField.setText(currentUser.getEmail());
        bioArea.setText(currentUser.getBio() != null ? currentUser.getBio() : "");
        avatarUrlField.setText(currentUser.getAvatarUrl() != null ? currentUser.getAvatarUrl() : "");
    }

    @FXML
    private void handleSave() {
        String email = emailField.getText().trim();
        String bio = bioArea.getText().trim();
        String avatarUrl = avatarUrlField.getText().trim();
        String newPassword = newPasswordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();

        errorLabel.setText("");
        successLabel.setText("");

        if (email.isEmpty()) {
            errorLabel.setText("Email cannot be empty.");
            return;
        }

        try {
            if (!email.equals(currentUser.getEmail())) {
                userService.updateEmail(currentUser, email);
            }

            userService.updateBio(currentUser, bio);
            userService.updateAvatar(currentUser, avatarUrl);

            if (!newPassword.isEmpty()) {
                if (!newPassword.equals(confirmPassword)) {
                    errorLabel.setText("Passwords do not match.");
                    return;
                }
                if (newPassword.length() < 6) {
                    errorLabel.setText("Password must be at least 6 characters.");
                    return;
                }
                userService.updatePassword(currentUser, newPassword);
            }

            SessionManager.getInstance().setCurrentUser(currentUser);
            successLabel.setText("Profile updated successfully!");

        } catch (IllegalArgumentException e) {
            errorLabel.setText(e.getMessage());
        } catch (Exception e) {
            errorLabel.setText("Something went wrong. Please try again.");
        }
    }

    @FXML
    private void handleDeleteAccount() {
        try {
            userService.deleteUser(currentUser.getId());
            SessionManager.getInstance().clearSession();
            NavigationUtil.navigateTo("/com/nook/views/login.fxml");
        } catch (Exception e) {
            errorLabel.setText("Error deleting account: " + e.getMessage());
        }
    }

    @FXML
    private void handleBack() {
        NavigationUtil.navigateTo("/com/nook/views/profile.fxml");
    }
}