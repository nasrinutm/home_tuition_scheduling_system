import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

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
            System.out.println("\nYou have no sessions scheduled.");
            return;
        }
        System.out.println("\n--- My Schedule ---");
        for (Session s : mySessions) {
            System.out.println(s.getDetails());
        }
    }

    // --- NEW METHODS FOR TUTOR ---
    public void scheduleSession(Scanner scanner, Scheduler scheduler) {
        if (scheduler.getParents().isEmpty()) {
            System.out.println("There are no parents in the system to schedule a session with.");
            return;
        }

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
        if (scheduler.doesSessionClash(this, selectedParent, newStart, newEnd)) {
            System.out.println("Error: This time slot is unavailable. It clashes with an existing session for you or the selected parent.");
            return;
        }
        
        String dateForStorage = scheduledDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
        scheduler.addSession(new Session(this, selectedParent, dateForStorage, times[0], times[1]));
        System.out.println("Session scheduled successfully with " + selectedParent.getName() + ".");
        saveSessions(scheduler);
    }
    
    // --- Private Helper Methods (copied for self-containment) ---
    private int tryReadInt(Scanner scanner) {
        try { return Integer.parseInt(scanner.nextLine()); } 
        catch (NumberFormatException e) { return -1; }
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
    private void saveSessions(Scheduler scheduler) {
        try {
            scheduler.saveSessionsToFile("sessions.txt");
            System.out.println("Sessions automatically saved to sessions.txt");
        } catch (Exception e) {
            System.out.println("Save failed: " + e.getMessage());
        }
    }
}