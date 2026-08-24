package crow.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Represents a task that occurs between a start and end date or time.
 */
public class Event extends Task {
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd yyyy h:mma", Locale.ENGLISH);

    protected LocalDateTime from;
    protected LocalDateTime to;

    /**
     * Creates an event task.
     *
     * @param description Description of the event.
     * @param from Start date and time.
     * @param to End date and time.
     */
    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the event's start date and time.
     *
     * @return Start date and time.
     */
    public LocalDateTime getFrom() {
        return from;
    }

    /**
     * Returns the event's end date and time.
     *
     * @return End date and time.
     */
    public LocalDateTime getTo() {
        return to;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from.format(DISPLAY_FORMAT)
                + " to: " + to.format(DISPLAY_FORMAT) + ")";
    }
}
