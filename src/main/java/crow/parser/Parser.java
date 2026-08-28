package crow.parser;

import crow.exception.CrowException;
import crow.task.Deadline;
import crow.task.Event;
import crow.task.Todo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/**
 * Interprets user input and converts command arguments into program data.
 */
public class Parser {
    private static final DateTimeFormatter INPUT_DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("d/M/uuuu HHmm").withResolverStyle(ResolverStyle.STRICT);

    /**
     * Returns the type represented by the first word of the input.
     *
     * @param input Full user input.
     * @return Recognized command type, or {@link CommandType#UNKNOWN}.
     */
    public static CommandType parseCommandType(String input) {
        String[] inputParts = input.trim().split("\\s+", 2);
        return CommandType.from(inputParts[0]);
    }

    /**
     * Returns everything after the command word.
     *
     * @param input Full user input.
     * @return Trimmed command arguments, or an empty string if absent.
     */
    public static String parseArguments(String input) {
        String[] inputParts = input.trim().split("\\s+", 2);
        return inputParts.length > 1 ? inputParts[1].trim() : "";
    }

    /**
     * Creates a todo from its command arguments.
     *
     * @param arguments Todo description.
     * @return Parsed todo.
     * @throws CrowException If the description is empty.
     */
    public static Todo parseTodo(String arguments) throws CrowException {
        if (arguments.isEmpty()) {
            throw new CrowException("Error: Todo description cannot be empty.");
        }
        return new Todo(arguments);
    }

    /**
     * Creates a deadline from its command arguments.
     *
     * @param arguments Deadline description and date-time.
     * @return Parsed deadline.
     * @throws CrowException If the arguments are incomplete or invalid.
     */
    public static Deadline parseDeadline(String arguments) throws CrowException {
        int byIndex = arguments.indexOf(" /by ");
        if (byIndex <= 0 || arguments.substring(byIndex + 5).trim().isEmpty()) {
            throw new CrowException("Error: Use deadline DESCRIPTION /by d/M/yyyy HHmm.");
        }
        String description = arguments.substring(0, byIndex).trim();
        LocalDateTime by = parseDateTime(arguments.substring(byIndex + 5).trim());
        return new Deadline(description, by);
    }

    /**
     * Creates an event from its command arguments.
     *
     * @param arguments Event description, start, and end date-times.
     * @return Parsed event.
     * @throws CrowException If the arguments are incomplete or invalid.
     */
    public static Event parseEvent(String arguments) throws CrowException {
        int fromIndex = arguments.indexOf(" /from ");
        if (fromIndex <= 0) {
            throw invalidEventFormat();
        }
        int toIndex = arguments.indexOf(" /to ", fromIndex + 7);
        if (toIndex < 0) {
            throw invalidEventFormat();
        }

        String description = arguments.substring(0, fromIndex).trim();
        String fromInput = arguments.substring(fromIndex + 7, toIndex).trim();
        String toInput = arguments.substring(toIndex + 5).trim();
        if (fromInput.isEmpty() || toInput.isEmpty()) {
            throw invalidEventFormat();
        }
        LocalDateTime from = parseDateTime(fromInput);
        LocalDateTime to = parseDateTime(toInput);
        return new Event(description, from, to);
    }

    /**
     * Validates and returns a keyword used to search for tasks.
     *
     * @param arguments Search keyword.
     * @return Validated search keyword.
     * @throws CrowException If the keyword is empty.
     */
    public static String parseFindKeyword(String arguments) throws CrowException {
        if (arguments.isEmpty()) {
            throw new CrowException("Error: Search keyword cannot be empty.");
        }
        return arguments;
    }

    /**
     * Converts a user-provided task number into a valid list index.
     *
     * @param input User-provided task number.
     * @param taskCount Number of tasks currently stored.
     * @return Zero-based list index of the task.
     * @throws CrowException If the input is not a number of an existing task.
     */
    public static int parseTaskIndex(String input, int taskCount) throws CrowException {
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
     */
    private static LocalDateTime parseDateTime(String input) throws CrowException {
        try {
            return LocalDateTime.parse(input, INPUT_DATE_TIME_FORMAT);
        } catch (DateTimeParseException e) {
            throw new CrowException("Error: Date and time must use d/M/yyyy HHmm format.");
        }
    }

    /**
     * Creates the standard error for malformed event arguments.
     */
    private static CrowException invalidEventFormat() {
        return new CrowException("Error: Use event DESCRIPTION /from d/M/yyyy HHmm /to d/M/yyyy HHmm.");
    }
}
