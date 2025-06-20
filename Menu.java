import java.util.Scanner;

public class Menu {
    private Person loggedInUser;
    private Scheduler scheduler;
    private Scanner scanner;

    public Menu(Person user, Scheduler scheduler, Scanner scanner) {
        this.loggedInUser = user;
        this.scheduler = scheduler;
        this.scanner = scanner;
    }

    public void show() {
        if (loggedInUser instanceof Admin adminUser) {
            showAdminMenu(adminUser);
        } else if (loggedInUser instanceof Parent parentUser) {
            showParentMenu(parentUser);
        } else if (loggedInUser instanceof Tutor tutorUser) {
            showTutorMenu(tutorUser);
        } else {
            System.out.println("Unknown user role. Exiting.");
        }
    }

    private void showAdminMenu(Admin adminUser) {
        while (true) {
            System.out.println("\n---- Admin Menu ----");
            System.out.println("1. Add Tutor");
            System.out.println("2. Add Parent");
            System.out.println("3. Schedule Session");
            System.out.println("4. Reschedule a Session");
            System.out.println("5. View All Sessions");
            System.out.println("6. View All Tutors");
            System.out.println("7. View All Parents");
            System.out.println("8. Delete a Session");
            System.out.println("9. Delete a Tutor");
            System.out.println("10. Delete a Parent");
            System.out.println("11. Exit");
            System.out.print("Choose option: ");

            int option;
            try {
                option = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Enter a number.");
                continue;
            }

            switch (option) {
                case 1 -> adminUser.addTutor(scanner, scheduler);
                case 2 -> adminUser.addParent(scanner, scheduler);
                case 3 -> adminUser.scheduleSession(scanner, scheduler); // Assuming Admin implements Schedulable
                case 4 -> adminUser.rescheduleSession(scanner, scheduler);
                case 5 -> adminUser.viewSessions(scheduler); // UPDATED CALL
                case 6 -> adminUser.viewAllTutors(scheduler);
                case 7 -> adminUser.viewAllParents(scheduler);
                case 8 -> adminUser.deleteSession(scanner, scheduler);
                case 9 -> adminUser.deleteTutor(scanner, scheduler);
                case 10 -> adminUser.deleteParent(scanner, scheduler);
                case 11 -> {
                    System.out.println("Exiting system.");
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private void showParentMenu(Parent parentUser) {
        System.out.println("\nWelcome, " + parentUser.getName() + "!");
        while (true) {
            System.out.println("\n---- Parent Menu ----");
            System.out.println("1. Schedule a Session");
            System.out.println("2. Reschedule My Session");
            System.out.println("3. View My Sessions");
            System.out.println("4. Cancel My Session");
            System.out.println("5. Exit");
            System.out.print("Choose option: ");

            int option;
            try {
                option = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Enter a number.");
                continue;
            }

            switch (option) {
                case 1 -> parentUser.scheduleSession(scanner, scheduler); // Assuming Parent implements Schedulable
                case 2 -> parentUser.rescheduleMySession(scanner, scheduler);
                case 3 -> parentUser.viewSessions(scheduler); // UPDATED CALL
                case 4 -> parentUser.cancelMySession(scanner, scheduler);
                case 5 -> {
                    System.out.println("Exiting system.");
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }
    
    private void showTutorMenu(Tutor tutorUser) {
        System.out.println("\nWelcome, " + tutorUser.getName() + "!");
        while (true) {
            System.out.println("\n---- Tutor Menu ----");
            System.out.println("1. View My Schedule");
            System.out.println("2. Exit");
            System.out.print("Choose option: ");

            int option;
            try {
                option = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Enter a number.");
                continue;
            }

            switch (option) {
                case 1 -> tutorUser.viewSessions(scheduler); // UPDATED CALL
                case 2 -> {
                    System.out.println("Exiting system.");
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }
}