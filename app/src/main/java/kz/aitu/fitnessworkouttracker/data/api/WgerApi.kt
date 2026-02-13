package kz.aitu.fitnessworkouttracker.data.api

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface WgerApi {

    // Старый оставляем (может пригодиться)
    @GET("api/v2/exercise/")
    suspend fun searchExercises(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
        @Query("search") search: String?,
        @Query("language") language: Int = 2
    ): ExercisePageDto

    // ✅ НОВОЕ: нормальный список с переводами и описанием + поиск
    @GET("api/v2/exerciseinfo/")
    suspend fun searchExerciseInfo(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
        @Query("search") search: String?,
        @Query("language") language: Int = 2
    ): ExerciseInfoPageDto

    @GET("api/v2/exerciseinfo/{id}/")
    suspend fun getExerciseInfo(
        @Path("id") id: Int,
        @Query("language") language: Int = 2
    ): ExerciseInfoDto
}
