import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Authentication {
    private List<String[]> users = new ArrayList<>();

    public void loadUsers(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    users.add(parts);
                }
            }
        } catch (IOException e) {
            System.out.println("Warning: No user file found or error reading file.");
        }
    }

    public Person login(Scanner scanner, Scheduler scheduler) {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        for (String[] user : users) {
            if (user[0].equals(username) && user[1].equals(password)) {
                String role = user[2];
                if ("admin".equals(role)) {
                    return new Admin(username, password);
                } else if ("parent".equals(role)) {
                    Parent p = scheduler.findParentByUsername(username);
                    if (p == null) {
                        System.out.println("Login Error: A user account exists for '" + username + "', but no matching parent profile was found in parents.txt.");
                        return null;
                    }
                    return p;
                }
            }
        }
        return null; 
    }
}