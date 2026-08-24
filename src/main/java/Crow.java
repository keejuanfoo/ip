import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Starts the CROW chatbot application.
 */
public class Crow {
    // relative to where the program is called
    private static final Path DATA_FILE_PATH = Path.of("data", "crow.txt");
    private static final DateTimeFormatter INPUT_DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("d/M/uuuu HHmm").withResolverStyle(ResolverStyle.STRICT);

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
        Storage storage = new Storage(DATA_FILE_PATH);
        ArrayList<Task> tasks;
        try {
            tasks = storage.load();
        } catch (CrowException e) {
            System.out.println(" " + e.getMessage());
            System.out.println(separator);
            tasks = new ArrayList<>();
        }

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
                    storage.save(tasks);
                    System.out.println(" Nice! I've marked this task as done:");
                    System.out.println("   " + tasks.get(taskIndex));
                    break;
                case UNMARK:
                    int unmarkIndex = parseTaskIndex(arguments, tasks.size());
                    tasks.get(unmarkIndex).markAsNotDone();
                    storage.save(tasks);
                    System.out.println(" OK, I've marked this task as not done yet:");
                    System.out.println("   " + tasks.get(unmarkIndex));
                    break;
                case DELETE:
                    int deleteIndex = parseTaskIndex(arguments, tasks.size());
                    Task removedTask = tasks.remove(deleteIndex);
                    storage.save(tasks);
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
                    storage.save(tasks);
                    printTaskAdded(todo, tasks.size());
                    break;
                case DEADLINE:
                    int byIndex = arguments.indexOf(" /by ");
                    if (byIndex <= 0 || arguments.substring(byIndex + 5).trim().isEmpty()) {
                        throw new CrowException("Error: Use deadline DESCRIPTION /by d/M/yyyy HHmm.");
                    }
                    String deadlineDescription = arguments.substring(0, byIndex).trim();
                    LocalDateTime by = parseDateTime(arguments.substring(byIndex + 5).trim());
                    Task deadline = new Deadline(deadlineDescription, by);
                    tasks.add(deadline);
                    storage.save(tasks);
                    printTaskAdded(deadline, tasks.size());
                    break;
                case EVENT:
                    int fromIndex = arguments.indexOf(" /from ");
                    if (fromIndex <= 0) {
                        throw new CrowException(
                                "Error: Use event DESCRIPTION /from d/M/yyyy HHmm /to d/M/yyyy HHmm.");
                    }
                    int toIndex = arguments.indexOf(" /to ", fromIndex + 7);
                    if (toIndex < 0) {
                        throw new CrowException(
                                "Error: Use event DESCRIPTION /from d/M/yyyy HHmm /to d/M/yyyy HHmm.");
                    }
                    String eventDescription = arguments.substring(0, fromIndex).trim();
                    String fromInput = arguments.substring(fromIndex + 7, toIndex).trim();
                    String toInput = arguments.substring(toIndex + 5).trim();
                    if (fromInput.isEmpty() || toInput.isEmpty()) {
                        throw new CrowException(
                                "Error: Use event DESCRIPTION /from d/M/yyyy HHmm /to d/M/yyyy HHmm.");
                    }
                    LocalDateTime from = parseDateTime(fromInput);
                    LocalDateTime to = parseDateTime(toInput);
                    Task event = new Event(eventDescription, from, to);
                    tasks.add(event);
                    storage.save(tasks);
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
     * Parses a date and time written in the format {@code d/M/yyyy HHmm}.
     *
     * @param input Date entered by the user.
     * @return Parsed date and time.
     * @throws CrowException If the date-time is absent, invalid, or in another format.
     */
    private static LocalDateTime parseDateTime(String input) throws CrowException {
        try {
            return LocalDateTime.parse(input, INPUT_DATE_TIME_FORMAT);
        } catch (DateTimeParseException e) {
            throw new CrowException("Error: Date and time must use d/M/yyyy HHmm format.");
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
