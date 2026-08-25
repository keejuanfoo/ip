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
}
