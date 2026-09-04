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
    private static final String DEADLINE_SEPARATOR = " /by ";
    private static final String EVENT_START_SEPARATOR = " /from ";
    private static final String EVENT_END_SEPARATOR = " /to ";
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
        return CommandType.parseCommandWord(inputParts[0]);
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
        int deadlineSeparatorIndex = arguments.indexOf(DEADLINE_SEPARATOR);
        int dateTimeStartIndex = deadlineSeparatorIndex + DEADLINE_SEPARATOR.length();
        if (deadlineSeparatorIndex <= 0 || arguments.substring(dateTimeStartIndex).trim().isEmpty()) {
            throw new CrowException("Error: Use deadline DESCRIPTION /by d/M/yyyy HHmm.");
        }
        String description = arguments.substring(0, deadlineSeparatorIndex).trim();
        LocalDateTime deadlineDateTime = parseDateTime(arguments.substring(dateTimeStartIndex).trim());
        return new Deadline(description, deadlineDateTime);
    }

    /**
     * Creates an event from its command arguments.
     *
     * @param arguments Event description, start, and end date-times.
     * @return Parsed event.
     * @throws CrowException If the arguments are incomplete or invalid.
     */
    public static Event parseEvent(String arguments) throws CrowException {
        int startSeparatorIndex = arguments.indexOf(EVENT_START_SEPARATOR);
        if (startSeparatorIndex <= 0) {
            throw createInvalidEventFormatException();
        }
        int startDateTimeIndex = startSeparatorIndex + EVENT_START_SEPARATOR.length();
        int endSeparatorIndex = arguments.indexOf(EVENT_END_SEPARATOR, startDateTimeIndex);
        if (endSeparatorIndex < 0) {
            throw createInvalidEventFormatException();
        }

        String description = arguments.substring(0, startSeparatorIndex).trim();
        String startDateTimeInput = arguments.substring(startDateTimeIndex, endSeparatorIndex).trim();
        String endDateTimeInput = arguments.substring(
                endSeparatorIndex + EVENT_END_SEPARATOR.length()).trim();
        if (startDateTimeInput.isEmpty() || endDateTimeInput.isEmpty()) {
            throw createInvalidEventFormatException();
        }
        LocalDateTime startDateTime = parseDateTime(startDateTimeInput);
        LocalDateTime endDateTime = parseDateTime(endDateTimeInput);
        return new Event(description, startDateTime, endDateTime);
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
    private static CrowException createInvalidEventFormatException() {
        return new CrowException("Error: Use event DESCRIPTION /from d/M/yyyy HHmm /to d/M/yyyy HHmm.");
    }
}
