import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Scanner;

public class Parent extends Person {
    private String childName;

    public Parent(String name, String username, String password, String childName) {
        super(name, username, password);
        this.childName = childName;
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

                // --- NEW VALIDATION: Check for same-day booking after 4 PM ---
                if (parsedDate.equals(LocalDate.now()) && LocalTime.now().getHour() >= 16) {
                    System.out.println("Error: It is past 4 PM, so you cannot book a session for today.");
                    parsedDate = null; // Reset to loop again
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

    // --- Other methods are unchanged ---
    public void viewMySessions(Scheduler scheduler) {
        ArrayList<Session> mySessions = scheduler.getSessionsForParent(this);
        if (mySessions.isEmpty()) {
            System.out.println("\nYou do not have any scheduled sessions.");
            return;
        }
        System.out.println("\n--- My Scheduled Sessions ---");
        for (Session s : mySessions) {
            System.out.println(s.getDetails());
        }
    }
    public void scheduleSession(Scanner scanner, Scheduler scheduler) {
        if (scheduler.getTutors().isEmpty()) {
            System.out.println("There are no tutors available to schedule a session with.");
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
        LocalDate scheduledDate = getValidDate(scanner);
        if (scheduledDate == null) return;
        String[] times = getValidStartAndEndTimes(scanner, scheduledDate, 48);
        if (times == null) return;
        LocalDateTime newStart = LocalDateTime.of(scheduledDate, LocalTime.parse(times[0]));
        LocalDateTime newEnd = LocalDateTime.of(scheduledDate, LocalTime.parse(times[1]));
        if (scheduler.doesSessionClash(selectedTutor, this, newStart, newEnd)) {
            System.out.println("Error: This time slot is unavailable. It clashes with an existing session for you or the selected tutor.");
            return;
        }
        String dateForStorage = scheduledDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
        scheduler.addSession(new Session(selectedTutor, this, dateForStorage, times[0], times[1]));
        System.out.println("Session scheduled successfully with " + selectedTutor.getName() + ".");
        saveSessions(scheduler);
    }
    public void rescheduleMySession(Scanner scanner, Scheduler scheduler) {
        ArrayList<Session> parentSessions = scheduler.getSessionsForParent(this);
        if (parentSessions.isEmpty()) {
            System.out.println("You have no sessions to reschedule.");
            return;
        }

        System.out.println("\nSelect a session to reschedule:");
        for (int i = 0; i < parentSessions.size(); i++) {
            System.out.println((i + 1) + ": " + parentSessions.get(i).getDetails());
        }

        System.out.print("Choose session number: ");
        int sessionIndex = tryReadInt(scanner) - 1;
        if (sessionIndex < 0 || sessionIndex >= parentSessions.size()) {
            System.out.println("Invalid session number.");
            return;
        }
        Session sessionToReschedule = parentSessions.get(sessionIndex);
        
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

        if (scheduler.doesSessionClash(sessionToReschedule, sessionToReschedule.getTutor(), this, newStart, newEnd)) {
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
    
    public void cancelMySession(Scanner scanner, Scheduler scheduler) {
        ArrayList<Session> parentSessions = scheduler.getSessionsForParent(this);
        if (parentSessions.isEmpty()) {
            System.out.println("You have no sessions to cancel.");
            return;
        }
        System.out.println("\nSelect a session to cancel:");
        for (int i = 0; i < parentSessions.size(); i++) {
            System.out.println((i + 1) + ": " + parentSessions.get(i).getDetails());
        }
        System.out.print("Choose session number: ");
        int sessionIndex = tryReadInt(scanner) - 1;
        if (sessionIndex < 0 || sessionIndex >= parentSessions.size()) {
            System.out.println("Invalid session number.");
            return;
        }
        Session sessionToDelete = parentSessions.get(sessionIndex);
        scheduler.deleteSession(sessionToDelete);
        System.out.println("Session canceled successfully.");
        saveSessions(scheduler);
    }
    
    public String getChildName() {
        return childName;
    }
    private int tryReadInt(Scanner scanner) {
        try { return Integer.parseInt(scanner.nextLine()); } 
        catch (NumberFormatException e) { return -1; }
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