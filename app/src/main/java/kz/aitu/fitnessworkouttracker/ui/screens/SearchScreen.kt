package kz.aitu.fitnessworkouttracker.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kz.aitu.fitnessworkouttracker.viewmodel.SearchViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun SearchScreen(navController: NavHostController) {
    val vm: SearchViewModel = koinViewModel()
    val state by vm.state.collectAsState()

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Search Exercises", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(10.dp))

        OutlinedTextField(
            value = state.query,
            onValueChange = { vm.onQueryChange(it) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Search") },
            singleLine = true
        )

        Spacer(Modifier.height(12.dp))

        if (state.loading && state.items.isEmpty()) {
            CircularProgressIndicator()
            Spacer(Modifier.height(8.dp))
            Text("Loading...")
            return@Column
        }

        state.error?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(8.dp))
        }

        LazyColumn(Modifier.weight(1f)) {
            items(state.items) { ex ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        // ✅ ФИКС: правильный маршрут
                        .clickable { navController.navigate("exerciseDetails/${ex.id}") },
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text(
                            text = ex.name,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = ex.description,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            item {
                Spacer(Modifier.height(12.dp))
                if (state.hasMore && !state.loading) {
                    Button(
                        onClick = { vm.loadMore() },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Load more") }
                } else if (state.loading && state.items.isNotEmpty()) {
                    CircularProgressIndicator()
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}
