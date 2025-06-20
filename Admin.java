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

    public void addTutor(Scanner scanner, Scheduler scheduler) {
        String name = getValidatedName(scanner, "Tutor name: ");
        // Use the new validation method for the username
        String username = getValidUsername(scanner, scheduler);
        String password = getValidPassword(scanner);
        System.out.print("Subject: ");
        String subject = scanner.nextLine();

        scheduler.addTutor(new Tutor(name, username, password, subject));
        System.out.println("Tutor " + name + " added.");
        saveTutors(scheduler);
    }

    public void addParent(Scanner scanner, Scheduler scheduler) {
        String name = getValidatedName(scanner, "Parent name: ");
        // Use the new validation method for the username
        String username = getValidUsername(scanner, scheduler);
        String password = getValidPassword(scanner);
        String childName = getValidatedName(scanner, "Child name: ");

        scheduler.addParent(new Parent(name, username, password, childName));
        System.out.println("Parent " + name + " added.");
        saveParents(scheduler);
    }

    // --- NEW HELPER METHOD FOR USERNAME VALIDATION ---
    private String getValidUsername(Scanner scanner, Scheduler scheduler) {
        String username;
        // Regex for 5-12 alphanumeric characters (more than 4)
        String usernamePattern = "^[a-zA-Z0-9]{5,12}$";
        
        while (true) {
            System.out.print("Username (5-12 chars, letters and digits only): ");
            username = scanner.nextLine();
            
            if (username == null || !username.matches(usernamePattern)) {
                System.out.println("Invalid format. Username must be 5-12 characters long and contain only letters and digits.");
                continue;
            }
            
            if (scheduler.isUsernameTaken(username)) {
                System.out.println("Error: This username is already taken. Please choose another.");
                continue;
            }
            
            // If both checks pass, the username is valid
            return username;
        }
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

    public void viewAllSessions(Scheduler scheduler) {
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
        System.out.print("Choose Tutor number: ");
        int tutorIndex = tryReadInt(scanner) - 1;
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
        int parentIndex = tryReadInt(scanner) - 1;
        if (parentIndex < 0 || parentIndex >= parents.size()) {
            System.out.println("Invalid parent number.");
            return;
        }
        Parent selectedParent = parents.get(parentIndex);

        LocalDate scheduledDate = getValidDate(scanner);
        if (scheduledDate == null) return;
        String[] times = getValidStartAndEndTimes(scanner, scheduledDate, 48);
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

        System.out.print("Choose session number: ");
        int sessionIndex = tryReadInt(scanner) - 1;
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
        String[] newTimes = getValidStartAndEndTimes(scanner, newDate, 24);
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
    public void deleteTutor(Scanner scanner, Scheduler scheduler) {
        if (scheduler.getTutors().isEmpty()) {
            System.out.println("There are no tutors to delete.");
            return;
        }
        System.out.println("\nSelect a tutor to delete:");
        ArrayList<Tutor> tutors = scheduler.getTutors();
        for (int i = 0; i < tutors.size(); i++) {
            System.out.println((i + 1) + ": " + tutors.get(i).getName());
        }
        System.out.print("Choose tutor number: ");
        int tutorIndex = tryReadInt(scanner) - 1;
        if (tutorIndex < 0 || tutorIndex >= tutors.size()) {
            System.out.println("Invalid tutor number.");
            return;
        }
        Tutor tutorToDelete = tutors.get(tutorIndex);
        scheduler.deleteTutor(tutorToDelete);
        System.out.println("Tutor '" + tutorToDelete.getName() + "' and all associated sessions have been deleted.");
        saveTutors(scheduler);
        saveSessions(scheduler);
    }

    public void deleteParent(Scanner scanner, Scheduler scheduler) {
        if (scheduler.getParents().isEmpty()) {
            System.out.println("There are no parents to delete.");
            return;
        }
        System.out.println("\nSelect a parent to delete:");
        ArrayList<Parent> parents = scheduler.getParents();
        for (int i = 0; i < parents.size(); i++) {
            System.out.println((i + 1) + ": " + parents.get(i).getName());
        }
        System.out.print("Choose parent number: ");
        int parentIndex = tryReadInt(scanner) - 1;
        if (parentIndex < 0 || parentIndex >= parents.size()) {
            System.out.println("Invalid parent number.");
            return;
        }
        Parent parentToDelete = parents.get(parentIndex);
        scheduler.deleteParent(parentToDelete);
        System.out.println("Parent '" + parentToDelete.getName() + "' and all associated sessions have been deleted.");
        saveParents(scheduler);
        saveSessions(scheduler);
    }

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
        System.out.print("Choose session number: ");
        int sessionIndex = tryReadInt(scanner) - 1;
        if (sessionIndex < 0 || sessionIndex >= sessions.size()) {
            System.out.println("Invalid session number.");
            return;
        }
        scheduler.deleteSessionByIndex(sessionIndex);
        System.out.println("Session deleted successfully.");
        saveSessions(scheduler);
    }
    private String getValidPassword(Scanner scanner) {
        String password;
        String passwordPattern = "^[a-zA-Z0-9]{6,12}$";
        
        while (true) {
            System.out.print("Password (6-12 characters, letters and digits only): ");
            password = scanner.nextLine();
            if (password != null && password.matches(passwordPattern)) {
                return password;
            } else {
                System.out.println("Invalid password. Please ensure it is 6-12 characters long and contains only letters and digits.");
            }
        }
    }
    private int tryReadInt(Scanner scanner) {
        try { return Integer.parseInt(scanner.nextLine()); } 
        catch (NumberFormatException e) { return -1; }
    }
    private String getValidatedName(Scanner scanner, String prompt) {
        String name;
        while (true) {
            System.out.print(prompt);
            name = scanner.nextLine();
            if (name != null && !name.trim().isEmpty() && name.matches("^[a-zA-Z\\s]+$")) {
                return formatName(name);
            } else {
                System.out.println("Invalid input. Name must contain only letters and spaces.");
            }
        }
    }
    private String formatName(String name) {
        if (name == null || name.trim().isEmpty()) return "";
        String[] words = name.trim().toLowerCase().split("\\s+");
        StringBuilder formattedName = new StringBuilder();
        for (String word : words) {
            if (word.length() > 0) {
                formattedName.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1)).append(" ");
            }
        }
        return formattedName.toString().trim();
    }
    private LocalDate getValidDate(Scanner scanner) {
        LocalDate parsedDate = null;
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        while (parsedDate == null) {
            System.out.print("Enter Date (dd-MM-yyyy): ");
            String dateStr = scanner.nextLine();
            String[] parts = dateStr.split("-");
            if (parts.length != 3) {
                System.out.println("Error: Invalid date format. Please use dd-MM-yyyy.");
                continue;
            }
            try {
                String day = parts[0].length() == 1 ? "0" + parts[0] : parts[0];
                String month = parts[1].length() == 1 ? "0" + parts[1] : parts[1];
                String year = parts[2];
                String standardizedDateStr = day + "-" + month + "-" + year;
                parsedDate = LocalDate.parse(standardizedDateStr, dateFormatter);
                if (parsedDate.equals(LocalDate.now()) && LocalTime.now().getHour() >= 16) {
                    System.out.println("Error: It is past 4 PM, so you cannot book a session for today.");
                    parsedDate = null; 
                    continue;
                }
                LocalDateTime maxBookingDate = LocalDateTime.now().plusMonths(2);
                if (parsedDate.isBefore(LocalDate.now())) {
                    System.out.println("Error: Cannot book a session on a past date.");
                    parsedDate = null; continue;
                }
                if (parsedDate.atStartOfDay().isAfter(maxBookingDate)) {
                    System.out.println("Error: Session must be booked within the next 2 months.");
                    parsedDate = null;
                }
            } catch (DateTimeParseException e) {
                System.out.println("Error: Invalid date value. Please enter a correct date.");
            }
        }
        return parsedDate;
    }
    private String[] getValidStartAndEndTimes(Scanner scanner, LocalDate scheduledDate, int advanceBookingHours) {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime startTime, endTime;
        while (true) {
            try {
                System.out.print("Enter Start Time (HH:mm 24-hour format): ");
                startTime = LocalTime.parse(scanner.nextLine(), timeFormatter);
                System.out.print("Enter End Time (HH:mm 24-hour format): ");
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
                LocalDateTime scheduledStartDateTime = LocalDateTime.of(scheduledDate, startTime);
                if (scheduledStartDateTime.isBefore(LocalDateTime.now().plusHours(advanceBookingHours))) {
                    System.out.println("Error: Session must be booked at least " + advanceBookingHours + " hours in advance.");
                    return null;
                }
                return new String[]{startTime.format(timeFormatter), endTime.format(timeFormatter)};
            } catch (DateTimeParseException e) {
                System.out.println("Error: Invalid time format. Please use HH:mm and try again.");
            }
        }
    }
    private void saveTutors(Scheduler scheduler) {
        try { scheduler.exportTutors("tutors.txt");
            System.out.println("Tutors automatically saved to tutors.txt");
        } catch (Exception e) { System.out.println("Export failed: " + e.getMessage()); }
    }
    private void saveParents(Scheduler scheduler) {
        try { scheduler.exportParents("parents.txt");
            System.out.println("Parents automatically saved to parents.txt");
        } catch (Exception e) { System.out.println("Export failed: " + e.getMessage()); }
    }
    private void saveSessions(Scheduler scheduler) {
        try { scheduler.saveSessionsToFile("sessions.txt");
            System.out.println("Sessions automatically saved to sessions.txt");
        } catch (Exception e) { System.out.println("Save failed: " + e.getMessage()); }
    }
}