import java.util.Locale;

/**
 * Represents the commands understood by Crow.
 */
public enum CommandType {
    TODO,
    DEADLINE,
    EVENT,
    LIST,
    MARK,
    UNMARK,
    DELETE,
    BYE,
    UNKNOWN;

    /**
     * Converts a command word into its corresponding command type.
     *
     * @param commandWord First word entered by the user.
     * @return Matching command type, or {@link #UNKNOWN} if there is no match.
     */
    public static CommandType from(String commandWord) {
        try {
            return valueOf(commandWord.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return UNKNOWN;
        }
    }
}
