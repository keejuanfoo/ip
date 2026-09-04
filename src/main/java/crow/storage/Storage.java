package crow.storage;

import crow.exception.CrowException;
import crow.task.Deadline;
import crow.task.Event;
import crow.task.Task;
import crow.task.Todo;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads tasks from and saves tasks to a local text file.
 */
public class Storage {
    private final Path filePath;

    /**
     * Creates a storage manager for the given file path.
     *
     * @param filePath Relative path of the data file.
     */
    public Storage(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads all tasks from the data file.
     * Returns an empty list when the file does not exist yet.
     *
     * @return Tasks loaded from the data file.
     * @throws CrowException If the file cannot be read or contains invalid data.
     */
    public ArrayList<Task> load() throws CrowException {
        ArrayList<Task> tasks = new ArrayList<>();
        if (Files.notExists(filePath)) {
            return tasks;
        }

        try {
            for (String taskDataLine : Files.readAllLines(filePath, StandardCharsets.UTF_8)) {
                if (!taskDataLine.isBlank()) {
                    tasks.add(parseTask(taskDataLine));
                }
            }
            return tasks;
        } catch (IOException e) {
            throw new CrowException("Error: Unable to read saved tasks.");
        }
    }

    /**
     * Saves all tasks, creating the data directory and file when necessary.
     *
     * @param tasks Tasks to save.
     * @throws CrowException If the tasks cannot be saved.
     */
    public void save(List<Task> tasks) throws CrowException {
        ArrayList<String> taskDataLines = new ArrayList<>();
        for (Task task : tasks) {
            taskDataLines.add(formatTask(task));
        }

        try {
            Path parentDirectory = filePath.getParent();
            if (parentDirectory != null) {
                Files.createDirectories(parentDirectory);
            }
            Files.write(filePath, taskDataLines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new CrowException("Error: Unable to save tasks.");
        }
    }

    /**
     * Converts one task into its file representation.
     */
    private String formatTask(Task task) {
        String completionStatus = task.isDone() ? "1" : "0";
        if (task instanceof Deadline deadline) {
            return "D | " + completionStatus + " | " + task.getDescription()
                    + " | " + deadline.getDeadlineDateTime();
        }
        if (task instanceof Event event) {
            return "E | " + completionStatus + " | " + task.getDescription()
                    + " | " + event.getStartDateTime() + " | " + event.getEndDateTime();
        }
        return "T | " + completionStatus + " | " + task.getDescription();
    }

    /**
     * Converts one line from the data file into a task.
     *
     * @throws CrowException If the line has an unsupported format.
     */
    private Task parseTask(String taskDataLine) throws CrowException {
        String[] taskFields = taskDataLine.split(" \\| ", -1);
        try {
            Task task = switch (taskFields[0]) {
            case "T" -> new Todo(taskFields[2]);
            case "D" -> new Deadline(taskFields[2], LocalDateTime.parse(taskFields[3]));
            case "E" -> new Event(taskFields[2], LocalDateTime.parse(taskFields[3]),
                    LocalDateTime.parse(taskFields[4]));
            default -> throw new CrowException("Error: Invalid task type in data file.");
            };
            if (taskFields[1].equals("1")) {
                task.markAsDone();
            } else if (!taskFields[1].equals("0")) {
                throw new CrowException("Error: Invalid task status in data file.");
            }
            return task;
        } catch (ArrayIndexOutOfBoundsException | DateTimeParseException e) {
            throw new CrowException("Error: Invalid task data in data file.");
        }
    }
}
