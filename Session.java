import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Session implements Serializable {
    private Tutor tutor;
    private Parent parent;
    private String date;
    private String startTime;
    private String endTime;

    public Session(Tutor tutor, Parent parent, String date, String startTime, String endTime) {
        this.tutor = tutor;
        this.parent = parent;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // --- NEW SETTER METHODS ---
    public void setDate(String date) {
        this.date = date;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    // --- Other methods are unchanged ---
    public String toDataString() {
        return tutor.getName() + "," + parent.getName() + "," + this.date + "," + this.startTime + "," + this.endTime;
    }

    public String getDetails() {
        return "Session with Tutor: " + tutor.getName() + ", Parent: " + parent.getName() + ", Date: " + this.date + ", Start: " + startTime + ", End: " + endTime;
    }

    public Parent getParent() { return parent; }
    public Tutor getTutor() { return tutor; }

    public LocalDateTime getStartDateTime() {
        return LocalDateTime.parse(this.date + " " + this.startTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public LocalDateTime getEndDateTime() {
        return LocalDateTime.parse(this.date + " " + this.endTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
}