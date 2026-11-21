package pl.edu.taskflow

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import androidx.navigation.toRoute
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import pl.edu.taskflow.model.TaskPriority
import pl.edu.taskflow.ui.navigation.*
import pl.edu.taskflow.ui.screens.AddTaskScreen

class TaskFlowTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun addTaskScreen_isolatedTest_invokesCallbackWithFormData() {
        var savedTitle = ""
        var savedPriority: TaskPriority? = null

        composeTestRule.setContent {
            AddTaskScreen(
                onSaveTask = { title, priority ->
                    savedTitle = title
                    savedPriority = priority
                },
                onBack = {}
            )
        }

        composeTestRule.onNodeWithTag("input_task_title").performTextInput("Kupić kawę")
        composeTestRule.onNodeWithText("Wysoki").performClick()
        composeTestRule.onNodeWithTag("btn_save_task").performClick()

        assertEquals("Kupić kawę", savedTitle)
        assertEquals(TaskPriority.HIGH, savedPriority)
    }

    @Test
    fun navigationFlow_addTaskAndNavigateToDetails() {
        lateinit var navController: TestNavHostController

        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current).apply {
                navigatorProvider.addNavigator(ComposeNavigator())
            }
            TaskFlowApp(navController = navController)
        }

        // 1. Ekran startowy
        assertTrue(navController.currentBackStackEntry?.destination?.hasRoute<TasksListRoute>() == true)

        // 2. Kliknięcie FAB i przejście na AddTaskRoute
        composeTestRule.onNodeWithTag("fab_add_task").performClick()
        assertTrue(navController.currentBackStackEntry?.destination?.hasRoute<AddTaskRoute>() == true)

        // 3. Wpisanie tytułu i zapis
        composeTestRule.onNodeWithTag("input_task_title").performTextInput("Testowy wpis E2E")
        composeTestRule.onNodeWithTag("btn_save_task").performClick()

        // 4. Powrót na listę i weryfikacja
        assertTrue(navController.currentBackStackEntry?.destination?.hasRoute<TasksListRoute>() == true)
        composeTestRule.onNodeWithText("Testowy wpis E2E").assertIsDisplayed()

        // 5. Otwarcie szczegółów
        composeTestRule.onNodeWithText("Testowy wpis E2E").performClick()
        assertTrue(navController.currentBackStackEntry?.destination?.hasRoute<TaskDetailsRoute>() == true)
        composeTestRule.onNodeWithTag("btn_delete_task").assertIsDisplayed()

        // --- Dopisane: dalsza część przepływu wymagana w sekcji 5.2 ---

        // 6. Odczyt argumentu trasy przez toRoute – taskId nowego zadania nie jest pusty
        val args = navController.currentBackStackEntry?.toRoute<TaskDetailsRoute>()
        assertTrue(args?.taskId?.isNotBlank() == true)

        // 7. Na ekranie szczegółów pasek dolny jest ukryty
        composeTestRule.onNodeWithTag("nav_tab_stats").assertDoesNotExist()

        // 8. Powrót strzałką „Wróć” na listę zadań
        composeTestRule.onNodeWithContentDescription("Wróć").performClick()
        assertTrue(navController.currentBackStackEntry?.destination?.hasRoute<TasksListRoute>() == true)

        // 9. Przełączenie zakładki na Statystyki
        composeTestRule.onNodeWithTag("nav_tab_stats").performClick()
        assertTrue(navController.currentBackStackEntry?.destination?.hasRoute<StatsRoute>() == true)

        // 10. Statystyki widzą 3 zadania (2 startowe + dodane w teście)
        composeTestRule.onNodeWithText("Wszystkie zadania: 3").assertIsDisplayed()
    }
}