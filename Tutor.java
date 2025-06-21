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
            System.out.println("\nYou have no sessions scheduled.");
            return;
        }
        System.out.println("\n--- My Schedule ---");
        for (Session s : mySessions) {
            System.out.println(s.getDetails());
        }
    }

}