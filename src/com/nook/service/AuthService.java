package com.nook.service;

import com.nook.dao.UserDAO;
import com.nook.model.User;
import com.nook.util.PasswordUtil;

import java.sql.SQLException;

public class AuthService {

    private final UserDAO userDAO = new UserDAO();

    public User login(String username, String password) throws SQLException {
        User user = userDAO.getUserByUsername(username);

        if (user == null) {
            throw new IllegalArgumentException("No account found with that username.");
        }

        if (user.isBanned()) {
            throw new IllegalStateException("This account has been banned.");
        }

        if (!PasswordUtil.checkPassword(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("Incorrect password.");
        }

        return user;
    }

    public User register(String username, String email, String password) throws SQLException {
        if (userDAO.getUserByUsername(username) != null) {
            throw new IllegalArgumentException("Username is already taken.");
        }

        if (userDAO.getUserByEmail(email) != null) {
            throw new IllegalArgumentException("Email is already registered.");
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPasswordHash(PasswordUtil.hashPassword(password));
        newUser.setRole("user");
        newUser.setBanned(false);

        userDAO.createUser(newUser);
        return userDAO.getUserByUsername(username);
    }

    public void logout() {
        com.nook.session.SessionManager.getInstance().clearSession();
    }
}