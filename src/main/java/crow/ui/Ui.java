package crow.ui;

import crow.task.Task;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

/**
 * Handles text input from and output to the user.
 */
public class Ui {
    private static final String SEPARATOR = "_".repeat(60);
    private static final String BANNER = "  ___  ____   __   _  _ \n"
            + " / __)(  _ \\ /  \\ / )( \\\n"
            + "( (__  )   /(  O )\\ /\\ /\n"
            + " \\___)(__\\_) \\__/ (_/\\_)";

    private final Scanner scanner;
    private final PrintStream output;

    /**
     * Creates a UI that reads from standard input.
     */
    public Ui() {
        this(System.in, System.out);
    }

    /**
     * Creates a UI using the supplied input and output streams.
     * This constructor allows the UI to be tested without replacing the global console streams.
     *
     * @param input Stream from which commands are read.
     * @param output Stream to which messages are written.
     */
    public Ui(InputStream input, PrintStream output) {
        scanner = new Scanner(input);
        this.output = output;
    }

    /**
     * Displays the chatbot banner and greeting.
     */
    public void showWelcome() {
        output.println(SEPARATOR);
        output.println(BANNER);
        output.println("Hello! I'm Crow.");
        output.println("What can I do for you?");
        showSeparator();
    }

    /**
     * Returns whether another line of user input is available.
     *
     * @return {@code true} if another command can be read.
     */
    public boolean hasNextCommand() {
        return scanner.hasNextLine();
    }

    /**
     * Reads the next command entered by the user.
     *
     * @return Trimmed user command.
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /**
     * Displays all tasks with their one-based list numbers.
     *
     * @param tasks Tasks to display.
     */
    public void showTaskList(List<Task> tasks) {
        output.println(" Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            output.println(" " + (i + 1) + "." + tasks.get(i));
        }
    }

    /**
     * Displays tasks that match a search keyword.
     *
     * @param tasks Matching tasks to display.
     */
    public void showMatchingTasks(List<Task> tasks) {
        output.println(" Here are the matching tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            output.println(" " + (i + 1) + "." + tasks.get(i));
        }
    }

    /**
     * Displays confirmation that a task was marked as done.
     *
     * @param task Task that was marked.
     */
    public void showTaskMarked(Task task) {
        output.println(" Nice! I've marked this task as done:");
        output.println("   " + task);
    }

    /**
     * Displays confirmation that a task was marked as not done.
     *
     * @param task Task that was unmarked.
     */
    public void showTaskUnmarked(Task task) {
        output.println(" OK, I've marked this task as not done yet:");
        output.println("   " + task);
    }

    /**
     * Displays confirmation that a task was added.
     *
     * @param task Task that was added.
     * @param taskCount Number of tasks currently stored.
     */
    public void showTaskAdded(Task task, int taskCount) {
        output.println(" Got it. I've added this task:");
        output.println("   " + task);
        showTaskCount(taskCount);
    }

    /**
     * Displays confirmation that a task was deleted.
     *
     * @param task Task that was deleted.
     * @param taskCount Number of tasks currently stored.
     */
    public void showTaskDeleted(Task task, int taskCount) {
        output.println(" Noted. I've removed this task:");
        output.println("   " + task);
        showTaskCount(taskCount);
    }

    /**
     * Displays an error message.
     *
     * @param message Error message to display.
     */
    public void showError(String message) {
        output.println(" " + message);
    }

    /**
     * Displays a potentially multiline response from the command engine.
     *
     * @param response Response to display.
     */
    public void showResponse(String response) {
        output.println(" " + response.replace("\n", "\n "));
    }

    /**
     * Displays the farewell message and closing separator.
     */
    public void showGoodbye() {
        output.println(" Bye. Hope to see you again soon!");
        showSeparator();
    }

    /**
     * Displays a separator between user interactions.
     */
    public void showSeparator() {
        output.println(SEPARATOR);
    }

    /**
     * Displays the current number of tasks.
     */
    private void showTaskCount(int taskCount) {
        String taskWord = taskCount == 1 ? "task" : "tasks";
        output.println(" Now you have " + taskCount + " " + taskWord + " in the list.");
    }
}
