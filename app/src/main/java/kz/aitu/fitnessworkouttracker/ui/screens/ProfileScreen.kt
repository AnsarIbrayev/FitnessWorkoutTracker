package kz.aitu.fitnessworkouttracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import kz.aitu.fitnessworkouttracker.repository.WorkoutRepository

@Composable
fun ProfileScreen() {
    val auth = remember { FirebaseAuth.getInstance() }
    val repo = remember { WorkoutRepository() }
    val workoutsFlow = remember { repo.observeWorkouts() }
    val workouts by workoutsFlow.collectAsState(initial = emptyList())

    val totalMinutes = workouts.sumOf { it.durationMin }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Profile", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(12.dp))

        Text("UID:", style = MaterialTheme.typography.titleMedium)
        Text(auth.currentUser?.uid ?: "No user", style = MaterialTheme.typography.bodyMedium)

        Spacer(Modifier.height(16.dp))

        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(12.dp)) {
                Text("Stats", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Text("Workouts: ${workouts.size}")
                Text("Total minutes: $totalMinutes")
            }
        }
    }
}
