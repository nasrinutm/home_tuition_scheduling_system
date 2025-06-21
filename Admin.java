import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

public class Admin extends Person {

    public Admin(String username, String password) {
        super("Admin", username, password);
    }
    
    // --- METHOD RE-ADDED ---
    public void deleteSession(Scanner scanner, Scheduler scheduler) {
        if (scheduler.getSessions().isEmpty()) {
            System.out.println("There are no sessions to delete.");
            return;
        }
        System.out.println("\nSelect a session to delete:");
        ArrayList<Session> sessions = scheduler.getSessions();
        for (int i = 0; i < sessions.size(); i++) {
            System.out.println((i + 1) + ": " + sessions.get(i).getDetails());
        }

        System.out.print("Choose session number (or 0 to cancel): ");
        int sessionChoice = tryReadInt(scanner);
        if (sessionChoice == 0) {
            return; // Exit the method
        }
        int sessionIndex = sessionChoice - 1;
        if (sessionIndex < 0 || sessionIndex >= sessions.size()) {
            System.out.println("Invalid session number.");
            return;
        }
        scheduler.deleteSessionByIndex(sessionIndex);
        System.out.println("Session deleted successfully.");
        saveSessions(scheduler);
    }

    // --- Other methods are unchanged ---
    public void viewAllTutors(Scheduler scheduler) {
        ArrayList<Tutor> tutors = scheduler.getTutors();
        if (tutors.isEmpty()) {
            System.out.println("No tutors have been added yet.");
            return;
        }
        System.out.println("\n--- List of Tutors ---");
        for (Tutor t : tutors) {
            System.out.println("Name: " + t.getName() + ", Subject: " + t.getSubject());
        }
    }

    public void viewAllParents(Scheduler scheduler) {
        ArrayList<Parent> parents = scheduler.getParents();
        if (parents.isEmpty()) {
            System.out.println("No parents have been added yet.");
            return;
        }
        System.out.println("\n--- List of Parents ---");
        for (Parent p : parents) {
            System.out.println("Name: " + p.getName() + ", Child's Name: " + p.getChildName());
        }
    }

    @Override
    public void viewSessions(Scheduler scheduler) {
        ArrayList<Session> allSessions = scheduler.getSessions();
        if (allSessions.isEmpty()) {
            System.out.println("\nNo sessions have been scheduled yet.");
            return;
        }
        System.out.println("\n--- All Scheduled Sessions ---");
        for (Session s : allSessions) {
            System.out.println(s.getDetails());
        }
    }

    public void scheduleSession(Scanner scanner, Scheduler scheduler) {
        if (scheduler.getTutors().isEmpty() || scheduler.getParents().isEmpty()) {
            System.out.println("Add at least one tutor and one parent before scheduling.");
            return;
        }

        System.out.println("\nAvailable Tutors:");
        ArrayList<Tutor> tutors = scheduler.getTutors();
        for (int i = 0; i < tutors.size(); i++) {
            System.out.println((i + 1) + ": " + tutors.get(i).getName() + " (" + tutors.get(i).getSubject() + ")");
        }
        System.out.print("Choose Tutor number (or 0 to cancel): ");
        int tutorChoice = tryReadInt(scanner);
        if (tutorChoice == 0) {
            return; // Exit the method
        }
        int tutorIndex = tutorChoice - 1;
        if (tutorIndex < 0 || tutorIndex >= tutors.size()) {
            System.out.println("Invalid tutor number.");
            return;
        }
        Tutor selectedTutor = tutors.get(tutorIndex);

        System.out.println("\nAvailable Parents:");
        ArrayList<Parent> parents = scheduler.getParents();
        for (int i = 0; i < parents.size(); i++) {
            System.out.println((i + 1) + ": " + parents.get(i).getName() + " (Child: " + parents.get(i).getChildName() + ")");
        }
        System.out.print("Choose Parent number: ");
        int parentChoice = tryReadInt(scanner);
        if (parentChoice == 0) {
            return; // Exit the method
        }
        int parentIndex = parentChoice - 1;
        if (parentIndex < 0 || parentIndex >= parents.size()) {
            System.out.println("Invalid parent number.");
            return;
        }
        Parent selectedParent = parents.get(parentIndex);

        LocalDate scheduledDate = getValidDate(scanner);
        if (scheduledDate == null) return;
        String[] times = getValidStartAndEndTimes(scanner);
        if (times == null) return;

        LocalDateTime newStart = LocalDateTime.of(scheduledDate, LocalTime.parse(times[0]));
        LocalDateTime newEnd = LocalDateTime.of(scheduledDate, LocalTime.parse(times[1]));
        if (scheduler.doesSessionClash(selectedTutor, selectedParent, newStart, newEnd)) {
            System.out.println("Error: This time slot is unavailable. It clashes with an existing session for the selected tutor or parent.");
            return;
        }
        
        String dateForStorage = scheduledDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
        scheduler.addSession(new Session(selectedTutor, selectedParent, dateForStorage, times[0], times[1]));
        System.out.println("Session scheduled successfully.");
        saveSessions(scheduler);
    }
    
    public void rescheduleSession(Scanner scanner, Scheduler scheduler) {
        if (scheduler.getSessions().isEmpty()) {
            System.out.println("There are no sessions to reschedule.");
            return;
        }
        System.out.println("\nSelect a session to reschedule:");
        ArrayList<Session> sessions = scheduler.getSessions();
        for (int i = 0; i < sessions.size(); i++) {
            System.out.println((i + 1) + ": " + sessions.get(i).getDetails());
        }

        System.out.print("Choose session number (or 0 to cancel): ");
        int sessionChoice = tryReadInt(scanner);
        if (sessionChoice == 0) {
            return; // Exit the method
        }
        int sessionIndex = sessionChoice - 1;
        if (sessionIndex < 0 || sessionIndex >= sessions.size()) {
            System.out.println("Invalid session number.");
            return;
        }
        Session sessionToReschedule = sessions.get(sessionIndex);

        if (sessionToReschedule.getStartDateTime().isBefore(LocalDateTime.now().plusHours(24))) {
            System.out.println("Error: This session is within 24 hours and cannot be rescheduled.");
            return;
        }

        System.out.println("Enter new details for the session:");
        LocalDate newDate = getValidDate(scanner);
        if (newDate == null) return;
        String[] newTimes = getValidStartAndEndTimes(scanner);
        if (newTimes == null) return;
        
        LocalDateTime newStart = LocalDateTime.of(newDate, LocalTime.parse(newTimes[0]));
        LocalDateTime newEnd = LocalDateTime.of(newDate, LocalTime.parse(newTimes[1]));

        if (scheduler.doesSessionClash(sessionToReschedule, sessionToReschedule.getTutor(), sessionToReschedule.getParent(), newStart, newEnd)) {
            System.out.println("Error: The new time slot is unavailable as it clashes with another session.");
            return;
        }
        
        String dateForStorage = newDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
        sessionToReschedule.setDate(dateForStorage);
        sessionToReschedule.setStartTime(newTimes[0]);
        sessionToReschedule.setEndTime(newTimes[1]);
        
        System.out.println("Session rescheduled successfully.");
        saveSessions(scheduler);
    }

    //helper method to read an integer from the scanner//
    private int tryReadInt(Scanner scanner) {
        try { return Integer.parseInt(scanner.nextLine()); } 
        catch (NumberFormatException e) { return -1; }
    }

    private LocalDate getValidDate(Scanner scanner) {
        LocalDate parsedDate = null;
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        while (parsedDate == null) {
            System.out.print("Enter Date (DD/MM/YYYY): ");
            String dateStr = scanner.nextLine();
            String[] parts = dateStr.split("/");
            if (parts.length != 3) {
                System.out.println("Error: Invalid date format. Please use DD/MM/YYYY.");
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
                    System.out.println("Error: Bookings must be made at least 2 days in advance.");
                    parsedDate = null;
                    continue;
                }
                
                LocalDate maxValidDate = LocalDate.now().plusMonths(2);
                if (parsedDate.isAfter(maxValidDate)) {
                    System.out.println("Error: Session must be booked within the next 2 months.");
                    parsedDate = null;
                }
            } catch (DateTimeParseException e) {
                System.out.println("Error: Invalid date value. Please enter a correct date.");
            }
        }
        return parsedDate;
    }

    // --- MODIFIED: Removed the now-redundant advance booking hour check ---
    private String[] getValidStartAndEndTimes(Scanner scanner) {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime startTime, endTime;
        while (true) {
            try {
                System.out.print("Enter Start Time (HH:MM 24-hour format): ");
                startTime = LocalTime.parse(scanner.nextLine(), timeFormatter);
                System.out.print("Enter End Time (HH:MM 24-hour format): ");
                endTime = LocalTime.parse(scanner.nextLine(), timeFormatter);
                if (!endTime.isAfter(startTime)) {
                    System.out.println("Error: End time must be after the start time.");
                    continue;
                }
                if (startTime.isBefore(LocalTime.of(8, 0)) || endTime.isAfter(LocalTime.of(17, 0))) {
                    System.out.println("Error: Session must be entirely within business hours (08:00 - 17:00).");
                    continue;
                }
                long durationMinutes = Duration.between(startTime, endTime).toMinutes();
                if (durationMinutes < 60 || durationMinutes > 180) {
                    System.out.println("Error: Session duration must be between 1 and 3 hours.");
                    continue;
                }
                // The precise hour check has been removed as per the new simplified rule.
                return new String[]{startTime.format(timeFormatter), endTime.format(timeFormatter)};
            } catch (DateTimeParseException e) {
                System.out.println("Error: Invalid time format. Please use HH:mm and try again.");
            }
        }
    } 
    // --- Other methods are unchanged --//
    
    private void saveSessions(Scheduler scheduler) {
        try { scheduler.saveSessionsToFile("sessions.txt");
            System.out.println("Sessions automatically saved to sessions.txt");
        } catch (Exception e) { System.out.println("Save failed: " + e.getMessage()); }
    }
}