package crow.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import crow.task.Todo;

class UiTest {
    private ByteArrayOutputStream outputBytes;
    private Ui ui;

    @BeforeEach
    void setUp() {
        outputBytes = new ByteArrayOutputStream();
        ui = createUi("");
    }

    @Test
    void readCommand_trimsInputAndDetectsEnd() {
        ui = createUi("  todo read book  \nbye\n");

        assertTrue(ui.hasNextCommand());
        assertEquals("todo read book", ui.readCommand());
        assertTrue(ui.hasNextCommand());
        assertEquals("bye", ui.readCommand());
        assertFalse(ui.hasNextCommand());
    }

    @Test
    void showWelcome_displaysBannerGreetingAndSeparator() {
        ui.showWelcome();

        String output = outputText();
        assertTrue(output.startsWith("____________________________________________________________"));
        assertTrue(output.contains("___  ____   __   _  _"));
        assertTrue(output.contains("Hello! I'm Crow."));
        assertTrue(output.contains("What can I do for you?"));
    }

    @Test
    void taskMessages_displayTaskAndCounts() {
        Todo todo = new Todo("read book");

        ui.showTaskAdded(todo, 1);
        ui.showTaskList(List.of(todo));
        ui.showMatchingTasks(List.of(todo));
        todo.markAsDone();
        ui.showTaskMarked(todo);
        todo.markAsNotDone();
        ui.showTaskUnmarked(todo);
        ui.showTaskDeleted(todo, 0);

        String output = outputText();
        assertTrue(output.contains("Got it. I've added this task:"));
        assertTrue(output.contains("Now you have 1 task in the list."));
        assertTrue(output.contains("1.[T][ ] read book"));
        assertTrue(output.contains("Here are the matching tasks in your list:"));
        assertTrue(output.contains("Nice! I've marked this task as done:"));
        assertTrue(output.contains("OK, I've marked this task as not done yet:"));
        assertTrue(output.contains("Noted. I've removed this task:"));
        assertTrue(output.contains("Now you have 0 tasks in the list."));
    }

    @Test
    void errorAndGoodbye_displayExpectedMessages() {
        ui.showError("Error: Unknown command.");
        ui.showGoodbye();

        String output = outputText();
        assertTrue(output.contains("Error: Unknown command."));
        assertTrue(output.contains("Bye. Hope to see you again soon!"));
        assertTrue(output.endsWith("____________________________________________________________\n"));
    }

    private Ui createUi(String input) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        PrintStream outputStream = new PrintStream(outputBytes, true, StandardCharsets.UTF_8);
        return new Ui(inputStream, outputStream);
    }

    private String outputText() {
        return outputBytes.toString(StandardCharsets.UTF_8);
    }
}
