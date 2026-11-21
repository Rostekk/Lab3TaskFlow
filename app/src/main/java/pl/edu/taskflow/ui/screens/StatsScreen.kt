package pl.edu.taskflow.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pl.edu.taskflow.model.TasksUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(uiState: TasksUiState) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Statystyki") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Wszystkie zadania: ${uiState.totalCount}", style = MaterialTheme.typography.titleMedium)
            Text("Ukończone: ${uiState.completedCount}", style = MaterialTheme.typography.titleMedium)
            Text("Postęp realizacji: ${uiState.completionPercentage}%", style = MaterialTheme.typography.titleMedium)
            LinearProgressIndicator(
                progress = { uiState.completionPercentage / 100f },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}