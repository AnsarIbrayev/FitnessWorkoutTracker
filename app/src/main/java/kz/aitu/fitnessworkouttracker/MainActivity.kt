package kz.aitu.fitnessworkouttracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import kz.aitu.fitnessworkouttracker.ui.navigation.AppNavGraph
import kz.aitu.fitnessworkouttracker.ui.theme.FitnessWorkoutTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FitnessWorkoutTrackerTheme {
                AppNavGraph()
            }
        }
    }
}
