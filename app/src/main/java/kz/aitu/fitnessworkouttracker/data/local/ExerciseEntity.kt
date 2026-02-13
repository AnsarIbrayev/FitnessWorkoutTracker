package kz.aitu.fitnessworkouttracker.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercises")
data class ExerciseEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val description: String,
    val updatedAt: Long
)
