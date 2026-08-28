package crow;

import crow.exception.CrowException;
import crow.parser.CommandType;
import crow.parser.Parser;
import crow.storage.Storage;
import crow.task.Task;
import crow.task.TaskList;
import crow.ui.Ui;

import java.nio.file.Path;
import java.util.List;

/**
 * Coordinates command parsing, task management, storage, and user responses.
 */
public class Crow {
    private static final Path DATA_FILE_PATH = Path.of("data", "crow.txt");

    private final Ui ui;
    private final Storage storage;
    private final TaskList tasks;
    private final String loadingError;

    /**
     * Creates a Crow chatbot using the project-relative data file.
     */
    public Crow() {
        this(DATA_FILE_PATH);
    }

    /**
     * Creates a Crow chatbot that saves tasks at the given path.
     *
     * @param filePath Path of the task data file.
     */
    public Crow(Path filePath) {
        this(filePath, new Ui());
    }

    /**
     * Creates a Crow chatbot with a supplied text UI.
     *
     * @param filePath Path of the task data file.
     * @param ui Text UI used when running Crow from the terminal.
     */
    public Crow(Path filePath, Ui ui) {
        this.ui = ui;
        storage = new Storage(filePath);

        TaskList loadedTasks;
        String loadError = null;
        try {
            loadedTasks = new TaskList(storage.load());
        } catch (CrowException e) {
            loadedTasks = new TaskList();
            loadError = e.getMessage();
        }
        tasks = loadedTasks;
        loadingError = loadError;
    }

    /**
     * Runs the terminal interface until input ends or the user enters {@code bye}.
     */
    public void run() {
        ui.showWelcome();
        if (loadingError != null) {
            ui.showError(loadingError);
            ui.showSeparator();
        }

        while (ui.hasNextCommand()) {
            String input = ui.readCommand();
            CommandType commandType = Parser.parseCommandType(input);
            ui.showSeparator();
            ui.showResponse(getResponse(input));
            ui.showSeparator();

            if (commandType == CommandType.BYE) {
                return;
            }
        }
    }

    /**
     * Executes one command and returns the response for either user interface.
     *
     * @param input Command entered by the user.
     * @return Response describing the command result or error.
     */
    public String getResponse(String input) {
        CommandType commandType = Parser.parseCommandType(input);
        String arguments = Parser.parseArguments(input);

        try {
            return switch (commandType) {
            case LIST -> formatTaskList("Here are the tasks in your list:", tasks.asList());
            case FIND -> formatTaskList("Here are the matching tasks in your list:",
                    tasks.find(Parser.parseFindKeyword(arguments)));
            case MARK -> markTask(arguments);
            case UNMARK -> unmarkTask(arguments);
            case DELETE -> deleteTask(arguments);
            case TODO -> addTask(Parser.parseTodo(arguments));
            case DEADLINE -> addTask(Parser.parseDeadline(arguments));
            case EVENT -> addTask(Parser.parseEvent(arguments));
            case BYE -> "Bye. Hope to see you again soon!";
            case UNKNOWN -> throw new CrowException("Error: Unknown command.");
            };
        } catch (CrowException e) {
            return e.getMessage();
        }
    }

    private String markTask(String arguments) throws CrowException {
        int taskIndex = Parser.parseTaskIndex(arguments, tasks.size());
        tasks.mark(taskIndex);
        storage.save(tasks.asList());
        return "Nice! I've marked this task as done:\n  " + tasks.get(taskIndex);
    }

    private String unmarkTask(String arguments) throws CrowException {
        int taskIndex = Parser.parseTaskIndex(arguments, tasks.size());
        tasks.unmark(taskIndex);
        storage.save(tasks.asList());
        return "OK, I've marked this task as not done yet:\n  " + tasks.get(taskIndex);
    }

    private String deleteTask(String arguments) throws CrowException {
        int taskIndex = Parser.parseTaskIndex(arguments, tasks.size());
        Task removedTask = tasks.delete(taskIndex);
        storage.save(tasks.asList());
        return "Noted. I've removed this task:\n  " + removedTask + "\n" + formatTaskCount();
    }

    private String addTask(Task task) throws CrowException {
        tasks.add(task);
        storage.save(tasks.asList());
        return "Got it. I've added this task:\n  " + task + "\n" + formatTaskCount();
    }

    private String formatTaskCount() {
        String taskWord = tasks.size() == 1 ? "task" : "tasks";
        return "Now you have " + tasks.size() + " " + taskWord + " in the list.";
    }

    private String formatTaskList(String heading, List<Task> taskList) {
        StringBuilder response = new StringBuilder(heading);
        for (int i = 0; i < taskList.size(); i++) {
            response.append("\n").append(i + 1).append(".").append(taskList.get(i));
        }
        return response.toString();
    }

    /**
     * Starts Crow using the project-relative data file.
     *
     * @param args Command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        new Crow(DATA_FILE_PATH).run();
    }
}
