package pl.edu.taskflow.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import pl.edu.taskflow.model.TaskItem
import pl.edu.taskflow.model.TaskPriority
import pl.edu.taskflow.model.TasksUiState

class TasksViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        TasksUiState(
            tasks = listOf(
                TaskItem(id = "1", title = "Skonfigurować NavHost", priority = TaskPriority.HIGH, isCompleted = true),
                TaskItem(id = "2", title = "Dodać testy nawigacji", priority = TaskPriority.MEDIUM, isCompleted = false)
            )
        )
    )
    val uiState: StateFlow<TasksUiState> = _uiState.asStateFlow()

    fun addTask(title: String, priority: TaskPriority) {
        // Walidacja: pusty tytuł albo same spacje – nic nie robimy.
        if (title.isBlank()) return

        // update {} atomowo podmienia stan; copy() tworzy nowy obiekt z nową listą.
        // Nowe zadanie trafia NA KONIEC listy; id generuje się samo (UUID w TaskItem).
        // totalCount w TasksUiState liczy się z tasks.size, więc zaktualizuje się automatycznie.
        _uiState.update { state ->
            state.copy(tasks = state.tasks + TaskItem(title = title.trim(), priority = priority))
        }
    }

    fun toggleTaskCompleted(taskId: String) {
        _uiState.update { state ->
            state.copy(
                tasks = state.tasks.map { task ->
                    if (task.id == taskId) task.copy(isCompleted = !task.isCompleted) else task
                }
            )
        }
    }

    fun deleteTask(taskId: String) {
        // Zostawiamy wszystkie zadania OPRÓCZ tego o podanym id.
        _uiState.update { state ->
            state.copy(tasks = state.tasks.filterNot { it.id == taskId })
        }
    }

    fun setShowOnlyActive(onlyActive: Boolean) {
        _uiState.update { it.copy(showOnlyActive = onlyActive) }
    }

    fun getTaskById(taskId: String): TaskItem? {
        return _uiState.value.tasks.find { it.id == taskId }
    }
}
