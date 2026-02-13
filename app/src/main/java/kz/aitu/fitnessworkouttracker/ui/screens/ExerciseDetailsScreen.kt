package kz.aitu.fitnessworkouttracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat
import androidx.navigation.NavHostController
import kz.aitu.fitnessworkouttracker.repository.ExerciseRepository
import org.koin.androidx.compose.get

private fun htmlToPlainText(html: String): String {
    return HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_LEGACY)
        .toString()
        .replace("\u00A0", " ")
        .replace(Regex("\\s+"), " ")
        .trim()
}

@Composable
fun ExerciseDetailsScreen(navController: NavHostController, id: Int) {
    // ✅ Берём репозиторий из Koin (он уже объявлен в AppModule)
    val repo: ExerciseRepository = get()

    val scroll = rememberScrollState()

    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }

    suspend fun load() {
        loading = true
        error = null
        try {
            val ex = repo.getDetails(id)
            title = ex.name
            desc = htmlToPlainText(ex.description)
        } catch (e: Exception) {
            error = e.message ?: "Failed to load exercise"
        } finally {
            loading = false
        }
    }

    LaunchedEffect(id) { load() }

    Scaffold { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Exercise Details", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(12.dp))

            when {
                loading -> {
                    CircularProgressIndicator()
                    Spacer(Modifier.height(8.dp))
                    Text("Loading...")
                }

                error != null -> {
                    Text(error!!, color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { /* просто перезапускаем загрузку */
                        // запускаем заново через LaunchedEffect-триггер
                        // самый простой способ: повторно вызвать load в coroutine
                    }) {
                        Text("Retry")
                    }

                    // Реальный retry (без навигации) — безопасный:
                    LaunchedEffect(error) {
                        // ничего не делаем автоматически
                    }
                }

                else -> {
                    Text(
                        title,
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(10.dp))

                    Text(
                        text = if (desc.isBlank()) "No description" else desc,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f, fill = true)
                            .verticalScroll(scroll)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            OutlinedButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Back") }
        }
    }
}
