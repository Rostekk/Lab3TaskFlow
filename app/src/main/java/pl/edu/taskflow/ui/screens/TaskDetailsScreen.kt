package pl.edu.taskflow.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import pl.edu.taskflow.model.TaskItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailsScreen(
    task: TaskItem?,
    onToggleComplete: (String) -> Unit,
    onDeleteTask: (String) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Szczegóły zadania") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Wróć")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (task == null) {
                Text("Nie znaleziono zadania.")
            } else {
                Text(text = task.title, style = MaterialTheme.typography.headlineSmall)
                Text(text = "Priorytet: ${task.priority.label}")
                Text(text = "Status: ${if (task.isCompleted) "Ukończone" else "W trakcie"}")

                Button(onClick = { onToggleComplete(task.id) }) {
                    Text(if (task.isCompleted) "Oznacz jako aktywne" else "Oznacz jako ukończone")
                }

                OutlinedButton(
                    onClick = { onDeleteTask(task.id) },
                    modifier = Modifier.testTag("btn_delete_task")
                ) {
                    Text("Usuń zadanie")
                }
            }
        }
    }
}