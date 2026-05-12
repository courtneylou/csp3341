package com.nook.service;

import com.nook.dao.UserDAO;
import com.nook.model.User;
import com.nook.util.PasswordUtil;

import java.sql.SQLException;
import java.util.List;

public class UserService {

    private final UserDAO userDAO = new UserDAO();

    public User getUserById(int id) throws SQLException {
        return userDAO.getUserById(id);
    }

    public User getUserByUsername(String username) throws SQLException {
        return userDAO.getUserByUsername(username);
    }

    public List<User> getAllUsers() throws SQLException {
        return userDAO.getAllUsers();
    }

    public boolean adminExists() throws SQLException {
        return userDAO.adminExists();
    }

    public void createInitialAdmin(
            String username,
            String email,
            String password
    ) throws SQLException {

        if (adminExists()) {
            throw new IllegalStateException("Admin already exists.");
        }

        User admin = new User();

        admin.setUsername(username);
        admin.setEmail(email);
        admin.setPasswordHash(
                PasswordUtil.hashPassword(password)
        );
        admin.setRole("admin");
        admin.setBanned(false);

        userDAO.createUser(admin);
    }

    public void updateEmail(User user, String newEmail) throws SQLException {
        if (userDAO.getUserByEmail(newEmail) != null) {
            throw new IllegalArgumentException("Email is already in use.");
        }
        user.setEmail(newEmail);
        userDAO.updateUser(user);
    }

    public void updatePassword(User user, String newPassword) throws SQLException {
        user.setPasswordHash(PasswordUtil.hashPassword(newPassword));
        userDAO.updateUser(user);
    }

    public void updateBio(User user, String newBio) throws SQLException {
        user.setBio(newBio);
        userDAO.updateUser(user);
    }

    public void updateAvatar(User user, String avatarUrl) throws SQLException {
        user.setAvatarUrl(avatarUrl);
        userDAO.updateUser(user);
    }

    public void deleteUser(int id) throws SQLException {
        userDAO.deleteUser(id);
    }

    public void banUser(int id) throws SQLException {
        userDAO.banUser(id, true);
    }

    public void unbanUser(int id) throws SQLException {
        userDAO.banUser(id, false);
    }

    public void promoteToAdmin(User user) throws SQLException {
        user.setRole("admin");
        userDAO.updateUser(user);
    }

    public void demoteToUser(User user) throws SQLException {
        user.setRole("user");
        userDAO.updateUser(user);
    }

    public void adminUpdateUser(User user, String username, String email, String role, boolean isBanned) throws SQLException {
        user.setUsername(username);
        user.setEmail(email);
        user.setRole(role);
        user.setBanned(isBanned);
        userDAO.updateUser(user);
    }
}