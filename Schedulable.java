import java.time.LocalDate;
import java.util.Scanner;

public interface Schedulable {
    public void scheduleSession(Scanner scanner, Scheduler scheduler);
    public void rescheduleSession(Scanner scanner, Scheduler scheduler);
    public LocalDate getValidDate(Scanner scanner);
    public String[] getValidStartAndEndTimes(Scanner scanner);
    public void saveSessions(Scheduler scheduler);
    
}