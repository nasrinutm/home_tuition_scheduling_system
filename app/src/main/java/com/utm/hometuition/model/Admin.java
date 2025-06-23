package com.utm.hometuition.model;
import javax.swing.JOptionPane;

import com.utm.hometuition.scheduler.Scheduler;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;


public class Admin extends Person implements Schedulable {

    public Admin(String username, String password) {
        super("Admin", username, password);
    }
    
    public void deleteSession(Scheduler scheduler) {
        if (scheduler.getSessions().isEmpty()) {
            JOptionPane.showMessageDialog(null, "There are no sessions to delete.");
            return;
        }
        
        StringBuilder sessionList = new StringBuilder("Select a session to delete:\n");
        ArrayList<Session> sessions = scheduler.getSessions();
        for (int i = 0; i < sessions.size(); i++) {
            sessionList.append((i + 1) + ": " + sessions.get(i).getDetails()).append("\n");
        }
        sessionList.append("\nChoose session number (or 0 to cancel):");

        String choiceStr = JOptionPane.showInputDialog(sessionList.toString());
        if (choiceStr == null) return; // User canceled

        int sessionChoice = tryReadInt(choiceStr);
        if (sessionChoice == 0) {
            return;
        }
        int sessionIndex = sessionChoice - 1;
        if (sessionIndex < 0 || sessionIndex >= sessions.size()) {
            JOptionPane.showMessageDialog(null, "Invalid session number.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        scheduler.deleteSessionByIndex(sessionIndex);
        JOptionPane.showMessageDialog(null, "Session deleted successfully.");
        saveSessions(scheduler);
    }

    public void viewAllTutors(Scheduler scheduler) {
        ArrayList<Tutor> tutors = scheduler.getTutors();
        if (tutors.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No tutors have been added yet.");
            return;
        }
        
        StringBuilder tutorList = new StringBuilder();
        tutorList.append("--- List of Tutors ---\n");
        for (Tutor t : tutors) {
            tutorList.append("Name: " + t.getName() + ", Subject: " + t.getSubject()).append("\n");
        }
        JOptionPane.showMessageDialog(null, tutorList.toString(), "All Tutors", JOptionPane.INFORMATION_MESSAGE);
    }

    public void viewAllParents(Scheduler scheduler) {
        ArrayList<Parent> parents = scheduler.getParents();
        if (parents.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No parents have been added yet.");
            return;
        }
        
        StringBuilder parentList = new StringBuilder();
        parentList.append("--- List of Parents ---\n");
        for (Parent p : parents) {
            parentList.append("Name: " + p.getName() + ", Child's Name: " + p.getChildName()).append("\n");
        }
        JOptionPane.showMessageDialog(null, parentList.toString(), "All Parents", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void viewSessions(Scheduler scheduler) {
        ArrayList<Session> allSessions = scheduler.getSessions();
        if (allSessions.isEmpty()) {
            JOptionPane.showMessageDialog(null, "\nNo sessions have been scheduled yet.");
            return;
        }
        
        StringBuilder sessionList = new StringBuilder();
        sessionList.append("--- All Scheduled Sessions ---\n");
        for (Session s : allSessions) {
            sessionList.append(s.getDetails()).append("\n");
        }
        JOptionPane.showMessageDialog(null, sessionList.toString(), "All Sessions", JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void scheduleSession(Scheduler scheduler) {
        if (scheduler.getTutors().isEmpty() || scheduler.getParents().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Add at least one tutor and one parent before scheduling.");
            return;
        }

        StringBuilder tutorListText = new StringBuilder("Available Tutors:\n");
        ArrayList<Tutor> tutors = scheduler.getTutors();
        for (int i = 0; i < tutors.size(); i++) {
            tutorListText.append((i + 1) + ": " + tutors.get(i).getName() + " (" + tutors.get(i).getSubject() + ")\n");
        }
        tutorListText.append("\nChoose Tutor number (or 0 to cancel):");
        String tutorChoiceStr = JOptionPane.showInputDialog(tutorListText.toString());
        if(tutorChoiceStr == null) return;
        int tutorChoice = tryReadInt(tutorChoiceStr);
        if (tutorChoice == 0) return;
        int tutorIndex = tutorChoice - 1;
        if (tutorIndex < 0 || tutorIndex >= tutors.size()) {
            JOptionPane.showMessageDialog(null, "Invalid tutor number.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Tutor selectedTutor = tutors.get(tutorIndex);

        StringBuilder parentListText = new StringBuilder("\nAvailable Parents:\n");
        ArrayList<Parent> parents = scheduler.getParents();
        for (int i = 0; i < parents.size(); i++) {
            parentListText.append((i + 1) + ": " + parents.get(i).getName() + " (Child: " + parents.get(i).getChildName() + ")\n");
        }
        parentListText.append("\nChoose Parent number (or 0 to cancel):");
        String parentChoiceStr = JOptionPane.showInputDialog(parentListText.toString());
        if(parentChoiceStr == null) return;
        int parentChoice = tryReadInt(parentChoiceStr);
        if (parentChoice == 0) return;
        int parentIndex = parentChoice - 1;
        if (parentIndex < 0 || parentIndex >= parents.size()) {
            JOptionPane.showMessageDialog(null, "Invalid parent number.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Parent selectedParent = parents.get(parentIndex);

        LocalDate scheduledDate = getValidDate();
        if (scheduledDate == null) return;
        String[] times = getValidStartAndEndTimes();
        if (times == null) return;

        LocalDateTime newStart = LocalDateTime.of(scheduledDate, LocalTime.parse(times[0]));
        LocalDateTime newEnd = LocalDateTime.of(scheduledDate, LocalTime.parse(times[1]));
        if (scheduler.doesSessionClash(selectedTutor, selectedParent, newStart, newEnd)) {
            JOptionPane.showMessageDialog(null, "Error: This time slot is unavailable.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String dateForStorage = scheduledDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
        scheduler.addSession(new Session(selectedTutor, selectedParent, dateForStorage, times[0], times[1]));
        JOptionPane.showMessageDialog(null, "Session scheduled successfully.");
        saveSessions(scheduler);
    }
    
    @Override
    public void rescheduleSession(Scheduler scheduler) {
        if (scheduler.getSessions().isEmpty()) {
            JOptionPane.showMessageDialog(null, "There are no sessions to reschedule.");
            return;
        }

        StringBuilder sessionList = new StringBuilder("Select a session to reschedule:\n");
        ArrayList<Session> sessions = scheduler.getSessions();
        for (int i = 0; i < sessions.size(); i++) {
            sessionList.append((i + 1) + ": " + sessions.get(i).getDetails()).append("\n");
        }
        sessionList.append("\nChoose session number (or 0 to cancel):");
        String choiceStr = JOptionPane.showInputDialog(sessionList.toString());
        if (choiceStr == null) return;

        int sessionChoice = tryReadInt(choiceStr);
        if (sessionChoice == 0) return;
        
        int sessionIndex = sessionChoice - 1;
        if (sessionIndex < 0 || sessionIndex >= sessions.size()) {
            JOptionPane.showMessageDialog(null, "Invalid session number.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Session sessionToReschedule = sessions.get(sessionIndex);

        if (sessionToReschedule.getStartDateTime().isBefore(LocalDateTime.now().plusHours(24))) {
            JOptionPane.showMessageDialog(null, "Error: This session is within 24 hours and cannot be rescheduled.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(null, "Enter new details for the session:");
        LocalDate newDate = getValidDate();
        if (newDate == null) return;
        String[] newTimes = getValidStartAndEndTimes();
        if (newTimes == null) return;
        
        LocalDateTime newStart = LocalDateTime.of(newDate, LocalTime.parse(newTimes[0]));
        LocalDateTime newEnd = LocalDateTime.of(newDate, LocalTime.parse(newTimes[1]));

        if (scheduler.doesSessionClash(sessionToReschedule, sessionToReschedule.getTutor(), sessionToReschedule.getParent(), newStart, newEnd)) {
            JOptionPane.showMessageDialog(null, "Error: The new time slot is unavailable.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String dateForStorage = newDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
        sessionToReschedule.setDate(dateForStorage);
        sessionToReschedule.setStartTime(newTimes[0]);
        sessionToReschedule.setEndTime(newTimes[1]);
        
        JOptionPane.showMessageDialog(null, "Session rescheduled successfully.");
        saveSessions(scheduler);
    }

    public int tryReadInt(String input) {
        try { return Integer.parseInt(input); } 
        catch (NumberFormatException e) { return -1; }
    }

    @Override
    public LocalDate getValidDate() {
        LocalDate parsedDate = null;
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        while (parsedDate == null) {
            String dateStr = JOptionPane.showInputDialog("Enter Date (DD/MM/YYYY):");
            if (dateStr == null) return null;

            String[] parts = dateStr.split("/");
            if (parts.length != 3) {
                JOptionPane.showMessageDialog(null, "Error: Invalid date format.", "Error", JOptionPane.ERROR_MESSAGE);
                continue;
            }
            try {
                String day = parts[0].length() == 1 ? "0" + parts[0] : parts[0];
                String month = parts[1].length() == 1 ? "0" + parts[1] : parts[1];
                String year = parts[2];
                String standardizedDateStr = day + "-" + month + "-" + year;
                parsedDate = LocalDate.parse(standardizedDateStr, dateFormatter);

                LocalDate earliestValidDate = LocalDate.now().plusDays(2);
                if (parsedDate.isBefore(earliestValidDate)) {
                    JOptionPane.showMessageDialog(null, "Error: Bookings must be made at least 2 days in advance.", "Error", JOptionPane.ERROR_MESSAGE);
                    parsedDate = null;
                    continue;
                }
                
                LocalDate maxValidDate = LocalDate.now().plusMonths(2);
                if (parsedDate.isAfter(maxValidDate)) {
                    JOptionPane.showMessageDialog(null, "Error: Session must be booked within the next 2 months.", "Error", JOptionPane.ERROR_MESSAGE);
                    parsedDate = null;
                }
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(null, "Error: Invalid date value.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        return parsedDate;
    }

    @Override
    public String[] getValidStartAndEndTimes() {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime startTime, endTime;
        while (true) {
            try {
                String startStr = JOptionPane.showInputDialog("Enter Start Time (HH:MM 24-hour format):");
                if(startStr == null) return null;
                startTime = LocalTime.parse(startStr, timeFormatter);
                
                String endStr = JOptionPane.showInputDialog("Enter End Time (HH:MM 24-hour format):");
                if(endStr == null) return null;
                endTime = LocalTime.parse(endStr, timeFormatter);

                if (!endTime.isAfter(startTime)) {
                    JOptionPane.showMessageDialog(null, "Error: End time must be after the start time.", "Error", JOptionPane.ERROR_MESSAGE);
                    continue;
                }
                if (startTime.isBefore(LocalTime.of(8, 0)) || endTime.isAfter(LocalTime.of(17, 0))) {
                    JOptionPane.showMessageDialog(null, "Error: Session must be within business hours (08:00 - 17:00).", "Error", JOptionPane.ERROR_MESSAGE);
                    continue;
                }
                long durationMinutes = Duration.between(startTime, endTime).toMinutes();
                if (durationMinutes < 60 || durationMinutes > 180) {
                    JOptionPane.showMessageDialog(null, "Error: Session duration must be between 1 and 3 hours.", "Error", JOptionPane.ERROR_MESSAGE);
                    continue;
                }
                return new String[]{startTime.format(timeFormatter), endTime.format(timeFormatter)};
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(null, "Error: Invalid time format.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    } 
    
    @Override
    public void saveSessions(Scheduler scheduler) {
        try { 
            scheduler.saveSessionsToFile("sessions.txt");
            JOptionPane.showMessageDialog(null, "Sessions automatically saved to sessions.txt");
        } catch (Exception e) { 
            JOptionPane.showMessageDialog(null, "Save failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}