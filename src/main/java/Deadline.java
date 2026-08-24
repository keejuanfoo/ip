/**
 * Represents a task that must be completed by a specific date or time.
 */
public class Deadline extends Task {
    protected String by;

    /**
     * Creates a deadline task.
     *
     * @param description Description of the task.
     * @param by Date or time by which the task must be completed.
     */
    public Deadline(String description, String by) {
        super(description);
        this.by = by;
    }

    /**
     * Returns the deadline as entered by the user.
     *
     * @return Deadline text.
     */
    public String getBy() {
        return by;
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + by + ")";
    }
}
