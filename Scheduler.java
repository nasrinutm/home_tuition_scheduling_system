import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Scheduler {
    private ArrayList<Tutor> tutors = new ArrayList<>();
    private ArrayList<Parent> parents = new ArrayList<>();
    private ArrayList<Session> sessions = new ArrayList<>();

    // --- NEW METHOD FOR USERNAME VALIDATION ---
    /**
     * Checks if a username is already in use by any Tutor or Parent.
     * @param username The username to check.
     * @return true if the username is taken, false otherwise.
     */
    public boolean isUsernameTaken(String username) {
        // Check against all tutor usernames
        for (Tutor tutor : tutors) {
            if (tutor.getUsername().equalsIgnoreCase(username)) {
                return true;
            }
        }
        // Check against all parent usernames
        for (Parent parent : parents) {
            if (parent.getUsername().equalsIgnoreCase(username)) {
                return true;
            }
        }
        return false;
    }
    
    // --- Other methods are unchanged ---
    public ArrayList<Tutor> getTutors() { return tutors; }
    public ArrayList<Parent> getParents() { return parents; }
    public ArrayList<Session> getSessions() { return sessions; }
    public void addTutor(Tutor t) { tutors.add(t); }
    public void addParent(Parent p) { parents.add(p); }
    public void addSession(Session s) { sessions.add(s); }
    public void deleteTutor(Tutor tutorToDelete) {
        sessions.removeIf(session -> session.getTutor().equals(tutorToDelete));
        tutors.remove(tutorToDelete);
    }
    public void deleteParent(Parent parentToDelete) {
        sessions.removeIf(session -> session.getParent().equals(parentToDelete));
        parents.remove(parentToDelete);
    }
    public void deleteSessionByIndex(int index) {
        if (index >= 0 && index < sessions.size()) { sessions.remove(index); }
    }
    public void deleteSession(Session sessionToDelete) { sessions.remove(sessionToDelete); }
    public ArrayList<Session> getSessionsForParent(Parent parentToFind) {
        ArrayList<Session> parentSessions = new ArrayList<>();
        for (Session s : sessions) {
            if (s.getParent().equals(parentToFind)) {
                parentSessions.add(s);
            }
        }
        return parentSessions;
    }
    public boolean doesSessionClash(Session sessionToExclude, Tutor tutor, Parent parent, LocalDateTime newStart, LocalDateTime newEnd) {
        for (Session existingSession : sessions) {
            if (existingSession.equals(sessionToExclude)) {
                continue;
            }
            LocalDateTime existingStart = existingSession.getStartDateTime();
            LocalDateTime existingEnd = existingSession.getEndDateTime();
            boolean overlaps = newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart);
            if (overlaps) {
                if (existingSession.getTutor().equals(tutor) || existingSession.getParent().equals(parent)) {
                    return true;
                }
            }
        }
        return false;
    }
    public boolean doesSessionClash(Tutor newTutor, Parent newParent, LocalDateTime newStart, LocalDateTime newEnd) {
        for (Session existingSession : sessions) {
            LocalDateTime existingStart = existingSession.getStartDateTime();
            LocalDateTime existingEnd = existingSession.getEndDateTime();
            boolean overlaps = newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart);
            if (overlaps) {
                if (existingSession.getTutor().equals(newTutor) || existingSession.getParent().equals(newParent)) {
                    return true;
                }
            }
        }
        return false;
    }
    public Parent findParentByUsername(String username) {
        for (Parent p : parents) {
            if (p.getUsername().equals(username)) {
                return p;
            }
        }
        return null;
    }
    private Tutor findTutorByName(String name) {
        for (Tutor t : tutors) {
            if (t.getName().equals(name)) { return t; }
        }
        return null;
    }
    private Parent findParentByName(String name) {
        for (Parent p : parents) {
            if (p.getName().equals(name)) { return p; }
        }
        return null;
    }
    public void exportTutors(String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            for (Tutor t : tutors) {
                writer.println(t.getName() + "," + t.getUsername() + "," + t.getPassword() + "," + t.getSubject());
            }
        }
    }
    public void exportParents(String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            for (Parent p : parents) {
                writer.println(p.getName() + "," + p.getUsername() + "," + p.getPassword() + "," + p.getChildName());
            }
        }
    }
    public void saveSessionsToFile(String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            for (Session s : sessions) {
                writer.println(s.toDataString());
            }
        }
    }
    public void loadTutors(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    tutors.add(new Tutor(parts[0], parts[1], parts[2], parts[3]));
                }
            }
        } catch (IOException e) { System.out.println("Info: tutors.txt not found, starting fresh."); }
    }
    public void loadParents(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    parents.add(new Parent(parts[0], parts[1], parts[2], parts[3]));
                }
            }
        } catch (IOException e) { System.out.println("Info: parents.txt not found, starting fresh."); }
    }
    public void loadSessions(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    Tutor foundTutor = findTutorByName(parts[0]);
                    Parent foundParent = findParentByName(parts[1]);
                    if (foundTutor != null && foundParent != null) {
                        sessions.add(new Session(foundTutor, foundParent, parts[2], parts[3], parts[4]));
                    } else {
                        System.out.println("Warning: Could not link session. Profile for Tutor '" + parts[0] + "' or Parent '" + parts[1] + "' not found.");
                    }
                }
            }
        } catch (IOException e) { System.out.println("Info: sessions.txt not found, starting fresh."); }
    }
}