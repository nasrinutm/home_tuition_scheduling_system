import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Scheduler {
    private ArrayList<Tutor> tutors = new ArrayList<>();
    private ArrayList<Parent> parents = new ArrayList<>();
    private ArrayList<Session> sessions = new ArrayList<>();

    // --- METHOD RE-ADDED ---
    public void deleteSessionByIndex(int index) {
        if (index >= 0 && index < sessions.size()) {
            sessions.remove(index);
        }
    }

    // --- Other methods are unchanged ---
    public ArrayList<Tutor> getTutors() { return tutors; }
    public ArrayList<Parent> getParents() { return parents; }
    public ArrayList<Session> getSessions() { return sessions; }
    public void addTutor(Tutor t) { tutors.add(t); }
    public void addParent(Parent p) { parents.add(p); }
    public void addSession(Session s) { sessions.add(s); }
    public ArrayList<Session> getSessionsForParent(Parent parentToFind) {
        ArrayList<Session> parentSessions = new ArrayList<>();
        for (Session s : sessions) {
            if (s.getParent().equals(parentToFind)) {
                parentSessions.add(s);
            }
        }
        return parentSessions;
    }
    public ArrayList<Session> getSessionsForTutor(Tutor tutorToFind) {
        ArrayList<Session> tutorSessions = new ArrayList<>();
        for (Session s : sessions) {
            if (s.getTutor().equals(tutorToFind)) {
                tutorSessions.add(s);
            }
        }
        return tutorSessions;
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
    public boolean isUsernameTaken(String username) {
        for (Tutor tutor : tutors) {
            if (tutor.getUsername().equalsIgnoreCase(username)) {
                return true;
            }
        }
        for (Parent parent : parents) {
            if (parent.getUsername().equalsIgnoreCase(username)) {
                return true;
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
    public Tutor findTutorByUsername(String username) {
        for (Tutor t : tutors) {
            if (t.getUsername().equals(username)) {
                return t;
            }
        }
        return null;
    }
    public Tutor findTutorByName(String name) {
        for (Tutor t : tutors) {
            if (t.getName().equals(name)) { return t; }
        }
        return null;
    }
    public Parent findParentByName(String name) {
        for (Parent p : parents) {
            if (p.getName().equals(name)) { return p; }
        }
        return null;
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
                if (line.trim().isEmpty() || line.trim().startsWith("#")) {
                continue;
            }
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
                if (line.trim().isEmpty() || line.trim().startsWith("#")) {
                continue;
            }
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
                if (line.trim().isEmpty() || line.trim().startsWith("#")) {
                continue;
            }
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