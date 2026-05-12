package com.nook.controller;

import com.nook.service.UserService;
import com.nook.util.NavigationUtil;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class SetupAdminController {

    @FXML
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    private final UserService userService =
            new UserService();

    @FXML
    public void handleCreateAdmin() {

        try {

            userService.createInitialAdmin(
                    usernameField.getText(),
                    emailField.getText(),
                    passwordField.getText()
            );

            NavigationUtil.navigateTo(
                    "/com/nook/views/login.fxml"
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}