import java.io.Serializable;

public abstract class Person implements Serializable {
    protected String name;
    protected String username;
    protected String password;

    public Person() {}

    public Person(String name, String username, String password) {
        this.name = name;
        this.username = username;
        this.password = password;
    }

    public abstract void viewSessions(Scheduler scheduler);

    public String getName() { return name; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
}