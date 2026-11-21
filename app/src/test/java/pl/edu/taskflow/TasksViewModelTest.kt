package pl.edu.taskflow

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import pl.edu.taskflow.model.TaskPriority
import pl.edu.taskflow.ui.viewmodel.TasksViewModel

class TasksViewModelTest {

    private lateinit var viewModel: TasksViewModel

    @Before
    fun setup() {
        viewModel = TasksViewModel()
    }

    @Test
    fun addTask_withBlankTitle_doesNotChangeTaskList() {
        val initialSize = viewModel.uiState.value.tasks.size
        viewModel.addTask("   ", TaskPriority.HIGH)
        assertEquals(initialSize, viewModel.uiState.value.tasks.size)

        // Dopisane: całkiem pusty tytuł też jest ignorowany, a licznik się nie zmienia
        viewModel.addTask("", TaskPriority.LOW)
        assertEquals(initialSize, viewModel.uiState.value.tasks.size)
        assertEquals(initialSize, viewModel.uiState.value.totalCount)
    }

    @Test
    fun addTask_withValidTitle_addsTaskAndUpdatesTotalCount() {
        val initialSize = viewModel.uiState.value.tasks.size
        viewModel.addTask("Nowe zadanie testowe", TaskPriority.HIGH)
        
        assertEquals(initialSize + 1, viewModel.uiState.value.tasks.size)
        assertEquals("Nowe zadanie testowe", viewModel.uiState.value.tasks.last().title)

        // Dopisane: licznik totalCount też wzrósł, a zadanie ma wybrany priorytet
        assertEquals(initialSize + 1, viewModel.uiState.value.totalCount)
        assertEquals(TaskPriority.HIGH, viewModel.uiState.value.tasks.last().priority)
    }

    @Test
    fun toggleTaskCompleted_updatesCompletedCountAndPercentage() {
        val taskId = "2" // Zadanie domyślnie nieukończone

        // Dopisane: stan początkowy – 1 z 2 ukończone = 50%
        assertEquals(1, viewModel.uiState.value.completedCount)
        assertEquals(50, viewModel.uiState.value.completionPercentage)

        viewModel.toggleTaskCompleted(taskId)

        val task = viewModel.getTaskById(taskId)
        assertEquals(true, task?.isCompleted)

        // Dopisane: po przełączeniu – 2 z 2 ukończone = 100%
        assertEquals(2, viewModel.uiState.value.completedCount)
        assertEquals(100, viewModel.uiState.value.completionPercentage)
    }

    @Test
    fun deleteTask_removesTaskFromState() {
        val taskId = "1"
        val initialCount = viewModel.uiState.value.totalCount // Dopisane
        viewModel.deleteTask(taskId)

        assertNull(viewModel.getTaskById(taskId))

        // Dopisane: licznik spadł o 1, a drugie zadanie zostało na liście
        assertEquals(initialCount - 1, viewModel.uiState.value.totalCount)
        assertEquals("2", viewModel.uiState.value.tasks.single().id)
    }
}