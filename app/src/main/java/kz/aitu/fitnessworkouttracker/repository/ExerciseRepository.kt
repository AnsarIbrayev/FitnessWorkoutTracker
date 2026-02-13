package kz.aitu.fitnessworkouttracker.repository

import kz.aitu.fitnessworkouttracker.data.api.ExerciseItem
import kz.aitu.fitnessworkouttracker.data.api.WgerApi
import kz.aitu.fitnessworkouttracker.data.local.ExerciseDao
import kz.aitu.fitnessworkouttracker.data.local.ExerciseEntity

class ExerciseRepository(
    private val api: WgerApi,
    private val dao: ExerciseDao
) {
    private fun clean(html: String): String =
        html.replace(Regex("<[^>]*>"), " ")
            .replace("\u00A0", " ")
            .replace(Regex("\\s+"), " ")
            .trim()

    /**
     * ✅ Offline-first:
     *  - try network -> map -> cache in Room -> return (with extra client-side filter)
     *  - if error -> return from Room cache
     */
    suspend fun searchWithNames(q: String, limit: Int, offset: Int): List<ExerciseItem> {
        val query = q.trim()
        if (query.isBlank()) return emptyList()

        return try {
            val page = api.searchExerciseInfo(
                limit = limit,
                offset = offset,
                search = query,
                language = 2
            )

            val mapped = page.results.map { d ->
                val tr = d.translations?.firstOrNull { it.language == 2 }
                    ?: d.translations?.firstOrNull()

                val nameFinal =
                    tr?.name?.takeIf { it.isNotBlank() }
                        ?: d.name?.takeIf { it.isNotBlank() }
                        ?: d.nameOriginal?.takeIf { it.isNotBlank() }
                        ?: "Exercise #${d.id}"

                val descFinal =
                    tr?.description?.takeIf { it.isNotBlank() }
                        ?: d.description?.takeIf { it.isNotBlank() }
                        ?: "No description"

                ExerciseItem(
                    id = d.id,
                    name = nameFinal,
                    description = clean(descFinal)
                )
            }

            // ✅ cache
            val now = System.currentTimeMillis()
            dao.upsertAll(
                mapped.map { ExerciseEntity(it.id, it.name, it.description, now) }
            )

            // ✅ страховка: сервер иногда отдаёт общий список → фильтруем сами
            val filtered = mapped.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.description.contains(query, ignoreCase = true)
            }
            if (filtered.isNotEmpty()) filtered else mapped

        } catch (e: Exception) {
            // ✅ offline fallback
            dao.searchCached(query, limit, offset)
                .map { ExerciseItem(it.id, it.name, it.description) }
        }
    }

    suspend fun getDetails(id: Int): ExerciseItem {
        // 1) cache first
        val cached = dao.getById(id)
        if (cached != null) {
            return ExerciseItem(cached.id, cached.name, cached.description)
        }

        // 2) network + save
        val d = api.getExerciseInfo(id, language = 2)
        val tr = d.translations?.firstOrNull { it.language == 2 }
            ?: d.translations?.firstOrNull()

        val nameFinal =
            tr?.name?.takeIf { it.isNotBlank() }
                ?: d.name?.takeIf { it.isNotBlank() }
                ?: d.nameOriginal?.takeIf { it.isNotBlank() }
                ?: "Exercise #${d.id}"

        val descFinal =
            tr?.description?.takeIf { it.isNotBlank() }
                ?: d.description?.takeIf { it.isNotBlank() }
                ?: "No description"

        val item = ExerciseItem(d.id, nameFinal, clean(descFinal))

        dao.upsertAll(
            listOf(ExerciseEntity(item.id, item.name, item.description, System.currentTimeMillis()))
        )

        return item
    }
}
