import javax.swing.JOptionPane;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

public class Parent extends Person implements Schedulable{
    private String childName;

    public Parent(String name, String username, String password, String childName) {
        super(name, username, password);
        this.childName = childName;
    }

    @Override
    public void viewSessions(Scheduler scheduler) {
        ArrayList<Session> mySessions = scheduler.getSessionsForParent(this);
        if (mySessions.isEmpty()) {
            JOptionPane.showMessageDialog(null, "You do not have any scheduled sessions.");
            return;
        }
        
        StringBuilder sessionList = new StringBuilder();
        sessionList.append("--- My Scheduled Sessions ---\n");
        for (Session s : mySessions) {
            sessionList.append(s.getDetails()).append("\n");
        }
        JOptionPane.showMessageDialog(null, sessionList.toString(), "My Sessions", JOptionPane.INFORMATION_MESSAGE);
    }
    public void scheduleSession(Scheduler scheduler) {
        if (scheduler.getTutors().isEmpty()) {
            JOptionPane.showMessageDialog(null, "There are no tutors available to schedule a session with.");
            return;
        }

        StringBuilder tutorListText = new StringBuilder("Available Tutors:\n");
        ArrayList<Tutor> tutors = scheduler.getTutors();
        for (int i = 0; i < tutors.size(); i++) {
            tutorListText.append((i + 1) + ": " + tutors.get(i).getName() + " (" + tutors.get(i).getSubject() + ")\n");
        }
        tutorListText.append("\nChoose Tutor number:");
        String tutorChoiceStr = JOptionPane.showInputDialog(tutorListText.toString());
        if(tutorChoiceStr == null) return;
        
        int tutorIndex = tryReadInt(tutorChoiceStr) - 1;
        if (tutorIndex < 0 || tutorIndex >= tutors.size()) {
            JOptionPane.showMessageDialog(null, "Invalid tutor number.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Tutor selectedTutor = tutors.get(tutorIndex);
        
        LocalDate scheduledDate = getValidDate();
        if (scheduledDate == null) return;
        String[] times = getValidStartAndEndTimes();
        if (times == null) return;
        
        LocalDateTime newStart = LocalDateTime.of(scheduledDate, LocalTime.parse(times[0]));
        LocalDateTime newEnd = LocalDateTime.of(scheduledDate, LocalTime.parse(times[1]));
        if (scheduler.doesSessionClash(selectedTutor, this, newStart, newEnd)) {
            JOptionPane.showMessageDialog(null, "Error: This time slot is unavailable.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String dateForStorage = scheduledDate.format(DateTimeFormatter.ISO_LOCAL_DATE);
        scheduler.addSession(new Session(selectedTutor, this, dateForStorage, times[0], times[1]));
        JOptionPane.showMessageDialog(null, "Session scheduled successfully with " + selectedTutor.getName() + ".");
        saveSessions(scheduler);
    }
    public void rescheduleSession(Scheduler scheduler) {
        ArrayList<Session> parentSessions = scheduler.getSessionsForParent(this);
        if (parentSessions.isEmpty()) {
            JOptionPane.showMessageDialog(null, "You have no sessions to reschedule.");
            return;
        }

        StringBuilder sessionList = new StringBuilder("Select a session to reschedule:\n");
        for (int i = 0; i < parentSessions.size(); i++) {
            sessionList.append((i + 1) + ": " + parentSessions.get(i).getDetails()).append("\n");
        }
        sessionList.append("\nChoose session number:");
        String choiceStr = JOptionPane.showInputDialog(sessionList.toString());
        if(choiceStr == null) return;

        int sessionIndex = tryReadInt(choiceStr) - 1;
        if (sessionIndex < 0 || sessionIndex >= parentSessions.size()) {
            JOptionPane.showMessageDialog(null, "Invalid session number.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Session sessionToReschedule = parentSessions.get(sessionIndex);
        
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

        if (scheduler.doesSessionClash(sessionToReschedule, sessionToReschedule.getTutor(), this, newStart, newEnd)) {
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
    
    public String getChildName() {
        return childName;
    }
    public int tryReadInt(String input) {
        try { return Integer.parseInt(input); } 
        catch (NumberFormatException e) { return -1; }
    }
    public LocalDate getValidDate() {
        LocalDate parsedDate = null;
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        while (parsedDate == null) {
            String dateStr = JOptionPane.showInputDialog("Enter Date (DD/MM/YYYY):");
            if(dateStr == null) return null;

            String[] parts = dateStr.split("/");
            if (parts.length != 3) {
                JOptionPane.showMessageDialog(null, "Error: Invalid date format. Please use DD/MM/YYYY.", "Error", JOptionPane.ERROR_MESSAGE);
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

    public String[] getValidStartAndEndTimes() {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime startTime, endTime;
        while (true) {
            try {
                String startStr = JOptionPane.showInputDialog("Enter Start Time (HH:MM 24-hour format):");
                if (startStr == null) return null;
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
                JOptionPane.showMessageDialog(null, "Error: Invalid time format. Please use HH:mm and try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    public void saveSessions(Scheduler scheduler) {
        try {
            scheduler.saveSessionsToFile("sessions.txt");
            JOptionPane.showMessageDialog(null, "Sessions automatically saved to sessions.txt");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Save failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}