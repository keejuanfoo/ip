import java.util.Scanner;

/**
 * Starts the CROW chatbot application.
 */
public class Crow {
    public static void main(String[] args) {
        String separator = "_".repeat(60);
        String banner = "  ___  ____   __   _  _ \n"
                + " / __)(  _ \\ /  \\ / )( \\\n"
                + "( (__  )   /(  O )\\ /\\ /\n"
                + " \\___)(__\\_) \\__/ (_/\\_)";

        System.out.println(separator);
        System.out.println(banner);
        System.out.println("Hello! I'm Crow.");
        System.out.println("What can I do for you?");
        System.out.println(separator);

        Scanner scanner = new Scanner(System.in);
        String[] tasks = new String[100];
        int taskCount = 0;

        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            System.out.println(separator);

            if (command.equals("bye")) {
                System.out.println(" Bye. Hope to see you again soon!");
                System.out.println(separator);
                break;
            }

            if (command.equals("list")) {
                for (int i = 0; i < taskCount; i++) {
                    System.out.println(" " + (i + 1) + ". " + tasks[i]);
                }
            } else {
                tasks[taskCount] = command;
                taskCount++;
                System.out.println(" added: " + command);
            }

            System.out.println(separator);
        }
    }
}
