package crow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import crow.exception.CrowException;
import crow.storage.Storage;
import crow.task.Task;
import crow.ui.Ui;

class CrowTest {
    @TempDir
    Path tempDirectory;

    @Test
    void run_completeWorkflow_updatesOutputAndStorage() throws CrowException {
        Path filePath = tempDirectory.resolve("data").resolve("crow.txt");
        String input = "todo read book\n"
                + "deadline return book /by 2/12/2019 1800\n"
                + "event meeting /from 3/12/2019 1400 /to 3/12/2019 1600\n"
                + "mark 1\n"
                + "unmark 1\n"
                + "find book\n"
                + "delete 2\n"
                + "list\n"
                + "blah\n"
                + "bye\n";
        ByteArrayOutputStream outputBytes = new ByteArrayOutputStream();
        Ui ui = createUi(input, outputBytes);

        new Crow(filePath, ui).run();

        String output = outputBytes.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("[T][ ] read book"));
        assertTrue(output.contains("[D][ ] return book (by: Dec 02 2019 6:00PM)"));
        assertTrue(output.contains("[E][ ] meeting (from: Dec 03 2019 2:00PM"));
        assertTrue(output.contains("Noted. I've removed this task:"));
        assertTrue(output.contains("Here are the matching tasks in your list:"));
        assertTrue(output.contains("Error: Unknown command."));
        assertTrue(output.contains("Bye. Hope to see you again soon!"));

        List<Task> savedTasks = new Storage(filePath).load();
        assertEquals(2, savedTasks.size());
        assertEquals("read book", savedTasks.get(0).getDescription());
        assertEquals("meeting", savedTasks.get(1).getDescription());
    }

    @Test
    void run_existingTasks_loadsAndListsTasks() throws IOException {
        Path filePath = tempDirectory.resolve("crow.txt");
        Files.writeString(filePath, "T | 1 | saved task", StandardCharsets.UTF_8);
        ByteArrayOutputStream outputBytes = new ByteArrayOutputStream();
        Ui ui = createUi("list\nbye\n", outputBytes);

        new Crow(filePath, ui).run();

        assertTrue(outputBytes.toString(StandardCharsets.UTF_8).contains("1.[T][X] saved task"));
    }

    @Test
    void run_invalidSavedData_reportsErrorAndStartsEmpty() throws IOException {
        Path filePath = tempDirectory.resolve("crow.txt");
        Files.writeString(filePath, "invalid data", StandardCharsets.UTF_8);
        ByteArrayOutputStream outputBytes = new ByteArrayOutputStream();
        Ui ui = createUi("list\nbye\n", outputBytes);

        new Crow(filePath, ui).run();

        String output = outputBytes.toString(StandardCharsets.UTF_8);
        assertTrue(output.contains("Error: Invalid task type in data file."));
        assertTrue(output.contains("Here are the tasks in your list:"));
    }

    private Ui createUi(String input, ByteArrayOutputStream outputBytes) {
        ByteArrayInputStream inputStream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        PrintStream outputStream = new PrintStream(outputBytes, true, StandardCharsets.UTF_8);
        return new Ui(inputStream, outputStream);
    }
}
