/**
 * Represents a task that occurs between a start and end date or time.
 */
public class Event extends Task {
    protected String from;
    protected String to;

    /**
     * Creates an event task.
     *
     * @param description Description of the event.
     * @param from Start date or time.
     * @param to End date or time.
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the event's start as entered by the user.
     *
     * @return Start text.
     */
    public String getFrom() {
        return from;
    }

    /**
     * Returns the event's end as entered by the user.
     *
     * @return End text.
     */
    public String getTo() {
        return to;
    }

    @Override
    public String toString() {
        return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
