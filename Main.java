import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Initialize scheduler and load all data from files
        Scheduler scheduler = new Scheduler();
        scheduler.loadTutors("tutors.txt");
        scheduler.loadParents("parents.txt");
        scheduler.loadSessions("sessions.txt");

        // Initialize authentication and load user credentials
        Authentication auth = new Authentication();
        auth.loadUsers("user.txt");
        
        Scanner scanner = new Scanner(System.in);
        Person loggedInUser = null;

        // Loop until a valid user logs in
        while (loggedInUser == null) {
            loggedInUser = auth.login(scanner, scheduler);
            if (loggedInUser == null) {
                System.out.println("Invalid username or password. Please try again.");
            }
        }

        System.out.println("\nLogin successful.");
        
        // Create the menu controller and pass it the logged-in user and other necessary objects
        Menu menu = new Menu(loggedInUser, scheduler, scanner);
        menu.show();

        scanner.close();
    }
}