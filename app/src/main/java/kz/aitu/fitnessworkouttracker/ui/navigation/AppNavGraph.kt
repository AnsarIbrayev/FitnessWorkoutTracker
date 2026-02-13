package kz.aitu.fitnessworkouttracker.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import kz.aitu.fitnessworkouttracker.ui.screens.*

private sealed class BottomDest(val route: String, val label: String, val icon: @Composable () -> Unit) {
    data object Search : BottomDest("search", "Search", { Icon(Icons.Filled.Search, contentDescription = "Search") })
    data object Workouts : BottomDest("workouts", "Workouts", { Icon(Icons.Filled.List, contentDescription = "Workouts") })
    data object Add : BottomDest("add", "Add", { Icon(Icons.Filled.Add, contentDescription = "Add") })
    data object Profile : BottomDest("profile", "Profile", { Icon(Icons.Filled.Person, contentDescription = "Profile") })
}

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val items = listOf(BottomDest.Search, BottomDest.Workouts, BottomDest.Add, BottomDest.Profile)

    // ✅ Ensure user exists (anonymous) so Firebase DB works immediately
    LaunchedEffect(Unit) {
        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser == null) {
            auth.signInAnonymously()
        }
    }

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: ""

    // ✅ скрываем bottom bar на обоих details экранах
    val showBottomBar = !currentRoute.startsWith("workoutDetails/") && !currentRoute.startsWith("exerciseDetails/")

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    val selectedRoute = navController.currentDestination?.route
                    items.forEach { dest ->
                        NavigationBarItem(
                            selected = selectedRoute == dest.route,
                            onClick = {
                                navController.navigate(dest.route) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            label = { Text(dest.label) },
                            icon = { dest.icon() }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = BottomDest.Search.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(BottomDest.Search.route) {
                SearchScreen(navController = navController)
            }
            composable(BottomDest.Workouts.route) {
                WorkoutListScreen(navController = navController)
            }
            composable(BottomDest.Add.route) {
                AddWorkoutScreen(navController = navController)
            }
            composable(BottomDest.Profile.route) {
                ProfileScreen()
            }

            // ✅ Workout details (Firebase workout)
            composable(
                route = "workoutDetails/{id}",
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) { entry ->
                val id = entry.arguments?.getString("id") ?: return@composable
                WorkoutDetailsScreen(navController = navController, id = id)
            }

            // ✅ Exercise details (Wger exercise)
            composable(
                route = "exerciseDetails/{id}",
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { entry ->
                val id = entry.arguments?.getInt("id") ?: return@composable
                ExerciseDetailsScreen(navController = navController, id = id)
            }
        }
    }
}
