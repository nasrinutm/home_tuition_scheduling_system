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
        if (loggedInUser instanceof Admin) {
            System.out.println("Welcome, " + loggedInUser.getName() + ".");
            showAdminMenu();
        } else if (loggedInUser instanceof Parent) {
            System.out.println("Welcome, " + loggedInUser.getName() + ".");
            showParentMenu();
        } else {
            System.out.println("Welcome, " + loggedInUser.getName() + ".");
            System.out.println("There are no actions available for your role at this time. Exiting.");
        }
    }

    private void showAdminMenu() {
        Admin adminUser = (Admin) loggedInUser;

        while (true) {
            System.out.println("\n---- Admin Menu ----");
            System.out.println("1. Add Tutor");
            System.out.println("2. Add Parent");
            System.out.println("3. Schedule Session");
            System.out.println("4. Reschedule a Session"); // --- NEW ---
            System.out.println("5. View All Sessions");
            System.out.println("6. View All Tutors");
            System.out.println("7. View All Parents");
            System.out.println("8. Delete a Session");
            System.out.println("9. Delete a Tutor");
            System.out.println("10. Delete a Parent");
            System.out.println("11. Exit"); // --- RE-NUMBERED ---
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
                    adminUser.addTutor(scanner, scheduler);
                    break;
                case 2:
                    adminUser.addParent(scanner, scheduler);
                    break;
                case 3:
                    adminUser.scheduleSession(scanner, scheduler);
                    break;
                case 4:
                    adminUser.rescheduleSession(scanner, scheduler);
                    break;
                case 5:
                    adminUser.viewAllSessions(scheduler);
                    break;
                case 6:
                    adminUser.viewAllTutors(scheduler);
                    break;
                case 7:
                    adminUser.viewAllParents(scheduler);
                    break;
                case 8:
                    adminUser.deleteSession(scanner, scheduler);
                    break;
                case 9:
                    adminUser.deleteTutor(scanner, scheduler);
                    break;
                case 10:
                    adminUser.deleteParent(scanner, scheduler);
                    break;
                case 11:
                    System.out.println("Exiting system.");
                    return;
                default:
                    System.out.println("Invalid option.");
                    break;
            }
        }
    }

    private void showParentMenu() {
        Parent parentUser = (Parent) loggedInUser;
        System.out.println("\nWelcome, " + parentUser.getName() + "!");

        while (true) {
            System.out.println("\n---- Parent Menu ----");
            System.out.println("1. Schedule a Session");
            System.out.println("2. Reschedule My Session"); // --- NEW ---
            System.out.println("3. View My Sessions");
            System.out.println("4. Cancel My Session");
            System.out.println("5. Exit"); // --- RE-NUMBERED ---
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
                    parentUser.viewMySessions(scheduler);
                    break;
                case 4:
                    parentUser.cancelMySession(scanner, scheduler);
                    break;
                case 5:
                    System.out.println("Exiting system.");
                    return;
                default:
                    System.out.println("Invalid option.");
                    break;
            }
        }
    }
}