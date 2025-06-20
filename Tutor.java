public class Tutor extends Person {
    private String subject;

    public Tutor(String name, String username, String password, String subject) {
        super(name, username, password);
        this.subject = subject;
    }
    
    public String getSubject() { 
        return subject; 
    }
}