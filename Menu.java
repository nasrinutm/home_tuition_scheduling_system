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
            System.out.println("1. Schedule Session");
            System.out.println("2. Reschedule a Session");
            System.out.println("3. View All Sessions");
            System.out.println("4. View All Tutors");
            System.out.println("5. View All Parents");
            System.out.println("6. Exit");
            System.out.print("Choose option: ");

            int option;
            try {
                option = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Enter a number.");
                continue;
            }

            switch (option) {
                case 1:
                    adminUser.scheduleSession(scanner, scheduler);
                    break;
                case 2:
                    adminUser.rescheduleSession(scanner, scheduler);
                    break;
                case 3:
                    adminUser.viewSessions(scheduler);
                    break;
                case 4:
                    adminUser.viewAllTutors(scheduler);
                    break;
                case 5:
                    adminUser.viewAllParents(scheduler);
                    break;
                case 6:
                    System.out.println("Exiting system.");
                    return;
                default:
                    System.out.println("Invalid option.");
                    break;
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
            System.out.println("4. Exit");
            System.out.print("Choose option: ");

            int option;
            try {
                option = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Enter a number.");
                continue;
            }

            switch (option) {
                case 1:
                    parentUser.scheduleSession(scanner, scheduler);
                    break;
                case 2:
                    parentUser.rescheduleMySession(scanner, scheduler);
                    break;
                case 3:
                    parentUser.viewSessions(scheduler);
                    break;
                case 4:
                    System.out.println("Exiting system.");
                    return;
                default:
                    System.out.println("Invalid option.");
                    break;
            }
        }
    }
    
    private void showTutorMenu(Tutor tutorUser) {
        System.out.println("\nWelcome, " + tutorUser.getName() + "!");
        while (true) {
            System.out.println("\n---- Tutor Menu ----");
            System.out.println("1. Schedule a Session");
            System.out.println("2. View My Schedule");
            System.out.println("3. Exit");
            System.out.print("Choose option: ");

            int option;
            try {
                option = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Enter a number.");
                continue;
            }

            switch (option) {
                case 1:
                    tutorUser.scheduleSession(scanner, scheduler);
                    break;
                case 2:
                    tutorUser.viewSessions(scheduler);
                    break;
                case 3:
                    System.out.println("Exiting system.");
                    return;
                default:
                    System.out.println("Invalid option.");
                    break;
            }
        }
    }
}