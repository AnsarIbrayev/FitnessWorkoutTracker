package kz.aitu.fitnessworkouttracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch
import kz.aitu.fitnessworkouttracker.repository.WorkoutRepository

@Composable
fun AddWorkoutScreen(navController: NavHostController) {
    val repo = remember { WorkoutRepository() }
    val scope = rememberCoroutineScope()

    val snackbarHostState = remember { SnackbarHostState() }

    var title by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var durationText by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Add Workout", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title*") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = durationText,
                onValueChange = { durationText = it.filter { ch -> ch.isDigit() } },
                label = { Text("Duration (minutes)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    val t = title.trim()
                    if (t.isBlank()) {
                        scope.launch { snackbarHostState.showSnackbar("Title is required") }
                        return@Button
                    }
                    val dur = durationText.toIntOrNull() ?: 0

                    scope.launch {
                        loading = true
                        try {
                            repo.addWorkout(t, notes, dur)
                            title = ""
                            notes = ""
                            durationText = ""
                            snackbarHostState.showSnackbar("Workout saved")
                            navController.navigate("workouts") {
                                launchSingleTop = true
                            }
                        } catch (e: Exception) {
                            snackbarHostState.showSnackbar(e.message ?: "Save failed")
                        } finally {
                            loading = false
                        }
                    }
                },
                enabled = !loading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (loading) "Saving..." else "Save")
            }

            Spacer(Modifier.height(10.dp))

            OutlinedButton(
                onClick = { navController.navigate("workouts") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Go to Workouts")
            }
        }
    }
}
