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
            String input = scanner.nextLine().trim();
            String[] inputParts = input.split("\\s+", 2);
            String commandWord = inputParts[0];
            String arguments = inputParts.length > 1 ? inputParts[1].trim() : "";
            CommandType commandType = CommandType.from(commandWord);
            System.out.println(separator);

            try {
                switch (commandType) {
                case LIST:
                    System.out.println(" Here are the tasks in your list:");
                    for (int i = 0; i < tasks.size(); i++) {
                        System.out.println(" " + (i + 1) + "." + tasks.get(i));
                    }
                    break;
                case MARK:
                    int taskIndex = parseTaskIndex(arguments, tasks.size());
                    tasks.get(taskIndex).markAsDone();
                    System.out.println(" Nice! I've marked this task as done:");
                    System.out.println("   " + tasks.get(taskIndex));
                    break;
                case UNMARK:
                    int unmarkIndex = parseTaskIndex(arguments, tasks.size());
                    tasks.get(unmarkIndex).markAsNotDone();
                    System.out.println(" OK, I've marked this task as not done yet:");
                    System.out.println("   " + tasks.get(unmarkIndex));
                    break;
                case DELETE:
                    int deleteIndex = parseTaskIndex(arguments, tasks.size());
                    Task removedTask = tasks.remove(deleteIndex);
                    System.out.println(" Noted. I've removed this task:");
                    System.out.println("   " + removedTask);
                    printTaskCount(tasks.size());
                    break;
                case TODO:
                    if (arguments.isEmpty()) {
                        throw new CrowException("Error: Todo description cannot be empty.");
                    }
                    Task todo = new Todo(arguments);
                    tasks.add(todo);
                    printTaskAdded(todo, tasks.size());
                    break;
                case DEADLINE:
                    int byIndex = arguments.indexOf(" /by ");
                    if (byIndex <= 0 || arguments.substring(byIndex + 5).trim().isEmpty()) {
                        throw new CrowException("Error: Use deadline DESCRIPTION /by TIME.");
                    }
                    String deadlineDescription = arguments.substring(0, byIndex).trim();
                    String by = arguments.substring(byIndex + 5).trim();
                    Task deadline = new Deadline(deadlineDescription, by);
                    tasks.add(deadline);
                    printTaskAdded(deadline, tasks.size());
                    break;
                case EVENT:
                    int fromIndex = arguments.indexOf(" /from ");
                    if (fromIndex <= 0) {
                        throw new CrowException("Error: Use event DESCRIPTION /from START /to END.");
                    }
                    int toIndex = arguments.indexOf(" /to ", fromIndex + 7);
                    if (toIndex < 0) {
                        throw new CrowException("Error: Use event DESCRIPTION /from START /to END.");
                    }
                    String eventDescription = arguments.substring(0, fromIndex).trim();
                    String from = arguments.substring(fromIndex + 7, toIndex).trim();
                    String to = arguments.substring(toIndex + 5).trim();
                    if (from.isEmpty() || to.isEmpty()) {
                        throw new CrowException("Error: Use event DESCRIPTION /from START /to END.");
                    }
                    Task event = new Event(eventDescription, from, to);
                    tasks.add(event);
                    printTaskAdded(event, tasks.size());
                    break;
                case BYE:
                    System.out.println(" Bye. Hope to see you again soon!");
                    System.out.println(separator);
                    return;
                case UNKNOWN:
                default:
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
