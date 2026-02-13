package kz.aitu.fitnessworkouttracker.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ExerciseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<ExerciseEntity>)

    @Query("""
        SELECT * FROM exercises
        WHERE name LIKE '%' || :q || '%' OR description LIKE '%' || :q || '%'
        ORDER BY updatedAt DESC
        LIMIT :limit OFFSET :offset
    """)
    suspend fun searchCached(q: String, limit: Int, offset: Int): List<ExerciseEntity>

    @Query("SELECT * FROM exercises WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): ExerciseEntity?
}
