package pl.edu.taskflow.ui.navigation

import kotlinx.serialization.Serializable

// TODO [Krok 3]: Utwórz trasy Type-Safe z adnotacją @Serializable:
// 1. TasksListRoute (data object)
// 2. AddTaskRoute (data object)
// 3. TaskDetailsRoute (data class z parametrem val taskId: String)
// 4. StatsRoute (data object)

@Serializable
data object TasksListRoute

@Serializable
data object AddTaskRoute

// Trasa z argumentem: przekazujemy tylko lekki identyfikator zadania,
// a pełny obiekt pobieramy z ViewModelu (jedno źródło prawdy).
@Serializable
data class TaskDetailsRoute(val taskId: String)

@Serializable
data object StatsRoute
