package crow;

import crow.exception.CrowException;
import crow.parser.CommandType;
import crow.parser.Parser;
import crow.storage.Storage;
import crow.task.Task;
import crow.task.TaskList;
import crow.ui.Ui;

import java.nio.file.Path;

/**
 * Starts the CROW chatbot application.
 */
public class Crow {
    // relative to where the program is called
    private static final Path DATA_FILE_PATH = Path.of("data", "crow.txt");

    private final Ui ui;
    private final Storage storage;
    private TaskList tasks;

    /**
     * Creates a Crow chatbot that saves tasks at the given path.
     *
     * @param filePath Relative path of the task data file.
     */
    public Crow(Path filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        tasks = new TaskList();
    }

    /**
     * Loads saved tasks and runs the chatbot until input ends or the user exits.
     */
    public void run() {
        ui.showWelcome();
        try {
            tasks = new TaskList(storage.load());
        } catch (CrowException e) {
            ui.showError(e.getMessage());
            ui.showSeparator();
            tasks = new TaskList();
        }

        while (ui.hasNextCommand()) {
            String input = ui.readCommand();
            CommandType commandType = Parser.parseCommandType(input);
            String arguments = Parser.parseArguments(input);
            ui.showSeparator();

            try {
                switch (commandType) {
                case LIST:
                    ui.showTaskList(tasks.asList());
                    break;
                case MARK:
                    int taskIndex = Parser.parseTaskIndex(arguments, tasks.size());
                    tasks.mark(taskIndex);
                    storage.save(tasks.asList());
                    ui.showTaskMarked(tasks.get(taskIndex));
                    break;
                case UNMARK:
                    int unmarkIndex = Parser.parseTaskIndex(arguments, tasks.size());
                    tasks.unmark(unmarkIndex);
                    storage.save(tasks.asList());
                    ui.showTaskUnmarked(tasks.get(unmarkIndex));
                    break;
                case DELETE:
                    int deleteIndex = Parser.parseTaskIndex(arguments, tasks.size());
                    Task removedTask = tasks.delete(deleteIndex);
                    storage.save(tasks.asList());
                    ui.showTaskDeleted(removedTask, tasks.size());
                    break;
                case TODO:
                    Task todo = Parser.parseTodo(arguments);
                    tasks.add(todo);
                    storage.save(tasks.asList());
                    ui.showTaskAdded(todo, tasks.size());
                    break;
                case DEADLINE:
                    Task deadline = Parser.parseDeadline(arguments);
                    tasks.add(deadline);
                    storage.save(tasks.asList());
                    ui.showTaskAdded(deadline, tasks.size());
                    break;
                case EVENT:
                    Task event = Parser.parseEvent(arguments);
                    tasks.add(event);
                    storage.save(tasks.asList());
                    ui.showTaskAdded(event, tasks.size());
                    break;
                case BYE:
                    ui.showGoodbye();
                    return;
                case UNKNOWN:
                default:
                    throw new CrowException("Error: Unknown command.");
                }
            } catch (CrowException e) {
                ui.showError(e.getMessage());
            }

            ui.showSeparator();
        }
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
