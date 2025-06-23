package com.utm.hometuition.main;
import javax.swing.JOptionPane;

import com.utm.hometuition.auth.Authentication;
import com.utm.hometuition.model.Person;
import com.utm.hometuition.ui.Menu;
import com.utm.hometuition.scheduler.Scheduler;

public class Main {
    public static void main(String[] args) {
        // Initialize scheduler and load all data from files
        Scheduler scheduler = new Scheduler();
        scheduler.loadTutors("tutors.txt");
        scheduler.loadParents("parents.txt");
        scheduler.loadSessions("sessions.txt");

        // Initialize authentication and load user credentials
        Authentication auth = new Authentication();
        auth.loadUsers("user.txt");
        
        Person loggedInUser = null;

        // Loop until a valid user logs in or the user cancels
        while (loggedInUser == null) {
            loggedInUser = auth.login(scheduler);
            if (loggedInUser == null) {
                // The login method will show its own error messages.
                // We check if the result of the login attempt was a deliberate exit.
                if (auth.isLoginAttemptCancelled()) {
                    JOptionPane.showMessageDialog(null, "Application exiting.");
                    return; // Exit the application
                }
            }
        }

        JOptionPane.showMessageDialog(null, "Login successful.");
        
        // Create the menu controller and pass it the logged-in user and other necessary objects
        Menu menu = new Menu(loggedInUser, scheduler);
        menu.show();
    }
}