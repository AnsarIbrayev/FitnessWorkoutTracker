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
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun WorkoutDetailsScreen(navController: NavHostController, id: String) {
    val repo = remember { WorkoutRepository() }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val workoutFlow = remember(id) { repo.observeWorkoutById(id) }
    val workout by workoutFlow.collectAsState(initial = null)

    var editMode by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var durationText by remember { mutableStateOf("") }
    var saving by remember { mutableStateOf(false) }

    val df = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()) }

    LaunchedEffect(workout?.id) {
        val w = workout ?: return@LaunchedEffect
        title = w.title
        notes = w.notes
        durationText = w.durationMin.toString()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Workout Details", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(10.dp))

            val w = workout
            if (w == null) {
                CircularProgressIndicator()
                Spacer(Modifier.height(10.dp))
                Text("Loading...")
                return@Column
            }

            Text(df.format(Date(w.dateMillis)), style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(12.dp))

            if (!editMode) {
                Text(w.title, style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(8.dp))
                Text("Duration: ${w.durationMin} min")
                Spacer(Modifier.height(8.dp))
                Text(if (w.notes.isBlank()) "No notes" else w.notes)

                Spacer(Modifier.height(16.dp))

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = { editMode = true },
                        modifier = Modifier.weight(1f)
                    ) { Text("Edit") }

                    OutlinedButton(
                        onClick = {
                            scope.launch {
                                try {
                                    repo.deleteWorkout(id)
                                    snackbarHostState.showSnackbar("Deleted")
                                    navController.popBackStack()
                                } catch (e: Exception) {
                                    snackbarHostState.showSnackbar(e.message ?: "Delete failed")
                                }
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) { Text("Delete") }
                }

                Spacer(Modifier.height(10.dp))

                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.fillMaxWidth()
                ) { Text("Back") }

            } else {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
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

                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = {
                            val t = title.trim()
                            if (t.isBlank()) {
                                scope.launch { snackbarHostState.showSnackbar("Title is required") }
                                return@Button
                            }
                            val dur = durationText.toIntOrNull() ?: 0

                            scope.launch {
                                saving = true
                                try {
                                    repo.updateWorkout(id, t, notes, dur)
                                    snackbarHostState.showSnackbar("Saved")
                                    editMode = false
                                } catch (e: Exception) {
                                    snackbarHostState.showSnackbar(e.message ?: "Save failed")
                                } finally {
                                    saving = false
                                }
                            }
                        },
                        enabled = !saving,
                        modifier = Modifier.weight(1f)
                    ) { Text(if (saving) "Saving..." else "Save") }

                    OutlinedButton(
                        onClick = { editMode = false },
                        modifier = Modifier.weight(1f)
                    ) { Text("Cancel") }
                }
            }
        }
    }
}
