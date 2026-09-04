package crow;

import crow.exception.CrowException;
import crow.parser.CommandType;
import crow.parser.Parser;
import crow.storage.Storage;
import crow.task.Deadline;
import crow.task.Event;
import crow.task.Task;
import crow.task.TaskList;
import crow.task.Todo;
import crow.ui.Ui;

import java.nio.file.Path;
import java.util.List;

/**
 * Coordinates command parsing, task management, storage, and user responses.
 */
public class Crow {
    private static final Path DATA_FILE_PATH = Path.of("data", "crow.txt");
    private static final String TASK_GROUP_SEPARATOR = "-".repeat(20);

    private final Ui ui;
    private final Storage storage;
    private final TaskList taskList;
    private final String loadingErrorMessage;

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

        TaskList loadedTaskList;
        String loadErrorMessage = null;
        try {
            loadedTaskList = new TaskList(storage.load());
        } catch (CrowException e) {
            loadedTaskList = new TaskList();
            loadErrorMessage = e.getMessage();
        }
        taskList = loadedTaskList;
        loadingErrorMessage = loadErrorMessage;
    }

    /**
     * Runs the terminal interface until input ends or the user enters {@code bye}.
     */
    public void run() {
        ui.showWelcome();
        if (loadingErrorMessage != null) {
            ui.showError(loadingErrorMessage);
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
     * @param command Command entered by the user.
     * @return Response describing the command result or error.
     */
    public String getResponse(String command) {
        CommandType commandType = Parser.parseCommandType(command);
        String commandArguments = Parser.parseArguments(command);

        try {
            return switch (commandType) {
            case LIST -> formatTaskList("Here are the tasks in your list:", taskList.asList());
            case FIND -> formatTaskList("Here are the matching tasks in your list:",
                    taskList.find(Parser.parseFindKeyword(commandArguments)));
            case MARK -> markTask(commandArguments);
            case UNMARK -> unmarkTask(commandArguments);
            case DELETE -> deleteTask(commandArguments);
            case TODO -> addTask(Parser.parseTodo(commandArguments));
            case DEADLINE -> addTask(Parser.parseDeadline(commandArguments));
            case EVENT -> addTask(Parser.parseEvent(commandArguments));
            case BYE -> "Bye. Hope to see you again soon!";
            case UNKNOWN -> throw new CrowException("Error: Unknown command.");
            default -> throw new AssertionError("Unhandled command type: " + commandType);
            };
        } catch (CrowException e) {
            return e.getMessage();
        }
    }

    private String markTask(String commandArguments) throws CrowException {
        int taskIndex = Parser.parseTaskIndex(commandArguments, taskList.size());
        taskList.mark(taskIndex);
        storage.save(taskList.asList());
        return "Nice! I've marked this task as done:\n  " + taskList.get(taskIndex);
    }

    private String unmarkTask(String commandArguments) throws CrowException {
        int taskIndex = Parser.parseTaskIndex(commandArguments, taskList.size());
        taskList.unmark(taskIndex);
        storage.save(taskList.asList());
        return "OK, I've marked this task as not done yet:\n  " + taskList.get(taskIndex);
    }

    private String deleteTask(String commandArguments) throws CrowException {
        int taskIndex = Parser.parseTaskIndex(commandArguments, taskList.size());
        Task removedTask = taskList.delete(taskIndex);
        storage.save(taskList.asList());
        return "Noted. I've removed this task:\n  " + removedTask + "\n" + formatTaskCount();
    }

    private String addTask(Task task) throws CrowException {
        taskList.add(task);
        storage.save(taskList.asList());
        return "Got it. I've added this task:\n  " + task + "\n" + formatTaskCount();
    }

    private String formatTaskCount() {
        String taskWord = taskList.size() == 1 ? "task" : "tasks";
        return "Now you have " + taskList.size() + " " + taskWord + " in the list.";
    }

    private String formatTaskList(String heading, List<Task> tasksToDisplay) {
        StringBuilder responseBuilder = new StringBuilder(heading);
        String previousTaskGroup = null;
        for (int i = 0; i < tasksToDisplay.size(); i++) {
            String taskGroup = getTaskGroupName(tasksToDisplay.get(i));
            if (!taskGroup.equals(previousTaskGroup)) {
                if (previousTaskGroup != null) {
                    responseBuilder.append("\n").append(TASK_GROUP_SEPARATOR);
                }
                responseBuilder.append("\n").append(taskGroup).append(":");
                previousTaskGroup = taskGroup;
            }
            responseBuilder.append("\n").append(i + 1).append(".").append(tasksToDisplay.get(i));
        }
        return responseBuilder.toString();
    }

    /**
     * Returns the display heading for a supported task type.
     */
    private String getTaskGroupName(Task task) {
        if (task instanceof Todo) {
            return "ToDos";
        }
        if (task instanceof Deadline) {
            return "Deadlines";
        }
        if (task instanceof Event) {
            return "Events";
        }
        throw new AssertionError("Unsupported task type: " + task.getClass().getName());
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
