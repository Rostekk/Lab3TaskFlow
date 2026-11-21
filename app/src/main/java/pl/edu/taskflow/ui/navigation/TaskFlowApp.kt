package pl.edu.taskflow.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import pl.edu.taskflow.ui.screens.AddTaskScreen
import pl.edu.taskflow.ui.screens.StatsScreen
import pl.edu.taskflow.ui.screens.TaskDetailsScreen
import pl.edu.taskflow.ui.screens.TasksListScreen
import pl.edu.taskflow.ui.viewmodel.TasksViewModel

@Composable
fun TaskFlowApp(
    navController: NavHostController = rememberNavController(),
    tasksViewModel: TasksViewModel = viewModel()
) {
    val uiState by tasksViewModel.uiState.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Pasek dolny tylko na ekranach zakładek (lista zadań i statystyki).
    // Na formularzu (AddTaskRoute) i szczegółach (TaskDetailsRoute) jest ukryty.
    val showBottomBar = currentDestination?.hierarchy?.any {
        it.hasRoute<TasksListRoute>() || it.hasRoute<StatsRoute>()
    } == true

    // Jedna funkcja do przełączania zakładek (jak w ćwiczeniu 4):
    // - popUpTo(start) { saveState = true } – nie budujemy w nieskończoność stosu zakładek,
    //   a stan opuszczanej zakładki (np. przewinięcie listy) zostaje zapisany,
    // - launchSingleTop – ponowne kliknięcie tej samej zakładki nie tworzy drugiej kopii ekranu,
    // - restoreState – po powrocie do zakładki przywracamy jej zapisany stan.
    fun navigateToTab(route: Any) {
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    NavigationBarItem(
                        modifier = Modifier.testTag("nav_tab_tasks"),
                        selected = currentDestination?.hierarchy?.any { it.hasRoute<TasksListRoute>() } == true,
                        onClick = { navigateToTab(TasksListRoute) },
                        icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Zadania") },
                        label = { Text("Zadania") }
                    )
                    NavigationBarItem(
                        modifier = Modifier.testTag("nav_tab_stats"),
                        selected = currentDestination?.hierarchy?.any { it.hasRoute<StatsRoute>() } == true,
                        onClick = { navigateToTab(StatsRoute) },
                        icon = { Icon(Icons.Default.Info, contentDescription = "Statystyki") },
                        label = { Text("Statystyki") }
                    )
                }
            }
        }
    ) { innerPadding ->
        // NavController zna tylko NavHost – ekrany dostają wyłącznie stan i lambdy.
        NavHost(
            navController = navController,
            startDestination = TasksListRoute,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<TasksListRoute> {
                TasksListScreen(
                    uiState = uiState,
                    onToggleComplete = { tasksViewModel.toggleTaskCompleted(it) },
                    onFilterChange = { tasksViewModel.setShowOnlyActive(it) },
                    // Przejście do formularza dodawania
                    onAddClick = { navController.navigate(AddTaskRoute) },
                    // Przejście do szczegółów – przekazujemy tylko id zadania
                    onTaskClick = { taskId -> navController.navigate(TaskDetailsRoute(taskId)) }
                )
            }

            composable<AddTaskRoute> {
                AddTaskScreen(
                    onSaveTask = { title, priority ->
                        tasksViewModel.addTask(title, priority)
                        // Po zapisie wracamy na listę
                        navController.navigateUp()
                    },
                    onBack = { navController.navigateUp() }
                )
            }

            composable<TaskDetailsRoute>(
                // Deep link: taskflow://tasks/{taskId} otwiera od razu szczegóły zadania.
                // Ścieżka /{taskId} jest dopisywana automatycznie na podstawie pól trasy.
                deepLinks = listOf(
                    navDeepLink<TaskDetailsRoute>(basePath = "taskflow://tasks")
                )
            ) { backStackEntry ->
                // Odczyt argumentu trasy w sposób bezpieczny typowo
                val args = backStackEntry.toRoute<TaskDetailsRoute>()

                // getTaskById czyta zwykłą wartość z ViewModelu, więc wiążemy go z uiState:
                // gdy stan się zmieni (np. „Oznacz jako ukończone”), zadanie zostanie
                // pobrane ponownie i ekran od razu się odświeży.
                val task = remember(uiState, args.taskId) {
                    tasksViewModel.getTaskById(args.taskId)
                }

                TaskDetailsScreen(
                    task = task,
                    onToggleComplete = { id -> tasksViewModel.toggleTaskCompleted(id) },
                    onDeleteTask = { id ->
                        tasksViewModel.deleteTask(id)
                        navController.navigateUp()
                    },
                    onBack = { navController.navigateUp() }
                )
            }

            composable<StatsRoute> {
                StatsScreen(uiState = uiState)
            }
        }
    }
}
