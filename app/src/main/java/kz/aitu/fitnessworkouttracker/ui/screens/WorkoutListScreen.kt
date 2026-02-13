package kz.aitu.fitnessworkouttracker.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kz.aitu.fitnessworkouttracker.repository.WorkoutRepository
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun WorkoutListScreen(navController: NavHostController) {
    val repo = remember { WorkoutRepository() }
    val workoutsFlow = remember { repo.observeWorkouts() }

    val workouts by workoutsFlow.collectAsState(initial = emptyList())

    val df = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()) }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Workouts", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(12.dp))

        if (workouts.isEmpty()) {
            Text("No workouts yet. Go to Add and save your first workout.")
            return@Column
        }

        LazyColumn(Modifier.fillMaxSize()) {
            items(workouts, key = { it.id }) { w ->
                Card(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        // ✅ ВАЖНО: теперь ведём на workoutDetails
                        .clickable { navController.navigate("workoutDetails/${w.id}") }
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text(w.title, style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(4.dp))
                        Text(df.format(Date(w.dateMillis)), style = MaterialTheme.typography.bodySmall)
                        Spacer(Modifier.height(6.dp))
                        val line = buildString {
                            append("Duration: ${w.durationMin} min")
                            if (w.notes.isNotBlank()) append(" • ${w.notes.take(60)}")
                        }
                        Text(line)
                    }
                }
            }
        }
    }
}
