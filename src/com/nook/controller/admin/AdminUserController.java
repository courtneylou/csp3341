package com.nook.controller.admin;

import com.nook.model.User;
import com.nook.service.UserService;
import com.nook.util.NavigationUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class AdminUserController {

    @FXML private TableView<User> usersTable;
    @FXML private TableColumn<User, String> usernameColumn;
    @FXML private TableColumn<User, String> emailColumn;
    @FXML private TableColumn<User, String> roleColumn;
    @FXML private TableColumn<User, String> bannedColumn;
    @FXML private Label statusLabel;

    private final UserService userService = new UserService();

    @FXML
    public void initialize() {
        usernameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUsername()));
        emailColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmail()));
        roleColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRole()));
        bannedColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().isBanned() ? "Banned" : "Active"));

        loadUsers();
    }

    private void loadUsers() {
        try {
            List<User> users = userService.getAllUsers();
            usersTable.setItems(FXCollections.observableArrayList(users));
        } catch (Exception e) {
            statusLabel.setText("Error loading users: " + e.getMessage());
        }
    }

    private User getSelectedUser() {
        User selected = usersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Please select a user first.");
        }
        return selected;
    }

    @FXML
    private void handleBanUser() {
        User user = getSelectedUser();
        if (user == null) return;
        try {
            if (user.isBanned()) {
                userService.unbanUser(user.getId());
                statusLabel.setText(user.getUsername() + " has been unbanned.");
            } else {
                userService.banUser(user.getId());
                statusLabel.setText(user.getUsername() + " has been banned.");
            }
            loadUsers();
        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeleteUser() {
        User user = getSelectedUser();
        if (user == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete User");
        confirm.setHeaderText("Delete " + user.getUsername() + "?");
        confirm.setContentText("This action cannot be undone.");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    userService.deleteUser(user.getId());
                    statusLabel.setText(user.getUsername() + " has been deleted.");
                    loadUsers();
                } catch (Exception e) {
                    statusLabel.setText("Error: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void handlePromoteUser() {
        User user = getSelectedUser();
        if (user == null) return;
        try {
            if (user.isAdmin()) {
                userService.demoteToUser(user);
                statusLabel.setText(user.getUsername() + " demoted to user.");
            } else {
                userService.promoteToAdmin(user);
                statusLabel.setText(user.getUsername() + " promoted to admin.");
            }
            loadUsers();
        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleBack() {
        NavigationUtil.navigateTo("/com/nook/views/admin/admin-dashboard.fxml");
    }
}