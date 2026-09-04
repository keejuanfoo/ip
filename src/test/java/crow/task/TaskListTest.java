package crow.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

class TaskListTest {
    @Test
    void constructor_copiesInitialList() {
        Todo todo = new Todo("read book");
        List<Task> initialTasks = new java.util.ArrayList<>();
        initialTasks.add(todo);

        TaskList taskList = new TaskList(initialTasks);
        initialTasks.clear();

        assertEquals(1, taskList.size());
        assertSame(todo, taskList.get(0));
    }

    @Test
    void addMarkUnmarkDelete_updatesList() {
        TaskList taskList = new TaskList();
        Todo todo = new Todo("read book");

        taskList.add(todo);
        assertEquals(1, taskList.size());

        taskList.mark(0);
        assertTrue(todo.isDone());

        taskList.unmark(0);
        assertFalse(todo.isDone());

        assertSame(todo, taskList.delete(0));
        assertEquals(0, taskList.size());
    }

    @Test
    void asList_cannotBeModifiedByCaller() {
        TaskList taskList = new TaskList();
        taskList.add(new Todo("read book"));

        assertThrows(UnsupportedOperationException.class,
                () -> taskList.asList().add(new Todo("return book")));
    }

    @Test
    void find_matchingKeyword_returnsMatchesInOriginalOrder() {
        TaskList taskList = new TaskList();
        Todo firstMatch = new Todo("read book");
        Todo nonMatch = new Todo("buy bread");
        Todo secondMatch = new Todo("return BOOK");
        taskList.add(firstMatch);
        taskList.add(nonMatch);
        taskList.add(secondMatch);

        List<Task> matchingTasks = taskList.find("book");

        assertEquals(List.of(firstMatch, secondMatch), matchingTasks);
    }

    @Test
    void find_noMatchingKeyword_returnsEmptyList() {
        TaskList taskList = new TaskList(List.of(new Todo("buy bread")));

        assertTrue(taskList.find("book").isEmpty());
    }

    @Test
    void internalOperations_invalidArguments_triggerAssertions() {
        TaskList taskList = new TaskList();

        assertThrows(AssertionError.class, () -> taskList.add(null));
        assertThrows(AssertionError.class, () -> taskList.find(" "));
        assertThrows(AssertionError.class, () -> taskList.mark(0));
    }
}
