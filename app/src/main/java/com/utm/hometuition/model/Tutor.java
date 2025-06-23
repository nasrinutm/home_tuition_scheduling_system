package com.utm.hometuition.model;
import javax.swing.JOptionPane;

import com.utm.hometuition.scheduler.Scheduler;

import java.util.ArrayList;

public class Tutor extends Person {
    private String subject;

    public Tutor(String name, String username, String password, String subject) {
        super(name, username, password);
        this.subject = subject;
    }
    
    public String getSubject() { 
        return subject; 
    }
    @Override
    public void viewSessions(Scheduler scheduler) {
        ArrayList<Session> mySessions = scheduler.getSessionsForTutor(this);
        if (mySessions.isEmpty()) {
            JOptionPane.showMessageDialog(null, "You have no sessions scheduled.");
            return;
        }

        StringBuilder scheduleText = new StringBuilder();
        scheduleText.append("--- My Schedule ---\n");
        for (Session s : mySessions) {
            scheduleText.append(s.getDetails()).append("\n");
        }
        JOptionPane.showMessageDialog(null, scheduleText.toString(), "My Schedule", JOptionPane.INFORMATION_MESSAGE);
    }
}