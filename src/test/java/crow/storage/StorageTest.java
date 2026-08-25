package crow.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import crow.exception.CrowException;
import crow.task.Deadline;
import crow.task.Event;
import crow.task.Task;
import crow.task.Todo;

class StorageTest {
    @TempDir
    Path tempDirectory;

    @Test
    void load_missingFile_returnsEmptyList() throws CrowException {
        Path filePath = tempDirectory.resolve("data").resolve("crow.txt");
        Storage storage = new Storage(filePath);

        assertTrue(storage.load().isEmpty());
        assertFalse(Files.exists(filePath));
    }

    @Test
    void save_missingDirectory_createsDirectoryAndFile() throws CrowException, IOException {
        Path filePath = tempDirectory.resolve("data").resolve("crow.txt");
        Storage storage = new Storage(filePath);
        Todo todo = new Todo("read book");
        todo.markAsDone();

        storage.save(List.of(todo));

        assertTrue(Files.exists(filePath));
        assertEquals(List.of("T | 1 | read book"),
                Files.readAllLines(filePath, StandardCharsets.UTF_8));
    }

    @Test
    void saveAndLoad_allTaskTypes_preservesData() throws CrowException {
        Path filePath = tempDirectory.resolve("crow.txt");
        Storage storage = new Storage(filePath);
        Todo todo = new Todo("read book");
        todo.markAsDone();
        Deadline deadline = new Deadline("return book", LocalDateTime.of(2019, 12, 2, 18, 0));
        Event event = new Event("meeting", LocalDateTime.of(2019, 12, 3, 14, 0),
                LocalDateTime.of(2019, 12, 3, 16, 0));

        storage.save(List.of(todo, deadline, event));
        List<Task> loadedTasks = storage.load();

        assertEquals(3, loadedTasks.size());
        assertInstanceOf(Todo.class, loadedTasks.get(0));
        assertInstanceOf(Deadline.class, loadedTasks.get(1));
        assertInstanceOf(Event.class, loadedTasks.get(2));
        assertEquals("[T][X] read book", loadedTasks.get(0).toString());
        assertEquals(deadline.toString(), loadedTasks.get(1).toString());
        assertEquals(event.toString(), loadedTasks.get(2).toString());
    }

    @Test
    void save_emptyList_createsEmptyFile() throws CrowException, IOException {
        Path filePath = tempDirectory.resolve("crow.txt");
        Storage storage = new Storage(filePath);

        storage.save(List.of());

        assertTrue(Files.exists(filePath));
        assertEquals(0, Files.size(filePath));
    }

    @Test
    void load_invalidTaskType_throwsException() throws IOException {
        Storage storage = storageContaining("Z | 0 | unknown");

        CrowException exception = assertThrows(CrowException.class, storage::load);

        assertEquals("Error: Invalid task type in data file.", exception.getMessage());
    }

    @Test
    void load_invalidStatus_throwsException() throws IOException {
        Storage storage = storageContaining("T | 2 | read book");

        CrowException exception = assertThrows(CrowException.class, storage::load);

        assertEquals("Error: Invalid task status in data file.", exception.getMessage());
    }

    @Test
    void load_incompleteOrInvalidDateData_throwsException() throws IOException {
        Storage incompleteStorage = storageContaining("D | 0 | return book");
        Storage invalidDateStorage = storageContaining("D | 0 | return book | Friday");

        assertThrows(CrowException.class, incompleteStorage::load);
        assertThrows(CrowException.class, invalidDateStorage::load);
    }

    private Storage storageContaining(String content) throws IOException {
        Path filePath = tempDirectory.resolve("data-" + Math.abs(content.hashCode()) + ".txt");
        Files.writeString(filePath, content, StandardCharsets.UTF_8);
        return new Storage(filePath);
    }
}
