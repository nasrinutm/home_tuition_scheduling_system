package com.utm.hometuition.auth;
import javax.swing.*;

import com.utm.hometuition.scheduler.Scheduler;
import com.utm.hometuition.model.Admin;
import com.utm.hometuition.model.Parent;
import com.utm.hometuition.model.Tutor;
import com.utm.hometuition.model.Person;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Authentication {
    private List<String[]> users = new ArrayList<>();
    private boolean loginAttemptCancelled = false;

    public boolean isLoginAttemptCancelled() {
        return loginAttemptCancelled;
    }

    public void loadUsers(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty() || line.trim().startsWith("#")) {
                    continue;
                }
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    users.add(parts);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Warning: No user file found or error reading file.", "File Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    public Person login(Scheduler scheduler) {
        // Create the input components
        JTextField usernameField = new JTextField(15);
        JPasswordField passwordField = new JPasswordField(15);

        // Create an array of objects to hold all components and labels
        Object[] message = {
            "Username:", usernameField,
            "Password:", passwordField
        };

        // Show the dialog with the array of components
        int option = JOptionPane.showConfirmDialog(null, message, "Login", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            loginAttemptCancelled = false; // User attempted to log in

            for (String[] user : users) {
                if (user[0].equals(username) && user[1].equals(password)) {
                    String role = user[2];
                    if ("admin".equals(role)) {
                        return new Admin(username, password);
                    } else if ("parent".equals(role)) {
                        Parent p = scheduler.findParentByUsername(username);
                        if (p == null) {
                            JOptionPane.showMessageDialog(null, "Login Error: A user account exists for '" + username + "', but no matching parent profile was found in parents.txt.", "Login Error", JOptionPane.ERROR_MESSAGE);
                            return null;
                        }
                        return p;
                    } else if ("tutor".equals(role)) {
                        Tutor t = scheduler.findTutorByUsername(username);
                        if (t == null) {
                            JOptionPane.showMessageDialog(null, "Login Error: A user account exists for '" + username + "', but no matching tutor profile was found in tutors.txt.", "Login Error", JOptionPane.ERROR_MESSAGE);
                            return null;
                        }
                        return t;
                    }
                }
            }
            JOptionPane.showMessageDialog(null, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            return null; // Invalid credentials
        } else {
            // User clicked Cancel or closed the dialog
            loginAttemptCancelled = true;
            return null;
        }
    }
}