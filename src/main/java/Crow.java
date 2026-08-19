import java.util.ArrayList;
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
        ArrayList<Task> tasks = new ArrayList<>();

        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            System.out.println(separator);

            if (command.equals("bye")) {
                System.out.println(" Bye. Hope to see you again soon!");
                System.out.println(separator);
                break;
            }

            try {
                if (command.equals("list")) {
                    System.out.println(" Here are the tasks in your list:");
                    for (int i = 0; i < tasks.size(); i++) {
                        System.out.println(" " + (i + 1) + "." + tasks.get(i));
                    }
                } else if (command.equals("mark") || command.startsWith("mark ")) {
                    int taskIndex = parseTaskIndex(command.substring(4), tasks.size());
                    tasks.get(taskIndex).markAsDone();
                    System.out.println(" Nice! I've marked this task as done:");
                    System.out.println("   " + tasks.get(taskIndex));
                } else if (command.equals("unmark") || command.startsWith("unmark ")) {
                    int taskIndex = parseTaskIndex(command.substring(6), tasks.size());
                    tasks.get(taskIndex).markAsNotDone();
                    System.out.println(" OK, I've marked this task as not done yet:");
                    System.out.println("   " + tasks.get(taskIndex));
                } else if (command.equals("delete") || command.startsWith("delete ")) {
                    int taskIndex = parseTaskIndex(command.substring(6), tasks.size());
                    Task removedTask = tasks.remove(taskIndex);
                    System.out.println(" Noted. I've removed this task:");
                    System.out.println("   " + removedTask);
                    printTaskCount(tasks.size());
                } else if (command.equals("todo") || command.startsWith("todo ")) {
                    String description = command.substring(4).trim();
                    if (description.isEmpty()) {
                        throw new CrowException("Error: Todo description cannot be empty.");
                    }
                    Task task = new Todo(description);
                    tasks.add(task);
                    printTaskAdded(task, tasks.size());
                } else if (command.equals("deadline") || command.startsWith("deadline ")) {
                    String taskDetails = command.substring(8).trim();
                    int byIndex = taskDetails.indexOf(" /by ");
                    if (byIndex <= 0 || taskDetails.substring(byIndex + 5).trim().isEmpty()) {
                        throw new CrowException("Error: Use deadline DESCRIPTION /by TIME.");
                    }
                    String description = taskDetails.substring(0, byIndex).trim();
                    String by = taskDetails.substring(byIndex + 5).trim();
                    Task task = new Deadline(description, by);
                    tasks.add(task);
                    printTaskAdded(task, tasks.size());
                } else if (command.equals("event") || command.startsWith("event ")) {
                    String taskDetails = command.substring(5).trim();
                    int fromIndex = taskDetails.indexOf(" /from ");
                    if (fromIndex <= 0) {
                        throw new CrowException("Error: Use event DESCRIPTION /from START /to END.");
                    }
                    int toIndex = taskDetails.indexOf(" /to ", fromIndex + 7);
                    if (toIndex < 0) {
                        throw new CrowException("Error: Use event DESCRIPTION /from START /to END.");
                    }
                    String description = taskDetails.substring(0, fromIndex).trim();
                    String from = taskDetails.substring(fromIndex + 7, toIndex).trim();
                    String to = taskDetails.substring(toIndex + 5).trim();
                    if (from.isEmpty() || to.isEmpty()) {
                        throw new CrowException("Error: Use event DESCRIPTION /from START /to END.");
                    }
                    Task task = new Event(description, from, to);
                    tasks.add(task);
                    printTaskAdded(task, tasks.size());
                } else {
                    throw new CrowException("Error: Unknown command.");
                }
            } catch (CrowException e) {
                System.out.println(" " + e.getMessage());
            }

            System.out.println(separator);
        }
    }

    /**
     * Converts a user-provided task number into a valid list index.
     *
     * @param input User-provided task number.
     * @param taskCount Number of tasks currently stored.
     * @return Zero-based array index of the task.
     * @throws CrowException If the input is not a number of an existing task.
     */
    private static int parseTaskIndex(String input, int taskCount) throws CrowException {
        try {
            int taskIndex = Integer.parseInt(input.trim()) - 1;
            if (taskIndex < 0 || taskIndex >= taskCount) {
                throw new CrowException("Error: Enter a valid task number.");
            }
            return taskIndex;
        } catch (NumberFormatException e) {
            throw new CrowException("Error: Enter a valid task number.");
        }
    }

    /**
     * Prints confirmation that a task was added.
     *
     * @param task Task that was added.
     * @param taskCount Number of tasks currently stored.
     */
    private static void printTaskAdded(Task task, int taskCount) {
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + task);
        printTaskCount(taskCount);
    }

    /**
     * Prints the number of tasks currently stored.
     *
     * @param taskCount Number of tasks currently stored.
     */
    private static void printTaskCount(int taskCount) {
        String taskWord = taskCount == 1 ? "task" : "tasks";
        System.out.println(" Now you have " + taskCount + " " + taskWord + " in the list.");
    }
}
