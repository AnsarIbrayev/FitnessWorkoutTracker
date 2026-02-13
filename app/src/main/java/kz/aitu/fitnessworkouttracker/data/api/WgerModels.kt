package kz.aitu.fitnessworkouttracker.data.api

import com.google.gson.annotations.SerializedName

// список /api/v2/exercise/
data class ExercisePageDto(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<ExerciseDto>
)

data class ExerciseDto(
    val id: Int
)

// ✅ список /api/v2/exerciseinfo/
data class ExerciseInfoPageDto(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<ExerciseInfoDto>
)

data class ExerciseTranslationDto(
    val id: Int?,
    val name: String?,
    val description: String?,
    val language: Int?
)

data class ExerciseInfoDto(
    val id: Int,
    val name: String? = null,
    val description: String? = null,

    @SerializedName("name_original")
    val nameOriginal: String? = null,

    val translations: List<ExerciseTranslationDto>? = null
)

data class ExerciseItem(
    val id: Int,
    val name: String,
    val description: String
)
