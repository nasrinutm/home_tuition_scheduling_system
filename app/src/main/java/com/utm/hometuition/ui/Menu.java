package com.utm.hometuition.ui;
import javax.swing.JOptionPane;

import com.utm.hometuition.model.Admin;
import com.utm.hometuition.model.Parent;
import com.utm.hometuition.model.Person;
import com.utm.hometuition.model.Tutor;
import com.utm.hometuition.scheduler.Scheduler;

public class Menu {
    private Person loggedInUser;
    private Scheduler scheduler;

    public Menu(Person user, Scheduler scheduler) {
        this.loggedInUser = user;
        this.scheduler = scheduler;
    }

    public void show() {
        if (loggedInUser instanceof Admin adminUser) {
            showAdminMenu(adminUser);
        } else if (loggedInUser instanceof Parent parentUser) {
            showParentMenu(parentUser);
        } else if (loggedInUser instanceof Tutor tutorUser) {
            showTutorMenu(tutorUser);
        } else {
            JOptionPane.showMessageDialog(null, "Unknown user role. Exiting.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void showAdminMenu(Admin adminUser) {
        while (true) {
            StringBuilder menuText = new StringBuilder();
            menuText.append("---- Admin Menu ----\n");
            menuText.append("1. Schedule Session\n");
            menuText.append("2. Reschedule a Session\n");
            menuText.append("3. View All Sessions\n");
            menuText.append("4. View All Tutors\n");
            menuText.append("5. View All Parents\n");
            menuText.append("6. Delete a Session\n");
            menuText.append("7. Exit");

            String choiceStr = JOptionPane.showInputDialog(null, menuText.toString(), "Admin Menu", JOptionPane.PLAIN_MESSAGE);

            if (choiceStr == null) { // User canceled or closed the dialog
                break;
            }

            int option;
            try {
                option = Integer.parseInt(choiceStr);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid input. Enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            switch (option) {
                case 1:
                    adminUser.scheduleSession(scheduler);
                    break;
                case 2:
                    adminUser.rescheduleSession(scheduler);
                    break;
                case 3:
                    adminUser.viewSessions(scheduler);
                    break;
                case 4:
                    adminUser.viewAllTutors(scheduler);
                    break;
                case 5:
                    adminUser.viewAllParents(scheduler);
                    break;
                case 6:
                    adminUser.deleteSession(scheduler);
                    break;
                case 7:
                    JOptionPane.showMessageDialog(null, "Exiting system.");
                    return;
                default:
                    JOptionPane.showMessageDialog(null, "Invalid option.", "Warning", JOptionPane.WARNING_MESSAGE);
                    break;
            }
        }
    }

    public void showParentMenu(Parent parentUser) {
        JOptionPane.showMessageDialog(null, "Welcome, " + parentUser.getName() + "!");
        while (true) {
            StringBuilder menuText = new StringBuilder();
            menuText.append("---- Parent Menu ----\n");
            menuText.append("1. Schedule a Session\n");
            menuText.append("2. Reschedule My Session\n");
            menuText.append("3. View My Sessions\n");
            menuText.append("4. Exit");

            String choiceStr = JOptionPane.showInputDialog(null, menuText.toString(), "Parent Menu", JOptionPane.PLAIN_MESSAGE);

            if (choiceStr == null) { // User canceled or closed the dialog
                break;
            }
            
            int option;
            try {
                option = Integer.parseInt(choiceStr);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid input. Enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            switch (option) {
                case 1:
                    parentUser.scheduleSession(scheduler);
                    break;
                case 2:
                    parentUser.rescheduleSession(scheduler);
                    break;
                case 3:
                    parentUser.viewSessions(scheduler);
                    break;
                case 4:
                    JOptionPane.showMessageDialog(null, "Exiting system.");
                    return;
                default:
                    JOptionPane.showMessageDialog(null, "Invalid option.", "Warning", JOptionPane.WARNING_MESSAGE);
                    break;
            }
        }
    }
    
    public void showTutorMenu(Tutor tutorUser) {
        JOptionPane.showMessageDialog(null, "Welcome, " + tutorUser.getName() + "!");
        while (true) {
            StringBuilder menuText = new StringBuilder();
            menuText.append("---- Tutor Menu ----\n");
            menuText.append("1. View My Schedule\n");
            menuText.append("2. Exit");
            
            String choiceStr = JOptionPane.showInputDialog(null, menuText.toString(), "Tutor Menu", JOptionPane.PLAIN_MESSAGE);

            if (choiceStr == null) { // User canceled or closed the dialog
                break;
            }

            int option;
            try {
                option = Integer.parseInt(choiceStr);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Invalid input. Enter a number.", "Error", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            switch (option) {
                case 1:
                    tutorUser.viewSessions(scheduler);
                    break;
                case 2:
                    JOptionPane.showMessageDialog(null, "Exiting system.");
                    return;
                default:
                    JOptionPane.showMessageDialog(null, "Invalid option.", "Warning", JOptionPane.WARNING_MESSAGE);
                    break;
            }
        }
    }
}