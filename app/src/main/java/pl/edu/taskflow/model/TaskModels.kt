package pl.edu.taskflow.model

import java.util.UUID

enum class TaskPriority(val label: String) {
    LOW("Niski"),
    MEDIUM("Średni"),
    HIGH("Wysoki")
}

data class TaskItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val isCompleted: Boolean = false
)

data class TasksUiState(
    val tasks: List<TaskItem> = emptyList(),
    val showOnlyActive: Boolean = false
) {
    val visibleTasks: List<TaskItem>
        get() = if (showOnlyActive) tasks.filter { !it.isCompleted } else tasks

    val totalCount: Int get() = tasks.size
    val completedCount: Int get() = tasks.count { it.isCompleted }
    val completionPercentage: Int
        get() = if (totalCount == 0) 0 else (completedCount * 100) / totalCount
}