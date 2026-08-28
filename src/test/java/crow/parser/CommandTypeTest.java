package crow.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class CommandTypeTest {
    @Test
    void from_knownCommand_isCaseInsensitive() {
        assertEquals(CommandType.DEADLINE, CommandType.from("deadline"));
        assertEquals(CommandType.DEADLINE, CommandType.from("dEADline"));
        assertEquals(CommandType.FIND, CommandType.from("find"));
        assertEquals(CommandType.BYE, CommandType.from("BYE"));
    }

    @Test
    void from_unknownCommand_returnsUnknown() {
        assertEquals(CommandType.UNKNOWN, CommandType.from("blah"));
        assertEquals(CommandType.UNKNOWN, CommandType.from(""));
    }
}
